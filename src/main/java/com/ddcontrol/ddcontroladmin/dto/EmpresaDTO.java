package com.ddcontrol.ddcontroladmin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

public class EmpresaDTO {

    @Data
    public static class Request {
        @NotBlank @Size(max = 150)
        private String nombre;

        @NotBlank @Size(max = 20)
        private String nif;

        @Size(max = 255)
        private String direccion;

        @Email @Size(max = 150)
        private String emailContacto;

        @Size(max = 20)
        private String telefono;
    }

    @Data
    public static class Response {
        private Integer id;
        private String nombre;
        private String nif;
        private String direccion;
        private String emailContacto;
        private String telefono;
        private Instant fechaAlta;
    }
}
