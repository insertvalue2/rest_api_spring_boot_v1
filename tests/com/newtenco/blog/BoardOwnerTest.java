package com.newtenco.blog;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BoardOwnerTest {

    private final HttpClient client = HttpClient.newHttpClient();

    private String loginToken(String username) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"username\":\"" + username + "\",\"password\":\"1234\"}"))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).headers().firstValue("Authorization").orElse("");
    }

    private HttpResponse<String> detail(int id, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/boards/" + id)).GET();
        if (token != null) {
            builder.header("Authorization", token);
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @DisplayName("S1 토큰 없이 글 상세를 보면 글의 owner와 모든 댓글의 owner가 false다")
    void s1() throws Exception {
        String json = detail(4, null).body();
        assertEquals(false, JsonPath.read(json, "$.body.owner"));
        assertEquals(List.of(false, false, false), JsonPath.read(json, "$.body.replies[*].owner"));
    }

    @Test
    @DisplayName("S2 작성자의 토큰으로 자기 글 상세를 보면 글의 owner가 true다")
    void s2() throws Exception {
        String json = detail(1, loginToken("ssar")).body();
        assertEquals(true, JsonPath.read(json, "$.body.owner"));
    }

    @Test
    @DisplayName("S3 남의 글에 내가 단 댓글이 있으면, 글의 owner는 false이고 내 댓글만 owner가 true다")
    void s3() throws Exception {
        String json = detail(5, loginToken("ssar")).body();
        assertEquals(false, JsonPath.read(json, "$.body.owner"));
        assertEquals(List.of(4, 5), JsonPath.read(json, "$.body.replies[*].id"));
        assertEquals(List.of(true, true), JsonPath.read(json, "$.body.replies[*].owner"));

        String other = detail(4, loginToken("ssar")).body();
        assertEquals(List.of(false, false, false), JsonPath.read(other, "$.body.replies[*].owner"));
    }

    @Test
    @DisplayName("S4 틀린 토큰으로 글 상세를 보면 막히지 않고 로그인하지 않은 것처럼 owner가 모두 false다")
    void s4() throws Exception {
        HttpResponse<String> response = detail(1, "Bearer abc.def.ghi");
        assertEquals(200, response.statusCode());
        assertEquals(false, JsonPath.read(response.body(), "$.body.owner"));
    }
}
