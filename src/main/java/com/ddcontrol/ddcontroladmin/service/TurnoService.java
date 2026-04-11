package com.ddcontrol.ddcontroladmin.service;

import com.ddcontrol.ddcontroladmin.dto.TurnoDTO;
import com.ddcontrol.ddcontroladmin.model.Empresa;
import com.ddcontrol.ddcontroladmin.model.Turno;
import com.ddcontrol.ddcontroladmin.repository.EmpresaRepository;
import com.ddcontrol.ddcontroladmin.repository.TurnoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<TurnoDTO.Response> findAll() {
        return turnoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TurnoDTO.Response> findByEmpresa(Integer idEmpresa) {
        return turnoRepository.findByIdEmpresa_Id(idEmpresa).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TurnoDTO.Response findById(Integer id) {
        return toResponse(getOrThrow(id));
    }

    public TurnoDTO.Response create(TurnoDTO.Request req) {
        Empresa empresa = empresaRepository.findById(req.getIdEmpresa())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada: " + req.getIdEmpresa()));

        Turno t = new Turno();
        t.setIdEmpresa(empresa);
        t.setNombre(req.getNombre());
        t.setHoraEntrada(req.getHoraEntrada());
        t.setHoraSalida(req.getHoraSalida());
        t.setDescripcion(req.getDescripcion());
        return toResponse(turnoRepository.save(t));
    }

    public TurnoDTO.Response update(Integer id, TurnoDTO.Request req) {
        Turno t = getOrThrow(id);
        Empresa empresa = empresaRepository.findById(req.getIdEmpresa())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada: " + req.getIdEmpresa()));

        t.setIdEmpresa(empresa);
        t.setNombre(req.getNombre());
        t.setHoraEntrada(req.getHoraEntrada());
        t.setHoraSalida(req.getHoraSalida());
        t.setDescripcion(req.getDescripcion());
        return toResponse(turnoRepository.save(t));
    }

    public void delete(Integer id) {
        if (!turnoRepository.existsById(id))
            throw new EntityNotFoundException("Turno no encontrado: " + id);
        turnoRepository.deleteById(id);
    }

    private Turno getOrThrow(Integer id) {
        return turnoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Turno no encontrado: " + id));
    }

    private TurnoDTO.Response toResponse(Turno t) {
        TurnoDTO.Response r = new TurnoDTO.Response();
        r.setId(t.getId());
        r.setIdEmpresa(t.getIdEmpresa().getId());
        r.setNombreEmpresa(t.getIdEmpresa().getNombre());
        r.setNombre(t.getNombre());
        r.setHoraEntrada(t.getHoraEntrada());
        r.setHoraSalida(t.getHoraSalida());
        r.setDescripcion(t.getDescripcion());
        return r;
    }
}
