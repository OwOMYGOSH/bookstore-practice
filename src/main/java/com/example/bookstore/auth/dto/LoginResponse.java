package com.example.bookstore.auth.dto;

public record LoginResponse(
        String token,
        Long expiresIn
) {
}
