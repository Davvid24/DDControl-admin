package com.ddcontrol.ddcontroladmin.service;

import com.ddcontrol.ddcontroladmin.dto.FichajeDTO;
import com.ddcontrol.ddcontroladmin.model.Fichaje;
import com.ddcontrol.ddcontroladmin.model.Sede;
import com.ddcontrol.ddcontroladmin.model.Usuario;
import com.ddcontrol.ddcontroladmin.repository.FichajeRepository;
import com.ddcontrol.ddcontroladmin.repository.SedeRepository;
import com.ddcontrol.ddcontroladmin.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FichajeService {

    private final FichajeRepository fichajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final SedeRepository sedeRepository;

    @Transactional(readOnly = true)
    public List<FichajeDTO.Response> findAll() {
        return fichajeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<FichajeDTO.Response> findByUsuario(Integer idUsuario) {
        return fichajeRepository.findByIdUsuario_Id(idUsuario).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public FichajeDTO.Response findById(Integer id) {
        return toResponse(getOrThrow(id));
    }

    public FichajeDTO.Response create(FichajeDTO.Request req) {
        Usuario usuario = usuarioRepository.findById(req.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + req.getIdUsuario()));
        Sede sede = sedeRepository.findById(req.getIdSede())
                .orElseThrow(() -> new EntityNotFoundException("Sede no encontrada: " + req.getIdSede()));

        boolean dentroDeRadio = calcularDentroDeRadio(
                req.getLatitudReal(), req.getLongitudReal(),
                sede.getLatitud(), sede.getLongitud(),
                sede.getRadioMetros());

        Fichaje f = new Fichaje();
        f.setIdUsuario(usuario);
        f.setIdSede(sede);
        f.setTipo(req.getTipo());
        f.setTimestampFicha(Instant.now());
        f.setLatitudReal(req.getLatitudReal());
        f.setLongitudReal(req.getLongitudReal());
        f.setDentroDeRadio(dentroDeRadio);
        f.setMetodo(req.getMetodo() != null ? req.getMetodo() : "movil");
        f.setObservaciones(req.getObservaciones());
        return toResponse(fichajeRepository.save(f));
    }

    public FichajeDTO.Response update(Integer id, FichajeDTO.Request req) {
        Fichaje f = getOrThrow(id);
        Usuario usuario = usuarioRepository.findById(req.getIdUsuario())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado: " + req.getIdUsuario()));
        Sede sede = sedeRepository.findById(req.getIdSede())
                .orElseThrow(() -> new EntityNotFoundException("Sede no encontrada: " + req.getIdSede()));

        f.setIdUsuario(usuario);
        f.setIdSede(sede);
        f.setTipo(req.getTipo());
        f.setLatitudReal(req.getLatitudReal());
        f.setLongitudReal(req.getLongitudReal());
        f.setDentroDeRadio(calcularDentroDeRadio(
                req.getLatitudReal(), req.getLongitudReal(),
                sede.getLatitud(), sede.getLongitud(),
                sede.getRadioMetros()));
        f.setMetodo(req.getMetodo());
        f.setObservaciones(req.getObservaciones());
        return toResponse(fichajeRepository.save(f));
    }

    public void delete(Integer id) {
        if (!fichajeRepository.existsById(id))
            throw new EntityNotFoundException("Fichaje no encontrado: " + id);
        fichajeRepository.deleteById(id);
    }


    private boolean calcularDentroDeRadio(BigDecimal latR, BigDecimal lonR,
                                           BigDecimal latS, BigDecimal lonS,
                                           Integer radioMetros) {
        final int R = 6_371_000; // radio Tierra en metros
        double lat1 = Math.toRadians(latR.doubleValue());
        double lat2 = Math.toRadians(latS.doubleValue());
        double dLat = Math.toRadians(latS.doubleValue() - latR.doubleValue());
        double dLon = Math.toRadians(lonS.doubleValue() - lonR.doubleValue());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double distancia = R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return distancia <= radioMetros;
    }

    private Fichaje getOrThrow(Integer id) {
        return fichajeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fichaje no encontrado: " + id));
    }

    private FichajeDTO.Response toResponse(Fichaje f) {
        FichajeDTO.Response r = new FichajeDTO.Response();
        r.setId(f.getId());
        r.setIdUsuario(f.getIdUsuario().getId());
        r.setNombreUsuario(f.getIdUsuario().getNombre() + " " + f.getIdUsuario().getApellidos());
        r.setIdSede(f.getIdSede().getId());
        r.setNombreSede(f.getIdSede().getNombre());
        r.setTipo(f.getTipo());
        r.setTimestampFicha(f.getTimestampFicha());
        r.setLatitudReal(f.getLatitudReal());
        r.setLongitudReal(f.getLongitudReal());
        r.setDentroDeRadio(f.getDentroDeRadio());
        r.setMetodo(f.getMetodo());
        r.setObservaciones(f.getObservaciones());
        return r;
    }
}
