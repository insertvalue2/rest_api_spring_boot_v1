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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserJoinTest {

    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void removeJoinedUsers() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("delete from user_role_tb where user_id in (select id from user_tb where username not in ('ssar', 'cos'))");
            connection.createStatement().executeUpdate("delete from user_tb where username not in ('ssar', 'cos')");
        }
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080" + path)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> join(String username, String password, String email) throws Exception {
        String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\",\"email\":\"" + email + "\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/join"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void assertFail(HttpResponse<String> response, int status, String msg) {
        assertEquals(status, response.statusCode());
        assertEquals(status, (Integer) JsonPath.read(response.body(), "$.status"));
        assertEquals(msg, JsonPath.read(response.body(), "$.msg"));
        assertNull(JsonPath.read(response.body(), "$.body"));
    }

    @Test
    @DisplayName("S1 Security를 넣어도 글 목록, 글 상세, H2 콘솔은 로그인 없이 그대로 열린다")
    void s1() throws Exception {
        HttpResponse<String> list = get("/api/boards");
        assertEquals(200, list.statusCode());
        assertEquals("SAMEORIGIN", list.headers().firstValue("X-Frame-Options").orElse(null));
        assertEquals(200, get("/api/boards/1").statusCode());
        HttpRequest console = HttpRequest.newBuilder(URI.create("http://localhost:8080/h2-console/")).GET().build();
        assertEquals(200, client.send(console, HttpResponse.BodyHandlers.ofString()).statusCode());
    }

    @Test
    @DisplayName("S2 POST /join이 아이디, 비밀번호, 이메일을 받아 회원으로 저장하고 id, username, email을 돌려준다")
    void s2() throws Exception {
        HttpResponse<String> response = join("love", "1234", "love@nate.com");
        assertEquals(200, response.statusCode());
        String json = response.body();
        assertNotNull(JsonPath.read(json, "$.body.id"));
        assertEquals("love", JsonPath.read(json, "$.body.username"));
        assertEquals("love@nate.com", JsonPath.read(json, "$.body.email"));
        assertTrue(!json.contains("password"));
    }

    @Test
    @DisplayName("S3 저장된 비밀번호는 BCrypt로 암호화돼 원문과 다르다")
    void s3() throws Exception {
        assertEquals(200, join("love", "1234", "love@nate.com").statusCode());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("select password from user_tb where username = ?")) {
            statement.setString(1, "love");
            ResultSet rs = statement.executeQuery();
            assertTrue(rs.next());
            String saved = rs.getString(1);
            assertNotEquals("1234", saved);
            assertTrue(saved.startsWith("$2"));
        }
    }

    @Test
    @DisplayName("S4 입력이 틀리면 400과 이유 문구만 돌려준다 (빈 값, 아이디와 비밀번호 4~20자, 이메일 형식)")
    void s4() throws Exception {
        assertFail(join("love", "12", "love@nate.com"), 400, "비밀번호는 4~20자여야 합니다");
        assertFail(join("abc", "1234", "love@nate.com"), 400, "유저네임은 4~20자여야 합니다");
        assertFail(join("love", "1234", "love-nate.com"), 400, "이메일 형식이 올바르지 않습니다");

        HttpResponse<String> blank = join("love", "", "love@nate.com");
        assertEquals(400, blank.statusCode());
        String msg = JsonPath.read(blank.body(), "$.msg");
        assertTrue(msg.equals("비밀번호를 입력해주세요") || msg.equals("비밀번호는 4~20자여야 합니다"));
    }

    @Test
    @DisplayName("S5 이미 있는 아이디로 가입하면 400과 \"이미 존재하는 유저네임입니다\"를 돌려준다")
    void s5() throws Exception {
        assertFail(join("ssar", "1234", "ssar@nate.com"), 400, "이미 존재하는 유저네임입니다");
    }

    @Test
    @DisplayName("S6 GET /check-username?username=ssar은 available false, 없는 아이디는 true를 돌려준다")
    void s6() throws Exception {
        HttpResponse<String> taken = get("/check-username?username=ssar");
        assertEquals(200, taken.statusCode());
        assertEquals(false, JsonPath.read(taken.body(), "$.body.available"));
        HttpResponse<String> free = get("/check-username?username=nobody");
        assertEquals(200, free.statusCode());
        assertEquals(true, JsonPath.read(free.body(), "$.body.available"));
    }

    @Test
    @DisplayName("S7 check-username에 username을 빼면 400과 \"username:값을 입력해주세요\"를 돌려준다")
    void s7() throws Exception {
        assertFail(get("/check-username"), 400, "username:값을 입력해주세요");
    }
}
