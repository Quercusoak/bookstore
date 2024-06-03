package httpserver.bookstore.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Book {

    private static Integer count = 0;

    private @Id
    @GeneratedValue Integer id; //global counter, starts from 1
    @JsonProperty("title")
    private String title; //Can contain spaces
    @JsonProperty("author")
    private String author; //Can contain spaces
    @JsonProperty("year")
    private Integer printYear; //must be 4-digits.
    @JsonProperty("price")
    private Integer price; //positive int

    @JsonProperty("genres")
    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    private List<Genre> genre;

    public Book(String title,String author,Integer printYear, Integer price, List<Genre> genre){
        this.id = ++count;
        this.title= title;
        this.author =author;
        this.printYear =printYear;
        this.price =price;
        this.genre =genre;
    }

    public Book(){}


    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPrintYear() {
        return printYear;
    }

    public void setPrintYear(Integer printYear) {
        this.printYear = printYear;
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

    public void setGenre(List<Genre> genre) {
        this.genre = genre;
    }
}
