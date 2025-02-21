package coursework.model;

import coursework.model.enums.Format;
import coursework.model.enums.Genre;
import coursework.model.enums.Language;
import coursework.model.enums.PublicationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

//You can generate getters and setters, here Lombok lib is used. They are generated for you
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Book extends Publication{

    //Variables should be private and their values retrieved and changed using getters and setters respectively

    private String publisher;
    private String isbn;
    @Enumerated(EnumType.STRING)
    private Genre genre;
    private int pageCount;
    @Enumerated(EnumType.STRING)
    private Language language;
    @Enumerated(EnumType.STRING)
    private Format format; // Hardcover, Paperback, Digital, etc.
    private String summary;

    public Book(String title, String author, LocalDate publicationDate, String publisher, String isbn, Genre genre, int pageCount, Language language, Format format, String summary) {
        super(title, author, publicationDate);
        this.publisher = publisher;
        this.isbn = isbn;
        this.genre = genre;
        this.pageCount = pageCount;
        this.language = language;
        this.format = format;
        this.summary = summary;
    }

    @Override
    public String toString() {
        return title;
    }
}
