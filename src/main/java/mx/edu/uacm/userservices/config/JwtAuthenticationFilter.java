package mx.edu.uacm.userservices.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.edu.uacm.userservices.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        if (jwtService.isTokenValid(token)) {

            String correo = jwtService.extractCorreo(token);
            String rol = jwtService.extractRol(token);

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            correo,
                            null,
                            List.of(authority)
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
