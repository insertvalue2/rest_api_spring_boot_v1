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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserUpdateTest {

    private static final String PASSWORD_1234 = "$2a$10$eiDus5jn3LsNyfjng4/HxukQh.7EZmn1ZNL1g4s3kkqPYpwY5DiuO";

    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void restoreSsar() throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "update user_tb set email = 'ssar@metacoding.com', password = ? where username = 'ssar'")) {
            statement.setString(1, PASSWORD_1234);
            statement.executeUpdate();
        }
    }

    private HttpResponse<String> login(String password) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"ssar\",\"password\":\"" + password + "\"}"))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String loginToken() throws Exception {
        return login("1234").headers().firstValue("Authorization").orElse("");
    }

    private HttpResponse<String> update(String token, String email, String password) throws Exception {
        String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/users/me"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body));
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

    private String savedPassword() throws Exception {
        try (Connection connection = dataSource.getConnection();
             ResultSet rs = connection.createStatement().executeQuery("select password from user_tb where username = 'ssar'")) {
            rs.next();
            return rs.getString(1);
        }
    }

    @Test
    @DisplayName("S1 PUT /api/users/me가 로그인 토큰과 이메일, 비밀번호를 받아 내 정보를 고치고 id, username, email을 돌려준다")
    void s1() throws Exception {
        HttpResponse<String> response = update(loginToken(), "ssar@nate.com", "");
        assertEquals(200, response.statusCode());
        assertEquals(1, (Integer) JsonPath.read(response.body(), "$.body.id"));
        assertEquals("ssar", JsonPath.read(response.body(), "$.body.username"));
        assertEquals("ssar@nate.com", JsonPath.read(response.body(), "$.body.email"));
    }

    @Test
    @DisplayName("S2 비밀번호 칸이 비어 있으면 비밀번호는 바꾸지 않고 이메일만 고친다 (기존 비밀번호로 계속 로그인된다)")
    void s2() throws Exception {
        assertEquals(200, update(loginToken(), "ssar@nate.com", "").statusCode());
        assertEquals(PASSWORD_1234, savedPassword());
        assertEquals(200, login("1234").statusCode());
    }

    @Test
    @DisplayName("S3 비밀번호에 글자가 있으면 새 비밀번호로 바뀌고 BCrypt로 저장된다 (새 비밀번호로 로그인된다)")
    void s3() throws Exception {
        assertEquals(200, update(loginToken(), "ssar@nate.com", "5678").statusCode());
        String saved = savedPassword();
        assertTrue(saved.startsWith("$2"));
        assertTrue(!saved.equals(PASSWORD_1234));
        assertEquals(200, login("5678").statusCode());
        assertEquals(401, login("1234").statusCode());
    }

    @Test
    @DisplayName("S4 비밀번호에 글자가 있는데 4~20자가 아니면 400과 \"비밀번호는 4~20자여야 합니다\"를 돌려준다")
    void s4() throws Exception {
        String token = loginToken();
        assertFail(update(token, "ssar@nate.com", "12"), 400, "비밀번호는 4~20자여야 합니다");
        assertFail(update(token, "ssar@nate.com", "a".repeat(21)), 400, "비밀번호는 4~20자여야 합니다");
    }

    @Test
    @DisplayName("S5 이메일이 비었거나 형식이 틀리면 400과 \"이메일을 입력해주세요\" 또는 \"이메일 형식이 올바르지 않습니다\"를 돌려준다")
    void s5() throws Exception {
        String token = loginToken();
        assertFail(update(token, "", ""), 400, "이메일을 입력해주세요");
        assertFail(update(token, "ssar-nate.com", ""), 400, "이메일 형식이 올바르지 않습니다");
    }

    @Test
    @DisplayName("S6 토큰 없이 부르면 401이다")
    void s6() throws Exception {
        assertFail(update(null, "ssar@nate.com", ""), 401, "로그인 후 이용해주세요");
    }
}
