package com.newtenco.blog;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class RefreshTokenTest {

    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void removeTokens() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("delete from refresh_token_tb");
        }
    }

    private HttpResponse<String> post(String path, String body, String origin) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:8080" + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (origin != null) {
            builder.header("Origin", origin);
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> login() throws Exception {
        return post("/login", "{\"username\":\"ssar\",\"password\":\"1234\"}", null);
    }

    private String refreshToken(HttpResponse<String> response) {
        return response.headers().firstValue("Refresh-Token").orElse("");
    }

    private HttpResponse<String> reissue(String refreshToken) throws Exception {
        return post("/reissue", "{\"refreshToken\":\"" + refreshToken + "\"}", null);
    }

    private void assertFail(HttpResponse<String> response, int status, String msg) {
        assertEquals(status, response.statusCode());
        assertEquals(status, (Integer) JsonPath.read(response.body(), "$.status"));
        assertEquals(msg, JsonPath.read(response.body(), "$.msg"));
        assertNull(JsonPath.read(response.body(), "$.body"));
    }

    @Test
    @DisplayName("S1 로그인하면 응답 헤더 Refresh-Token에 무작위 문자열을 담아 주고, 서버는 그 값을 14일 만료 시각과 함께 DB에 저장한다")
    void s1() throws Exception {
        String token = refreshToken(login());
        assertTrue(token.length() > 20);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select expires_at from refresh_token_tb where token = ?")) {
            statement.setString(1, token);
            ResultSet rs = statement.executeQuery();
            assertTrue(rs.next());
            Timestamp expiresAt = rs.getTimestamp(1);
            long fourteenDaysLater = System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 14;
            assertTrue(Math.abs(expiresAt.getTime() - fourteenDaysLater) < 60_000);
        }
    }

    @Test
    @DisplayName("S2 POST /reissue가 리프레시 토큰을 받아 새 액세스 토큰(Authorization)과 새 리프레시 토큰(Refresh-Token)을 응답 헤더로 준다")
    void s2() throws Exception {
        String first = refreshToken(login());
        HttpResponse<String> response = reissue(first);
        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("Authorization").orElse("").startsWith("Bearer "));
        String second = refreshToken(response);
        assertTrue(second.length() > 20);
        assertNotEquals(first, second);
        assertNull(JsonPath.read(response.body(), "$.body"));
    }

    @Test
    @DisplayName("S3 재발급에 쓴 리프레시 토큰은 바로 지워져서, 같은 값으로 다시 재발급하면 401과 \"다시 로그인해주세요\"를 돌려준다")
    void s3() throws Exception {
        String first = refreshToken(login());
        assertEquals(200, reissue(first).statusCode());
        assertFail(reissue(first), 401, "다시 로그인해주세요");
    }

    @Test
    @DisplayName("S4 없거나 만료된 리프레시 토큰으로 재발급하면 401과 \"다시 로그인해주세요\"를 돌려준다")
    void s4() throws Exception {
        assertFail(reissue("nothing-like-this"), 401, "다시 로그인해주세요");

        String token = refreshToken(login());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("update refresh_token_tb set expires_at = ? where token = ?")) {
            statement.setTimestamp(1, new Timestamp(System.currentTimeMillis() - 1000));
            statement.setString(2, token);
            statement.executeUpdate();
        }
        assertFail(reissue(token), 401, "다시 로그인해주세요");
    }

    @Test
    @DisplayName("S5 POST /logout이 리프레시 토큰을 받아 서버에서 지운다. 그 뒤 그 값으로 재발급하면 401이다")
    void s5() throws Exception {
        String token = refreshToken(login());
        HttpResponse<String> response = post("/logout", "{\"refreshToken\":\"" + token + "\"}", null);
        assertEquals(200, response.statusCode());
        assertNull(JsonPath.read(response.body(), "$.body"));
        assertFail(reissue(token), 401, "다시 로그인해주세요");
    }

    @Test
    @DisplayName("S6 리프레시 토큰을 비우면 400과 \"리프레시 토큰이 필요합니다\"를 돌려준다")
    void s6() throws Exception {
        assertFail(reissue(""), 400, "리프레시 토큰이 필요합니다");
        assertFail(post("/logout", "{\"refreshToken\":\"\"}", null), 400, "리프레시 토큰이 필요합니다");
    }

    @Test
    @DisplayName("S7 /reissue와 /logout은 액세스 토큰 없이 부를 수 있고, 브라우저 코드가 Refresh-Token 응답 헤더를 읽을 수 있다")
    void s7() throws Exception {
        HttpResponse<String> response = post("/login", "{\"username\":\"ssar\",\"password\":\"1234\"}", "http://localhost:5173");
        assertTrue(response.headers().firstValue("Access-Control-Expose-Headers").orElse("").contains("Refresh-Token"));
    }
}
