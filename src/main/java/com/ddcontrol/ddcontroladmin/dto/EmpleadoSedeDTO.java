package com.ddcontrol.ddcontroladmin.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

public class EmpleadoSedeDTO {

    @Data
    public static class Request {
        @NotNull
        private Integer idUsuario;

        @NotNull
        private Integer idSede;

        private LocalDate fechaAsignacion;
    }

    @Data
    public static class Response {
        private Integer idUsuario;
        private String nombreUsuario;
        private Integer idSede;
        private String nombreSede;
        private LocalDate fechaAsignacion;
    }
}
