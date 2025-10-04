package com.logiservices.config;

import com.logiservices.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos
 *
 * Se ejecuta al iniciar la aplicación para crear usuarios de ejemplo
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioService usuarioService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔐 Inicializando usuarios de ejemplo...");

        // Crear usuarios de ejemplo
        usuarioService.crearUsuariosEjemplo();

        System.out.println("✅ Usuarios de ejemplo creados:");
        System.out.println("   👤 admin / admin123 (Administrador)");
        System.out.println("   👤 operador / operador123 (Operador)");
        System.out.println("   👤 consultor / consultor123 (Consultor)");
        System.out.println("   👤 cliente / cliente123 (Cliente)");
        System.out.println("");
        System.out.println("🔑 Para obtener token JWT:");
        System.out.println("   POST http://localhost:8080/api/v1/auth/login");
        System.out.println("");
        System.out.println("📚 Documentación Swagger:");
        System.out.println("   http://localhost:8080/swagger-ui.html");
        System.out.println("");
    }
}
