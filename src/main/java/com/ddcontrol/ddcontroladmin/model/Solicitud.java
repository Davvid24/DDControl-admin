package com.ddcontrol.ddcontroladmin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "solicitud")
public class Solicitud {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "id_admin_revisor")
    private Usuario idAdminRevisor;

    @Size(max = 30)
    @NotNull
    @Column(name = "tipo", nullable = false, length = 30)
    private String tipo;

    @NotNull
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Lob
    @Column(name = "motivo", columnDefinition = "TEXT")
    private String motivo;

    @Size(max = 20)
    @NotNull
    @ColumnDefault("'pendiente'")
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "fecha_solicitud", nullable = false)
    private Instant fechaSolicitud;

    @Column(name = "fecha_resolucion")
    private Instant fechaResolucion;

    @Lob
    @Column(name = "comentario_admin", columnDefinition = "TEXT")
    private String comentarioAdmin;

}