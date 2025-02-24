package com.dgnl_backend.project.dgnl_backend.schemas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_device", schema = "private")
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "device_id")
    private String deviceId;
    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "trusted")
    private Boolean trusted;

    public UserDevice() {}

    public UserDevice(Long id, User user, String deviceId, String fingerprint, Boolean trusted) {
        this.id = id;
        this.user = user;
        this.deviceId = deviceId;
        this.fingerprint = fingerprint;
        this.trusted = trusted;
    }

    public String getDeviceId(){
        return this.deviceId;
    }

    public void setDeviceId(String deviceId){
        this.deviceId = deviceId;
    }

    public void setUser(User user){
        this.user = user;
    }

    public String getFingerprint(){
        return this.fingerprint;
    }

    public void setFingerprint(String fingerprint){
        this.fingerprint = fingerprint;
    }

    public Boolean getTrusted(){
        return this.trusted;
    }

    public void setTrusted(Boolean trusted){
        this.trusted = trusted;
    }


}
