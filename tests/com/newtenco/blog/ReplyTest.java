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
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ReplyTest {

    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void removeWritten() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("delete from reply_tb where id > 5");
            connection.createStatement().executeUpdate("delete from board_tb where id > 5");
        }
    }

    private int count(String sql) throws Exception {
        try (Connection connection = dataSource.getConnection();
             ResultSet rs = connection.createStatement().executeQuery(sql)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    private String loginToken(String username) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"" + username + "\",\"password\":\"1234\"}"))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).headers().firstValue("Authorization").orElse("");
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

    private String replyJson(Integer boardId, String comment) {
        String board = boardId == null ? "" : "\"boardId\":" + boardId + ",";
        return "{" + board + "\"comment\":\"" + comment + "\"}";
    }

    private void assertFail(HttpResponse<String> response, int status, String msg) {
        assertEquals(status, response.statusCode());
        assertEquals(status, (Integer) JsonPath.read(response.body(), "$.status"));
        assertEquals(msg, JsonPath.read(response.body(), "$.msg"));
        assertNull(JsonPath.read(response.body(), "$.body"));
    }

    @Test
    @DisplayName("S1 서버를 띄우면 댓글 5개가 DB에 들어간다 (글 4에 cos 댓글 3개, 글 5에 ssar 댓글 2개)")
    void s1() throws Exception {
        assertEquals(5, count("select count(*) from reply_tb"));
        assertEquals(3, count("select count(*) from reply_tb where board_id = 4 and user_id = 2"));
        assertEquals(2, count("select count(*) from reply_tb where board_id = 5 and user_id = 1"));
    }

    @Test
    @DisplayName("S2 GET /api/boards/{id} 글 상세가 댓글 목록(id, comment, userId, username)을 댓글 번호 순으로 함께 준다. 댓글이 없는 글은 빈 목록이다")
    void s2() throws Exception {
        String detail = send("GET", "/api/boards/4", null, null).body();
        assertEquals(List.of(1, 2, 3), JsonPath.read(detail, "$.body.replies[*].id"));
        assertEquals(List.of("댓글1", "댓글2", "댓글3"), JsonPath.read(detail, "$.body.replies[*].comment"));
        assertEquals(List.of(2, 2, 2), JsonPath.read(detail, "$.body.replies[*].userId"));
        assertEquals(List.of("cos", "cos", "cos"), JsonPath.read(detail, "$.body.replies[*].username"));

        List<Object> empty = JsonPath.read(send("GET", "/api/boards/1", null, null).body(), "$.body.replies");
        assertTrue(empty.isEmpty());
    }

    @Test
    @DisplayName("S3 POST /api/replies가 로그인 토큰과 글 번호, 댓글을 받아 저장하고 id, comment, boardId, userId, username을 돌려준다")
    void s3() throws Exception {
        HttpResponse<String> response = send("POST", "/api/replies", loginToken("ssar"), replyJson(4, "댓글6"));
        assertEquals(200, response.statusCode());
        Integer id = JsonPath.read(response.body(), "$.body.id");
        assertEquals("댓글6", JsonPath.read(response.body(), "$.body.comment"));
        assertEquals(4, (Integer) JsonPath.read(response.body(), "$.body.boardId"));
        assertEquals(1, (Integer) JsonPath.read(response.body(), "$.body.userId"));
        assertEquals("ssar", JsonPath.read(response.body(), "$.body.username"));

        List<Integer> ids = JsonPath.read(send("GET", "/api/boards/4", null, null).body(), "$.body.replies[*].id");
        assertTrue(ids.contains(id));
    }

    @Test
    @DisplayName("S4 없는 글에 댓글을 쓰면 404와 \"게시글을 찾을 수 없습니다\"를 돌려준다")
    void s4() throws Exception {
        assertFail(send("POST", "/api/replies", loginToken("ssar"), replyJson(99, "댓글6")), 404, "게시글을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("S5 댓글이 비었거나 100자를 넘거나 글 번호가 없으면 400과 \"댓글을 입력해주세요\", \"댓글은 100자 이하로 입력해주세요\", \"게시글 번호가 필요합니다\"를 돌려준다")
    void s5() throws Exception {
        String token = loginToken("ssar");
        assertFail(send("POST", "/api/replies", token, replyJson(4, "")), 400, "댓글을 입력해주세요");
        assertFail(send("POST", "/api/replies", token, replyJson(4, "a".repeat(101))), 400, "댓글은 100자 이하로 입력해주세요");
        assertFail(send("POST", "/api/replies", token, replyJson(null, "댓글6")), 400, "게시글 번호가 필요합니다");
    }

    @Test
    @DisplayName("S6 DELETE /api/replies/{id}가 작성자의 댓글을 지운다. 남의 댓글은 403 \"댓글을 삭제할 권한이 없습니다\", 없는 댓글은 404 \"댓글을 찾을 수 없습니다\"")
    void s6() throws Exception {
        String token = loginToken("ssar");
        Integer id = JsonPath.read(send("POST", "/api/replies", token, replyJson(4, "지울 댓글")).body(), "$.body.id");
        HttpResponse<String> response = send("DELETE", "/api/replies/" + id, token, null);
        assertEquals(200, response.statusCode());
        assertEquals(0, count("select count(*) from reply_tb where id = " + id));

        assertFail(send("DELETE", "/api/replies/1", token, null), 403, "댓글을 삭제할 권한이 없습니다");
        assertFail(send("DELETE", "/api/replies/99", token, null), 404, "댓글을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("S7 글을 지우면 그 글의 댓글도 함께 지워진다")
    void s7() throws Exception {
        String token = loginToken("ssar");
        Integer boardId = JsonPath.read(send("POST", "/api/boards", token, "{\"title\":\"지울 글\",\"content\":\"지울 내용\"}").body(), "$.body.id");
        send("POST", "/api/replies", token, replyJson(boardId, "함께 지울 댓글"));
        assertEquals(1, count("select count(*) from reply_tb where board_id = " + boardId));

        assertEquals(200, send("DELETE", "/api/boards/" + boardId, token, null).statusCode());
        assertEquals(0, count("select count(*) from reply_tb where board_id = " + boardId));
    }
}
