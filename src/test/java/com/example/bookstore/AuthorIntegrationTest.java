package com.example.bookstore;

import com.example.bookstore.auth.dto.LoginRequest;
import com.example.bookstore.auth.dto.LoginResponse;
import com.example.bookstore.author.Author;
import com.example.bookstore.author.AuthorRepository;
import com.example.bookstore.author.dto.AuthorRequest;
import com.example.bookstore.author.dto.AuthorResponse;
import com.example.bookstore.book.Book;
import com.example.bookstore.book.BookRepository;
import com.example.bookstore.book.dto.BookRequest;
import com.example.bookstore.book.dto.BookResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) //
@Import(TestcontainersConfiguration.class)
class AuthorIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void cleanDb() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    /**
     * Helper: 用 admin & user 帳號登入拿 token
     */
    String tokenFor(String username, String password) {
        var body = new LoginRequest(username, password);
        ResponseEntity<LoginResponse> resp = rest.postForEntity("/auth/login", body, LoginResponse.class);
        assertThat(resp.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(resp.getBody()).isNotNull();
        return resp.getBody().token();
    }

    String adminToken() {
        return tokenFor("admin", "admin123");
    }

    String userToken() {
        return tokenFor("user", "user123");
    }

    /**
     * Helper: 包成 Authorization Bearer header
     */
    HttpHeaders bearerAuth(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void createAuthor_thenGet_dataMatches() {
        HttpHeaders h = bearerAuth(adminToken());
        var req = new AuthorRequest("J.K. Rowling", "British author");

        // POST 用 exchange
        ResponseEntity<AuthorResponse> created =
                rest.exchange("/authors", HttpMethod.POST, new HttpEntity<>(req, h), AuthorResponse.class);
        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        AuthorResponse createdBody = created.getBody();
        assertThat(createdBody).isNotNull();

        // GET 不用塞 Header
        ResponseEntity<AuthorResponse> got =
                rest.getForEntity("/authors/" + createdBody.id(), AuthorResponse.class);

        assertThat(got.getStatusCode().is2xxSuccessful()).isTrue();
        AuthorResponse gotBody = got.getBody();
        assertThat(gotBody).isNotNull();
        assertThat(gotBody.id()).isEqualTo(createdBody.id());
        assertThat(gotBody.name()).isEqualTo("J.K. Rowling");
        assertThat(gotBody.bio()).isEqualTo("British author");
    }

    @Test
    void createBook_withValidAuthorId_returns201AndData() {
        Author saved = saveAuthor("J.K. Rowling");

        var req = new BookRequest(
                "Harry Potter",
                "978-0-7475-3269-9",
                new BigDecimal("19.99"),
                100,
                LocalDate.of(1997, 6, 26),
                saved.getId()
        );

        HttpHeaders h = bearerAuth(adminToken());
        ResponseEntity<BookResponse> created =
                rest.exchange("/books", HttpMethod.POST, new HttpEntity<>(req, h), BookResponse.class);

        assertThat(created.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        BookResponse createdBody = created.getBody();
        assertThat(createdBody).isNotNull();
        assertThat(createdBody.title()).isEqualTo("Harry Potter");
        assertThat(createdBody.isbn()).isEqualTo("978-0-7475-3269-9");
        assertThat(createdBody.price()).isEqualByComparingTo("19.99");  // BigDecimal 比較專用
        assertThat(createdBody.stock()).isEqualTo(100);
        assertThat(createdBody.authorId()).isEqualTo(saved.getId());
        assertThat(createdBody.authorName()).isEqualTo("J.K. Rowling");
        assertThat(createdBody.id()).isNotNull();
    }

    @Test
    void createBook_withInvalidAuthorId_returns404() {
        HttpHeaders h = bearerAuth(adminToken());
        long ghostId = 99999L;

        var req = new BookRequest("X", "111", new BigDecimal("1.00"), 1, null, ghostId);

        ResponseEntity<Map> resp =
                rest.exchange("/books", HttpMethod.POST, new HttpEntity<>(req, h), Map.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createBook_withInvalidBookTitle_returns422() {
        Author saved = saveAuthor("Tester");
        HttpHeaders h = bearerAuth(adminToken());
        var req = new BookRequest(null, "111", new BigDecimal("1.00"), 1, null, saved.getId());

        ResponseEntity<Map> resp =
                rest.exchange("/books", HttpMethod.POST, new HttpEntity<>(req, h), Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        @SuppressWarnings("unchecked")
        Map<String, String> fieldErrors = (Map<String, String>) resp.getBody().get("fieldErrors");
        assertThat(fieldErrors).containsKey("title");
    }

    @Test
    void postAuthor_withoutJwt_returns401() {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);

        var req = new AuthorRequest("Anyone", null);

        ResponseEntity<Map> resp =
                rest.exchange("/authors", HttpMethod.POST, new HttpEntity<>(req, h), Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void postAuthor_withUserRole_returns403() {
        HttpHeaders h = bearerAuth(userToken());
        var req = new AuthorRequest("Anyone", null);

        ResponseEntity<Map> resp =
                rest.exchange("/authors", HttpMethod.POST, new HttpEntity<>(req, h), Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(resp.getBody()).containsEntry("status", 403);
    }

    @Test
    void deleteBook_isHiddenFromGetBooks() {
        Author author = saveAuthor("Author");
        Book b1 = saveBook(author, "Book1", "111");
        Book b2 = saveBook(author, "Book2", "222");

        HttpHeaders h = bearerAuth(adminToken());
        ResponseEntity<Void> deleted =
                rest.exchange("/books/" + b1.getId(), HttpMethod.DELETE, new HttpEntity<>(h), Void.class);
        assertThat(deleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // assert：GET /books 只回 b2
        ResponseEntity<BookResponse[]> list =
                rest.getForEntity("/books", BookResponse[].class);
        assertThat(list.getBody()).hasSize(1);
        assertThat(list.getBody()[0].id()).isEqualTo(b2.getId());

        // assert：看看 Book1 是不是真軟刪除
        Book stillThere = bookRepository.findById(b1.getId()).orElseThrow();
        assertThat(stillThere.isActive()).isFalse();
    }

    @Test
    void login_withValidCredentials_returnsValidJwt() {
        var body = new LoginRequest("admin", "admin123");

        ResponseEntity<LoginResponse> resp =
                rest.postForEntity("/auth/login", body, LoginResponse.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        LoginResponse loginBody = resp.getBody();
        assertThat(loginBody).isNotNull();

        assertThat(loginBody.token()).isNotBlank();
        assertThat(loginBody.token().split("\\.")).hasSize(3); // JWT 三段結構
        assertThat(loginBody.expiresIn()).isPositive(); // expiresIn 是正數

        // Bonus：以下是完整的端到端驗證
        HttpHeaders h = bearerAuth(loginBody.token());
        ResponseEntity<AuthorResponse> probe =
                rest.exchange("/authors", HttpMethod.POST, new HttpEntity<>(new AuthorRequest("X", null), h), AuthorResponse.class);
        assertThat(probe.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }


    private Author saveAuthor(String name) {
        Author author = new Author();
        author.setName(name);
        return authorRepository.save(author);
    }

    private Book saveBook(Author author, String title, String isbn) {
        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setPrice(new BigDecimal("1"));
        book.setStock(1);
        book.setAuthor(author);
        return bookRepository.save(book);
    }

}