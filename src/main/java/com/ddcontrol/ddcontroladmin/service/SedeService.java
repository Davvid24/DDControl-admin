package com.ddcontrol.ddcontroladmin.service;

import com.ddcontrol.ddcontroladmin.dto.SedeDTO;
import com.ddcontrol.ddcontroladmin.model.Empresa;
import com.ddcontrol.ddcontroladmin.model.Sede;
import com.ddcontrol.ddcontroladmin.repository.EmpresaRepository;
import com.ddcontrol.ddcontroladmin.repository.SedeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SedeService {

    private final SedeRepository sedeRepository;
    private final EmpresaRepository empresaRepository;

    @Transactional(readOnly = true)
    public List<SedeDTO.Response> findAll() {
        return sedeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<SedeDTO.Response> findByEmpresa(Integer idEmpresa) {
        return sedeRepository.findByIdEmpresa_Id(idEmpresa).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SedeDTO.Response findById(Integer id) {
        return toResponse(getOrThrow(id));
    }

    public SedeDTO.Response create(SedeDTO.Request req) {
        Empresa empresa = empresaRepository.findById(req.getIdEmpresa())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada: " + req.getIdEmpresa()));

        Sede s = new Sede();
        s.setIdEmpresa(empresa);
        s.setNombre(req.getNombre());
        s.setDireccion(req.getDireccion());
        s.setLatitud(req.getLatitud());
        s.setLongitud(req.getLongitud());
        s.setRadioMetros(req.getRadioMetros() != null ? req.getRadioMetros() : 100);
        s.setActiva(req.getActiva() != null ? req.getActiva() : true);
        return toResponse(sedeRepository.save(s));
    }

    public SedeDTO.Response update(Integer id, SedeDTO.Request req) {
        Sede s = getOrThrow(id);
        Empresa empresa = empresaRepository.findById(req.getIdEmpresa())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada: " + req.getIdEmpresa()));

        s.setIdEmpresa(empresa);
        s.setNombre(req.getNombre());
        s.setDireccion(req.getDireccion());
        s.setLatitud(req.getLatitud());
        s.setLongitud(req.getLongitud());
        s.setRadioMetros(req.getRadioMetros());
        s.setActiva(req.getActiva());
        return toResponse(sedeRepository.save(s));
    }

    public void delete(Integer id) {
        if (!sedeRepository.existsById(id))
            throw new EntityNotFoundException("Sede no encontrada: " + id);
        sedeRepository.deleteById(id);
    }

    private Sede getOrThrow(Integer id) {
        return sedeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sede no encontrada: " + id));
    }

    private SedeDTO.Response toResponse(Sede s) {
        SedeDTO.Response r = new SedeDTO.Response();
        r.setId(s.getId());
        r.setIdEmpresa(s.getIdEmpresa().getId());
        r.setNombreEmpresa(s.getIdEmpresa().getNombre());
        r.setNombre(s.getNombre());
        r.setDireccion(s.getDireccion());
        r.setLatitud(s.getLatitud());
        r.setLongitud(s.getLongitud());
        r.setRadioMetros(s.getRadioMetros());
        r.setActiva(s.getActiva());
        return r;
    }
}
