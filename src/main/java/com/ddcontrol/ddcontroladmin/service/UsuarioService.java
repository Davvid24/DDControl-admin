package com.ddcontrol.ddcontroladmin.service;

import com.ddcontrol.ddcontroladmin.dto.UsuarioDTO;
import com.ddcontrol.ddcontroladmin.model.Empresa;
import com.ddcontrol.ddcontroladmin.model.Usuario;
import com.ddcontrol.ddcontroladmin.repository.EmpresaRepository;
import com.ddcontrol.ddcontroladmin.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioDTO.Response> findAll() {
        return usuarioRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO.Response> findByEmpresa(Integer idEmpresa) {
        return usuarioRepository.findByIdEmpresa_Id(idEmpresa).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO.Response findById(Integer id) {
        return toResponse(getOrThrow(id));
    }

    public UsuarioDTO.Response create(UsuarioDTO.Request req) {
        if (usuarioRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Ya existe un usuario con ese email");

        Empresa empresa = empresaRepository.findById(req.getIdEmpresa())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada: " + req.getIdEmpresa()));

        Usuario u = new Usuario();
        u.setIdEmpresa(empresa);
        u.setNombre(req.getNombre());
        u.setApellidos(req.getApellidos());
        u.setEmail(req.getEmail());
        u.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        u.setRol(req.getRol());
        u.setTipoEmpleado(req.getTipoEmpleado());
        u.setTelefono(req.getTelefono());
        u.setFechaAlta(Instant.now());
        u.setActivo(true);
        u.setFotoPerfil(req.getFotoPerfil());
        return toResponse(usuarioRepository.save(u));
    }

    public UsuarioDTO.Response update(Integer id, UsuarioDTO.Request req) {
        Usuario u = getOrThrow(id);
        Empresa empresa = empresaRepository.findById(req.getIdEmpresa())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada: " + req.getIdEmpresa()));

        u.setIdEmpresa(empresa);
        u.setNombre(req.getNombre());
        u.setApellidos(req.getApellidos());
        u.setRol(req.getRol());
        u.setTipoEmpleado(req.getTipoEmpleado());
        u.setTelefono(req.getTelefono());
        u.setFotoPerfil(req.getFotoPerfil());

        if (req.getPassword() != null && !req.getPassword().isBlank())
            u.setPasswordHash(passwordEncoder.encode(req.getPassword()));

        return toResponse(usuarioRepository.save(u));
    }

    public void toggleActivo(Integer id) {
        Usuario u = getOrThrow(id);
        u.setActivo(!u.getActivo());
        usuarioRepository.save(u);
    }

    public void delete(Integer id) {
        if (!usuarioRepository.existsById(id))
            throw new EntityNotFoundException("Usuario no encontrado: " + id);
        usuarioRepository.deleteById(id);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Usuario getOrThrow(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + id));
    }

    private UsuarioDTO.Response toResponse(Usuario u) {
        UsuarioDTO.Response r = new UsuarioDTO.Response();
        r.setId(u.getId());
        r.setIdEmpresa(u.getIdEmpresa().getId());
        r.setNombreEmpresa(u.getIdEmpresa().getNombre());
        r.setNombre(u.getNombre());
        r.setApellidos(u.getApellidos());
        r.setEmail(u.getEmail());
        r.setRol(u.getRol());
        r.setTipoEmpleado(u.getTipoEmpleado());
        r.setTelefono(u.getTelefono());
        r.setFechaAlta(u.getFechaAlta());
        r.setActivo(u.getActivo());
        r.setFotoPerfil(u.getFotoPerfil());
        return r;
    }
}
