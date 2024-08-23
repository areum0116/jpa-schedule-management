package com.sparta.schedulemanagement.filter;

import com.sparta.schedulemanagement.entity.User;
import com.sparta.schedulemanagement.jwt.JwtUtil;
import com.sparta.schedulemanagement.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j(topic = "AuthFilter")
@Component
@RequiredArgsConstructor
@Order(1)
public class AuthFilter implements Filter {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String url = httpServletRequest.getRequestURI();
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if(StringUtils.hasText(url) && (url.startsWith("/users/login") || url.startsWith("/users/register"))) {
            chain.doFilter(request, response);
        } else {
            String tokenValue = jwtUtil.getTokenFromRequest(httpServletRequest);

            if(StringUtils.hasText(tokenValue)) {
                String token = jwtUtil.substringToken(tokenValue);

                if(!jwtUtil.validateToken(token)) {
                    httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }

                Claims info = jwtUtil.getClaimsFromToken(token);

                User user = userRepository.findByUsername(info.getSubject()).orElseThrow(
                        () -> new NullPointerException("User not found")
                );

                request.setAttribute("user", user);
                httpServletResponse.setHeader(JwtUtil.AUTHORIZATION_HEADER, token);
                chain.doFilter(request, response);
            } else {
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
