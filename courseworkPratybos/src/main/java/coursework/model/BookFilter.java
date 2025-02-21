package coursework.model;

import coursework.model.enums.PublicationStatus;
import coursework.model.enums.PublicationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookFilter {
    PublicationType publicationType;
    PublicationStatus status;
    String author;
    String title;
    LocalDate publicationDate;

    @Override
    public String toString() {
        return publicationType + " " + author + " " + title + " " + publicationDate;
    }
}
