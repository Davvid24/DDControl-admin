package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.TurnoDTO;
import com.ddcontrol.ddcontroladmin.service.TurnoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
public class TurnoController {

    private final TurnoService turnoService;

    @GetMapping
    public List<TurnoDTO.Response> findAll() { return turnoService.findAll(); }

    @GetMapping("/{id}")
    public TurnoDTO.Response findById(@PathVariable Integer id) { return turnoService.findById(id); }

    @GetMapping("/empresa/{idEmpresa}")
    public List<TurnoDTO.Response> findByEmpresa(@PathVariable Integer idEmpresa) {
        return turnoService.findByEmpresa(idEmpresa);
    }

    @PostMapping
    public ResponseEntity<TurnoDTO.Response> create(@Valid @RequestBody TurnoDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(turnoService.create(req));
    }

    @PutMapping("/{id}")
    public TurnoDTO.Response update(@PathVariable Integer id, @Valid @RequestBody TurnoDTO.Request req) {
        return turnoService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        turnoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
