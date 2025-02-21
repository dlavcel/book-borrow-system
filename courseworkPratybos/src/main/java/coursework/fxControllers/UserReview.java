package coursework.fxControllers;

import coursework.hibernateControllers.CustomHibernate;
import coursework.model.*;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import static coursework.utils.FxUtils.generateAlert;
import static javafx.scene.control.Alert.AlertType.WARNING;

public class UserReview {
    @FXML
    public TreeView<Comment> userReview;
    @FXML
    public TextArea commentBody;
    @FXML
    public TextField commentTitle;
    @FXML
    public Button updateButton;
    @FXML
    public ContextMenu commentContextMenu;
    @FXML
    public MenuItem deleteItem;
    @FXML
    public Button addButton;

    private CustomHibernate hibernate;
    private User currentUser;
    private Client targetClient;
    private Chat chat;

    public void setData(EntityManagerFactory entityManagerFactory, User user, Client targetClient, Publication publication) {
        hibernate = new CustomHibernate(entityManagerFactory);
        currentUser = user;
        this.targetClient = targetClient;
        this.chat = hibernate.getChatByPublication(publication.getId());
        fillTree();

        if (currentUser instanceof Client) {
            updateButton.setDisable(true);
            commentContextMenu.hide();
            deleteItem.setDisable(true);
        } else if (currentUser instanceof Admin) {
            addButton.setDisable(true);
        }
    }

    private void fillTree() {
        userReview.setRoot(new TreeItem<>());
        userReview.setShowRoot(false);
        userReview.getRoot().setExpanded(true);

        Chat chatFromDb = hibernate.getEntityById(Chat.class, chat.getId());

        // Добавляем только корневые комментарии
        chatFromDb.getCommentList().stream()
                .filter(comment -> comment.getParentComment() == null) // Фильтруем только корневые
                .forEach(c -> addTreeItem(c, userReview.getRoot()));
    }


    public void addTreeItem(Comment comment, TreeItem<Comment> parentComment) {
        TreeItem<Comment> treeItem = new TreeItem<>(comment);
        parentComment.getChildren().add(treeItem);

        // Рекурсивно добавляем дочерние комментарии
        comment.getReplies().forEach(sub -> addTreeItem(sub, treeItem));
    }


    public void insertComment() {
        if (currentUser instanceof Client) {
            TreeItem<Comment> selectedTreeItem = userReview.getSelectionModel().getSelectedItem();
            Comment comment;

            if (selectedTreeItem != null) {
                // Ответ на комментарий
                Comment selectedComment = selectedTreeItem.getValue();
                comment = new Comment(
                        commentTitle.getText(),
                        commentBody.getText(),
                        selectedComment, // Устанавливаем родительский комментарий
                        chat,
                        (Client) currentUser
                );
            } else {
                // Корневой комментарий
                comment = new Comment(
                        commentTitle.getText(),
                        commentBody.getText(),
                        targetClient, // Привязываем к клиенту
                        chat,
                        (Client) currentUser
                );
            }

            hibernate.create(comment);
            // Обновляем только ту часть дерева, которая связана с добавленным комментарием
            if (selectedTreeItem != null) {
                addTreeItem(comment, selectedTreeItem); // Добавляем как дочерний элемент
            } else {
                addTreeItem(comment, userReview.getRoot()); // Добавляем как корневой элемент
            }
        }
    }



    public void loadComment() {
        TreeItem<Comment> selectedTreeItem = userReview.getSelectionModel().getSelectedItem();

        if (selectedTreeItem != null) {
            Comment selectedComment = selectedTreeItem.getValue();
            commentTitle.setText(selectedComment.getTitle());
            commentBody.setText(selectedComment.getBody());
        }
    }


    public void updateComment() {
        Comment selectedComment = userReview.getSelectionModel().getSelectedItem().getValue();
        selectedComment.setTitle(commentTitle.getText());
        selectedComment.setBody(commentBody.getText());
        hibernate.update(selectedComment);
        fillTree();
    }

    public void deleteComment() {
        TreeItem<Comment> selectedItem = userReview.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            generateAlert(WARNING, "Error", "No comment selected for deletion.");
            return;
        }

        Comment selectedComment = selectedItem.getValue();
        if (selectedComment != null) {
            hibernate.deleteComment(selectedComment.getId());
            fillTree(); // Обновляем дерево после удаления
        }
    }

}
