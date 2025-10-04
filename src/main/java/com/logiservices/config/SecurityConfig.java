package com.logiservices.config;

import com.logiservices.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración de Spring Security
 *
 * Define la configuración de seguridad, autenticación y autorización del sistema
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // Removido @Autowired para evitar referencia circular
    // private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configuración del filtro de seguridad
     *
     * @param http Configuración HTTP de seguridad
     * @return SecurityFilterChain configurado
     * @throws Exception Si hay error en la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configuración CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Deshabilitar CSRF para APIs REST
            .csrf(csrf -> csrf.disable())

            // Configuración de sesiones (stateless para JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Configuración de autorización
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos (sin autenticación)
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()

                // Endpoints de información (públicos)
                .requestMatchers("/api/v1/envios/info").permitAll()
                .requestMatchers("/api/v1/envios/sistemas/info").permitAll()

                // Endpoints de consulta (requieren autenticación)
                .requestMatchers("/api/v1/envios/{id}").hasAnyRole("ADMIN", "OPERADOR", "CONSULTOR", "CLIENTE")
                .requestMatchers("/api/v1/envios/sistemas").hasAnyRole("ADMIN", "OPERADOR", "CONSULTOR")
                .requestMatchers("/api/v1/envios/sistema/**").hasAnyRole("ADMIN", "OPERADOR", "CONSULTOR")
                .requestMatchers("/api/v1/envios/estado/**").hasAnyRole("ADMIN", "OPERADOR", "CONSULTOR")
                .requestMatchers("/api/v1/envios/estadisticas").hasAnyRole("ADMIN", "OPERADOR", "CONSULTOR")
                .requestMatchers("/api/v1/envios").hasAnyRole("ADMIN", "OPERADOR", "CONSULTOR")

                // Endpoints de modificación (requieren roles específicos)
                .requestMatchers("/api/v1/envios", "POST").hasAnyRole("ADMIN", "OPERADOR")
                .requestMatchers("/api/v1/envios/{id}", "PUT").hasAnyRole("ADMIN", "OPERADOR")
                .requestMatchers("/api/v1/envios/{id}", "DELETE").hasRole("ADMIN")
                .requestMatchers("/api/v1/envios/{id}/estado/sistema", "PUT").hasAnyRole("ADMIN", "OPERADOR")

                // Todos los demás endpoints requieren autenticación
                .anyRequest().authenticated()
            )

            // Configuración de autenticación se maneja automáticamente

            // Agregar el filtro JWT
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Configuración especial para H2 Console (desarrollo)
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    /**
     * Configuración del proveedor de autenticación
     * 
     * @return DaoAuthenticationProvider configurado
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configuración del codificador de contraseñas
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuración del gestor de autenticación
     *
     * @param authConfig Configuración de autenticación
     * @return AuthenticationManager
     * @throws Exception Si hay error en la configuración
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Configuración CORS
     *
     * @return CorsConfigurationSource configurado
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir orígenes específicos (en producción, especificar dominios reales)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Permitir credenciales
        configuration.setAllowCredentials(true);

        // Headers expuestos
        configuration.setExposedHeaders(Arrays.asList("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
