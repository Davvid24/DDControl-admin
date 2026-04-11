package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.JustificanteDTO;
import com.ddcontrol.ddcontroladmin.service.JustificanteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/justificantes")
@RequiredArgsConstructor
public class JustificanteController {

    private final JustificanteService justificanteService;

    @GetMapping("/solicitud/{idSolicitud}")
    public List<JustificanteDTO.Response> findBySolicitud(@PathVariable Integer idSolicitud) {
        return justificanteService.findBySolicitud(idSolicitud);
    }

    @GetMapping("/{id}")
    public JustificanteDTO.Response findById(@PathVariable Integer id) {
        return justificanteService.findById(id);
    }

    @PostMapping
    public ResponseEntity<JustificanteDTO.Response> create(@Valid @RequestBody JustificanteDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(justificanteService.create(req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        justificanteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
