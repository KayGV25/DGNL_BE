package com.dgnl_backend.project.dgnl_backend.dtos.user.request;

public record NewUserDTO(
    String username,
    String email,
    String password,
    Integer genderId,
    Integer yob,
    Integer mob,
    Integer dob,
    Integer gradeLv,
    Integer roleId
) {

}
