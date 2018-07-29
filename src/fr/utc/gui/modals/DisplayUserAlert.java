package fr.utc.gui.modals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import fr.utc.dataStructure.UserLocal;
import fr.utc.gui.AbstractController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DisplayUserAlert extends Alert {
	private static final String TITLE = "User profile";
	private static final String DATE_FORMAT = "M/d/yyyy";
	private static final String FIRSTNAME_HEADER = "Firstname";
	private static final String LASTNAME_HEADER = "Lastname";
	private static final String BIRTHDAY_HEADER = "Date of birth";
	private Label lastName;
	private Label firstName;
	private Label birthday;
	private GridPane alertContent;

	public DisplayUserAlert(UserLocal userToDisplay) {
		super(AlertType.INFORMATION);

		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(AbstractController.class.getResourceAsStream(AbstractController.ICON)));

		firstName = new Label();
		lastName = new Label();
		birthday = new Label();
		alertContent = new GridPane();
		alertContent.setHgap(5);
		alertContent.setVgap(5);

		setTitle(TITLE);
		setHeaderText(userToDisplay.getLogin());

		firstName.setText(userToDisplay.getFirstName());
		lastName.setText(userToDisplay.getLastName());
		if (userToDisplay.getBirthday() != null) {
			DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
			birthday.setText(dateFormat.format(userToDisplay.getBirthday()));
		}

		alertContent.add(new Label(FIRSTNAME_HEADER), 0, 0);
		alertContent.add(firstName, 1, 0);
		alertContent.add(new Label(LASTNAME_HEADER), 0, 1);
		alertContent.add(lastName, 1, 1);
		alertContent.add(new Label(BIRTHDAY_HEADER), 0, 2);
		alertContent.add(birthday, 1, 2);

		getDialogPane().setContent(alertContent);
		getButtonTypes().setAll(ButtonType.CLOSE);
		showAndWait();
	}
}
