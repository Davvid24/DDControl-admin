package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.SolicitudDTO;
import com.ddcontrol.ddcontroladmin.service.SolicitudService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @GetMapping
    public List<SolicitudDTO.Response> findAll() { return solicitudService.findAll(); }

    @GetMapping("/{id}")
    public SolicitudDTO.Response findById(@PathVariable Integer id) { return solicitudService.findById(id); }

    @GetMapping("/usuario/{idUsuario}")
    public List<SolicitudDTO.Response> findByUsuario(@PathVariable Integer idUsuario) {
        return solicitudService.findByUsuario(idUsuario);
    }

    @GetMapping("/pendientes")
    public List<SolicitudDTO.Response> findPendientes() { return solicitudService.findPendientes(); }

    @PostMapping
    public ResponseEntity<SolicitudDTO.Response> create(@Valid @RequestBody SolicitudDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solicitudService.create(req));
    }

    @PatchMapping("/{id}/resolver")
    public SolicitudDTO.Response resolver(@PathVariable Integer id,
                                          @Valid @RequestBody SolicitudDTO.ResolucionRequest req) {
        return solicitudService.resolver(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        solicitudService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
