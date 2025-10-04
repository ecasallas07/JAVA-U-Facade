package com.logiservices.dto;

/**
 * DTO (Data Transfer Object) para representar información de envíos
 *
 * Este DTO se usa para transferir datos entre las capas de la aplicación
 * y será la respuesta del Service Facade que integra TMS, ACMS y SMCS
 */
public class EnvioDto {

    private Long id;
    private String origen;
    private String destino;
    private String estado;
    private String sistemaOrigen; // TMS, ACMS o SMCS

    /**
     * Constructor por defecto (requerido para serialización JSON)
     */
    public EnvioDto() {}

    /**
     * Constructor con parámetros
     */
    public EnvioDto(Long id, String origen, String destino, String estado, String sistemaOrigen) {
        this.id = id;
        this.origen = origen;
        this.destino = destino;
        this.estado = estado;
        this.sistemaOrigen = sistemaOrigen;
    }

    // Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSistemaOrigen() {
        return sistemaOrigen;
    }

    public void setSistemaOrigen(String sistemaOrigen) {
        this.sistemaOrigen = sistemaOrigen;
    }

    @Override
    public String toString() {
        return "EnvioDto{" +
                "id=" + id +
                ", origen='" + origen + '\'' +
                ", destino='" + destino + '\'' +
                ", estado='" + estado + '\'' +
                ", sistemaOrigen='" + sistemaOrigen + '\'' +
                '}';
    }
}
