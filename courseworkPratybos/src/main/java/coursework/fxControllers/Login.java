package coursework.fxControllers;

import coursework.StartGUI;
import coursework.hibernateControllers.CustomHibernate;
import coursework.model.Admin;
import coursework.model.Client;
import coursework.model.User;
import coursework.utils.FxUtils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class Login {
    @FXML
    public TextField userNameField;
    @FXML
    public PasswordField pswField;
    EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");
    CustomHibernate customHibernate = new CustomHibernate(entityManagerFactory);

    public void validateAndLoad() throws IOException {
        //tikrinam ar yra user
        User user = customHibernate.getUserByCredentials(userNameField.getText(), pswField.getText());
//        Admin client = new Admin(
//                "admin",
//                "admin",
//                "avvv",
//                "vvvv",
//                "12344566789"
//        );
//        customHibernate.create(client);
        //jei yra load newWindow
        if (user != null) {
            FXMLLoader fxmlLoader = new FXMLLoader(StartGUI.class.getResource("main.fxml"));
            Parent parent = fxmlLoader.load();

            Main main = fxmlLoader.getController();
            main.setData(entityManagerFactory, user);
            Scene scene = new Scene(parent);

            Stage currentStage = (Stage) userNameField.getScene().getWindow();
            currentStage.setTitle("Book Exchange");
            currentStage.setScene(scene);
            currentStage.show();
        } else {
            FxUtils.generateAlert(Alert.AlertType.WARNING, "No such user", "Please check credentials");
        }
        //jei nera -> alert kas neegzistuoja
    }

    public void newUserRegistration() {
    }
}
