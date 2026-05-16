package com.ddcontrol.ddcontroladmin;

import com.ddcontrol.ddcontroladmin.model.*;
import com.ddcontrol.ddcontroladmin.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DdControlAdminApplicationTests {

    private Empresa empresa;
    private Usuario usuario;
    private Sede sede;
    private Turno turno;
    private Solicitud solicitud;
    private Incidencia incidencia;
    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1);
        empresa.setNombre("DDControl S.L.");
        empresa.setNif("B12345678");
        empresa.setFechaAlta(Instant.now());

        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombre("David");
        usuario.setApellidos("Domínguez");
        usuario.setEmail("david@ddcontrol.com");
        usuario.setPasswordHash("$2a$10$hash");
        usuario.setRol("Empleado");
        usuario.setTipoEmpleado("Técnico");
        usuario.setIdEmpresa(empresa);
        usuario.setActivo(true);
        usuario.setFechaAlta(Instant.now());

        sede = new Sede();
        sede.setId(1);
        sede.setNombre("Oficina Getafe");
        sede.setLatitud(new BigDecimal("40.3058"));
        sede.setLongitud(new BigDecimal("-3.7314"));
        sede.setRadioMetros(150);
        sede.setActiva(true);
        sede.setIdEmpresa(empresa);

        turno = new Turno();
        turno.setId(1);
        turno.setNombre("Mañana L-V");
        turno.setHoraEntrada(LocalTime.of(8, 0));
        turno.setHoraSalida(LocalTime.of(16, 0));
        turno.setDescripcion("$L-M-X-J-V$");
        turno.setIdEmpresa(empresa);

        solicitud = new Solicitud();
        solicitud.setId(1);
        solicitud.setIdUsuario(usuario);
        solicitud.setTipo("VACACIONES");
        solicitud.setFechaInicio(LocalDate.of(2025, 8, 1));
        solicitud.setFechaFin(LocalDate.of(2025, 8, 15));
        solicitud.setEstado("pendiente");
        solicitud.setFechaSolicitud(Instant.now());

        incidencia = new Incidencia();
        incidencia.setId(1);
        incidencia.setIdUsuario(usuario);
        incidencia.setTipo("olvido");
        incidencia.setDescripcion("Me olvidé de fichar la salida el lunes");
        incidencia.setFecha(Instant.now());
        incidencia.setResuelta(false);

        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret",
                "clave-secreta-ddcontrol-minimo-32-caracteres-ok");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);

        userDetails = new User("david@ddcontrol.com", "password",
                List.of(new SimpleGrantedAuthority("ROLE_EMPLEADO")));
    }

    @Test
    void usuarioNuevoActivo() {
        assertTrue(usuario.getActivo());
    }

    @Test
    void solicitudEnPendiente() {
        assertEquals("pendiente", solicitud.getEstado());
    }

    @Test
    void cambioEstadoSolicitud() {
        solicitud.setEstado("aprobada");
        assertEquals("aprobada", solicitud.getEstado());
    }

    @Test
    void incidenciaNoResuelta() {
        assertFalse(incidencia.getResuelta());
    }

    @Test
    void cambioEstadoIncidencia() {
        incidencia.setResuelta(true);
        assertTrue(incidencia.getResuelta());
    }

    @Test
    void turnoALasOcho() {
        assertEquals(LocalTime.of(8, 0), turno.getHoraEntrada());
    }

    @Test
    void salidaDespuesDeEntrada() {
        assertTrue(turno.getHoraSalida().isAfter(turno.getHoraEntrada()));
    }

    @Test
    void radioMayorQueCero() {
        assertTrue(sede.getRadioMetros() > 0);
    }

    @Test
    void TokenConEmail() {
        String token = jwtService.generateToken(userDetails, 1, 1, "Empleado");
        assertEquals("david@ddcontrol.com", jwtService.extractEmail(token));
    }

    @Test
    void tokenExpiradoValidez() {
        JwtService jwtExpirado = new JwtService();
        ReflectionTestUtils.setField(jwtExpirado, "secret",
                "clave-secreta-ddcontrol-minimo-32-caracteres-ok");
        ReflectionTestUtils.setField(jwtExpirado, "expiration", -1000L);

        String token = jwtExpirado.generateToken(userDetails, 1, 1, "Empleado");
        assertFalse(jwtExpirado.isTokenValid(token, userDetails));
    }
}