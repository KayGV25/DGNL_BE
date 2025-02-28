package com.dgnl_backend.project.dgnl_backend.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dgnl_backend.project.dgnl_backend.schemas.identity.UserDevice;

@Service
public class UserDeviceService {
    public static Boolean isNoDevice(Optional<UserDevice> existingDevice, String fingerprint){
        return existingDevice.isEmpty() || !existingDevice.get().getFingerprint().equals(fingerprint);
    }
}
