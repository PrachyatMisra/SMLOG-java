package com.smartlogistics.servlet;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public abstract class BaseApiServlet extends HttpServlet {

    protected static final Gson GSON = new Gson();

    protected void applyCors(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        applyCors(response);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    protected void writeJson(HttpServletResponse response, int status, Object payload) throws IOException {
        applyCors(response);
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(GSON.toJson(payload));
    }

    protected void writeError(HttpServletResponse response, int status, String message) throws IOException {
        writeJson(response, status, Map.of("error", message));
    }

    protected String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }
        return body.toString();
    }
}
