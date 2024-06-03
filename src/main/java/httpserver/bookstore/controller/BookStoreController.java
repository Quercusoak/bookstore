package httpserver.bookstore.controller;

import java.util.List;
import java.util.Optional;

import httpserver.bookstore.book.Book;
import httpserver.bookstore.book.Genre;
import httpserver.bookstore.dto.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookStoreController {
    private final static String NO_SUCH_BOOK = "Error: no such Book with id ";

    private final BookService store;

    BookStoreController(BookRepository repository) {
        this.store = new BookService(repository);
    }

    // 1. Get health
    @GetMapping("/books/health")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok("OK");
    }

    // 2. Create a new Book, returns newly assigned Book number
    @PostMapping("/book")
    public ResponseEntity<ServerResponse<Integer>> newBook(@RequestBody Book newBook) {

        HttpStatus status = HttpStatus.CONFLICT;
        String msg = null;
        Integer id = null;

        if (store.getAllBooks().stream().anyMatch(book -> book.getTitle().equalsIgnoreCase(newBook.getTitle()))){
            msg = "Error: Book with the title "+newBook.getTitle()+" already exists in the system";
            //return ResponseEntity.status(HttpStatus.CONFLICT).body(new ServerResponse<>(null, msg));
        }
        else if (newBook.getPrice() <= 0){
            msg = "Error: Can’t create new Book with negative price";
            //return ResponseEntity.status(HttpStatus.CONFLICT).body(new ServerResponse<>(null, msg));
        }
        else if (newBook.getPrintYear() < 1940 || newBook.getPrintYear() > 2100){
            msg = "Error: Can’t create new Book that its year "+newBook.getPrintYear()
                    +" is not in the accepted range [1940-> 2100]";
            //return ResponseEntity.status(HttpStatus.CONFLICT).body(new ServerResponse<>(null, msg));
        }
        else {
            status = HttpStatus.OK;
            id = store.saveBook(newBook).getId();
        }

        return ResponseEntity.status(status).body(new ServerResponse<>(id, msg));
    }

    // 3. Returns the total number of Books in the system, according to optional filters.
    // All filters optional, if none submitted then returns all books.
    @GetMapping("/books/total")
    public ResponseEntity<ServerResponse<Integer>> getTotal(
            @RequestParam(name = "author",required = false) Optional<String> author,
            @RequestParam(name = "price-bigger-than" ,required = false) Optional<Integer> priceMin,
            @RequestParam(name = "price-less-than" ,required = false) Optional<Integer> priceMax,
            @RequestParam(name = "year-bigger-than" ,required = false) Optional<Integer> yearMin,
            @RequestParam(name = "year-less-than" ,required = false) Optional<Integer> yearMax,
            @RequestParam(name = "genres" ,required = false) Optional<List<Genre>> genres) {

        Integer books = store.getBooksByFilters(author, priceMin, priceMax, yearMin, yearMax, genres).size();

        return ResponseEntity.ok(new ServerResponse<>(books, null));
    }

    // 4. Returns the content of the books according to the given filters as described by the total endpoint.
    // All filters optional, if none submitted then returns all books.
    @GetMapping("/books")
    public ResponseEntity<ServerResponse<List<Book>>> getBooksData(
            @RequestParam(name = "author",required = false) Optional<String> author,
            @RequestParam(name = "price-bigger-than" ,required = false) Optional<Integer> priceMin,
            @RequestParam(name = "price-less-than" ,required = false) Optional<Integer> priceMax,
            @RequestParam(name = "year-bigger-than" ,required = false) Optional<Integer> yearMin,
            @RequestParam(name = "year-less-than" ,required = false) Optional<Integer> yearMax,
            @RequestParam(name = "genres" ,required = false) Optional<List<Genre>> genres) {

        List<Book> books = store.getBooksByFilters(author, priceMin, priceMax, yearMin, yearMax, genres);

        return ResponseEntity.ok(new ServerResponse<>(books, null));
    }

    // 5. Gets a single book’s data according to its id
    @GetMapping("/book")
    public ResponseEntity<ServerResponse<Book>> getSingleBookData(@RequestParam Integer id) {

        return store.getBookById(id)
                .map(value -> ResponseEntity.ok(new ServerResponse<>(value, null)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ServerResponse<>(null, NO_SUCH_BOOK + id)));
    }

    // 6. Updates given book’s price. Query Parameters: id, price. result = old price
    @PutMapping("/book")
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


/*        return store.getBookById(id)
                .map(b->
                     (price <= 0) ?
                        ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(new ServerResponse<>(0, "Error: price update for book " + id + " must be a positive integer"))
                    :
                        ResponseEntity.ok(new ServerResponse<>(store.updatePrice(b, price), null))
                )
                .orElseGet(() ->
                    ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ServerResponse<>(null, NO_SUCH_BOOK + id)));*/
    }

    // 7. Delete book. result = number of books left in the system
    @DeleteMapping("/book")
    public ResponseEntity<ServerResponse<Integer>> deleteBook(@RequestParam Integer id) {

        return store.getBookById(id)
                .map(value -> ResponseEntity
                        .ok(new ServerResponse<>(store.deleteBook(value), null)))
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new ServerResponse<>(null, NO_SUCH_BOOK + id)));


    }
}
