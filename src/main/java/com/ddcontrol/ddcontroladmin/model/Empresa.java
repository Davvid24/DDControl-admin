package com.ddcontrol.ddcontroladmin.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "empresa")
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 150)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Size(max = 20)
    @NotNull
    @Column(name = "nif", nullable = false, length = 20)
    private String nif;

    @Size(max = 255)
    @Column(name = "direccion")
    private String direccion;

    @Size(max = 150)
    @Column(name = "email_contacto", length = 150)
    private String emailContacto;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @NotNull
    @ColumnDefault("current_timestamp()")
    @Column(name = "fecha_alta", nullable = false)
    private Instant fechaAlta;

}