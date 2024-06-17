package httpserver.bookstore.controller;

import java.util.Optional;

import httpserver.bookstore.book.Book;
import httpserver.bookstore.dto.ServerResponse;
import httpserver.bookstore.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/book")
public class BookStoreController {
    private static Logger log = LogManager.getLogger(BookStoreController.class);
    private static String REQUEST_LOG_MESSAGE = "Incoming request | #{request number} | resource: {resource name} | HTTP Verb {HTTP VERB in capital letter (GET, POST, etc)}";
    private final static String NO_SUCH_BOOK = "Error: no such Book with id ";

    private final BookService store;

    BookStoreController(BookService store) {
        this.store = store;
    }

    // 2. Create a new Book, returns newly assigned Book number
    @PostMapping
    public ResponseEntity<ServerResponse<Integer>> newBook(@RequestBody Book newBook) {
        HttpStatus status = HttpStatus.CONFLICT;
        String msg = null;
        Integer id = null;

        if (store.getAllBooks().stream().anyMatch(book -> book.getTitle().equalsIgnoreCase(newBook.getTitle()))){
            msg = "Error: Book with the title "+newBook.getTitle()+" already exists in the system";
        }
        else if (newBook.getPrice() <= 0){
            msg = "Error: Can’t create new Book with negative price";
        }
        else if (newBook.getPrintYear() < 1940 || newBook.getPrintYear() > 2100){
            msg = "Error: Can’t create new Book that its year "+newBook.getPrintYear()
                    +" is not in the accepted range [1940-> 2100]";
        }
        else {
            status = HttpStatus.OK;
            id = store.saveBook(newBook).getId();
        }

        return ResponseEntity.status(status).body(new ServerResponse<>(id, msg));
    }


    // 5. Gets a single book’s data according to its id
    @GetMapping
    public ResponseEntity<ServerResponse<Book>> getSingleBookData(@RequestParam Integer id) {

        return store.getBookById(id)
                .map(value -> ResponseEntity.ok(new ServerResponse<>(value, null)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ServerResponse<>(null, NO_SUCH_BOOK + id)));
    }

    // 6. Updates given book’s price. Query Parameters: id, price. result = old price
    @PutMapping
    public ResponseEntity<ServerResponse<Integer>> updatePrice(
            @RequestParam(name = "id") Integer id,
            @RequestParam(name = "price") Integer price) {

        Optional<Book> book = store.getBookById(id);
        if (book.isPresent()) {
            if (price <= 0) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ServerResponse<>(null, "Error: price update for book " + id + " must be a positive integer"));
            } else {
                return ResponseEntity.ok(new ServerResponse<>(store.updatePrice(book.get(), price), null));
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServerResponse<>(null, NO_SUCH_BOOK + id));
        }
    }

    // 7. Delete book. result = number of books left in the system
    @DeleteMapping
    public ResponseEntity<ServerResponse<Integer>> deleteBook(@RequestParam Integer id) {

        return store.getBookById(id)
                .map(value -> ResponseEntity
                        .ok(new ServerResponse<>(store.deleteBook(value), null)))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ServerResponse<>(null, NO_SUCH_BOOK + id)));


    }
}
