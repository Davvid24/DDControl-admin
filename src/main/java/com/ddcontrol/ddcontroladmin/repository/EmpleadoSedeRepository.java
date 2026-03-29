package com.ddcontrol.ddcontroladmin.repository;

import com.ddcontrol.ddcontroladmin.model.EmpleadoSede;
import com.ddcontrol.ddcontroladmin.model.EmpleadoSedeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoSedeRepository extends JpaRepository<EmpleadoSede, EmpleadoSedeId> {
    List<EmpleadoSede> findById_IdUsuario(Integer idUsuario);
    List<EmpleadoSede> findById_IdSede(Integer idSede);
}
