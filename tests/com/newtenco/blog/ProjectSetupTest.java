package com.newtenco.blog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ProjectSetupTest {

    private final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Autowired
    private ApplicationContext context;

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:8080" + path)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @DisplayName("S1 서버를 띄우면 8080 포트에서 요청을 기다린다")
    void s1() throws Exception {
        HttpResponse<String> response = get("/");
        assertTrue(response.statusCode() > 0);
    }

    @Test
    @DisplayName("S2 서버가 H2 메모리 DB(jdbc:h2:mem:test, 사용자 sa)에 연결된다")
    void s2() throws Exception {
        DataSource dataSource = context.getBeanProvider(DataSource.class).getIfAvailable();
        assertNotNull(dataSource, "DataSource가 없습니다");
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.getMetaData().getURL().startsWith("jdbc:h2:mem:test"));
            assertEquals("SA", connection.getMetaData().getUserName().toUpperCase());
        }
    }

    @Test
    @DisplayName("S3 H2 콘솔 주소(/h2-console)에 접속하면 로그인 화면이 열린다")
    void s3() throws Exception {
        HttpResponse<String> response = get("/h2-console");
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("H2 Console"));
    }
}
