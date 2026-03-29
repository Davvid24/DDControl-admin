package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.SedeDTO;
import com.ddcontrol.ddcontroladmin.service.SedeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sedes")
@RequiredArgsConstructor
public class SedeController {

    private final SedeService sedeService;

    @GetMapping
    public List<SedeDTO.Response> findAll() {
        return sedeService.findAll();
    }

    @GetMapping("/{id}")
    public SedeDTO.Response findById(@PathVariable Integer id) {
        return sedeService.findById(id);
    }

    @GetMapping("/empresa/{idEmpresa}")
    public List<SedeDTO.Response> findByEmpresa(@PathVariable Integer idEmpresa) {
        return sedeService.findByEmpresa(idEmpresa);
    }

    @PostMapping
    public ResponseEntity<SedeDTO.Response> create(@Valid @RequestBody SedeDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sedeService.create(req));
    }

    @PutMapping("/{id}")
    public SedeDTO.Response update(@PathVariable Integer id, @Valid @RequestBody SedeDTO.Request req) {
        return sedeService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        sedeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
