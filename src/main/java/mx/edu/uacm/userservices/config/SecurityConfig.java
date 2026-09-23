package mx.edu.uacm.userservices.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/comensal/registrar",
                                "/comensal/auth",
                                "/comensal/recuperar",
                                "/comensal/restablecer-contrasenia")
                        .permitAll()
                        .requestMatchers("/comensal/perfil")
                        .hasRole("USER")
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                )
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);;

        return http.build();
    }
    /*
    @Bean
    CommandLineRunner cargarRoles(RolRepository rolRepository) {
        return args -> {

            if (rolRepository.findByNombre("USER").isEmpty()) {
                rolRepository.save(new Rol("USER"));
            }

            if (rolRepository.findByNombre("ADMIN").isEmpty()) {
                rolRepository.save(new Rol("ADMIN"));
            }
        };
    }*/

}
