package coursework.fxControllers;

import coursework.hibernateControllers.GenericHibernate;
import coursework.model.Chat;
import coursework.model.Comment;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML
    public ListView<Chat> chatListField;
    @FXML
    public ListView<Comment> commentListField;
    @FXML
    public TextField chatTitleField;
    @FXML
    public TextField commentTitleField;
    @FXML
    public TextArea commentArea;

    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");
    GenericHibernate hibernate = new GenericHibernate(entityManagerFactory);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fillChatList();
    }

    public void createNewChat() {
        if (chatTitleField.getText().isEmpty()) {
            showAlert("Error", "Title is required!");
            return;
        }

        Chat chat = new Chat(chatTitleField.getText());
        hibernate.create(chat);
        fillChatList();
        clearChatInput();
    }

    public void fillChatList() {
        chatListField.getItems().clear();
        List<Chat> chatList = hibernate.getAllRecords(Chat.class);
        chatListField.getItems().addAll(chatList);
    }

    public void deleteChat() {
        Chat selectedChat = chatListField.getSelectionModel().getSelectedItem();
        if (selectedChat == null) {
            showAlert("Error", "You must select a chat first!");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete the selected chat?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            hibernate.delete(Chat.class, selectedChat.getId());
            fillChatList();
            commentListField.getItems().clear();
            //clearChatInput();
        }
    }

    public void loadChatData() {
        Chat selectedChat = chatListField.getSelectionModel().getSelectedItem();

        if (selectedChat != null) {
            commentListField.getItems().clear();
            List<Comment> commentList = hibernate.getAllRecords(Comment.class);

            List<Comment> filteredComments = commentList.stream()
                    .filter(comment -> comment.getChat().getId() == selectedChat.getId())
                    .toList();

            commentListField.getItems().addAll(filteredComments);
        }
    }

    public void addComment() {
        Chat selectedChat = chatListField.getSelectionModel().getSelectedItem();
        if (selectedChat == null) {
            showAlert("Error", "You must select a chat first!");
            return;
        }

        if (commentTitleField.getText().isEmpty()) {
            showAlert("Error", "Title is required!");
            return;
        }
        if (commentArea.getText().isEmpty()) {
            showAlert("Error", "Comment is required!");
            return;
        }

        Comment comment = new Comment(
                commentTitleField.getText(),
                commentArea.getText(),
                LocalDateTime.now(),
                selectedChat);

        hibernate.create(comment);
        fillCommentList();
        clearCommentInput();
    }

    public void loadCommentData() {
        Comment selectedComment = commentListField.getSelectionModel().getSelectedItem();
        if (selectedComment != null) {
            commentTitleField.setText(selectedComment.getTitle());
            commentArea.setText(selectedComment.getBody());
        }
    }

    public void updateComment() {
        Comment selectedComment = commentListField.getSelectionModel().getSelectedItem();
        if (selectedComment == null) {
            showAlert("Error", "You must select a comment first!");
            return;
        }

        Comment commentFromDb = hibernate.getEntityById(Comment.class, selectedComment.getId());
        commentFromDb.setTitle(commentTitleField.getText());
        commentFromDb.setBody(commentArea.getText());
        hibernate.update(commentFromDb);
        fillCommentList();
        clearCommentInput();
    }

    public void deleteComment() {
        Comment selectedComment = commentListField.getSelectionModel().getSelectedItem();

        if (selectedComment == null) {
            showAlert("Error", "You must select a comment first!");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete the selected comment?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Chat selectedChat = selectedComment.getChat();

            if (selectedChat != null) {
                selectedChat.getCommentList().remove(selectedComment);
                hibernate.update(selectedChat);
            }

            hibernate.delete(Comment.class, selectedComment.getId());

            fillCommentList();
            clearCommentInput();
        }
    }


    public void fillCommentList() {
        commentListField.getItems().clear();
        List<Comment> commentList = hibernate.getAllRecords(Comment.class);
        commentListField.getItems().addAll(commentList);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void clearChatInput() {
        chatTitleField.clear();
    }

    public void clearCommentInput() {
        commentTitleField.clear();
        commentArea.clear();
    }

    public void updateChat() {
        // Получаем выбранный чат из списка
        Chat selectedChat = chatListField.getSelectionModel().getSelectedItem();

        if (selectedChat == null) {
            showAlert("Error", "You must select a chat first!");
            return;
        }

        // Получаем чат из базы данных по ID выбранного чата
        Chat chatFromDb = hibernate.getEntityById(Chat.class, selectedChat.getId());

        if (chatTitleField.getText().isEmpty()) {
            showAlert("Error", "Chat title cannot be empty!");
            return;
        }

        chatFromDb.setTitle(chatTitleField.getText());
        hibernate.update(chatFromDb);
        fillChatList();
    }
}
