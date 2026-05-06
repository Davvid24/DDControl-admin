package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class FcmTokenController {

    private final UsuarioService usuarioService;

    @PatchMapping("/{id}/fcm-token")
    public ResponseEntity<Void> actualizarFcmToken(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        usuarioService.actualizarFcmToken(id, body.get("fcmToken"));
        return ResponseEntity.noContent().build();
    }
}