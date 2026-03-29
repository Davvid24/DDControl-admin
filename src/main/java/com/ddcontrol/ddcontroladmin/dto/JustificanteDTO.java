package com.ddcontrol.ddcontroladmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

public class JustificanteDTO {

    @Data
    public static class Request {
        @NotNull
        private Integer idSolicitud;

        @NotBlank @Size(max = 255)
        private String nombreArchivo;

        @NotBlank @Size(max = 500)
        private String rutaArchivo;

        @NotBlank @Size(max = 100)
        private String tipoMime;
    }

    @Data
    public static class Response {
        private Integer id;
        private Integer idSolicitud;
        private String nombreArchivo;
        private String rutaArchivo;
        private String tipoMime;
        private Instant fechaSubida;
    }
}
