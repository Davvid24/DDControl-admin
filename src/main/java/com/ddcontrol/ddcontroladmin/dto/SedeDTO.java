package com.ddcontrol.ddcontroladmin.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

public class SedeDTO {

    @Data
    public static class Request {
        @NotNull
        private Integer idEmpresa;

        @NotBlank @Size(max = 150)
        private String nombre;

        @Size(max = 255)
        private String direccion;

        @NotNull
        @DecimalMin("-90.0000000") @DecimalMax("90.0000000")
        private BigDecimal latitud;

        @NotNull
        @DecimalMin("-180.0000000") @DecimalMax("180.0000000")
        private BigDecimal longitud;

        private Integer radioMetros = 100;
        private Boolean activa = true;
    }

    @Data
    public static class Response {
        private Integer id;
        private Integer idEmpresa;
        private String nombreEmpresa;
        private String nombre;
        private String direccion;
        private BigDecimal latitud;
        private BigDecimal longitud;
        private Integer radioMetros;
        private Boolean activa;
    }
}
