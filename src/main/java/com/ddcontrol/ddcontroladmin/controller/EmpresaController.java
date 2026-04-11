package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.EmpresaDTO;
import com.ddcontrol.ddcontroladmin.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    @GetMapping
    public List<EmpresaDTO.Response> findAll() {
        return empresaService.findAll();
    }

    @GetMapping("/{id}")
    public EmpresaDTO.Response findById(@PathVariable Integer id) {
        return empresaService.findById(id);
    }

    @PostMapping
    public ResponseEntity<EmpresaDTO.Response> create(@Valid @RequestBody EmpresaDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.create(req));
    }

    @PutMapping("/{id}")
    public EmpresaDTO.Response update(@PathVariable Integer id, @Valid @RequestBody EmpresaDTO.Request req) {
        return empresaService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        empresaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
