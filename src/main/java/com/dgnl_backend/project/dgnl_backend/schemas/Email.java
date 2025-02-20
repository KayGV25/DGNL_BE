package com.dgnl_backend.project.dgnl_backend.schemas;


public record Email(
    String recipient,
    String subject,
    String content
) {

}
