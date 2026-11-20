package com.newtenco.blog;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BoardPagingTest {

    private final HttpClient client = HttpClient.newHttpClient();

    private String get(String query) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080/api/boards" + query)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        return response.body();
    }

    @Test
    @DisplayName("S1 GET /api/boards?page=0이 최신 글 3개를 준다. page는 0부터 센다")
    void s1() throws Exception {
        List<Integer> first = JsonPath.read(get("?page=0"), "$.body..id");
        List<Integer> second = JsonPath.read(get("?page=1"), "$.body..id");
        assertEquals(List.of(5, 4, 3), first);
        assertEquals(List.of(2, 1), second);
    }

    @Test
    @DisplayName("S2 page를 주지 않으면 첫 쪽(page=0)을 준다")
    void s2() throws Exception {
        List<Integer> ids = JsonPath.read(get(""), "$.body..id");
        assertEquals(List.of(5, 4, 3), ids);
    }

    @Test
    @DisplayName("S3 목록과 함께 쪽 정보를 준다: 지금 쪽 번호(number), 전체 쪽 수(totalPage), 첫 쪽인지(isFirst), 마지막 쪽인지(isLast)")
    void s3() throws Exception {
        String first = get("?page=0");
        assertEquals(3, ((List<?>) JsonPath.read(first, "$.body.boards")).size());
        assertEquals(0, (Integer) JsonPath.read(first, "$.body.number"));
        assertEquals(2, (Integer) JsonPath.read(first, "$.body.totalPage"));
        assertEquals(true, JsonPath.read(first, "$.body.isFirst"));
        assertEquals(false, JsonPath.read(first, "$.body.isLast"));

        String last = get("?page=1");
        assertEquals(1, (Integer) JsonPath.read(last, "$.body.number"));
        assertEquals(false, JsonPath.read(last, "$.body.isFirst"));
        assertEquals(true, JsonPath.read(last, "$.body.isLast"));
    }

    @Test
    @DisplayName("S4 keyword를 주면 제목에 그 글자가 들어간 글만 준다. 비어 있으면 전체에서 준다")
    void s4() throws Exception {
        String keyword = URLEncoder.encode("제목1", StandardCharsets.UTF_8);
        List<String> titles = JsonPath.read(get("?page=0&keyword=" + keyword), "$.body..title");
        assertEquals(1, titles.size());
        assertTrue(titles.get(0).contains("제목1"));

        List<Integer> all = JsonPath.read(get("?page=0&keyword="), "$.body..id");
        assertEquals(List.of(5, 4, 3), all);
    }
}
