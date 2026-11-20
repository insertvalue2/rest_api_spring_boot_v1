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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BoardWriteTest {

    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void removeWrittenBoards() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("delete from board_tb where id > 5");
        }
    }

    private String loginToken() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"ssar\",\"password\":\"1234\"}"))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).headers().firstValue("Authorization").orElse("");
    }

    private HttpResponse<String> write(String token, String title, String content) throws Exception {
        String body = "{\"title\":\"" + title + "\",\"content\":\"" + content + "\"}";
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/boards"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
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
    @DisplayName("S1 POST /api/boards가 로그인 토큰과 제목, 내용을 받아 로그인한 사람의 글로 저장하고 id, title, content, username을 돌려준다")
    void s1() throws Exception {
        HttpResponse<String> response = write(loginToken(), "제목6", "내용6");
        assertEquals(200, response.statusCode());
        assertNotNull(JsonPath.read(response.body(), "$.body.id"));
        assertEquals("제목6", JsonPath.read(response.body(), "$.body.title"));
        assertEquals("내용6", JsonPath.read(response.body(), "$.body.content"));
        assertEquals("ssar", JsonPath.read(response.body(), "$.body.username"));
    }

    @Test
    @DisplayName("S2 저장한 글은 글 목록 첫 줄(최신 글)에 나온다")
    void s2() throws Exception {
        Integer id = JsonPath.read(write(loginToken(), "제목6", "내용6").body(), "$.body.id");
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/boards")).GET().build();
        String list = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        assertEquals(id, (Integer) JsonPath.read(list, "$.body.boards[0].id"));
        assertEquals("제목6", JsonPath.read(list, "$.body.boards[0].title"));
        assertEquals("ssar", JsonPath.read(list, "$.body.boards[0].username"));
    }

    @Test
    @DisplayName("S3 토큰 없이 글을 쓰면 401과 \"로그인 후 이용해주세요\"를 돌려준다")
    void s3() throws Exception {
        assertFail(write(null, "제목6", "내용6"), 401, "로그인 후 이용해주세요");
    }

    @Test
    @DisplayName("S4 제목이나 내용을 비우면 400과 \"제목을 입력해주세요\" 또는 \"내용을 입력해주세요\"를 돌려준다")
    void s4() throws Exception {
        String token = loginToken();
        assertFail(write(token, "", "내용6"), 400, "제목을 입력해주세요");
        assertFail(write(token, "제목6", ""), 400, "내용을 입력해주세요");
    }

    @Test
    @DisplayName("S5 제목이 100자, 내용이 1000자를 넘으면 400과 \"제목은 100자 이하로 입력해주세요\" 또는 \"내용은 1000자 이하로 입력해주세요\"를 돌려준다")
    void s5() throws Exception {
        String token = loginToken();
        assertFail(write(token, "a".repeat(101), "내용6"), 400, "제목은 100자 이하로 입력해주세요");
        assertFail(write(token, "제목6", "a".repeat(1001)), 400, "내용은 1000자 이하로 입력해주세요");
    }
}
