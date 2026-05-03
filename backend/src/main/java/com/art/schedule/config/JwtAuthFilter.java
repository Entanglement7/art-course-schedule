package com.art.schedule.config;

import com.art.schedule.common.JwtUtil;
import com.art.schedule.entity.User;
import com.art.schedule.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    public JwtAuthFilter(JwtUtil jwtUtil, UserMapper userMapper) {
        this.jwtUtil = jwtUtil;
        this.userMapper = userMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.isTokenValid(token)) {
                Long userId = jwtUtil.getUserId(token);
                User user = userMapper.selectById(userId);
                if (user != null) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            user, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().toUpperCase()))
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
