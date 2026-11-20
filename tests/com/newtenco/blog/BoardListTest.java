package com.newtenco.blog;

import com.jayway.jsonpath.JsonPath;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BoardListTest {

    private final HttpClient client = HttpClient.newHttpClient();

    @Autowired
    private DataSource dataSource;

    private String getBoards() throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/boards")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        return response.body();
    }

    private int count(String table) throws Exception {
        try (Connection connection = dataSource.getConnection();
             ResultSet rs = connection.createStatement().executeQuery("select count(*) from " + table)) {
            rs.next();
            return rs.getInt(1);
        }
    }

    @Test
    @DisplayName("S1 서버를 띄우면 회원 2명(ssar, cos)과 글 5개가 DB에 들어간다")
    void s1() throws Exception {
        assertEquals(2, count("user_tb"));
        assertEquals(5, count("board_tb"));
    }

    @Test
    @DisplayName("S2 GET /api/boards가 모든 글을 작성자 아이디와 함께 돌려준다")
    void s2() throws Exception {
        String json = getBoards();
        List<Object> titles = JsonPath.read(json, "$.body.boards[*].title");
        List<String> usernames = JsonPath.read(json, "$.body.boards[*].username");
        assertEquals(3, titles.size());
        assertEquals(3, usernames.size());
        assertTrue(usernames.containsAll(List.of("ssar", "cos")));
        assertFalse(json.contains("password"));
    }

    @Test
    @DisplayName("S3 글 목록은 최신 글(번호 큰 글)부터 온다")
    void s3() throws Exception {
        List<Integer> ids = JsonPath.read(getBoards(), "$.body.boards[*].id");
        assertEquals(List.of(5, 4, 3), ids);
    }

    @Test
    @DisplayName("S4 응답은 status, msg, body 세 칸으로 감싸서 온다")
    void s4() throws Exception {
        String json = getBoards();
        assertEquals(200, (Integer) JsonPath.read(json, "$.status"));
        assertEquals("성공", JsonPath.read(json, "$.msg"));
        List<Object> boards = JsonPath.read(json, "$.body.boards");
        assertEquals(3, boards.size());
    }
}
