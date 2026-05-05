package com.example.bookstore.auth.dto;

public record LoginRequest(
        String username,
        String password
) {
}
