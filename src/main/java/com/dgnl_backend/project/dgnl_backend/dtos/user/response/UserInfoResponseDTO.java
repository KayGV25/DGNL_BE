package com.dgnl_backend.project.dgnl_backend.dtos.user.response;

import java.util.Date;

public record UserInfoResponseDTO(
    String username,
    String gender,
    Date dob,
    Integer tokens,
    Integer gradeLv,
    String role
) {

}
