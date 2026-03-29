package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.EmpleadoSedeDTO;
import com.ddcontrol.ddcontroladmin.service.EmpleadoSedeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empleado-sede")
@RequiredArgsConstructor
public class EmpleadoSedeController {

    private final EmpleadoSedeService empleadoSedeService;

    @GetMapping("/usuario/{idUsuario}")
    public List<EmpleadoSedeDTO.Response> findByUsuario(@PathVariable Integer idUsuario) {
        return empleadoSedeService.findByUsuario(idUsuario);
    }

    @GetMapping("/sede/{idSede}")
    public List<EmpleadoSedeDTO.Response> findBySede(@PathVariable Integer idSede) {
        return empleadoSedeService.findBySede(idSede);
    }

    @PostMapping
    public ResponseEntity<EmpleadoSedeDTO.Response> asignar(@Valid @RequestBody EmpleadoSedeDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empleadoSedeService.asignar(req));
    }

    @DeleteMapping("/usuario/{idUsuario}/sede/{idSede}")
    public ResponseEntity<Void> desasignar(@PathVariable Integer idUsuario, @PathVariable Integer idSede) {
        empleadoSedeService.desasignar(idUsuario, idSede);
        return ResponseEntity.noContent().build();
    }
}
