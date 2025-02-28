package com.dgnl_backend.project.dgnl_backend.repositories.identity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dgnl_backend.project.dgnl_backend.schemas.identity.User;
import com.dgnl_backend.project.dgnl_backend.schemas.identity.UserDevice;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    Optional<UserDevice> findByUserAndDeviceId(User user, String deviceId);
    Optional<UserDevice> findByUserAndFingerprint(User user, String fingerprint);
    Optional<UserDevice> findByUserAndFingerprintAndDeviceId(User user, String fingerprint, String deviceId);
}
