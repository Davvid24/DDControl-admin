package com.ddcontrol.ddcontroladmin.repository;

import com.ddcontrol.ddcontroladmin.model.Fichaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface FichajeRepository extends JpaRepository<Fichaje, Integer> {
    List<Fichaje> findByIdUsuario_Id(Integer idUsuario);
    List<Fichaje> findByIdUsuario_IdAndTimestampFichaBetween(Integer idUsuario, Instant from, Instant to);
    List<Fichaje> findByIdSede_Id(Integer idSede);
}
