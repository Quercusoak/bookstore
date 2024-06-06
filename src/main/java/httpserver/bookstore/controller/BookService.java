package httpserver.bookstore.controller;

import httpserver.bookstore.book.Book;
import httpserver.bookstore.book.Genre;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BookService {

    private static final Map<Integer, Book> store = new HashMap<>();

    public List<Book> getAllBooks() {
        return store.values().stream().toList();
    }

    public Optional<Book> getBookById(Integer id) {
        return Optional.ofNullable(store.get(id));
    }

    public Book saveBook(Book book) {
        Book newBook = new Book(book);
        store.put(newBook.getId(), newBook);
        return store.get(newBook.getId());
    }

    /* Deletes book. returns number of books left in the system*/
    public Integer deleteBook(Book book) {
        store.remove(book.getId());
        return store.size();
    }

    public List<Book> getBooksByFilters(Optional<String> author, Optional<Integer> priceMin, Optional<Integer> priceMax,
            Optional<Integer> yearMin, Optional<Integer> yearMax, Optional<List<Genre>> genres){
        return store.values().stream()
                .filter(b-> author.map(s -> b.getAuthor().equalsIgnoreCase(s)).orElse(true))
                .filter(b-> priceMin.map(x-> b.getPrice() >= x).orElse(true))
                .filter(b-> priceMax.map(integer -> b.getPrice() <= integer).orElse(true))
                .filter(b-> yearMin.map(value -> b.getPrintYear() >= value).orElse(true))
                .filter(b-> yearMax.map(integer1 -> b.getPrintYear() <= integer1).orElse(true))
                .filter(b-> genres.map(x -> b.getGenre().stream().anyMatch(x::contains)).orElse(true))
                .sorted((a,b) -> a.getTitle().compareToIgnoreCase(b.getTitle()))
                .toList();
    }

    // Updates to new (positive) price and returns the old
    public Integer updatePrice(Book book, Integer price){
        Integer oldPrice = book.getPrice();
        book.setPrice(price);
        store.put(book.getId(), book);
        return oldPrice;
    }
}
