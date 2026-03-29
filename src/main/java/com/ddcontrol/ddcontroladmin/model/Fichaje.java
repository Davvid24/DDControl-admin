package com.ddcontrol.ddcontroladmin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "fichaje")
public class Fichaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede idSede;

    @Size(max = 20)
    @NotNull
    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "timestamp_ficha", nullable = false)
    private Instant timestampFicha;

    @NotNull
    @Column(name = "latitud_real", nullable = false, precision = 10, scale = 7)
    private BigDecimal latitudReal;

    @NotNull
    @Column(name = "longitud_real", nullable = false, precision = 10, scale = 7)
    private BigDecimal longitudReal;

    @NotNull
    @Column(name = "dentro_de_radio", nullable = false)
    private Boolean dentroDeRadio = false;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'movil'")
    @Column(name = "metodo", nullable = false, length = 20)
    private String metodo;

    @Lob
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

}