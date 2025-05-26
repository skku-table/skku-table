package com.skkutable.config;

import com.skkutable.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final CustomUserDetailsService userDetailsService;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http
        /* CSRF - REST JSON 클라이언트면 비활성화 */
        .cors(withDefaults())
        .csrf(csrf -> csrf.disable())

        /* 세션 정책: 필요할 때 생성 & 동시 로그인 1개 */
        .sessionManagement(sm -> sm
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            .maximumSessions(1))

        /* URL 인가 규칙 */
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/users/signup", "/users/login").permitAll()
            /* GET /festivals → 모든 사용자 */
            .requestMatchers(HttpMethod.GET, "/festivals", "/festivals/**").permitAll()
            /* GET /users → ADMIN 권한만 */
            .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
            .anyRequest().authenticated())

        /* 폼 로그인 -> REST 에서도 x-www-form-urlencoded 전송이면 OK */
        .formLogin(form -> form
            .loginProcessingUrl("/users/login")      // POST
            .usernameParameter("email")              // ← email 로 로그인
            .passwordParameter("password")
            .successHandler((req,res,auth) -> res.setStatus(200))
            .failureHandler((req,res,ex) -> res.sendError(401, "Login Failed"))
            .permitAll())

        /* 로그아웃 */
        .logout(out -> out
            .logoutUrl("/users/logout")              // POST /users/logout
            .logoutSuccessHandler((req,res,auth)->res.setStatus(200))
            .deleteCookies("JSESSIONID"));

    http.userDetailsService(userDetailsService);
    return http.build();
  }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowedOrigins(List.of("http://localhost:3000", "https://skkutable.com"));
      config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"));
      config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
      config.setExposedHeaders(List.of("*"));
      config.setAllowCredentials(true); // ✅ 핵심
      config.setMaxAge(3600L);

      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", config);
      return source;
    }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
