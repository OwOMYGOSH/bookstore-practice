package com.example.bookstore.book.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookResponse(Long id,
                           String title,
                           String isbn,
                           BigDecimal price,
                           Integer stock,
                           LocalDate publishedAt,
                           boolean isActive,
                           Long authorId,
                           String authorName
) {
}
