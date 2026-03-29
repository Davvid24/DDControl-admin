package com.ddcontrol.ddcontroladmin.service;

import com.ddcontrol.ddcontroladmin.dto.IncidenciaDTO;
import com.ddcontrol.ddcontroladmin.model.Fichaje;
import com.ddcontrol.ddcontroladmin.model.Incidencia;
import com.ddcontrol.ddcontroladmin.model.Usuario;
import com.ddcontrol.ddcontroladmin.repository.FichajeRepository;
import com.ddcontrol.ddcontroladmin.repository.IncidenciaRepository;
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
public class IncidenciaService {

    private final IncidenciaRepository incidenciaRepository;
    private final UsuarioRepository usuarioRepository;
    private final FichajeRepository fichajeRepository;

    @Transactional(readOnly = true)
    public List<IncidenciaDTO.Response> findAll() {
        return incidenciaRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<IncidenciaDTO.Response> findByUsuario(Integer idUsuario) {
        return incidenciaRepository.findByIdUsuario_Id(idUsuario).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<IncidenciaDTO.Response> findPendientes() {
        return incidenciaRepository.findByResuelta(false).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public IncidenciaDTO.Response findById(Integer id) {
        return toResponse(getOrThrow(id));
    }

    public IncidenciaDTO.Response create(IncidenciaDTO.Request req) {
        Usuario usuario = usuarioRepository.findById(req.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + req.getIdUsuario()));

        Fichaje fichaje = null;
        if (req.getIdFichaje() != null)
            fichaje = fichajeRepository.findById(req.getIdFichaje())
                    .orElseThrow(() -> new EntityNotFoundException("Fichaje no encontrado: " + req.getIdFichaje()));

        Incidencia i = new Incidencia();
        i.setIdUsuario(usuario);
        i.setIdFichaje(fichaje);
        i.setTipo(req.getTipo());
        i.setDescripcion(req.getDescripcion());
        i.setFecha(Instant.now());
        i.setResuelta(false);
        return toResponse(incidenciaRepository.save(i));
    }

    public IncidenciaDTO.Response marcarResuelta(Integer id) {
        Incidencia i = getOrThrow(id);
        i.setResuelta(true);
        return toResponse(incidenciaRepository.save(i));
    }

    public void delete(Integer id) {
        if (!incidenciaRepository.existsById(id))
            throw new EntityNotFoundException("Incidencia no encontrada: " + id);
        incidenciaRepository.deleteById(id);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Incidencia getOrThrow(Integer id) {
        return incidenciaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Incidencia no encontrada: " + id));
    }

    private IncidenciaDTO.Response toResponse(Incidencia i) {
        IncidenciaDTO.Response r = new IncidenciaDTO.Response();
        r.setId(i.getId());
        r.setIdUsuario(i.getIdUsuario().getId());
        r.setNombreUsuario(i.getIdUsuario().getNombre() + " " + i.getIdUsuario().getApellidos());
        r.setIdFichaje(i.getIdFichaje() != null ? i.getIdFichaje().getId() : null);
        r.setTipo(i.getTipo());
        r.setDescripcion(i.getDescripcion());
        r.setFecha(i.getFecha());
        r.setResuelta(i.getResuelta());
        return r;
    }
}
