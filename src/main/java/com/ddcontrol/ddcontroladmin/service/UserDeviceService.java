package com.ddcontrol.ddcontroladmin.service;

import com.ddcontrol.ddcontroladmin.model.UserDevice;
import com.ddcontrol.ddcontroladmin.model.Usuario;
import com.ddcontrol.ddcontroladmin.repository.UserDeviceRepository;
import com.ddcontrol.ddcontroladmin.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDeviceService {

    private final UserDeviceRepository repo;
    private final UsuarioRepository usuarioRepository;

    public void registerDevice(Integer userId, String token) {
        Usuario user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Si ya existe ese token, no hacemos nada
        if (repo.findByFcmToken(token).isPresent()) return;

        UserDevice device = new UserDevice();
        device.setUsuario(user);
        device.setFcmToken(token);

        repo.save(device);
    }

    public void removeDevice(String token) {
        repo.deleteByFcmToken(token);
    }

    public List<String> getTokensByUser(Integer userId) {
        return repo.findByUsuario_Id(userId)
                .stream()
                .map(UserDevice::getFcmToken)
                .toList();
    }
}