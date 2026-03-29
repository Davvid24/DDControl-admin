package com.ddcontrol.ddcontroladmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

public class HorarioAsignadoDTO {

    @Data
    public static class Request {
        @NotNull
        private Integer idUsuario;

        @NotNull
        private Integer idTurno;

        @NotNull
        private LocalDate fechaInicio;

        private LocalDate fechaFin;

        @NotBlank @Size(max = 20)
        private String diasSemana;
    }

    @Data
    public static class Response {
        private Integer id;
        private Integer idUsuario;
        private String nombreUsuario;
        private Integer idTurno;
        private String nombreTurno;
        private LocalDate fechaInicio;
        private LocalDate fechaFin;
        private String diasSemana;
    }
}
