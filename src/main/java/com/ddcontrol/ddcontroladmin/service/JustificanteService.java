package com.ddcontrol.ddcontroladmin.service;

import com.ddcontrol.ddcontroladmin.dto.JustificanteDTO;
import com.ddcontrol.ddcontroladmin.model.Justificante;
import com.ddcontrol.ddcontroladmin.model.Solicitud;
import com.ddcontrol.ddcontroladmin.repository.JustificanteRepository;
import com.ddcontrol.ddcontroladmin.repository.SolicitudRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JustificanteService {

    private final JustificanteRepository justificanteRepository;
    private final SolicitudRepository solicitudRepository;

    @Transactional(readOnly = true)
    public List<JustificanteDTO.Response> findBySolicitud(Integer idSolicitud) {
        return justificanteRepository.findByIdSolicitud_Id(idSolicitud).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public JustificanteDTO.Response findById(Integer id) {
        return toResponse(getOrThrow(id));
    }

    public JustificanteDTO.Response create(JustificanteDTO.Request req) {
        Solicitud solicitud = solicitudRepository.findById(req.getIdSolicitud())
                .orElseThrow(() -> new EntityNotFoundException("Solicitud no encontrada: " + req.getIdSolicitud()));

        Justificante j = new Justificante();
        j.setIdSolicitud(solicitud);
        j.setNombreArchivo(req.getNombreArchivo());
        j.setRutaArchivo(req.getRutaArchivo());
        j.setTipoMime(req.getTipoMime());
        j.setFechaSubida(Instant.now());
        return toResponse(justificanteRepository.save(j));
    }

    public void delete(Integer id) {
        if (!justificanteRepository.existsById(id))
            throw new EntityNotFoundException("Justificante no encontrado: " + id);
        justificanteRepository.deleteById(id);
    }


    private Justificante getOrThrow(Integer id) {
        return justificanteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Justificante no encontrado: " + id));
    }

    private JustificanteDTO.Response toResponse(Justificante j) {
        JustificanteDTO.Response r = new JustificanteDTO.Response();
        r.setId(j.getId());
        r.setIdSolicitud(j.getIdSolicitud().getId());
        r.setNombreArchivo(j.getNombreArchivo());
        r.setRutaArchivo(j.getRutaArchivo());
        r.setTipoMime(j.getTipoMime());
        r.setFechaSubida(j.getFechaSubida());
        return r;
    }
}
