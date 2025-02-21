package coursework.model;

import coursework.model.enums.Demographic;
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

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Manga extends Publication{

    private String illustrator;
    @Enumerated(EnumType.STRING)
    private Language originalLanguage;
    private int volumeNumber;
    @Enumerated(EnumType.STRING)
    private Demographic demographic;
    private boolean isColor;

    public Manga(String title, String author, LocalDate publicationDate, String illustrator, Language originalLanguage, int volumeNumber, Demographic demographic, boolean isColor) {
        super(title, author, publicationDate);
        this.illustrator = illustrator;
        this.originalLanguage = originalLanguage;
        this.volumeNumber = volumeNumber;
        this.demographic = demographic;
        this.isColor = isColor;
    }

    @Override
    public String toString() {
        return title;
    }
}
