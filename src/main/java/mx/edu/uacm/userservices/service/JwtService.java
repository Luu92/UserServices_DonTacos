package mx.edu.uacm.userservices.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

        @Value("${jwt.secret}")
        private String secret;
        @Value("${jwt.expiration}")
        private Long expiration;

        private SecretKey getSigningKey() {
            return Keys.hmacShaKeyFor(
                    secret.getBytes(StandardCharsets.UTF_8)
            );
        }

        public String gererateToken(Long id, String correo, String rol){
            Date ahora = new Date();
            Date expiracion = new Date(ahora.getTime() + expiration );

            return Jwts.builder()
                    .subject(correo)
                    .claim("id", id)
                    .claim("rol", rol)
                    .issuedAt(ahora)
                    .expiration(expiracion)
                    .signWith(getSigningKey())
                    .compact();
        }

        private Claims extractClaims(String token){
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }

    public String extractCorreo(String token) {
        return extractClaims(token).getSubject();
    }

    public Long extractId(String token) {
        return extractClaims(token)
                .get("id", Long.class);
    }

    public String extractRol(String token) {
        return extractClaims(token)
                .get("rol", String.class);
    }

    public boolean isTokenValid(String token) {

        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String generateRecoveryToken(Long id, String correo) {

        Date ahora = new Date();

        // 10 minutos de vida para el token temporal
        Date expiracion = new Date(
                ahora.getTime() + 600000
        );

        return Jwts.builder()
                .subject(correo)
                .claim("id", id)
                .claim("tipo", "RECUPERACION")
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractTipo(String token) {
        return extractClaims(token)
                .get("tipo", String.class);
    }

    public boolean isRecoveryTokenValid(String token) {

        try {

            Claims claims = extractClaims(token);

            String tipo = claims.get(
                    "tipo",
                    String.class
            );

            return "RECUPERACION".equals(tipo);

        } catch (Exception e) {
            return false;
        }
    }

}
