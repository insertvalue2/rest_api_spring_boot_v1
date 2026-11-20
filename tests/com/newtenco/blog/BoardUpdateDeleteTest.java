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
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BoardUpdateDeleteTest {

    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    private DataSource dataSource;

    @AfterEach
    void restoreBoards() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            connection.createStatement().executeUpdate("update board_tb set title = '제목1', content = '내용1' where id = 1");
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

    private HttpResponse<String> send(String method, String path, String token, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:8080" + path))
                .header("Content-Type", "application/json")
                .method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        if (token != null) {
            builder.header("Authorization", token);
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String boardJson(String title, String content) {
        return "{\"title\":\"" + title + "\",\"content\":\"" + content + "\"}";
    }

    private void assertFail(HttpResponse<String> response, int status, String msg) {
        assertEquals(status, response.statusCode());
        assertEquals(status, (Integer) JsonPath.read(response.body(), "$.status"));
        assertEquals(msg, JsonPath.read(response.body(), "$.msg"));
        assertNull(JsonPath.read(response.body(), "$.body"));
    }

    @Test
    @DisplayName("S1 GET /api/boards/{id}/edit가 작성자에게 수정 화면에 채울 제목과 내용을 돌려준다")
    void s1() throws Exception {
        HttpResponse<String> response = send("GET", "/api/boards/1/edit", loginToken(), null);
        assertEquals(200, response.statusCode());
        assertEquals("제목1", JsonPath.read(response.body(), "$.body.title"));
        assertEquals("내용1", JsonPath.read(response.body(), "$.body.content"));
    }

    @Test
    @DisplayName("S2 PUT /api/boards/{id}가 작성자의 제목, 내용 수정을 받아 고치고 고친 글을 돌려준다")
    void s2() throws Exception {
        HttpResponse<String> response = send("PUT", "/api/boards/1", loginToken(), boardJson("제목1 수정", "내용1 수정"));
        assertEquals(200, response.statusCode());
        assertEquals("제목1 수정", JsonPath.read(response.body(), "$.body.title"));
        assertEquals("내용1 수정", JsonPath.read(response.body(), "$.body.content"));

        String detail = send("GET", "/api/boards/1", null, null).body();
        assertEquals("제목1 수정", JsonPath.read(detail, "$.body.title"));
        assertEquals("내용1 수정", JsonPath.read(detail, "$.body.content"));
    }

    @Test
    @DisplayName("S3 DELETE /api/boards/{id}가 작성자의 글을 지우고, 지운 글은 상세에서 404가 된다")
    void s3() throws Exception {
        String token = loginToken();
        Integer id = JsonPath.read(send("POST", "/api/boards", token, boardJson("지울 글", "지울 내용")).body(), "$.body.id");
        HttpResponse<String> response = send("DELETE", "/api/boards/" + id, token, null);
        assertEquals(200, response.statusCode());
        assertNull(JsonPath.read(response.body(), "$.body"));
        assertFail(send("GET", "/api/boards/" + id, null, null), 404, "게시글을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("S4 남의 글을 수정 정보 조회, 수정, 삭제하면 403과 \"게시글에 접근할 권한이 없습니다\", \"게시글을 수정할 권한이 없습니다\", \"게시글을 삭제할 권한이 없습니다\"를 돌려준다")
    void s4() throws Exception {
        String token = loginToken();
        assertFail(send("GET", "/api/boards/4/edit", token, null), 403, "게시글에 접근할 권한이 없습니다");
        assertFail(send("PUT", "/api/boards/4", token, boardJson("남의 글", "남의 내용")), 403, "게시글을 수정할 권한이 없습니다");
        assertFail(send("DELETE", "/api/boards/4", token, null), 403, "게시글을 삭제할 권한이 없습니다");
    }

    @Test
    @DisplayName("S5 없는 글을 수정 정보 조회, 수정, 삭제하면 404와 \"게시글을 찾을 수 없습니다\"를 돌려준다")
    void s5() throws Exception {
        String token = loginToken();
        assertFail(send("GET", "/api/boards/99/edit", token, null), 404, "게시글을 찾을 수 없습니다");
        assertFail(send("PUT", "/api/boards/99", token, boardJson("없는 글", "없는 내용")), 404, "게시글을 찾을 수 없습니다");
        assertFail(send("DELETE", "/api/boards/99", token, null), 404, "게시글을 찾을 수 없습니다");
    }

    @Test
    @DisplayName("S6 수정할 제목이나 내용이 비었거나 너무 길면 글쓰기와 같은 400 문구를 돌려준다")
    void s6() throws Exception {
        String token = loginToken();
        assertFail(send("PUT", "/api/boards/1", token, boardJson("", "내용1")), 400, "제목을 입력해주세요");
        assertFail(send("PUT", "/api/boards/1", token, boardJson("제목1", "a".repeat(1001))), 400, "내용은 1000자 이하로 입력해주세요");
    }

    @Test
    @DisplayName("S7 수정 정보 조회(GET .../edit)는 토큰이 없으면 401이다. 글 상세(GET /api/boards/{id})는 그대로 열려 있다")
    void s7() throws Exception {
        assertFail(send("GET", "/api/boards/1/edit", null, null), 401, "로그인 후 이용해주세요");
        assertEquals(200, send("GET", "/api/boards/1", null, null).statusCode());
    }
}
