package com.ddcontrol.ddcontroladmin.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService {

    public void enviarNotificacion(String fcmToken, String titulo, String cuerpo, String tipo) {
        if (fcmToken == null || fcmToken.isBlank()) return;

        try {
            Message message = Message.builder()
                    .setToken(fcmToken)
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(cuerpo)
                            .build())
                    .putData("type", tipo)
                    .build();

            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            System.err.println("Error enviando FCM: " + e.getMessage());
        }
    }
}