package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.UsuarioDTO;
import com.ddcontrol.ddcontroladmin.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioDTO.Response> findAll() {
        return usuarioService.findAll();
    }

    @GetMapping("/{id}")
    public UsuarioDTO.Response findById(@PathVariable Integer id) {
        return usuarioService.findById(id);
    }

    @GetMapping("/empresa/{idEmpresa}")
    public List<UsuarioDTO.Response> findByEmpresa(@PathVariable Integer idEmpresa) {
        return usuarioService.findByEmpresa(idEmpresa);
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO.Response> create(@Valid @RequestBody UsuarioDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.create(req));
    }

    @PutMapping("/{id}")
    public UsuarioDTO.Response update(@PathVariable Integer id, @Valid @RequestBody UsuarioDTO.Request req) {
        return usuarioService.update(id, req);
    }

    @PatchMapping("/{id}/toggle-activo")
    public ResponseEntity<Void> toggleActivo(@PathVariable Integer id) {
        usuarioService.toggleActivo(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        usuarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
