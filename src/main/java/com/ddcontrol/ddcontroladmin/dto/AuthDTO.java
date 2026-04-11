package com.ddcontrol.ddcontroladmin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public class AuthDTO {

    @Data
    public static class LoginRequest {
        @NotBlank @Email
        private String email;

        @NotBlank
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private Integer idUsuario;
        private Integer idEmpresa;
        private String  nombre;
        private String  apellidos;
        private String  rol;

        public LoginResponse(String token, Integer idUsuario, Integer idEmpresa,
                             String nombre, String apellidos, String rol) {
            this.token      = token;
            this.idUsuario  = idUsuario;
            this.idEmpresa  = idEmpresa;
            this.nombre     = nombre;
            this.apellidos  = apellidos;
            this.rol        = rol;
        }
    }
}