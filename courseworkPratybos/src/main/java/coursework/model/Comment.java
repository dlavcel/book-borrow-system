package coursework.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String body;
    private LocalDateTime timestamp;
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<Comment> replies;
    @ManyToOne
    private Comment parentComment;
    @ManyToOne
    private Client client;
    @ManyToOne
    private Chat chat;
    @ManyToOne
    private Client commentOwner;

    public Comment(String title, String body, LocalDateTime timestamp, Chat chat) {
        this.title = title;
        this.body = body;
        this.timestamp = timestamp;
        this.chat = chat;
        this.timestamp = LocalDateTime.now();
    }

    public Comment(String body, Comment parentComment, Chat chat, Client commentOwner) {
        this.body = body;
        this.parentComment = parentComment;
        this.chat = chat;
        this.commentOwner = commentOwner;
        this.timestamp = LocalDateTime.now();
    }

    public Comment(String body, Client client, Chat chat, Client commentOwner) {
        this.body = body;
        this.client = client;
        this.chat = chat;
        this.commentOwner = commentOwner;
        this.timestamp = LocalDateTime.now();
    }

    public Comment(String title, String body, Comment parentComment, Chat chat, Client commentOwner) {
        this.title = title;
        this.body = body;
        this.parentComment = parentComment;
        this.chat = chat;
        this.commentOwner = commentOwner;
    }

    public Comment(String title, String body, Client client, Chat chat, Client commentOwner) {
        this.title = title;
        this.body = body;
        this.client = client;
        this.chat = chat;
        this.commentOwner = commentOwner;
    }

    @Override
    public String toString() {
        return body;
    }
}
