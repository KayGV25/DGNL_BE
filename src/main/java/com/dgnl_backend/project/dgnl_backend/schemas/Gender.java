package com.dgnl_backend.project.dgnl_backend.schemas;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "gender", schema = "public")
public class Gender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false, unique = true)
    private Integer id;

    @Column(name = "gender_type", nullable = false, length = 10)
    private String genderType;

    public Gender() {}

    public Integer getId(){
        return this.id;
    }
    public String getGenderType() {
        return genderType;
    }
    public void setId(Integer id){
        this.id = id;
    }
    public void setGenderType(String genderType) {
        this.genderType = genderType;
    }
}
