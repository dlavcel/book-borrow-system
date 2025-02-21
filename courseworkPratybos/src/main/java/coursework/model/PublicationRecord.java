package coursework.model;

import coursework.model.enums.PublicationStatus;
import jakarta.persistence.*;
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
public class PublicationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne
    private Client user;
    @ManyToOne
    private Publication publication;
    private LocalDate transactionDate;
    @Enumerated(EnumType.STRING)
    private PublicationStatus status;

    public PublicationRecord(Client user, Publication publication, PublicationStatus status) {
        this.user = user;
        this.publication = publication;
        this.status = status;
        this.transactionDate = LocalDate.now();
    }

    public PublicationRecord(int id, Client user, Publication publication, LocalDate transactionDate, LocalDate returnDate, PublicationStatus status) {
        this.id = id;
        this.user = user;
        this.publication = publication;
        this.transactionDate = transactionDate;
        this.status = status;
    }

    public PublicationRecord(Client user, Publication publication, LocalDate transactionDate, LocalDate returnDate, PublicationStatus status) {
        this.user = user;
        this.publication = publication;
        this.transactionDate = transactionDate;
        this.status = status;
    }
}
