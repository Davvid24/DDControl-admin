package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.FichajeDTO;
import com.ddcontrol.ddcontroladmin.service.FichajeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fichajes")
@RequiredArgsConstructor
public class FichajeController {

    private final FichajeService fichajeService;

    @GetMapping
    public List<FichajeDTO.Response> findAll() { return fichajeService.findAll(); }

    @GetMapping("/{id}")
    public FichajeDTO.Response findById(@PathVariable Integer id) { return fichajeService.findById(id); }

    @GetMapping("/usuario/{idUsuario}")
    public List<FichajeDTO.Response> findByUsuario(@PathVariable Integer idUsuario) {
        return fichajeService.findByUsuario(idUsuario);
    }

    @PostMapping
    public ResponseEntity<FichajeDTO.Response> create(@Valid @RequestBody FichajeDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fichajeService.create(req));
    }

    @PutMapping("/{id}")
    public FichajeDTO.Response update(@PathVariable Integer id, @Valid @RequestBody FichajeDTO.Request req) {
        return fichajeService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        fichajeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
