package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.IncidenciaDTO;
import com.ddcontrol.ddcontroladmin.service.IncidenciaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incidencias")
@RequiredArgsConstructor
public class IncidenciaController {

    private final IncidenciaService incidenciaService;

    @GetMapping
    public List<IncidenciaDTO.Response> findAll() { return incidenciaService.findAll(); }

    @GetMapping("/{id}")
    public IncidenciaDTO.Response findById(@PathVariable Integer id) { return incidenciaService.findById(id); }

    @GetMapping("/usuario/{idUsuario}")
    public List<IncidenciaDTO.Response> findByUsuario(@PathVariable Integer idUsuario) {
        return incidenciaService.findByUsuario(idUsuario);
    }

    @GetMapping("/pendientes")
    public List<IncidenciaDTO.Response> findPendientes() { return incidenciaService.findPendientes(); }

    @PostMapping
    public ResponseEntity<IncidenciaDTO.Response> create(@Valid @RequestBody IncidenciaDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(incidenciaService.create(req));
    }

    @PatchMapping("/{id}/resolver")
    public IncidenciaDTO.Response marcarResuelta(@PathVariable Integer id) {
        return incidenciaService.marcarResuelta(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        incidenciaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
