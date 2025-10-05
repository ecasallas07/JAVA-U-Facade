package com.logiservices.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Base64;

/**
 * Utilidad para manejo de tokens JWT
 *
 * Proporciona métodos para generar, validar y extraer información de tokens JWT
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:LogiServicesSecretKeyForJWTTokenGeneration2024}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24 horas en milisegundos
    private Long expiration;

    /**
     * Genera un token JWT para un usuario
     *
     * @param userDetails Detalles del usuario
     * @return Token JWT
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Genera un token JWT con claims adicionales
     *
     * @param userDetails Detalles del usuario
     * @param additionalClaims Claims adicionales
     * @return Token JWT
     */
    public String generateToken(UserDetails userDetails, Map<String, Object> additionalClaims) {
        Map<String, Object> claims = new HashMap<>(additionalClaims);
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crea un token JWT
     *
     * @param claims Claims del token
     * @param subject Subject (usuario)
     * @return Token JWT
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrae el nombre de usuario del token
     *
     * @param token Token JWT
     * @return Nombre de usuario
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token
     *
     * @param token Token JWT
     * @return Fecha de expiración
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token
     *
     * @param token Token JWT
     * @param claimsResolver Función para resolver el claim
     * @param <T> Tipo del claim
     * @return Valor del claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token
     *
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica si el token ha expirado
     *
     * @param token Token JWT
     * @return true si ha expirado, false en caso contrario
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valida un token JWT
     *
     * @param token Token JWT
     * @param userDetails Detalles del usuario
     * @return true si es válido, false en caso contrario
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Valida la estructura y firma del token
     *
     * @param token Token JWT
     * @return true si es válido, false en caso contrario
     */
    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Obtiene la clave de firma
     *
     * @return Clave secreta
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Obtiene el tiempo de expiración en milisegundos
     *
     * @return Tiempo de expiración
     */
    public Long getExpirationTime() {
        return expiration;
    }
}
