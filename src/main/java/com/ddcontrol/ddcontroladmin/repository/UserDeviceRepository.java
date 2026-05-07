package com.ddcontrol.ddcontroladmin.repository;

import com.ddcontrol.ddcontroladmin.model.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Integer> {

    Optional<UserDevice> findByFcmToken(String token);

    List<UserDevice> findByUsuario_Id(Integer userId);

    void deleteByFcmToken(String token);

    @Query("SELECT u.fcmToken FROM UserDevice u WHERE u.usuario.id = :userId")
    List<String> findTokensByUserId(@Param("userId") Integer userId);
}