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
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AdminRolesTest {

    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void restoreSsarRoles() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("delete from user_role_tb where user_id = 1");
            connection.createStatement().executeUpdate("insert into user_role_tb (user_id, role) values (1, 'USER')");
        }
    }

    private int count(String sql) throws Exception {
        try (Connection connection = dataSource.getConnection();
             ResultSet rs = connection.createStatement().executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
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

    private HttpResponse<String> changeRoles(int userId, String rolesJson) throws Exception {
        return send("PUT", "/api/admin/users/" + userId, loginToken("cos"), "{\"roles\":" + rolesJson + "}");
    }

    private void assertFail(HttpResponse<String> response, int status, String msg) {
        assertEquals(status, response.statusCode());
        assertEquals(status, (Integer) JsonPath.read(response.body(), "$.status"));
        assertEquals(msg, JsonPath.read(response.body(), "$.msg"));
        assertNull(JsonPath.read(response.body(), "$.body"));
    }

    @Test
    @DisplayName("S1 PUT /api/admin/users/{id}가 ADMIN 토큰과 역할 목록을 받아 그 회원의 역할을 바꾸고 id, username, roles를 돌려준다. 같은 역할이 여러 번 오면 하나로 친다")
    void s1() throws Exception {
        HttpResponse<String> response = changeRoles(1, "[\"USER\",\"ADMIN\",\"ADMIN\"]");
        assertEquals(200, response.statusCode());
        assertEquals(1, (Integer) JsonPath.read(response.body(), "$.body.id"));
        assertEquals("ssar", JsonPath.read(response.body(), "$.body.username"));
        assertEquals(List.of("USER", "ADMIN"), JsonPath.read(response.body(), "$.body.roles"));
        assertEquals(1, count("select count(*) from user_role_tb where user_id = 1 and role = 'ADMIN'"));
    }

    @Test
    @DisplayName("S2 바뀐 역할은 그 회원이 다시 로그인해 받은 토큰부터 적용된다 (ssar을 ADMIN으로 바꾸면 새 토큰으로 관리자 주소를 쓸 수 있다)")
    void s2() throws Exception {
        String oldToken = loginToken("ssar");
        assertEquals(200, changeRoles(1, "[\"USER\",\"ADMIN\"]").statusCode());
        assertEquals(403, send("GET", "/api/admin/hello", oldToken, null).statusCode());
        assertEquals(200, send("GET", "/api/admin/hello", loginToken("ssar"), null).statusCode());
    }

    @Test
    @DisplayName("S3 없는 회원이면 404와 \"회원을 찾을 수 없습니다\"를 돌려준다")
    void s3() throws Exception {
        assertFail(changeRoles(99, "[\"USER\",\"ADMIN\"]"), 404, "회원을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("S4 역할 목록이 비었으면 400과 \"역할을 설정해주세요\"를 돌려준다")
    void s4() throws Exception {
        assertFail(changeRoles(1, "[]"), 400, "역할을 설정해주세요");
    }

    @Test
    @DisplayName("S5 USER, ADMIN 말고 다른 역할이 있으면 400과 \"역할은 USER, ADMIN 만 사용할 수 있습니다\"를 돌려준다")
    void s5() throws Exception {
        assertFail(changeRoles(1, "[\"USER\",\"MANAGER\"]"), 400, "역할은 USER, ADMIN 만 사용할 수 있습니다");
    }

    @Test
    @DisplayName("S6 지금과 같은 역할이면 400과 \"현재와 같은 역할입니다\"를 돌려준다")
    void s6() throws Exception {
        assertFail(changeRoles(1, "[\"USER\"]"), 400, "현재와 같은 역할입니다");
    }
}
