package com.logiservices.exception;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones
 *
 * Captura y maneja todas las excepciones de la aplicación,
 * devolviendo respuestas HTTP apropiadas con códigos de error documentados
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de validación (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
            "VALIDATION_ERROR",
            "Error de validación en los datos de entrada",
            errors,
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now(),
            request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja errores de autenticación (401 Unauthorized)
     */
    @ExceptionHandler({AuthenticationException.class, BadCredentialsException.class})
    @ApiResponse(responseCode = "401", description = "Error de autenticación",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            Exception ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            "AUTHENTICATION_ERROR",
            "Error de autenticación: " + ex.getMessage(),
            null,
            HttpStatus.UNAUTHORIZED.value(),
            LocalDateTime.now(),
            request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja errores de autorización (403 Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ApiResponse(responseCode = "403", description = "Acceso denegado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            "ACCESS_DENIED",
            "Acceso denegado: " + ex.getMessage(),
            null,
            HttpStatus.FORBIDDEN.value(),
            LocalDateTime.now(),
            request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja errores de recurso no encontrado (404 Not Found)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ApiResponse(responseCode = "404", description = "Recurso no encontrado",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            "RESOURCE_NOT_FOUND",
            ex.getMessage(),
            null,
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now(),
            request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja errores de argumentos ilegales (400 Bad Request)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ApiResponse(responseCode = "400", description = "Argumento inválido",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            "ILLEGAL_ARGUMENT",
            "Argumento inválido: " + ex.getMessage(),
            null,
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now(),
            request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja errores de integración con servicios externos (503 Service Unavailable)
     */
    @ExceptionHandler(ServiceIntegrationException.class)
    @ApiResponse(responseCode = "503", description = "Error de integración con servicio externo",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleServiceIntegrationException(
            ServiceIntegrationException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            "SERVICE_INTEGRATION_ERROR",
            "Error de integración con servicio externo: " + ex.getMessage(),
            Map.of("service", ex.getServiceName()),
            HttpStatus.SERVICE_UNAVAILABLE.value(),
            LocalDateTime.now(),
            request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * Maneja errores internos del servidor (500 Internal Server Error)
     */
    @ExceptionHandler(Exception.class)
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
            "INTERNAL_SERVER_ERROR",
            "Error interno del servidor: " + ex.getMessage(),
            null,
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            LocalDateTime.now(),
            request.getDescription(false)
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Clase para respuestas de error estructuradas
     */
    @Schema(description = "Respuesta de error estructurada")
    public static class ErrorResponse {

        @Schema(description = "Código de error", example = "VALIDATION_ERROR")
        private String code;

        @Schema(description = "Mensaje de error", example = "Error de validación en los datos de entrada")
        private String message;

        @Schema(description = "Detalles adicionales del error")
        private Object details;

        @Schema(description = "Código HTTP", example = "400")
        private int status;

        @Schema(description = "Timestamp del error")
        private LocalDateTime timestamp;

        @Schema(description = "Ruta donde ocurrió el error")
        private String path;

        // Constructores
        public ErrorResponse() {}

        public ErrorResponse(String code, String message, Object details,
                           int status, LocalDateTime timestamp, String path) {
            this.code = code;
            this.message = message;
            this.details = details;
            this.status = status;
            this.timestamp = timestamp;
            this.path = path;
        }

        // Getters y Setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public Object getDetails() { return details; }
        public void setDetails(Object details) { this.details = details; }

        public int getStatus() { return status; }
        public void setStatus(int status) { this.status = status; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }

        @Override
        public String toString() {
            return "ErrorResponse{" +
                    "code='" + code + '\'' +
                    ", message='" + message + '\'' +
                    ", details=" + details +
                    ", status=" + status +
                    ", timestamp=" + timestamp +
                    ", path='" + path + '\'' +
                    '}';
        }
    }
}
