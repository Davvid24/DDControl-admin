package com.ddcontrol.ddcontroladmin.repository;

import com.ddcontrol.ddcontroladmin.model.Solicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudRepository extends JpaRepository<Solicitud, Integer> {
    List<Solicitud> findByIdUsuario_Id(Integer idUsuario);
    List<Solicitud> findByIdUsuario_IdAndEstado(Integer idUsuario, String estado);
    List<Solicitud> findByEstado(String estado);
}
