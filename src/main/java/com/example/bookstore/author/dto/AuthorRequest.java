package com.example.bookstore.author.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(
        @NotBlank String name,
        String bio
) {
}
