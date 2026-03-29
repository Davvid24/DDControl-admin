package com.ddcontrol.ddcontroladmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

public class IncidenciaDTO {

    @Data
    public static class Request {
        @NotNull
        private Integer idUsuario;

        private Integer idFichaje;

        @NotBlank @Size(max = 30)
        private String tipo;

        private String descripcion;
    }

    @Data
    public static class Response {
        private Integer id;
        private Integer idUsuario;
        private String nombreUsuario;
        private Integer idFichaje;
        private String tipo;
        private String descripcion;
        private Instant fecha;
        private Boolean resuelta;
    }
}
