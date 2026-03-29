package com.ddcontrol.ddcontroladmin.service;

import com.ddcontrol.ddcontroladmin.dto.SolicitudDTO;
import com.ddcontrol.ddcontroladmin.model.Solicitud;
import com.ddcontrol.ddcontroladmin.model.Usuario;
import com.ddcontrol.ddcontroladmin.repository.SolicitudRepository;
import com.ddcontrol.ddcontroladmin.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<SolicitudDTO.Response> findAll() {
        return solicitudRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitudDTO.Response> findByUsuario(Integer idUsuario) {
        return solicitudRepository.findByIdUsuario_Id(idUsuario).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SolicitudDTO.Response> findPendientes() {
        return solicitudRepository.findByEstado("pendiente").stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SolicitudDTO.Response findById(Integer id) {
        return toResponse(getOrThrow(id));
    }

    public SolicitudDTO.Response create(SolicitudDTO.Request req) {
        Usuario usuario = usuarioRepository.findById(req.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + req.getIdUsuario()));

        Solicitud s = new Solicitud();
        s.setIdUsuario(usuario);
        s.setTipo(req.getTipo());
        s.setFechaInicio(req.getFechaInicio());
        s.setFechaFin(req.getFechaFin());
        s.setMotivo(req.getMotivo());
        s.setEstado("pendiente");
        s.setFechaSolicitud(Instant.now());
        return toResponse(solicitudRepository.save(s));
    }

    public SolicitudDTO.Response resolver(Integer id, SolicitudDTO.ResolucionRequest req) {
        Solicitud s = getOrThrow(id);
        if (!s.getEstado().equals("pendiente"))
            throw new IllegalStateException("La solicitud ya fue resuelta");

        Usuario admin = usuarioRepository.findById(req.getIdAdminRevisor())
                .orElseThrow(() -> new EntityNotFoundException("Admin no encontrado: " + req.getIdAdminRevisor()));

        s.setIdAdminRevisor(admin);
        s.setEstado(req.getEstado());
        s.setComentarioAdmin(req.getComentarioAdmin());
        s.setFechaResolucion(Instant.now());
        return toResponse(solicitudRepository.save(s));
    }

    public void delete(Integer id) {
        if (!solicitudRepository.existsById(id))
            throw new EntityNotFoundException("Solicitud no encontrada: " + id);
        solicitudRepository.deleteById(id);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Solicitud getOrThrow(Integer id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada: " + id));
    }

    private SolicitudDTO.Response toResponse(Solicitud s) {
        SolicitudDTO.Response r = new SolicitudDTO.Response();
        r.setId(s.getId());
        r.setIdUsuario(s.getIdUsuario().getId());
        r.setNombreUsuario(s.getIdUsuario().getNombre() + " " + s.getIdUsuario().getApellidos());
        if (s.getIdAdminRevisor() != null) {
            r.setIdAdminRevisor(s.getIdAdminRevisor().getId());
            r.setNombreAdminRevisor(s.getIdAdminRevisor().getNombre() + " " + s.getIdAdminRevisor().getApellidos());
        }
        r.setTipo(s.getTipo());
        r.setFechaInicio(s.getFechaInicio());
        r.setFechaFin(s.getFechaFin());
        r.setMotivo(s.getMotivo());
        r.setEstado(s.getEstado());
        r.setFechaSolicitud(s.getFechaSolicitud());
        r.setFechaResolucion(s.getFechaResolucion());
        r.setComentarioAdmin(s.getComentarioAdmin());
        return r;
    }
}
