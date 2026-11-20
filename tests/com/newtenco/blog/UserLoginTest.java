package com.newtenco.blog;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserLoginTest {

    private final HttpClient client = HttpClient.newHttpClient();

    private HttpResponse<String> login(String username, String password, String origin) throws Exception {
        String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:8080/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body));
        if (origin != null) {
            builder.header("Origin", origin);
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
    @DisplayName("S1 POST /login이 맞는 아이디와 비밀번호를 받으면 200과 회원 정보(id, username, email)를 돌려준다")
    void s1() throws Exception {
        HttpResponse<String> response = login("ssar", "1234", null);
        assertEquals(200, response.statusCode());
        String json = response.body();
        assertEquals(1, (Integer) JsonPath.read(json, "$.body.id"));
        assertEquals("ssar", JsonPath.read(json, "$.body.username"));
        assertEquals("ssar@metacoding.com", JsonPath.read(json, "$.body.email"));
        assertTrue(!json.contains("password"));
    }

    @Test
    @DisplayName("S2 로그인 응답 헤더 Authorization에 \"Bearer \"로 시작하는 JWT를 담아 준다")
    void s2() throws Exception {
        String token = login("ssar", "1234", null).headers().firstValue("Authorization").orElse("");
        assertTrue(token.startsWith("Bearer "));
        assertEquals(3, token.substring(7).split("\\.").length);
    }

    @Test
    @DisplayName("S3 JWT 안에는 회원 번호(id), 아이디(username), 만료 시각(1일 뒤)이 들어 있다")
    void s3() throws Exception {
        String token = login("ssar", "1234", null).headers().firstValue("Authorization").orElse("Bearer a.e30.c");
        String payload = new String(Base64.getUrlDecoder().decode(token.substring(7).split("\\.")[1]), StandardCharsets.UTF_8);
        assertEquals(1, (Integer) JsonPath.read(payload, "$.id"));
        assertEquals("ssar", JsonPath.read(payload, "$.username"));
        long exp = ((Number) JsonPath.read(payload, "$.exp")).longValue();
        long oneDayLater = System.currentTimeMillis() / 1000 + 60 * 60 * 24;
        assertTrue(Math.abs(exp - oneDayLater) < 60);
    }

    @Test
    @DisplayName("S4 없는 아이디이거나 비밀번호가 틀리면 401과 \"유저네임 혹은 비밀번호가 일치하지 않습니다\"를 돌려준다")
    void s4() throws Exception {
        assertFail(login("ssar", "9999", null), 401, "유저네임 혹은 비밀번호가 일치하지 않습니다");
        assertFail(login("nobody", "1234", null), 401, "유저네임 혹은 비밀번호가 일치하지 않습니다");
    }

    @Test
    @DisplayName("S5 아이디나 비밀번호를 비우면 400과 \"~를 입력해주세요\"를 돌려준다")
    void s5() throws Exception {
        assertFail(login("ssar", "", null), 400, "비밀번호를 입력해주세요");
        assertFail(login("", "1234", null), 400, "유저네임을 입력해주세요");
    }

    @Test
    @DisplayName("S6 리액트 주소(localhost:5173, 3000)와 크롬 확장(Talend API Tester 같은)에서 온 요청을 허락하고, 브라우저 코드가 Authorization 응답 헤더를 읽을 수 있게 노출한다")
    void s6() throws Exception {
        HttpResponse<String> response = login("ssar", "1234", "http://localhost:5173");
        assertEquals(200, response.statusCode());
        assertEquals("http://localhost:5173", response.headers().firstValue("Access-Control-Allow-Origin").orElse(null));
        assertTrue(response.headers().firstValue("Access-Control-Expose-Headers").orElse("").contains("Authorization"));

        HttpResponse<String> other = login("ssar", "1234", "http://localhost:3000");
        assertEquals("http://localhost:3000", other.headers().firstValue("Access-Control-Allow-Origin").orElse(null));

        HttpResponse<String> extension = login("ssar", "1234", "chrome-extension://aejoelaoggembcahagimdiliamlcdmfm");
        assertEquals("chrome-extension://aejoelaoggembcahagimdiliamlcdmfm", extension.headers().firstValue("Access-Control-Allow-Origin").orElse(null));
    }
}
