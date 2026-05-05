package com.example.bookstore.author.dto;


import java.time.LocalDateTime;

public record AuthorResponse(
        Long id,
        String name,
        String bio,
        LocalDateTime createdAt
) {
}
