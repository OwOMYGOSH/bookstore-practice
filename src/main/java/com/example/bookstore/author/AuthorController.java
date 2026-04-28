package com.example.bookstore.author;

import com.example.bookstore.book.Book;
import com.example.bookstore.book.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Author> getAuthors() {
        return authorService.getAllAuthors();
    }

    @GetMapping("{authorId}")
    public Author getAuthor(@PathVariable Long authorId) {
        return authorService.getAuthorById(authorId);
    }

    @GetMapping("{authorId}/books")
    public List<Book> getBooks(@PathVariable Long authorId) {
        return bookService.getBooksByAuthorId(authorId);
    }

    @PostMapping
    public Author createAuthor(@Valid @RequestBody Author author) {
        return authorService.createAuthor(author);
    }

    @PutMapping("{authorId}")
    public Author updateAuthor(@PathVariable Long authorId, @Valid @RequestBody Author author) {
        return authorService.updateAuthor(authorId, author);
    }

}
