package com.example.hospitalmanage.filter;

import com.example.hospitalmanage.domain.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static com.example.hospitalmanage.constant.SecurityConstant.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {


    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException exception)
            throws IOException, ServletException {
        HttpResponse httpResponse = new HttpResponse(
                UNAUTHORIZED.value(),
                UNAUTHORIZED,
                UNAUTHORIZED.getReasonPhrase().toUpperCase(),
                ACCESS_DENIED_MESSAGE
        );
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
