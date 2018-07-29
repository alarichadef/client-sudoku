package fr.utc.gui.modals;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import fr.utc.gui.AbstractController;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class ExceptionAlert extends Alert {
	private static final String TITLE = "Error";
	private static final String STACKTRACE_HEADER = "The exception stacktrace was:";
	private static final String CLIPBOARD_BUTTON = "Copy to clipboard";
	private TextArea stacktraceArea;
	private GridPane alertContent;

	public ExceptionAlert(Throwable throwableToDisplay) {
		super(AlertType.ERROR);

		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(AbstractController.class.getResourceAsStream(AbstractController.ICON)));

		stacktraceArea = new TextArea();
		alertContent = new GridPane();
		alertContent.setHgap(5);
		alertContent.setVgap(5);

		setTitle(TITLE);
		setHeaderText(throwableToDisplay.getMessage());
		setContentText(throwableToDisplay.getClass().getName());

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwableToDisplay.printStackTrace(pw);
		String exceptionText = sw.toString();

		stacktraceArea.setText(exceptionText);
		stacktraceArea.setEditable(false);
		stacktraceArea.setWrapText(true);
		stacktraceArea.setMaxWidth(Double.MAX_VALUE);
		stacktraceArea.setMaxHeight(Double.MAX_VALUE);

		GridPane.setVgrow(stacktraceArea, Priority.ALWAYS);
		GridPane.setHgrow(stacktraceArea, Priority.ALWAYS);

		alertContent.setMaxWidth(Double.MAX_VALUE);
		alertContent.add(new Label(STACKTRACE_HEADER), 0, 0);
		alertContent.add(stacktraceArea, 0, 1);

		getDialogPane().setExpandableContent(alertContent);
		getButtonTypes().setAll(new ButtonType(CLIPBOARD_BUTTON, ButtonData.OTHER), ButtonType.CLOSE);

		Optional<ButtonType> result = showAndWait();
		result.ifPresent(event -> {
			if (event.getButtonData() == ButtonData.OTHER) {
				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();
				content.putString(exceptionText);
				clipboard.setContent(content);
			}
		});
	}
}
