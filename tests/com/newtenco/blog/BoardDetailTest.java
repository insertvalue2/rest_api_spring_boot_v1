package com.newtenco.blog;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BoardDetailTest {

    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080" + path)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @DisplayName("S1 GET /api/boards/{id}가 글 하나를 작성자 번호, 작성자 아이디와 함께 돌려준다")
    void s1() throws Exception {
        HttpResponse<String> response = get("/api/boards/4");
        assertEquals(200, response.statusCode());
        String json = response.body();
        assertEquals(4, (Integer) JsonPath.read(json, "$.body.id"));
        assertEquals("제목4", JsonPath.read(json, "$.body.title"));
        assertEquals("내용4", JsonPath.read(json, "$.body.content"));
        assertEquals(2, (Integer) JsonPath.read(json, "$.body.userId"));
        assertEquals("cos", JsonPath.read(json, "$.body.username"));
    }

    @Test
    @DisplayName("S2 없는 글 번호를 요청하면 404와 \"게시글을 찾을 수 없습니다\"를 공통 응답 모양으로 돌려준다")
    void s2() throws Exception {
        HttpResponse<String> response = get("/api/boards/99");
        assertEquals(404, response.statusCode());
        String json = response.body();
        assertEquals(404, (Integer) JsonPath.read(json, "$.status"));
        assertEquals("게시글을 찾을 수 없습니다", JsonPath.read(json, "$.msg"));
        assertNull(JsonPath.read(json, "$.body"));
    }

    @Test
    @DisplayName("S3 처리하지 못한 오류(예: 글 번호에 abc)는 500과 \"관리자에게 문의하세요\"를 공통 응답 모양으로 돌려준다")
    void s3() throws Exception {
        HttpResponse<String> response = get("/api/boards/abc");
        assertEquals(500, response.statusCode());
        String json = response.body();
        assertEquals(500, (Integer) JsonPath.read(json, "$.status"));
        assertEquals("관리자에게 문의하세요", JsonPath.read(json, "$.msg"));
        assertNull(JsonPath.read(json, "$.body"));
    }

    @Test
    @DisplayName("S4 없는 주소를 요청하면 404를 공통 응답 모양으로 돌려준다")
    void s4() throws Exception {
        HttpRequest login = HttpRequest.newBuilder(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"ssar\",\"password\":\"1234\"}"))
                .build();
        String token = client.send(login, HttpResponse.BodyHandlers.ofString()).headers().firstValue("Authorization").orElse("");
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/nothing-here"))
                .header("Authorization", token)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        Map<String, Object> json = JsonPath.read(response.body(), "$");
        assertEquals(404, json.get("status"));
        assertTrue(json.containsKey("msg"));
        assertTrue(json.containsKey("body"));
        assertNull(json.get("body"));
    }
}
