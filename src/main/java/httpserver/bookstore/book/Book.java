package httpserver.bookstore.book;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public class Book {

    private static Integer count = 0;

    private final Integer id; //global counter, starts from 1
    @JsonProperty("title")
    private String title; //Can contain spaces
    @JsonProperty("author")
    private String author; //Can contain spaces
    @JsonProperty("year")
    private Integer printYear; //must be 4-digits.
    @JsonProperty("price")
    private Integer price; //positive int

    @JsonProperty("genres")
    private List<Genre> genre;

    public Book(String title, String author, Integer printYear, Integer price, List<Genre> genre) {
        this.id = 0;
        this.title = title;
        this.author = author;
        this.printYear = printYear;
        this.price = price;
        this.genre = genre;
    }

    public Book(Book b){
        this.id = ++count;
        this.title= b.getTitle();
        this.author =b.getAuthor();
        this.printYear =b.getPrintYear();
        this.price =b.getPrice();
        this.genre =b.getGenre();
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Integer getPrintYear() {
        return printYear;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public List<Genre> getGenre() {
        return genre;
    }
}
