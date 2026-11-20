package com.newtenco.blog.core.util;

import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

public class RespFilter {

    private static final JsonMapper mapper = JsonMapper.builder().build();

    public static void fail(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(mapper.writeValueAsString(new Resp<>(status, msg, null)));
    }
}
