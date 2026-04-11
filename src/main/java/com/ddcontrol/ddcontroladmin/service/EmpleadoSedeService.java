package com.ddcontrol.ddcontroladmin.service;

import com.ddcontrol.ddcontroladmin.dto.EmpleadoSedeDTO;
import com.ddcontrol.ddcontroladmin.model.EmpleadoSede;
import com.ddcontrol.ddcontroladmin.model.EmpleadoSedeId;
import com.ddcontrol.ddcontroladmin.model.Sede;
import com.ddcontrol.ddcontroladmin.model.Usuario;
import com.ddcontrol.ddcontroladmin.repository.EmpleadoSedeRepository;
import com.ddcontrol.ddcontroladmin.repository.SedeRepository;
import com.ddcontrol.ddcontroladmin.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpleadoSedeService {

    private final EmpleadoSedeRepository empleadoSedeRepository;
    private final UsuarioRepository usuarioRepository;
    private final SedeRepository sedeRepository;

    @Transactional(readOnly = true)
    public List<EmpleadoSedeDTO.Response> findByUsuario(Integer idUsuario) {
        return empleadoSedeRepository.findById_IdUsuario(idUsuario).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<EmpleadoSedeDTO.Response> findBySede(Integer idSede) {
        return empleadoSedeRepository.findById_IdSede(idSede).stream().map(this::toResponse).toList();
    }

    public EmpleadoSedeDTO.Response asignar(EmpleadoSedeDTO.Request req) {
        Usuario usuario = usuarioRepository.findById(req.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + req.getIdUsuario()));
        Sede sede = sedeRepository.findById(req.getIdSede())
                .orElseThrow(() -> new EntityNotFoundException("Sede no encontrada: " + req.getIdSede()));

        EmpleadoSedeId pk = new EmpleadoSedeId();
        pk.setIdUsuario(req.getIdUsuario());
        pk.setIdSede(req.getIdSede());

        if (empleadoSedeRepository.existsById(pk))
            throw new IllegalArgumentException("El empleado ya está asignado a esa sede");

        EmpleadoSede es = new EmpleadoSede();
        es.setId(pk);
        es.setIdUsuario(usuario);
        es.setIdSede(sede);
        es.setFechaAsignacion(req.getFechaAsignacion() != null ? req.getFechaAsignacion() : LocalDate.now());
        return toResponse(empleadoSedeRepository.save(es));
    }

    public void desasignar(Integer idUsuario, Integer idSede) {
        EmpleadoSedeId pk = new EmpleadoSedeId();
        pk.setIdUsuario(idUsuario);
        pk.setIdSede(idSede);
        if (!empleadoSedeRepository.existsById(pk))
            throw new EntityNotFoundException("Asignación no encontrada");
        empleadoSedeRepository.deleteById(pk);
    }

    private EmpleadoSedeDTO.Response toResponse(EmpleadoSede es) {
        EmpleadoSedeDTO.Response r = new EmpleadoSedeDTO.Response();
        r.setIdUsuario(es.getIdUsuario().getId());
        r.setNombreUsuario(es.getIdUsuario().getNombre() + " " + es.getIdUsuario().getApellidos());
        r.setIdSede(es.getIdSede().getId());
        r.setNombreSede(es.getIdSede().getNombre());
        r.setFechaAsignacion(es.getFechaAsignacion());
        return r;
    }
}
