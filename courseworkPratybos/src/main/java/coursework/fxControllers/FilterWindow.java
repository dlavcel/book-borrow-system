package coursework.fxControllers;

import coursework.hibernateControllers.CustomHibernate;
import coursework.model.BookFilter;
import coursework.model.Client;
import coursework.model.User;
import coursework.model.enums.PublicationStatus;
import coursework.model.enums.PublicationType;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class FilterWindow implements Initializable {
    @FXML
    public ChoiceBox<PublicationType> filterByType;
    @FXML
    public ChoiceBox<PublicationStatus> filterByStatus;
    @FXML
    public AnchorPane windowBody;
    @FXML
    public TextField filterByAuthor;
    @FXML
    public TextField filterByTitle;
    @FXML
    public DatePicker filterByDate;

    private EntityManagerFactory entityManagerFactory;
    private CustomHibernate hibernate; //= new GenericHibernate(entityManagerFactory);
    private Client currentUser;

    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        currentUser = (Client) user;
        hibernate = new CustomHibernate(entityManagerFactory);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filterByStatus.getItems().setAll(PublicationStatus.values());
        filterByType.getItems().setAll(PublicationType.values());
    }

    private BookFilter getFilters() {
        BookFilter filters = new BookFilter();
        filters.setStatus(filterByStatus.getValue());
        filters.setPublicationType(filterByType.getValue());
        filters.setAuthor(filterByAuthor.getText());
        filters.setTitle(filterByTitle.getText());
        filters.setPublicationDate(filterByDate.getValue());

        return filters;
    }

    public BookFilter applyFilter() {
        Stage stage = (Stage) filterByStatus.getScene().getWindow();
        stage.close();
        return getFilters();
    }
}
