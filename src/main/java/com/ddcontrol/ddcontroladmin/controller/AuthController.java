package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.AuthDTO;
import com.ddcontrol.ddcontroladmin.repository.UsuarioRepository;
import com.ddcontrol.ddcontroladmin.security.JwtService;
import com.ddcontrol.ddcontroladmin.security.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository      usuarioRepository;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtService             jwtService;
    private final PasswordEncoder        passwordEncoder;


    @PostMapping("/login")
    public AuthDTO.LoginResponse login(@Valid @RequestBody AuthDTO.LoginRequest req) {

        var usuario = usuarioRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Credenciales incorrectas"));

        System.out.println("=== DEBUG LOGIN ===");
        System.out.println("Email:    " + usuario.getEmail());
        System.out.println("Hash BD:  " + usuario.getPasswordHash());
        System.out.println("Hash len: " + usuario.getPasswordHash().length());
        System.out.println("Pass req: " + req.getPassword());
        System.out.println("Matches:  " + passwordEncoder.matches(req.getPassword(), usuario.getPasswordHash()));
        System.out.println("Activo:   " + usuario.getActivo());
        System.out.println("===================");

        if (!passwordEncoder.matches(req.getPassword(), usuario.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");

        if (!usuario.getActivo())
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario desactivado");

        var userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        var token = jwtService.generateToken(
                userDetails,
                usuario.getId(),
                usuario.getIdEmpresa().getId(),
                usuario.getRol()
        );

        return new AuthDTO.LoginResponse(
                token,
                usuario.getId(),
                usuario.getIdEmpresa().getId(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getRol()
        );
    }
}