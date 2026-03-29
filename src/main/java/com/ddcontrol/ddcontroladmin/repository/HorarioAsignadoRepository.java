package com.ddcontrol.ddcontroladmin.repository;

import com.ddcontrol.ddcontroladmin.model.HorarioAsignado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioAsignadoRepository extends JpaRepository<HorarioAsignado, Integer> {
    List<HorarioAsignado> findByIdUsuario_Id(Integer idUsuario);
}
