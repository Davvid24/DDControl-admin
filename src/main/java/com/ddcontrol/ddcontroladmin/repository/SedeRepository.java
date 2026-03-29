package com.ddcontrol.ddcontroladmin.repository;

import com.ddcontrol.ddcontroladmin.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Integer> {
    List<Sede> findByIdEmpresa_Id(Integer idEmpresa);
    List<Sede> findByIdEmpresa_IdAndActivaTrue(Integer idEmpresa);
}
