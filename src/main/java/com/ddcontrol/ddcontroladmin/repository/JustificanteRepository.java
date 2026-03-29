package com.ddcontrol.ddcontroladmin.repository;

import com.ddcontrol.ddcontroladmin.model.Justificante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JustificanteRepository extends JpaRepository<Justificante, Integer> {
    List<Justificante> findByIdSolicitud_Id(Integer idSolicitud);
}
