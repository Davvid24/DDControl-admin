package com.ddcontrol.ddcontroladmin.service;

import com.ddcontrol.ddcontroladmin.dto.EmpresaDTO;
import com.ddcontrol.ddcontroladmin.model.Empresa;
import com.ddcontrol.ddcontroladmin.repository.EmpresaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<EmpresaDTO.Response> findAll() {
        return empresaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmpresaDTO.Response findById(Integer id) {
        return toResponse(getOrThrow(id));
    }

    public EmpresaDTO.Response create(EmpresaDTO.Request req) {
        if (empresaRepository.existsByNif(req.getNif()))
            throw new IllegalArgumentException("Ya existe una empresa con ese NIF");
        if (req.getEmailContacto() != null && empresaRepository.existsByEmailContacto(req.getEmailContacto()))
            throw new IllegalArgumentException("Ya existe una empresa con ese email");

        Empresa e = new Empresa();
        e.setNombre(req.getNombre());
        e.setNif(req.getNif());
        e.setDireccion(req.getDireccion());
        e.setEmailContacto(req.getEmailContacto());
        e.setTelefono(req.getTelefono());
        e.setFechaAlta(Instant.now());
        return toResponse(empresaRepository.save(e));
    }

    public EmpresaDTO.Response update(Integer id, EmpresaDTO.Request req) {
        Empresa e = getOrThrow(id);
        e.setNombre(req.getNombre());
        e.setDireccion(req.getDireccion());
        e.setEmailContacto(req.getEmailContacto());
        e.setTelefono(req.getTelefono());
        return toResponse(empresaRepository.save(e));
    }

    public void delete(Integer id) {
        if (!empresaRepository.existsById(id))
            throw new EntityNotFoundException("Empresa no encontrada: " + id);
        empresaRepository.deleteById(id);
    }

    private Empresa getOrThrow(Integer id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada: " + id));
    }

    private EmpresaDTO.Response toResponse(Empresa e) {
        EmpresaDTO.Response r = new EmpresaDTO.Response();
        r.setId(e.getId());
        r.setNombre(e.getNombre());
        r.setNif(e.getNif());
        r.setDireccion(e.getDireccion());
        r.setEmailContacto(e.getEmailContacto());
        r.setTelefono(e.getTelefono());
        r.setFechaAlta(e.getFechaAlta());
        return r;
    }
}
