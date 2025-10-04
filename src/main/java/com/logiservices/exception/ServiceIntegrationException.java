package com.logiservices.exception;

/**
 * Excepción personalizada para errores de integración con servicios externos
 *
 * Se lanza cuando hay problemas al comunicarse con servicios TMS, ACMS o SMCS
 */
public class ServiceIntegrationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String serviceName;

    public ServiceIntegrationException(String serviceName, String message) {
        super(message);
        this.serviceName = serviceName;
    }

    public ServiceIntegrationException(String serviceName, String message, Throwable cause) {
        super(message, cause);
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    @Override
    public String toString() {
        return "ServiceIntegrationException{" +
                "serviceName='" + serviceName + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}
