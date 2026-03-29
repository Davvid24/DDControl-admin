package com.ddcontrol.ddcontroladmin.service;

import com.ddcontrol.ddcontroladmin.dto.HorarioAsignadoDTO;
import com.ddcontrol.ddcontroladmin.model.HorarioAsignado;
import com.ddcontrol.ddcontroladmin.model.Turno;
import com.ddcontrol.ddcontroladmin.model.Usuario;
import com.ddcontrol.ddcontroladmin.repository.HorarioAsignadoRepository;
import com.ddcontrol.ddcontroladmin.repository.TurnoRepository;
import com.ddcontrol.ddcontroladmin.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HorarioAsignadoService {

    private final HorarioAsignadoRepository horarioRepo;
    private final UsuarioRepository usuarioRepository;
    private final TurnoRepository turnoRepository;

    @Transactional(readOnly = true)
    public List<HorarioAsignadoDTO.Response> findAll() {
        return horarioRepo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<HorarioAsignadoDTO.Response> findByUsuario(Integer idUsuario) {
        return horarioRepo.findByIdUsuario_Id(idUsuario).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public HorarioAsignadoDTO.Response findById(Integer id) {
        return toResponse(getOrThrow(id));
    }

    public HorarioAsignadoDTO.Response create(HorarioAsignadoDTO.Request req) {
        Usuario usuario = usuarioRepository.findById(req.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + req.getIdUsuario()));
        Turno turno = turnoRepository.findById(req.getIdTurno())
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado: " + req.getIdTurno()));

        HorarioAsignado h = new HorarioAsignado();
        h.setIdUsuario(usuario);
        h.setIdTurno(turno);
        h.setFechaInicio(req.getFechaInicio());
        h.setFechaFin(req.getFechaFin());
        h.setDiasSemana(req.getDiasSemana());
        return toResponse(horarioRepo.save(h));
    }

    public HorarioAsignadoDTO.Response update(Integer id, HorarioAsignadoDTO.Request req) {
        HorarioAsignado h = getOrThrow(id);
        Usuario usuario = usuarioRepository.findById(req.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + req.getIdUsuario()));
        Turno turno = turnoRepository.findById(req.getIdTurno())
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado: " + req.getIdTurno()));

        h.setIdUsuario(usuario);
        h.setIdTurno(turno);
        h.setFechaInicio(req.getFechaInicio());
        h.setFechaFin(req.getFechaFin());
        h.setDiasSemana(req.getDiasSemana());
        return toResponse(horarioRepo.save(h));
    }

    public void delete(Integer id) {
        if (!horarioRepo.existsById(id))
            throw new EntityNotFoundException("HorarioAsignado no encontrado: " + id);
        horarioRepo.deleteById(id);
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private HorarioAsignado getOrThrow(Integer id) {
        return horarioRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("HorarioAsignado no encontrado: " + id));
    }

    private HorarioAsignadoDTO.Response toResponse(HorarioAsignado h) {
        HorarioAsignadoDTO.Response r = new HorarioAsignadoDTO.Response();
        r.setId(h.getId());
        r.setIdUsuario(h.getIdUsuario().getId());
        r.setNombreUsuario(h.getIdUsuario().getNombre() + " " + h.getIdUsuario().getApellidos());
        r.setIdTurno(h.getIdTurno().getId());
        r.setNombreTurno(h.getIdTurno().getNombre());
        r.setFechaInicio(h.getFechaInicio());
        r.setFechaFin(h.getFechaFin());
        r.setDiasSemana(h.getDiasSemana());
        return r;
    }
}
