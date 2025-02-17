package com.dgnl_backend.project.dgnl_backend.schemas;

import java.sql.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "gender_id")
    @JsonIgnore
    private Integer genderId;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "token", nullable = false)
    private Integer token = 0;

    @Column(name = "grade_lv")
    private Integer gradeLv;

    @Column(name = "role_id")
    @JsonIgnore
    private Integer roleId;

    public User(){}

    public User(String username, String password, Integer genderId, Date dob, Integer gradeLv) {
        this.username = username;
        this.password = password;
        this.dob = dob;
        this.gradeLv = gradeLv;
        this.genderId = genderId;
    }
    public User(String username, String password, Integer genderId, Date dob, Integer gradeLv, Integer roleId) {
        this.username = username;
        this.password = password;
        this.dob = dob;
        this.gradeLv = gradeLv;
        this.genderId = genderId;
        this.roleId = roleId;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getGenderId() {
        return genderId;
    }

    public void setGenderId(Integer genderId) {
        this.genderId = genderId;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public Integer getToken() {
        return token;
    }

    public void setToken(Integer token) {
        this.token = token;
    }

    public Integer getGradeLv() {
        return gradeLv;
    }

    public void setGradeLv(Integer gradeLv) {
        this.gradeLv = gradeLv;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}