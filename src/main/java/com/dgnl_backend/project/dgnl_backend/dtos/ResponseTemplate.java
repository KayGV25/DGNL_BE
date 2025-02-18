package com.dgnl_backend.project.dgnl_backend.dtos;

public record ResponseTemplate<T>(T data, String message) {

}
