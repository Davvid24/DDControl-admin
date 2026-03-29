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

@Getter
@Setter
@Entity
@Table(name = "justificante")
public class Justificante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_solicitud", nullable = false)
    private Solicitud idSolicitud;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Size(max = 500)
    @NotNull
    @Column(name = "ruta_archivo", nullable = false, length = 500)
    private String rutaArchivo;

    @Size(max = 100)
    @NotNull
    @Column(name = "tipo_mime", nullable = false, length = 100)
    private String tipoMime;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "fecha_subida", nullable = false)
    private Instant fechaSubida;

}