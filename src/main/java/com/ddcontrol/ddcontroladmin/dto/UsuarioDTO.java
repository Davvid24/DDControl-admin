package com.ddcontrol.ddcontroladmin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

public class UsuarioDTO {

    @Data
    public static class Request {
        @NotNull
        private Integer idEmpresa;

        @NotBlank @Size(max = 100)
        private String nombre;

        @NotBlank @Size(max = 150)
        private String apellidos;

        @NotBlank @Email @Size(max = 150)
        private String email;

        @NotBlank @Size(min = 8, max = 255)
        private String password;

        @NotBlank @Size(max = 20)
        private String rol;

        @NotBlank @Size(max = 30)
        private String tipoEmpleado;

        @Size(max = 20)
        private String telefono;

        @Size(max = 255)
        private String fotoPerfil;
    }

    @Data
    public static class Response {
        private Integer id;
        private Integer idEmpresa;
        private String nombreEmpresa;
        private String nombre;
        private String apellidos;
        private String email;
        private String rol;
        private String tipoEmpleado;
        private String telefono;
        private Instant fechaAlta;
        private Boolean activo;
        private String fotoPerfil;
    }
}
