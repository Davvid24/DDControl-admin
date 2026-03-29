package com.ddcontrol.ddcontroladmin.repository;

import com.ddcontrol.ddcontroladmin.model.Incidencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Integer> {
    List<Incidencia> findByIdUsuario_Id(Integer idUsuario);
    List<Incidencia> findByIdUsuario_IdAndResuelta(Integer idUsuario, Boolean resuelta);
    List<Incidencia> findByResuelta(Boolean resuelta);
}
