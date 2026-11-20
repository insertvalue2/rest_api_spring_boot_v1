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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class JwtAuthTest {

    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void removeJoinedUsers() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("delete from user_tb where username not in ('ssar', 'cos')");
        }
    }

    private String loginToken() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"ssar\",\"password\":\"1234\"}"))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).headers().firstValue("Authorization").orElse("");
    }

    private HttpResponse<String> get(String path, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:8080" + path)).GET();
        if (token != null) {
            builder.header("Authorization", token);
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private void assertFail(HttpResponse<String> response, int status, String msg) {
        assertEquals(status, response.statusCode());
        assertEquals(status, (Integer) JsonPath.read(response.body(), "$.status"));
        assertEquals(msg, JsonPath.read(response.body(), "$.msg"));
        assertNull(JsonPath.read(response.body(), "$.body"));
    }

    @Test
    @DisplayName("S1 GET /api/users/me에 로그인 토큰(Authorization: Bearer ...)을 담아 보내면 내 정보(id, username, email)를 돌려준다")
    void s1() throws Exception {
        HttpResponse<String> response = get("/api/users/me", loginToken());
        assertEquals(200, response.statusCode());
        assertEquals(1, (Integer) JsonPath.read(response.body(), "$.body.id"));
        assertEquals("ssar", JsonPath.read(response.body(), "$.body.username"));
        assertEquals("ssar@metacoding.com", JsonPath.read(response.body(), "$.body.email"));
    }

    @Test
    @DisplayName("S2 토큰 없이 /api/** 로그인 필요 주소를 부르면 401과 \"로그인 후 이용해주세요\"를 공통 응답 모양으로 돌려준다")
    void s2() throws Exception {
        assertFail(get("/api/users/me", null), 401, "로그인 후 이용해주세요");
    }

    @Test
    @DisplayName("S3 위조되었거나 만료된 토큰도 401과 같은 문구를 돌려준다")
    void s3() throws Exception {
        String token = loginToken();
        int signatureStart = token.lastIndexOf('.') + 1;
        char first = token.charAt(signatureStart);
        String forged = token.substring(0, signatureStart) + (first == 'A' ? 'B' : 'A') + token.substring(signatureStart + 1);
        assertFail(get("/api/users/me", forged), 401, "로그인 후 이용해주세요");
        assertFail(get("/api/users/me", "Bearer abc.def.ghi"), 401, "로그인 후 이용해주세요");
    }

    @Test
    @DisplayName("S4 글 목록, 글 상세, /join, /login, /check-username, H2 콘솔은 토큰 없이 그대로 열린다")
    void s4() throws Exception {
        assertEquals(200, get("/api/boards", null).statusCode());
        assertEquals(200, get("/api/boards/1", null).statusCode());
        assertEquals(200, get("/check-username?username=nobody", null).statusCode());
        assertEquals(200, get("/h2-console/", null).statusCode());
        assertEquals(false, loginToken().isEmpty());

        HttpRequest join = HttpRequest.newBuilder(URI.create("http://localhost:8080/join"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"jwtuser\",\"password\":\"1234\",\"email\":\"jwt@nate.com\"}"))
                .build();
        assertEquals(200, client.send(join, HttpResponse.BodyHandlers.ofString()).statusCode());

        HttpRequest preflight = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/users/me"))
                .header("Origin", "http://localhost:5173")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "authorization")
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> options = client.send(preflight, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, options.statusCode());
        assertEquals("http://localhost:5173", options.headers().firstValue("Access-Control-Allow-Origin").orElse(null));
    }

    @Test
    @DisplayName("S5 규칙에 없는 주소는 로그인해도 403과 \"권한이 없습니다\"를 돌려준다")
    void s5() throws Exception {
        assertFail(get("/nothing", loginToken()), 403, "권한이 없습니다");
    }

    @Test
    @DisplayName("S6 서버는 로그인 상태를 세션에 저장하지 않는다 (응답에 JSESSIONID 쿠키가 없다)")
    void s6() throws Exception {
        HttpResponse<String> denied = get("/api/users/me", null);
        assertEquals(401, denied.statusCode());
        assertFalse(denied.headers().allValues("Set-Cookie").stream().anyMatch(cookie -> cookie.contains("JSESSIONID")));

        HttpResponse<String> response = get("/api/users/me", loginToken());
        assertEquals(200, response.statusCode());
        assertFalse(response.headers().allValues("Set-Cookie").stream().anyMatch(cookie -> cookie.contains("JSESSIONID")));
    }
}
