package com.example.bookstore.author;

import com.example.bookstore.author.dto.AuthorRequest;
import com.example.bookstore.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Author getAuthorById(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author " + authorId + " not found"));
    }

    public Author createAuthor(AuthorRequest request) {
        Author author = new Author();

        author.setName(request.getName());
        author.setBio(request.getBio());
        return authorRepository.save(author);
    }

    public Author updateAuthor(Long authorId, AuthorRequest request) {
        Author existAuthor = getAuthorById(authorId);

        existAuthor.setName(request.getName());
        existAuthor.setBio(request.getBio());
        return authorRepository.save(existAuthor);
    }
}
