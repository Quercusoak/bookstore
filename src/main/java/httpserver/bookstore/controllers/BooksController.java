package httpserver.bookstore.controllers;

import java.util.List;
import java.util.Optional;

import httpserver.bookstore.book.Book;
import httpserver.bookstore.book.Genre;
import httpserver.bookstore.dto.ServerResponse;
import httpserver.bookstore.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/books")
public class BooksController {
    private static final Logger logger = LogManager.getLogger("books-logger");

    private final BookService store;

    BooksController(BookService store) {
        this.store = store;
    }

    // 1. Get health
    @GetMapping("/health")
    public ResponseEntity<String> health(HttpServletRequest request){
        return ResponseEntity.ok("OK");
    }

    // 3. Returns the total number of Books in the system, according to optional filters.
    // All filters optional, if none submitted then returns all books.
    @GetMapping("/total")
    public ResponseEntity<ServerResponse<Integer>> getTotal(HttpServletRequest request,
            @RequestParam(name = "author",required = false) Optional<String> author,
            @RequestParam(name = "price-bigger-than" ,required = false) Optional<Integer> priceMin,
            @RequestParam(name = "price-less-than" ,required = false) Optional<Integer> priceMax,
            @RequestParam(name = "year-bigger-than" ,required = false) Optional<Integer> yearMin,
            @RequestParam(name = "year-less-than" ,required = false) Optional<Integer> yearMax,
            @RequestParam(name = "genres" ,required = false) Optional<List<Genre>> genres) {

        Integer num_books = store.getBooksByFilters(author, priceMin, priceMax, yearMin, yearMax, genres).size();
        logger.info("Total Books found for requested filters is {}",num_books);

        return ResponseEntity.ok(new ServerResponse<>(num_books, null));
    }

    // 4. Returns the content of the books according to the given filters as described by the total endpoint.
    // All filters optional, if none submitted then returns all books.
    @GetMapping
    public ResponseEntity<ServerResponse<List<Book>>> getBooksData(HttpServletRequest request,
            @RequestParam(name = "author",required = false) Optional<String> author,
            @RequestParam(name = "price-bigger-than" ,required = false) Optional<Integer> priceMin,
            @RequestParam(name = "price-less-than" ,required = false) Optional<Integer> priceMax,
            @RequestParam(name = "year-bigger-than" ,required = false) Optional<Integer> yearMin,
            @RequestParam(name = "year-less-than" ,required = false) Optional<Integer> yearMax,
            @RequestParam(name = "genres" ,required = false) Optional<List<Genre>> genres) {

        List<Book> books = store.getBooksByFilters(author, priceMin, priceMax, yearMin, yearMax, genres);
        logger.info("Total Books found for requested filters is {}",books.size());

        return ResponseEntity.ok(new ServerResponse<>(books, null));
    }

}
