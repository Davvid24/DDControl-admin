package com.ddcontrol.ddcontroladmin.controller;

import com.ddcontrol.ddcontroladmin.service.UserDeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/devices")
public class DeviceController {

    private final UserDeviceService service;

    @PostMapping("/register/{userId}")
    public void register(@PathVariable Integer userId,
                         @RequestBody Map<String, String> body) {

        String token = body.get("fcmToken");
        service.registerDevice(userId, token);
    }

    @DeleteMapping
    public void remove(@RequestBody Map<String, String> body) {
        service.removeDevice(body.get("fcmToken"));
    }
}