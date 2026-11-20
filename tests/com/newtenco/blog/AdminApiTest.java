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
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AdminApiTest {

    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void removeWritten() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("delete from reply_tb where id > 5");
            connection.createStatement().executeUpdate("delete from board_tb where id > 5");
            connection.createStatement().executeUpdate("delete from user_role_tb where user_id > 2");
            connection.createStatement().executeUpdate("delete from user_tb where id > 2");
        }
    }

    private List<String> roles(String username) throws Exception {
        List<String> roles = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             ResultSet rs = connection.createStatement().executeQuery(
                     "select r.role from user_role_tb r join user_tb u on r.user_id = u.id where u.username = '" + username + "' order by r.role desc")) {
            while (rs.next()) {
                roles.add(rs.getString(1));
            }
        }
        return roles;
    }

    private HttpResponse<String> send(String method, String path, String token, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:8080" + path))
                .header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        if (token != null) {
            builder.header("Authorization", token);
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String loginToken(String username) throws Exception {
        String body = "{\"username\":\"" + username + "\",\"password\":\"1234\"}";
        return send("POST", "/login", null, body).headers().firstValue("Authorization").orElse("");
    }

    private void assertFail(HttpResponse<String> response, int status, String msg) {
        assertEquals(status, response.statusCode());
        assertEquals(status, (Integer) JsonPath.read(response.body(), "$.status"));
        assertEquals(msg, JsonPath.read(response.body(), "$.msg"));
        assertNull(JsonPath.read(response.body(), "$.body"));
    }

    @Test
    @DisplayName("S1 서버를 띄우면 회원 역할이 들어간다: ssar은 USER, cos는 USER와 ADMIN. 새로 가입한 회원은 USER다")
    void s1() throws Exception {
        assertEquals(List.of("USER"), roles("ssar"));
        assertEquals(List.of("USER", "ADMIN"), roles("cos"));

        String join = "{\"username\":\"adminnew\",\"password\":\"1234\",\"email\":\"adminnew@nate.com\"}";
        assertEquals(200, send("POST", "/join", null, join).statusCode());
        assertEquals(List.of("USER"), roles("adminnew"));
    }

    @Test
    @DisplayName("S2 로그인 토큰 안에 역할 목록(roles)이 들어 있다")
    void s2() throws Exception {
        String token = loginToken("cos");
        String payload = new String(Base64.getUrlDecoder().decode(token.substring(7).split("[.]")[1]), StandardCharsets.UTF_8);
        List<String> roles = JsonPath.read(payload, "$.roles");
        assertEquals(List.of("USER", "ADMIN"), roles);
    }

    @Test
    @DisplayName("S3 GET /api/admin/hello는 ADMIN 토큰이면 \"관리자 cos님 환영합니다\"를, USER 토큰이면 403 \"권한이 없습니다\"를 돌려준다")
    void s3() throws Exception {
        HttpResponse<String> admin = send("GET", "/api/admin/hello", loginToken("cos"), null);
        assertEquals(200, admin.statusCode());
        assertEquals("관리자 cos님 환영합니다", JsonPath.read(admin.body(), "$.body"));

        assertFail(send("GET", "/api/admin/hello", loginToken("ssar"), null), 403, "권한이 없습니다");
    }

    @Test
    @DisplayName("S4 DELETE /api/admin/boards/{id}는 관리자가 작성자와 상관없이 글을 지운다(댓글도 함께). 없는 글은 404 \"게시글을 찾을 수 없습니다\"")
    void s4() throws Exception {
        String ssar = loginToken("ssar");
        Integer boardId = JsonPath.read(send("POST", "/api/boards", ssar, "{\"title\":\"지울 글\",\"content\":\"지울 내용\"}").body(), "$.body.id");
        send("POST", "/api/replies", ssar, "{\"boardId\":" + boardId + ",\"comment\":\"함께 지울 댓글\"}");

        String cos = loginToken("cos");
        HttpResponse<String> response = send("DELETE", "/api/admin/boards/" + boardId, cos, null);
        assertEquals(200, response.statusCode());
        assertFail(send("GET", "/api/boards/" + boardId, null, null), 404, "게시글을 찾을 수 없습니다");

        assertFail(send("DELETE", "/api/admin/boards/99", cos, null), 404, "게시글을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("S5 관리자 주소가 아닌 /api/** 주소는 지금처럼 USER와 ADMIN 모두 쓸 수 있다")
    void s5() throws Exception {
        assertEquals(200, send("GET", "/api/users/me", loginToken("ssar"), null).statusCode());
        assertEquals(200, send("GET", "/api/users/me", loginToken("cos"), null).statusCode());
    }
}
