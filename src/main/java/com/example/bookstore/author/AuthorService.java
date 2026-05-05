package com.example.bookstore.author;

import com.example.bookstore.author.dto.AuthorRequest;
import com.example.bookstore.author.dto.AuthorResponse;
import com.example.bookstore.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public AuthorResponse getAuthorById(Long authorId) {
        return toResponse(findAuthorOrThrow(authorId));
    }

    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = new Author();

        applyToAuthor(author, request);
        authorRepository.save(author);
        return toResponse(author);
    }

    public AuthorResponse updateAuthor(Long authorId, AuthorRequest request) {
        Author existAuthor = findAuthorOrThrow(authorId);

        applyToAuthor(existAuthor, request);
        authorRepository.save(existAuthor);
        return toResponse(existAuthor);
    }

    private Author findAuthorOrThrow(Long authorId) {
        return authorRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Author " + authorId + " not found"));
    }

    private void applyToAuthor(Author author, AuthorRequest request) {
        author.setName(request.name());
        author.setBio(request.bio());
    }

    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName(),
                author.getBio(),
                author.getCreatedAt()
        );
    }
}
