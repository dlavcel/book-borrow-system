package coursework.model;

import coursework.model.enums.Frequency;
import coursework.model.enums.PublicationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Periodical extends Publication {

    private int issueNumber;
    private String editor;
    @Enumerated(EnumType.STRING)
    private Frequency frequency;
    private String publisher;

    public Periodical(String title, String author, LocalDate publicationDate, int issueNumber, String editor, Frequency frequency, String publisher) {
        super(title, author, publicationDate);
        this.issueNumber = issueNumber;
        this.editor = editor;
        this.frequency = frequency;
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return title;
    }
}
