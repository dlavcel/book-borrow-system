package coursework.fxControllers;

import coursework.hibernateControllers.CustomHibernate;
import coursework.model.*;
import coursework.model.enums.*;
import coursework.utils.FxUtils;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductWindow implements Initializable {
    @FXML
    public ListView<Publication> productListField;
    @FXML
    public ChoiceBox<String> publicationType;

    // Common fields
    @FXML
    public TextField titleField;
    @FXML
    public TextField authorField;

    // Periodical-specific fields
    @FXML
    public TextField issueNumberField;
    @FXML
    public DatePicker publicationDateField;
    @FXML
    public TextField editorField;
    @FXML
    public ChoiceBox<Frequency> frequencyField;

    // Manga-specific fields
    @FXML
    public TextField illustratorField;
    @FXML
    public ChoiceBox<Language> ogLanguageField;
    @FXML
    public TextField volumeField;
    @FXML
    public ChoiceBox<Demographic> demographicField;
    @FXML
    public CheckBox colorField;

    // Book-specific fields
    @FXML
    public TextField publisherField;
    @FXML
    public TextField isbnField;
    @FXML
    public ChoiceBox<Genre> genreField;
    @FXML
    public TextField pageCountField;
    @FXML
    public ChoiceBox<Language> languageField;
    @FXML
    public TextField pubYearField;
    @FXML
    public ChoiceBox<Format> formatField;
    @FXML
    public TextField summaryField;
    @FXML
    public Button updateButton;
    @FXML
    public Button deleteButton;
    @FXML
    public Button addButton;

    private EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coursework");
    private CustomHibernate hibernate = new CustomHibernate(entityManagerFactory);
    private User currentUser;

    public void setData(EntityManagerFactory entityManagerFactory, User user) {
        this.entityManagerFactory = entityManagerFactory;
        this.currentUser = user;
        //hibernate = new CustomHibernate(entityManagerFactory);
        fillProductList();
        enableVisibility();
    }

    private void enableVisibility() {
        if (currentUser instanceof Client) {
            updateButton.setDisable(true);
            deleteButton.setDisable(true);
        } else if (currentUser instanceof Admin) {
            addButton.setDisable(true);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        publicationType.getItems().addAll("Periodical", "Book", "Manga");
        publicationType.setValue("Periodical");
        colorField.setSelected(false);

        demographicField.getItems().addAll(Demographic.values());
        frequencyField.getItems().addAll(Frequency.values());
        ogLanguageField.getItems().addAll(Language.values());
        genreField.getItems().addAll(Genre.values());
        languageField.getItems().addAll(Language.values());
        formatField.getItems().addAll(Format.values());

        publicationType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFields());
        fillProductList();
        updateFields();
    }

    private void createChat(Publication publication) {
        Chat chat = new Chat(publication.getTitle(), publication);
        hibernate.create(chat);
    }

    private void clearInput() {
        // Очищаем общие поля
        titleField.clear();
        authorField.clear();

        // Очищаем поля для всех типов публикаций
        issueNumberField.clear();
        publicationDateField.setValue(null);
        editorField.clear();
        frequencyField.setValue(null);

        illustratorField.clear();
        ogLanguageField.setValue(null);
        volumeField.clear();
        demographicField.setValue(null);
        colorField.setSelected(false);

        publisherField.clear();
        isbnField.clear();
        genreField.setValue(null);
        pageCountField.clear();
        languageField.setValue(null);
        formatField.setValue(null);
        summaryField.clear();
    }


    public void createNewProduct() {
        if (!validatePublicationInput()) {
            return;
        }

        switch (publicationType.getValue()) {
            case "Periodical":
                Periodical periodical = new Periodical(
                        titleField.getText(),
                        authorField.getText(),
                        publicationDateField.getValue(),
                        Integer.parseInt(issueNumberField.getText()),
                        editorField.getText(),
                        frequencyField.getValue(),
                        publisherField.getText());
                hibernate.create(periodical);
                createChat(periodical);
                break;
            case "Manga":
                Manga manga = new Manga(
                        titleField.getText(),
                        authorField.getText(),
                        publicationDateField.getValue(),
                        illustratorField.getText(),
                        ogLanguageField.getValue(),
                        Integer.parseInt(volumeField.getText()),
                        demographicField.getValue(), colorField.isSelected());
                hibernate.create(manga);
                createChat(manga);
                break;
            case "Book":
                Book book = new Book(
                        titleField.getText(),
                        authorField.getText(),
                        publicationDateField.getValue(),
                        publisherField.getText(),
                        isbnField.getText(),
                        genreField.getValue(),
                        Integer.parseInt(pageCountField.getText()),
                        languageField.getValue(),
                        formatField.getValue(),
                        summaryField.getText());
                hibernate.create(book);
                createChat(book);
                break;
        }
        fillProductList();
        clearInput();
    }

    public void fillProductList() {
        if (currentUser instanceof Admin) {
            productListField.getItems().clear();
            List<Publication> productList = hibernate.getAllRecords(Publication.class);
            productListField.getItems().addAll(productList);
        } else if (currentUser instanceof Client) {
            productListField.getItems().clear();
            List<Publication> productList = hibernate.getOwnPublications(currentUser);
            productListField.getItems().addAll(productList);
        }
    }

    public void loadProductData() {
        Publication selectedProduct = productListField.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showAlert("Error", "Select product");
            return;
        }

        Publication productInfoFromDb = hibernate.getEntityById(Publication.class, selectedProduct.getId());

        titleField.setText(productInfoFromDb.getTitle());
        authorField.setText(productInfoFromDb.getAuthor());
        publicationDateField.setValue(LocalDate.parse(productInfoFromDb.getPublicationDate().toString()));

        if (productInfoFromDb instanceof Periodical) {
            publicationType.setValue("Periodical");
            Periodical periodical = (Periodical) productInfoFromDb;
            issueNumberField.setText(String.valueOf(periodical.getIssueNumber()));
            editorField.setText(periodical.getEditor());
            frequencyField.setValue(periodical.getFrequency());
        } else if (productInfoFromDb instanceof Manga) {
            publicationType.setValue("Manga");
            Manga manga = (Manga) productInfoFromDb;
            illustratorField.setText(manga.getIllustrator());
            ogLanguageField.setValue(manga.getOriginalLanguage());
            volumeField.setText(String.valueOf(manga.getVolumeNumber()));
            demographicField.setValue(manga.getDemographic());
            colorField.setSelected(manga.isColor());
        } else if (productInfoFromDb instanceof Book) {
            publicationType.setValue("Book");
            Book book = (Book) productInfoFromDb;
            publisherField.setText(book.getPublisher());
            isbnField.setText(book.getIsbn());
            genreField.setValue(book.getGenre());
            pageCountField.setText(String.valueOf(book.getPageCount()));
            languageField.setValue(book.getLanguage());
            formatField.setValue(book.getFormat());
            summaryField.setText(book.getSummary());
        }
    }


    public void updateProduct() {
        Publication selectedProduct = productListField.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showAlert("Error", "Choose product to update");
            return;
        }

        Publication productInfoFromDb = hibernate.getEntityById(Publication.class, selectedProduct.getId());

        productInfoFromDb.setTitle(titleField.getText());
        productInfoFromDb.setAuthor(authorField.getText());
        productInfoFromDb.setPublicationDate(publicationDateField.getValue());

        switch (publicationType.getValue()) {
            case "Periodical":
                if (productInfoFromDb instanceof Periodical periodical) {
                    publicationType.setValue("Periodical");
                    periodical.setIssueNumber(Integer.parseInt(issueNumberField.getText()));
                    periodical.setPublicationDate(publicationDateField.getValue());
                    periodical.setEditor(editorField.getText());
                    periodical.setFrequency(frequencyField.getValue());
                }
                break;
            case "Manga":
                if (productInfoFromDb instanceof Manga manga) {
                    publicationType.setValue("Manga");
                    manga.setIllustrator(illustratorField.getText());
                    manga.setOriginalLanguage(ogLanguageField.getValue());
                    manga.setVolumeNumber(Integer.parseInt(volumeField.getText()));
                    manga.setDemographic(demographicField.getValue());
                    manga.setColor(colorField.isSelected());
                }
                break;
            case "Book":
                if (productInfoFromDb instanceof Book book) {
                    publicationType.setValue("Book");
                    book.setPublisher(publisherField.getText());
                    book.setIsbn(isbnField.getText());
                    book.setGenre(genreField.getValue());
                    book.setPageCount(Integer.parseInt(pageCountField.getText()));
                    book.setLanguage(languageField.getValue());
                    book.setFormat(formatField.getValue());
                    book.setSummary(summaryField.getText());
                }
                break;
        }

        hibernate.update(productInfoFromDb);

        fillProductList();
        clearInput();
    }

    public void deleteProduct() {
        Publication selectedProduct = productListField.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            showAlert("Error", "Select product to delete");
            return;
        }

        if (selectedProduct.getPublicationStatus() != PublicationStatus.AVAILABLE) {
            showAlert("Error", "Cant delete this product");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to delete the selected product?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            hibernate.delete(Publication.class, selectedProduct.getId());
            fillProductList();
            clearInput();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validatePublicationInput() {
        String errorMessage = "";

        // Validate common fields
        if (titleField.getText() == null || titleField.getText().isEmpty()) {
            errorMessage += "Title is required!\n";
        }
        if (authorField.getText() == null || authorField.getText().isEmpty()) {
            errorMessage += "Author is required!\n";
        }

        switch (publicationType.getValue()) {
            case "Periodical":
                errorMessage += validatePeriodicalInput();
                break;
            case "Book":
                errorMessage += validateBookInput();
                break;
            case "Manga":
                errorMessage += validateMangaInput();
                break;
        }

        if (errorMessage.isEmpty()) {
            return true;  // All fields are valid
        } else {
            FxUtils.generateAlert(Alert.AlertType.ERROR, "Input Validation Error", errorMessage);
            return false;  // There are validation errors
        }
    }

    private String validatePeriodicalInput() {
        String errorMessage = "";
        if (issueNumberField.getText() == null || issueNumberField.getText().isEmpty()) {
            errorMessage += "Issue number is required for periodical!\n";
        }
        try {
            Integer.parseInt(issueNumberField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "Issue number must be a number!\n";
        }

        if (publicationDateField.getValue() == null) {
            errorMessage += "Publication date is required for periodical!\n";
        }

        if (editorField.getText() == null || editorField.getText().isEmpty()) {
            errorMessage += "Editor field is required for periodical!\n";
        }
        if (frequencyField.getValue() == null) {
            errorMessage += "Frequency must be selected for periodical!\n";
        }
        return errorMessage;
    }

    private String validateBookInput() {
        String errorMessage = "";
        if (publisherField.getText() == null || publisherField.getText().isEmpty()) {
            errorMessage += "Publisher is required for books!\n";
        }
        if (isbnField.getText().isEmpty()) {
            errorMessage += "ISBN is required for books!\n";
        }
        if (genreField.getValue() == null) {
            errorMessage += "Genre must be selected for books!\n";
        }

        if (pageCountField.getText() == null || pageCountField.getText().isEmpty())
        {
            errorMessage += "Page count is required for books!\n";
        }
        try {
            Integer.parseInt(pageCountField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "Page count must be a number!\n";
        }

        if (languageField.getValue() == null) {
            errorMessage += "Language must be selected for books!\n";
        }

        if (formatField.getValue() == null) {
            errorMessage += "Format must be selected for books!\n";
        }
        if (summaryField.getText() == null || summaryField.getText().isEmpty()) {
            errorMessage += "Summary is required!\n";
        }

        return errorMessage;
    }

    private String validateMangaInput() {
        String errorMessage = "";
        if (illustratorField.getText() == null || illustratorField.getText().isEmpty()) {
            errorMessage += "Illustrator is required for manga!\n";
        }
        if (ogLanguageField.getValue() == null) {
            errorMessage += "Original language must be selected for manga!\n";
        }

        if (volumeField.getText() == null || volumeField.getText().isEmpty()) {
            errorMessage += "Volume is required for manga!\n";
        }
        try {
            Integer.parseInt(volumeField.getText());
        } catch (NumberFormatException e) {
            errorMessage += "Volume must be a number!\n";
        }

        if (demographicField.getValue() == null) {
            errorMessage += "Demographic must be selected for manga!\n";
        }
        return errorMessage;
    }

    private void updateFields() {
        String selectedType = publicationType.getValue();
        switch (selectedType) {
            case "Periodical":
                enablePeriodicalFields();
                disableMangaFields();
                disableBookFields();
                break;
            case "Manga":
                disablePeriodicalFields();
                enableMangaFields();
                disableBookFields();
                break;
            case "Book":
                disablePeriodicalFields();
                disableMangaFields();
                enableBookFields();
                break;
        }
    }

    private void enablePeriodicalFields() {
        issueNumberField.setDisable(false);
        editorField.setDisable(false);
        frequencyField.setDisable(false);
    }

    private void disablePeriodicalFields() {
        issueNumberField.setDisable(true);
        editorField.setDisable(true);
        frequencyField.setDisable(true);
    }

    private void enableMangaFields() {
        illustratorField.setDisable(false);
        ogLanguageField.setDisable(false);
        volumeField.setDisable(false);
        demographicField.setDisable(false);
        colorField.setDisable(false);
    }

    private void disableMangaFields() {
        illustratorField.setDisable(true);
        ogLanguageField.setDisable(true);
        volumeField.setDisable(true);
        demographicField.setDisable(true);
        colorField.setDisable(true);
    }

    private void enableBookFields() {
        publisherField.setDisable(false);
        isbnField.setDisable(false);
        genreField.setDisable(false);
        pageCountField.setDisable(false);
        languageField.setDisable(false);
        formatField.setDisable(false);
        summaryField.setDisable(false);
    }

    private void disableBookFields() {
        publisherField.setDisable(true);
        isbnField.setDisable(true);
        genreField.setDisable(true);
        pageCountField.setDisable(true);
        languageField.setDisable(true);
        formatField.setDisable(true);
        summaryField.setDisable(true);
    }
}
