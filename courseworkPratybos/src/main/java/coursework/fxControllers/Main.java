package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibernateControllers.CustomHibernate;
import coursework.model.*;
import coursework.model.enums.PublicationStatus;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;

import javafx.util.Callback;
import lombok.Getter;
import org.mindrot.jbcrypt.BCrypt;

public class Main implements Initializable {

    @FXML
    public ListView<User> userListField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField surnameField;
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField pswField;
    @FXML
    public TextField addressField;
    @FXML
    public DatePicker bDate;
    @FXML
    public TextField phoneNumField;
    @FXML
    public RadioButton adminChk;
    @FXML
    public RadioButton clientChk;
    @FXML
    public Tab chatTab;
    @FXML
    public TableView<UserTableParameters> userTable;
    @FXML
    public TableColumn<UserTableParameters, Integer> colId;
    @FXML
    public TableColumn<UserTableParameters, String> colLogin;
    @FXML
    public TableColumn<UserTableParameters, String> colPassword;
    @FXML
    public TableColumn<UserTableParameters, String> colName;
    @FXML
    public TableColumn<UserTableParameters, String> colSurname;
    @FXML
    public ObservableList<UserTableParameters> data = FXCollections.observableArrayList();
    @FXML
    public TableColumn<UserTableParameters, Void> dummyCol;
    @FXML
    public TableColumn<UserTableParameters, String> colAddress;
    @FXML
    public TableColumn<UserTableParameters, String> colPhoneNum;

    //</editor-fold>
    @FXML
    public ListView<Publication> availableBookList;
    @FXML
    public TextArea aboutPublication;
    @FXML
    public TextArea ownerBio;
    @FXML
    public Label ownerInfo;
    @FXML
    public ListView<Comment> chatList;
    @FXML
    public TextArea messageArea;

    @FXML
    public TabPane allTabs;
    @FXML
    public Tab publicationManagementTab;
    @FXML
    public Tab userManagementTab;
    @FXML
    public Tab clientBookManagementTab;
    @FXML
    public Button leaveReviewButton;
    @FXML
    public Tab bookExchangeTab;
    @FXML
    public Tab userTab;
    @FXML
    public Button reserveBookButton;
    @FXML
    public Button addMessage;
    @FXML
    public ListView<Publication> borrowedBooksList;
    @FXML
    public ListView<Publication> ownedBooksList;
    @FXML
    public Button myBooksFilter;


    private EntityManagerFactory entityManagerFactory;

    private CustomHibernate hibernate; //= new GenericHibernate(entityManagerFactory);

