package com.elias.attendancecontrol.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception e, HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        Map<String, Object> body = new HashMap<>();
        body.put("path", path);
        body.put("method", method);
        body.put("timestamp", LocalDateTime.now());
        body.put("message", e.getMessage());

        log.error("An Unexpected Exception occurred at path: {}, {}", path, body);

        return ResponseEntity.internalServerError().body(body);
    }
}
