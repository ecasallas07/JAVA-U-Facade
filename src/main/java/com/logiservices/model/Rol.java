package com.logiservices.model;

/**
 * Enum que define los roles disponibles en el sistema
 *
 * Los roles determinan qué operaciones puede realizar un usuario
 */
public enum Rol {

    /**
     * Administrador: Acceso completo a todas las funcionalidades
     */
    ADMIN("Administrador", "Acceso completo al sistema"),

    /**
     * Operador: Puede consultar y actualizar envíos
     */
    OPERADOR("Operador", "Puede consultar y actualizar envíos"),

    /**
     * Consultor: Solo puede consultar información
     */
    CONSULTOR("Consultor", "Solo puede consultar información"),

    /**
     * Cliente: Acceso limitado a sus propios envíos
     */
    CLIENTE("Cliente", "Acceso limitado a sus envíos");

    private final String nombre;
    private final String descripcion;

    Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
