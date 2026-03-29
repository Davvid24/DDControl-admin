// ── EmpresaRepository ──────────────────────────────────────────────────────
package com.ddcontrol.ddcontroladmin.repository;

import com.ddcontrol.ddcontroladmin.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    boolean existsByNif(String nif);
    boolean existsByEmailContacto(String emailContacto);
}
