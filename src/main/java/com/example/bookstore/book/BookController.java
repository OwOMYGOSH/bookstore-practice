package com.example.bookstore.book;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("{bookId}")
    public Book getBookById(@PathVariable Long bookId) {
        return bookService.getBookById(bookId);
    }

    @PutMapping("{bookId}")
    public Book updateBook(@PathVariable Long bookId, @Valid @RequestBody Book newbook) {
        return bookService.updateBook(bookId, newbook);
    }

    @DeleteMapping("{bookId}")
    public void deleteBookById(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
    }
}
