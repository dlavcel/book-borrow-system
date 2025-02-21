package coursework.model;

import coursework.fxControllers.Main;
import coursework.model.enums.PublicationStatus;
import coursework.model.enums.PublicationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
@Entity
public abstract class Publication implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    protected String title;
    protected String author;
    protected LocalDate publicationDate;

    @ManyToOne
    protected Client owner;
    @ManyToOne
    protected Client client;
//    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
//    protected List<PublicationRecord> records;
    @JoinColumn(name = "status")
    @Enumerated(EnumType.STRING)
    protected PublicationStatus publicationStatus;
//    protected LocalDate requestDate;

    public Publication(String title, String author, LocalDate publicationDate) {
        this.owner = (Client) Main.getCurrentUser();
        this.title = title;
        this.author = author;
        this.publicationDate = publicationDate;
        this.publicationStatus = PublicationStatus.AVAILABLE;
    }
}