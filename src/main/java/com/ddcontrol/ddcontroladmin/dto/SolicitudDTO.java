package com.ddcontrol.ddcontroladmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

public class SolicitudDTO {

    @Data
    public static class Request {
        @NotNull
        private Integer idUsuario;

        @NotBlank @Size(max = 30)
        private String tipo;

        @NotNull
        private LocalDate fechaInicio;

        @NotNull
        private LocalDate fechaFin;

        private String motivo;
    }

    @Data
    public static class ResolucionRequest {
        @NotNull
        private Integer idAdminRevisor;

        @NotBlank @Size(max = 20)
        private String estado; // aprobada | denegada

        private String comentarioAdmin;
    }

    @Data
    public static class Response {
        private Integer id;
        private Integer idUsuario;
        private String nombreUsuario;
        private Integer idAdminRevisor;
        private String nombreAdminRevisor;
        private String tipo;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String motivo;
        private String estado;
        private Instant fechaSolicitud;
        private Instant fechaResolucion;
        private String comentarioAdmin;
    }
}
