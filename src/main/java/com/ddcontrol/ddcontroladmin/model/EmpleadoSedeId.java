package com.ddcontrol.ddcontroladmin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class EmpleadoSedeId implements Serializable {
    private static final long serialVersionUID = 6595404974806247678L;
    @NotNull
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @NotNull
    @Column(name = "id_sede", nullable = false)
    private Integer idSede;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EmpleadoSedeId entity = (EmpleadoSedeId) o;
        return Objects.equals(this.idUsuario, entity.idUsuario) &&
                Objects.equals(this.idSede, entity.idSede);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idSede);
    }

}