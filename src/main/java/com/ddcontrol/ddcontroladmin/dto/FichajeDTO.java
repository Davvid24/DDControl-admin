package com.ddcontrol.ddcontroladmin.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

public class FichajeDTO {

    @Data
    public static class Request {
        @NotNull
        private Integer idUsuario;

        @NotNull
        private Integer idSede;

        @NotBlank @Size(max = 20)
        private String tipo;

        @NotNull
        @DecimalMin("-90.0000000") @DecimalMax("90.0000000")
        private BigDecimal latitudReal;

        @NotNull
        @DecimalMin("-180.0000000") @DecimalMax("180.0000000")
        private BigDecimal longitudReal;

        @Size(max = 20)
        private String metodo = "movil";

        private String observaciones;
    }

    @Data
    public static class Response {
        private Integer id;
        private Integer idUsuario;
        private String nombreUsuario;
        private Integer idSede;
        private String nombreSede;
        private String tipo;
        private Instant timestampFicha;
        private BigDecimal latitudReal;
        private BigDecimal longitudReal;
        private Boolean dentroDeRadio;
        private String metodo;
        private String observaciones;
    }
}