    @Getter
    private static User currentUser;

    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        currentUser = user;
        hibernate = new CustomHibernate(entityManagerFactory);
        fillUserList();
        fillBorrowedBooks();
        fillOwnedBooks();
        fillPublicationList();
        enableVisibility();
    }

    private void fillPublicationList() {
        availableBookList.getItems().clear();
        List<Publication> publications = hibernate.getAvailablePublications(currentUser);
        if (publications != null) {
            availableBookList.getItems().addAll(publications);
        }
    }

    public void fillUserList() {
        userListField.getItems().clear();
        List<User> userList = hibernate.getAllRecords(User.class);
        userListField.getItems().addAll(userList);
    }

    private void enableVisibility() {
        if (currentUser instanceof Client) {
            allTabs.getTabs().remove(publicationManagementTab);
            allTabs.getTabs().remove(userManagementTab);
            allTabs.getTabs().remove(userTab);
        } else if (currentUser instanceof Admin) {
            allTabs.getTabs().remove(clientBookManagementTab);
            //leaveReviewButton.setDisable(true);
            allTabs.getTabs().remove(publicationManagementTab);
            reserveBookButton.setDisable(true);
            addMessage.setDisable(true);
        }
    }

    private void fillUserTable() {
        List<User> allUsers = hibernate.getAllRecords(User.class);
        for (User user : allUsers) {
            UserTableParameters userTableParameters = new UserTableParameters();
            userTableParameters.setId(user.getId());
            userTableParameters.setLogin(user.getLogin());
            userTableParameters.setPassword(user.getPassword());
            userTableParameters.setName(user.getName());
            userTableParameters.setSurname(user.getSurname());
            if (user instanceof Client) {
                userTableParameters.setAddress(((Client) user).getAddress());
            }
            if (user instanceof Admin) {
                userTableParameters.setPhoneNum(((Admin) user).getPhoneNum());
            }
            data.add(userTableParameters);
        }
        userTable.setItems(data);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        aboutPublication.setEditable(false);
        ownerBio.setEditable(false);

        userTable.setEditable(true);

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colPhoneNum.setCellValueFactory(new PropertyValueFactory<>("phoneNum"));

        colLogin.setCellFactory(TextFieldTableCell.forTableColumn());
        colLogin.setOnEditCommit(event -> {
            UserTableParameters userParams = event.getRowValue();
            userParams.setLogin(event.getNewValue());
            User user = hibernate.getEntityById(User.class, userParams.getId());
            user.setLogin(event.getNewValue());
            hibernate.update(user);
        });

        colPassword.setCellFactory(TextFieldTableCell.forTableColumn());
        colPassword.setOnEditCommit(event -> {
            UserTableParameters userParams = event.getRowValue();
            userParams.setPassword(event.getNewValue());
            User user = hibernate.getEntityById(User.class, userParams.getId());
            user.setPassword(event.getNewValue());
            hibernate.update(user);
        });

        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(event -> {
            UserTableParameters userParams = event.getRowValue();
            userParams.setName(event.getNewValue());
            User user = hibernate.getEntityById(User.class, userParams.getId());
            user.setName(event.getNewValue());
            hibernate.update(user);
        });

        colSurname.setCellFactory(TextFieldTableCell.forTableColumn());
        colSurname.setOnEditCommit(event -> {
            UserTableParameters userParams = event.getRowValue();
            userParams.setSurname(event.getNewValue());
            User user = hibernate.getEntityById(User.class, userParams.getId());
            user.setSurname(event.getNewValue());
            hibernate.update(user);
        });

        colAddress.setCellFactory(TextFieldTableCell.forTableColumn());
        colAddress.setOnEditCommit(event -> {
            UserTableParameters userParams = event.getRowValue();
            User user = hibernate.getEntityById(User.class, userParams.getId());
            if (user instanceof Client) {
                userParams.setAddress(event.getNewValue());
                ((Client) user).setAddress(event.getNewValue());
                hibernate.update(user);
            }
        });

        colPhoneNum.setCellFactory(TextFieldTableCell.forTableColumn());
        colPhoneNum.setOnEditCommit(event -> {
            UserTableParameters userParams = event.getRowValue();
            User user = hibernate.getEntityById(User.class, userParams.getId());
            if (user instanceof Admin) {
                userParams.setPhoneNum(event.getNewValue());
                ((Admin) user).setPhoneNum(event.getNewValue());
                hibernate.update(user);
            }
        });

        dummyCol.setCellFactory(createDeleteButtonCellFactory());

        clientChk.setSelected(true);
        disableFields();
    }

    Callback<TableColumn<UserTableParameters, Void>, TableCell<UserTableParameters, Void>> createDeleteButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");

            {
                deleteButton.setOnAction(event -> {
                    UserTableParameters row = getTableView().getItems().get(getIndex());
                    if (row != null) {
                        hibernate.delete(User.class, row.getId());
                        getTableView().getItems().remove(row);
                        getTableView().refresh(); // Обновление таблицы после удаления
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        };
    }

    //------------USER CREATE---------------
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public void createNewUser() {
        if (!validateUserInput()) {
            return;
        }

        if (clientChk.isSelected()) {
            Client client = new Client(
                    loginField.getText(),
                    hashPassword(pswField.getText()),
                    nameField.getText(),
                    surnameField.getText(),
                    addressField.getText(),
                    bDate.getValue()
            );
            hibernate.create(client);
        } else {
            Admin admin = new Admin(
                    loginField.getText(),
                    hashPassword(pswField.getText()),
                    nameField.getText(),
                    surnameField.getText(),
                    phoneNumField.getText()
            );
            hibernate.create(admin);
        }

        fillUserList();
        clearUserInput();
    }

    public void loadUserData() {
        User selectedUser = userListField.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Select a user to load data");
            return;
        }

        User userInfoFromDb = hibernate.getEntityById(User.class, selectedUser.getId());

        nameField.setText(userInfoFromDb.getName());
        surnameField.setText(userInfoFromDb.getSurname());
        loginField.setText(userInfoFromDb.getLogin());

        if (userInfoFromDb instanceof Client) {
            addressField.setText(((Client) userInfoFromDb).getAddress());
        } else {
            phoneNumField.setText(((Admin) userInfoFromDb).getPhoneNum());
        }
    }

    public void updateUser() {
        User selectedUser = userListField.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Choose product to update");
            return;
        }

        User userInfoFromDb = hibernate.getEntityById(User.class, selectedUser.getId());
        userInfoFromDb.setName(nameField.getText());
        userInfoFromDb.setSurname(surnameField.getText());
        userInfoFromDb.setLogin(loginField.getText());
        userInfoFromDb.setPassword(pswField.getText());

        if (userInfoFromDb instanceof Client client) {
            client.setAddress(addressField.getText());
            client.setBirthDate(bDate.getValue());
        } else if (userInfoFromDb instanceof Admin admin) {
            admin.setPhoneNum(phoneNumField.getText());
        }

        hibernate.update(userInfoFromDb);
        fillUserList();
        clearUserInput();
    }

    public void deleteUser() {
        User selectedUser = userListField.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Select user to delete");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete the selected user?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            hibernate.delete(User.class, selectedUser.getId());
            fillUserList();
        }
        clearUserInput();
    }

    private boolean validateUserInput() {
        String errorMessage = "";

        if (loginField.getText() == null || loginField.getText().isEmpty()) {
            errorMessage += "Login is required!\n";
        }
        if (pswField.getText() == null || pswField.getText().isEmpty()) {
            errorMessage += "Password is required!\n";
        }
        if (nameField.getText() == null || nameField.getText().isEmpty()) {
            errorMessage += "Name is required!\n";
        }
        if (surnameField.getText() == null || surnameField.getText().isEmpty()) {
            errorMessage += "Surname is required!\n";
        }
        if (clientChk.isSelected()) {
            if (addressField.getText() == null || addressField.getText().isEmpty()) {
                errorMessage += "Address is required for clients!\n";
            }
            if (bDate.getValue() == null) {
                errorMessage += "Birthdate is required for clients!\n";
            }
        } else if (adminChk.isSelected()) {
            if (phoneNumField.getText() == null || phoneNumField.getText().isEmpty()) {
                errorMessage += "Phone number is required for admins!\n";
            }
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showAlert("Error", errorMessage);
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void disableFields() {
        if (clientChk.isSelected()) {
            addressField.setDisable(false);
            bDate.setDisable(false);
            phoneNumField.setDisable(true);
        } else {
            addressField.setDisable(true);
            bDate.setDisable(true);
            phoneNumField.setDisable(false);
        }
    }

    public void clearUserInput() {
        nameField.clear();
        surnameField.clear();
        loginField.clear();
        pswField.clear();

        addressField.clear();
        bDate.setValue(null);
        phoneNumField.clear();
    }

    public void loadProductForm() throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("productWindow.fxml"));

        Parent parent = fxmlLoader.load();
        ProductWindow productWindow = fxmlLoader.getController();
        productWindow.setData(entityManagerFactory, currentUser);
        Scene scene = new Scene(parent);

        stage.setTitle("Book Exchange");
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
        fillOwnedBooks();
    }

    //---------------CHAT WITH OWNER-----------------------
    public void chatWithOwner() {
        Client owner = hibernate.getEntityById(Client.class, availableBookList.getSelectionModel().getSelectedItem().getOwner().getId());
        if (owner == null) {
            showAlert("Error", "Choose publication to chat");
        }

        Chat chat = getPrivateChat(owner, currentUser);

        if (messageArea.getText().isEmpty()) {
            showAlert("Error", "Write message to chat");
        }

        Comment comment = getComment(chat);
        hibernate.create(comment);
        fillChatList(chat);
    }

    private Chat getPrivateChat(Client owner, User currentUser) {
        Chat chat = hibernate.getChatByClientsId(owner.getId(), currentUser.getId());

        if (chat == null) {
            chat = new Chat("Chat between " + owner.getName() + " and " + currentUser.getName());
            chat.setOwner(owner);
            chat.setClient((Client) currentUser);
            hibernate.create(chat);
        }

        return chat;
    }

    private Comment getComment(Chat chat) {
        Comment selectedComment = chatList.getSelectionModel().getSelectedItem();
        System.out.println(selectedComment);
        Comment comment;
        //reply comment
        if (selectedComment != null) {
            comment = new Comment(
                    messageArea.getText(),
                    selectedComment,
                    chat,
                    (Client) currentUser
            );
        } else {
            //new comment
            comment = new Comment(
                    messageArea.getText(),
                    availableBookList.getSelectionModel().getSelectedItem().getOwner(),
                    chat,
                    (Client) currentUser
            );
        }
        return comment;
    }

    public void fillChatList(Chat chat) {
        chatList.getItems().clear();
        List<Comment> comments = hibernate.getCommentsByChat(chat);

        chatList.getItems().addAll(comments);
    }

    public void reserveBook() {
        Publication publication = availableBookList.getSelectionModel().getSelectedItem();

        if (publication != null) {
            publication.setPublicationStatus(PublicationStatus.RESERVED);
            publication.setClient((Client) currentUser);
            hibernate.update(publication);

            PublicationRecord publicationRecord = new PublicationRecord(
                    (Client) currentUser,
                    publication,
                    publication.getPublicationStatus()
            );
            ((Client) currentUser).getPublicationRecords().add(publicationRecord);
            hibernate.create(publicationRecord);
            fillPublicationList();
            fillBorrowedBooks();
        }
    }

    public void loadData() {
        if (userManagementTab.isSelected()) {
            fillUserTable();
        } else if (bookExchangeTab.isSelected()) {
            fillPublicationList();
        }
    }

    //-----------BOOK EXCHANGE--------------------
    public void loadPublicationInfo() {
        Publication publication = availableBookList.getSelectionModel().getSelectedItem();
        Publication publicationFromDb = hibernate.getEntityById(Publication.class, publication.getId());

        //fill owner and client chat
        if (currentUser instanceof Client) {
            Client owner = hibernate.getEntityById(Client.class, publicationFromDb.getOwner().getId());
            Chat chat = getPrivateChat(owner, currentUser);
            fillChatList(chat);
        }

        //periodical
        if (publicationFromDb instanceof Periodical periodical) {
            aboutPublication.setText("Title: " + publicationFromDb.getTitle() + "\n" +
                    "Editor: " + periodical.getEditor());
        } else if (publicationFromDb instanceof Book book) {
            aboutPublication.setText("Title: " + publicationFromDb.getTitle() + "\n" +
                    "Year: " + book.getPublicationDate());
        } else if (publicationFromDb instanceof Manga manga) {
            aboutPublication.setText("Title: " + publicationFromDb.getTitle() + "\n" +
                    "Illustrator: " + manga.getIllustrator());
        }

        ownerInfo.setText(publicationFromDb.getOwner().getName());
        ownerBio.setText(publicationFromDb.getOwner().getClientBio());
    }

    public void loadReviewWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("userReview.fxml"));
        Parent parent = fxmlLoader.load();

        //System.out.println(availableBookList.getItems());
        UserReview userReview = fxmlLoader.getController();
        userReview.setData(entityManagerFactory, currentUser,
                availableBookList.getSelectionModel().getSelectedItem().getOwner(),
                availableBookList.getSelectionModel().getSelectedItem());

        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setTitle("Book Exchange");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
    }

    //-----------BORROWED BOOKS---------------

    public void fillBorrowedBooks() {
        borrowedBooksList.getItems().clear();
        List<Publication> publicationList = hibernate.getBorrowedPublications(currentUser);
        borrowedBooksList.getItems().addAll(publicationList);
    }

    public void giveBack() {
        Publication publication = (Publication) borrowedBooksList.getSelectionModel().getSelectedItem();

        if (publication == null) {
            showAlert("Error", "No book selected.");
            return;
        }

        if (publication.getPublicationStatus() == PublicationStatus.SOLD) {
            return;
        }

        if (!(currentUser instanceof Client)) {
            showAlert("Error", "Current user is not a client.");
            return;
        }

        publication.setPublicationStatus(PublicationStatus.AVAILABLE);
        publication.setClient(null);

        hibernate.update(publication);

        PublicationRecord publicationRecord = new PublicationRecord(
                (Client) currentUser,
                publication,
                publication.getPublicationStatus()
        );

        ((Client) currentUser).getPublicationRecords().add(publicationRecord);
        hibernate.create(publicationRecord);
        fillBorrowedBooks();
    }

    public void buyBook() {
        Publication publication = (Publication) borrowedBooksList.getSelectionModel().getSelectedItem();

        if (publication == null) {
            showAlert("Error", "No book selected.");
            return;
        }

        if (publication.getPublicationStatus() == PublicationStatus.SOLD) {
            return;
        }

        if (!(currentUser instanceof Client)) {
            showAlert("Error", "Current user is not a client.");
            return;
        }

        publication.setPublicationStatus(PublicationStatus.SOLD);
        publication.setTitle(publication.getTitle() + " " + PublicationStatus.SOLD);
        hibernate.update(publication);

        PublicationRecord publicationRecord = new PublicationRecord(
                (Client) currentUser,
                publication,
                publication.getPublicationStatus()
        );

        ((Client) currentUser).getPublicationRecords().add(publicationRecord);
        hibernate.create(publicationRecord);
        fillBorrowedBooks();
    }

    //MY BOOKS
    public void fillOwnedBooks() {
        ownedBooksList.getItems().clear();
        List<Publication> publicationList = hibernate.getOwnPublications(currentUser);
        ownedBooksList.getItems().addAll(publicationList);
    }

    //--------FILTER-----------
    public void openFilterWindow() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("filterWindow.fxml"));
        Parent parent = fxmlLoader.load();

        FilterWindow filterWindow = fxmlLoader.getController();
        filterWindow.setData(entityManagerFactory, currentUser);

        Stage stage = new Stage();
        Scene scene = new Scene(parent);
        stage.setTitle("Book Exchange");
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();

        BookFilter bookFilter = filterWindow.applyFilter();
        List<Publication> filteredPublications = hibernate.filterBooks(currentUser, bookFilter);

        ownedBooksList.getItems().clear();
        ownedBooksList.getItems().addAll(filteredPublications);
    }
}
