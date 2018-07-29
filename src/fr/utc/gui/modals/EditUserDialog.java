package fr.utc.gui.modals;

import java.io.File;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import fr.utc.dataStructure.UserLocal;
import fr.utc.gui.AbstractController;
import fr.utc.gui.GUIManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;

public class EditUserDialog extends Dialog<Pair<String, String>> {
	private static final String TITLE = "Edit profile";
	private static final String HEADER = "Edit your account information here.";
	private static final String TEXT_REQUIRED = "Required";
	private static final String TEXT_BROWSE = "Browse...";
	private static final String TEXT_FILECHOOSER = "Choose a picture";
	private static final String ALERT_TITLE = "Missing required fields";
	private static final String ALERT_CONTENT = "Please set mandatory information.\nFirstname and lastname are required.";
	private static final String LOGIN_HEADER = "Login";
	private static final String FIRSTNAME_HEADER = "Firstname";
	private static final String LASTNAME_HEADER = "Lastname";
	private static final String BIRTHDAY_HEADER = "Date of birth";
	private static final String AVATAR_HEADER = "Avatar";
	private TextField login;
	private TextField firstName;
	private TextField lastName;
	private DatePicker birthday;
	private Button avatar;
	private GridPane dialogContent;

	public EditUserDialog(GUIManager guiManager) {
		super();

		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(AbstractController.class.getResourceAsStream(AbstractController.ICON)));

		setTitle(TITLE);
		setHeaderText(HEADER);

		login = new TextField();
		firstName = new TextField();
		lastName = new TextField();
		birthday = new DatePicker();
		avatar = new Button();
		dialogContent = new GridPane();
		dialogContent.setHgap(5);
		dialogContent.setVgap(5);

		UserLocal currentUser = guiManager.getGuiToProcessing().getCurrentUser();

		login.setText(currentUser.getLogin());
		login.setDisable(true);
		firstName.setText(currentUser.getFirstName());
		firstName.setPromptText(TEXT_REQUIRED);
		lastName.setText(currentUser.getLastName());
		lastName.setPromptText(TEXT_REQUIRED);
		if (currentUser.getBirthday() != null) {
			birthday.setValue(currentUser.getBirthday().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
		}
		avatar.setText(TEXT_BROWSE);
		avatar.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(TEXT_FILECHOOSER);
			fileChooser.setInitialDirectory(new File("."));
			File file = fileChooser.showOpenDialog(new Stage());
			if (file != null) {
				String name = file.getName();
				avatar.setText(name);
			}
		});

		dialogContent.add(new Label(LOGIN_HEADER), 0, 0);
		dialogContent.add(login, 1, 0);
		dialogContent.add(new Label(FIRSTNAME_HEADER), 0, 2);
		dialogContent.add(firstName, 1, 2);
		dialogContent.add(new Label(LASTNAME_HEADER), 0, 3);
		dialogContent.add(lastName, 1, 3);
		dialogContent.add(new Label(BIRTHDAY_HEADER), 0, 4);
		dialogContent.add(birthday, 1, 4);
		dialogContent.add(new Label(AVATAR_HEADER), 0, 5);
		dialogContent.add(avatar, 1, 5);

		getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
		getDialogPane().setContent(dialogContent);

		final Button buttonOk = (Button) getDialogPane().lookupButton(ButtonType.OK);
		buttonOk.addEventFilter(ActionEvent.ACTION, event -> {
			if (firstName.getText().isEmpty() || lastName.getText().isEmpty()) {
				showAlert();
				event.consume();
			} else {
				LocalDate localDate = birthday.getValue();
				Date dateOfBirth = null;
				if (localDate != null) {
					Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
					dateOfBirth = Date.from(instant);
				}
				guiManager.getGuiToProcessing().modifyUser(currentUser.getLogin(), firstName.getText(),
						lastName.getText(), dateOfBirth, null);
				close();
			}
		});
		show();
	}

	private void showAlert() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(ALERT_TITLE);
		alert.setHeaderText(null);
		alert.setContentText(ALERT_CONTENT);
		alert.getButtonTypes().setAll(ButtonType.CLOSE);
		alert.showAndWait();
	}
}
