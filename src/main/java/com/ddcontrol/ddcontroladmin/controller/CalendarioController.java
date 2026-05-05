package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.dto.CalendarioDTO;
import com.ddcontrol.ddcontroladmin.model.Fichaje;
import com.ddcontrol.ddcontroladmin.model.Usuario;
import com.ddcontrol.ddcontroladmin.repository.FichajeRepository;
import com.ddcontrol.ddcontroladmin.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/calendario")
@RequiredArgsConstructor
public class CalendarioController {

    private final UsuarioRepository usuarioRepository;
    private final FichajeRepository fichajeRepository;

    @GetMapping("/{idUsuario}")
    @Transactional(readOnly = true)
    public CalendarioDTO.Response getCalendario(
            @PathVariable Integer idUsuario,
            @RequestParam int year,
            @RequestParam int month) {

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));

        CalendarioDTO.TurnoInfo turnoInfo = new CalendarioDTO.TurnoInfo();
        List<String> diasTurno = new ArrayList<>();

        if (usuario.getTurno() != null) {
            var turno = usuario.getTurno();
            turnoInfo.setNombre(turno.getNombre());
            turnoInfo.setHoraEntrada(turno.getHoraEntrada().toString());
            turnoInfo.setHoraSalida(turno.getHoraSalida().toString());

            String desc = turno.getDescripcion();
            if (desc != null && desc.startsWith("$")) {
                int end = desc.indexOf("$", 1);
                if (end > 1) {
                    String diasStr = desc.substring(1, end);
                    diasTurno = Arrays.asList(diasStr.split("-"));
                }
            }
            turnoInfo.setDiasSemana(diasTurno);
        }

        Instant inicio = LocalDate.of(year, month, 1)
                .atStartOfDay(ZoneId.of("Europe/Madrid")).toInstant();
        Instant fin = LocalDate.of(year, month, 1)
                .plusMonths(1).atStartOfDay(ZoneId.of("Europe/Madrid")).toInstant();

        List<Fichaje> fichajes = fichajeRepository
                .findByIdUsuario_IdAndTimestampFichaBetween(idUsuario, inicio, fin);

        Map<LocalDate, List<Fichaje>> porDia = fichajes.stream()
                .collect(Collectors.groupingBy(f ->
                        f.getTimestampFicha().atZone(ZoneId.of("Europe/Madrid")).toLocalDate()));

        Map<String, DayOfWeek> diaMap = Map.of(
                "L", DayOfWeek.MONDAY,
                "M", DayOfWeek.TUESDAY,
                "X", DayOfWeek.WEDNESDAY,
                "J", DayOfWeek.THURSDAY,
                "V", DayOfWeek.FRIDAY,
                "S", DayOfWeek.SATURDAY,
                "D", DayOfWeek.SUNDAY
        );

        Set<DayOfWeek> diasDeSemana = diasTurno.stream()
                .map(d -> diaMap.getOrDefault(d, null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<CalendarioDTO.DiaCalendario> dias = new ArrayList<>();

        LocalDate cursor = LocalDate.of(year, month, 1);
        LocalDate finMes = cursor.plusMonths(1);

        while (cursor.isBefore(finMes)) {
            boolean esDiaTurno = diasDeSemana.contains(cursor.getDayOfWeek());
            List<Fichaje> fichajesDia = porDia.getOrDefault(cursor, List.of());

            CalendarioDTO.DiaCalendario dia = new CalendarioDTO.DiaCalendario();
            dia.setFecha(cursor.format(fmt));
            dia.setEsDiaTurno(esDiaTurno);
            dia.setTieneFichaje(!fichajesDia.isEmpty());

            fichajesDia.stream()
                    .filter(f -> f.getTipo().equals("entrada"))
                    .findFirst()
                    .ifPresent(f -> dia.setHoraEntrada(
                            f.getTimestampFicha().atZone(ZoneId.of("Europe/Madrid"))
                                    .toLocalTime().toString()));

            fichajesDia.stream()
                    .filter(f -> f.getTipo().equals("salida"))
                    .findFirst()
                    .ifPresent(f -> {
                        dia.setHoraSalida(
                                f.getTimestampFicha().atZone(ZoneId.of("Europe/Madrid"))
                                        .toLocalTime().toString());
                        dia.setDentroDeRadio(f.getDentroDeRadio() != null && f.getDentroDeRadio());
                    });

            dias.add(dia);
            cursor = cursor.plusDays(1);
        }

        CalendarioDTO.Response response = new CalendarioDTO.Response();
        response.setTurno(turnoInfo);
        response.setDias(dias);
        return response;
    }
}