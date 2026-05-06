package com.ddcontrol.ddcontroladmin.dto;

import lombok.Data;
import java.util.List;

public class CalendarioDTO {

    @Data
    public static class Response {
        private TurnoInfo turno;
        private List<DiaCalendario> dias;
    }

    @Data
    public static class TurnoInfo {
        private String nombre;
        private String horaEntrada;
        private String horaSalida;
        private List<String> diasSemana;
    }

    @Data
    public static class DiaCalendario {
        private String fecha;
        private boolean esDiaTurno;
        private boolean tieneFichaje;
        private String horaEntrada;
        private String horaSalida;
        private boolean dentroDeRadio;
    }
}