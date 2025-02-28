package com.dgnl_backend.project.dgnl_backend.schemas.identity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles", schema = "identity")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, unique = true, nullable = false)
    private Integer id;

    @Column(name = "role_name", nullable = false, length = 20)
    private String roleName;

    public Role() {}

    public String getRoleName() {
        return roleName;
    }
}
