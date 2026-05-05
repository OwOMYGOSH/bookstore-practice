package com.example.bookstore.author;

import com.example.bookstore.author.dto.AuthorRequest;
import com.example.bookstore.author.dto.AuthorResponse;
import com.example.bookstore.book.Book;
import com.example.bookstore.book.BookService;
import com.example.bookstore.book.dto.BookResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<AuthorResponse> getAuthors() {
        return authorService.getAllAuthors();
    }

    @GetMapping("{authorId}")
    public AuthorResponse getAuthor(@PathVariable Long authorId) {
        return authorService.getAuthorById(authorId);
    }

    @GetMapping("{authorId}/books")
    public List<BookResponse> getBooks(@PathVariable Long authorId) {
        return bookService.getBooksByAuthorId(authorId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorResponse createAuthor(@Valid @RequestBody AuthorRequest request) {
        return authorService.createAuthor(request);
    }

    @PutMapping("{authorId}")
    public AuthorResponse updateAuthor(@PathVariable Long authorId, @Valid @RequestBody AuthorRequest request) {
        return authorService.updateAuthor(authorId, request);
    }

}
