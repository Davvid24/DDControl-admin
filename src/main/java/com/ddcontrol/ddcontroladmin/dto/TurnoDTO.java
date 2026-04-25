package com.ddcontrol.ddcontroladmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

public class TurnoDTO {

    @Data
    public static class Request {
        @NotNull
        private Integer idEmpresa;

        @NotBlank @Size(max = 100)
        private String nombre;

        @NotNull
        private LocalTime horaEntrada;

        @NotNull
        private LocalTime horaSalida;

        private String descripcion;
    }

    @Data
    public static class Response {
        private Integer id;
        private Integer idEmpresa;
        private String nombreEmpresa;
        private String nombre;
        private LocalTime horaEntrada;
        private LocalTime horaSalida;
        private String descripcion;
        private List<EmpleadoResumen> empleados;
    }

    @Data
    public static class EmpleadoResumen {
        private Integer id;
        private String nombre;
        private String apellidos;
        private String email;
    }
}