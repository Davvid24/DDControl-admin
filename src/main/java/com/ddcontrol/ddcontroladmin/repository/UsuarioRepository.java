package com.ddcontrol.ddcontroladmin.repository;

import com.ddcontrol.ddcontroladmin.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Usuario> findByIdEmpresa_Id(Integer idEmpresa);
    List<Usuario> findByIdEmpresa_IdAndRol(Integer idEmpresa, String rol);
}
