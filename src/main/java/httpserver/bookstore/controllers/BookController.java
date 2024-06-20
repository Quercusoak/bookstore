package httpserver.bookstore.controllers;

import java.util.Optional;

import httpserver.bookstore.book.Book;
import httpserver.bookstore.dto.ServerResponse;
import httpserver.bookstore.service.BookService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/book")
public class BookController {
    private static final Logger logger = LogManager.getLogger("books-logger");
    private String NO_SUCH_BOOK(Integer id){
        return "Error: no such Book with id "+id;
    }

    private final BookService store;

    BookController(BookService store) {
        this.store = store;
    }

    // 2. Create a new Book, returns newly assigned Book number
    @PostMapping
    public ResponseEntity<ServerResponse<Integer>> newBook(HttpServletRequest request, @RequestBody Book newBook) {
        HttpStatus status = HttpStatus.CONFLICT;
        String errorMessage = null;
        Integer id = null;

        if (store.getAllBooks().stream().anyMatch(book -> book.getTitle().equalsIgnoreCase(newBook.getTitle()))){
            errorMessage = "Error: Book with the title "+newBook.getTitle()+" already exists in the system";
        }
        else if (newBook.getPrice() <= 0){
            errorMessage = "Error: Can’t create new Book with negative price";
        }
        else if (newBook.getPrintYear() < 1940 || newBook.getPrintYear() > 2100){
            errorMessage = "Error: Can’t create new Book that its year "+newBook.getPrintYear()
                    +" is not in the accepted range [1940-> 2100]";
        }
        else {
            status = HttpStatus.OK;
            id = store.saveBook(newBook).getId();
            logger.info("Creating new Book with Title [{}]", newBook.getTitle());
            logger.debug("Currently there are {} Books in the system. New Book will be assigned with id {}", store.getAllBooks().size()-1, id);
        }

        return ResponseEntity.status(status).body(new ServerResponse<>(id, errorMessage));
    }


    // 5. Gets a single book’s data according to its id
    @GetMapping
    public ResponseEntity<ServerResponse<Book>> getSingleBookData(HttpServletRequest request, @RequestParam Integer id) {

        return store.getBookById(id)
                .map(value -> {
                    logger.debug("Fetching book id {} details", id);
                    return ResponseEntity.ok(new ServerResponse<>(value, null));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServerResponse<>(null, NO_SUCH_BOOK(id))));
    }

    // 6. Updates given book’s price. Query Parameters: id, price. result = old price
    @PutMapping
    public ResponseEntity<ServerResponse<Integer>> updatePrice( HttpServletRequest request,
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "price") Integer price) {

        Optional<Book> book = store.getBookById(id);
        if (book.isPresent()) {
            if (price <= 0) {
                String errorMsg = "Error: price update for book " + id + " must be a positive integer";
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ServerResponse<>(null, errorMsg));
            } else {
                logger.info("Update Book id [{}] price to {}", id, price);
                logger.debug("Book [{}] price change: {} --> {}", book.get().getTitle(), book.get().getPrice() ,price);
                return ResponseEntity.ok(new ServerResponse<>(store.updatePrice(book.get(), price), null));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServerResponse<>(null, NO_SUCH_BOOK(id)));
        }
    }

    // 7. Delete book. result = number of books left in the system
    @DeleteMapping
    public ResponseEntity<ServerResponse<Integer>> deleteBook(HttpServletRequest request, @RequestParam Integer id) {

        return store.getBookById(id)
                .map(book -> {
                    logger.info("Removing book [{}]", book.getTitle());
                    logger.debug("After removing book [{}] id: [{}] there are {} books in the system",book.getTitle(),id,store.getAllBooks().size()-1);
                    return ResponseEntity.ok(new ServerResponse<>(store.deleteBook(book), null));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServerResponse<>(null, NO_SUCH_BOOK(id))));
    }
}
