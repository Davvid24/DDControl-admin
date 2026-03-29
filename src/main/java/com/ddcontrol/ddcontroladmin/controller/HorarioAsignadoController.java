package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.HorarioAsignadoDTO;
import com.ddcontrol.ddcontroladmin.service.HorarioAsignadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/horarios")
@RequiredArgsConstructor
public class HorarioAsignadoController {

    private final HorarioAsignadoService horarioService;

    @GetMapping
    public List<HorarioAsignadoDTO.Response> findAll() { return horarioService.findAll(); }

    @GetMapping("/{id}")
    public HorarioAsignadoDTO.Response findById(@PathVariable Integer id) { return horarioService.findById(id); }

    @GetMapping("/usuario/{idUsuario}")
    public List<HorarioAsignadoDTO.Response> findByUsuario(@PathVariable Integer idUsuario) {
        return horarioService.findByUsuario(idUsuario);
    }

    @PostMapping
    public ResponseEntity<HorarioAsignadoDTO.Response> create(@Valid @RequestBody HorarioAsignadoDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioService.create(req));
    }

    @PutMapping("/{id}")
    public HorarioAsignadoDTO.Response update(@PathVariable Integer id, @Valid @RequestBody HorarioAsignadoDTO.Request req) {
        return horarioService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        horarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
