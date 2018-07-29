package fr.utc.gui.modals;

import fr.utc.exceptions.BadRatingException;
import fr.utc.exceptions.CurrentUserIsGridOwner;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.exceptions.NullCommentException;
import fr.utc.exceptions.NullUserNetworkException;
import fr.utc.gui.AbstractController;
import fr.utc.gui.GUIManager;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Pair;

public class RateCommentDialog extends Dialog<Pair<String, String>> {
	private static final String TITLE = "Congratulations";
	private static final String HEADER = "Give your opinion about this grid.";
	private static final String RATING_HEADER = "Rating";
	private static final String COMMENT_HEADER = "Comment";
	private TextArea comment;
	private HBox vbradio;
	private GridPane dialogContent;

	public RateCommentDialog(GUIManager guiManager) {
		super();

		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(AbstractController.class.getResourceAsStream(AbstractController.ICON)));

		comment = new TextArea();
		vbradio = new HBox();
		dialogContent = new GridPane();
		dialogContent.setHgap(5);
		dialogContent.setVgap(5);

		setTitle(TITLE);
		setHeaderText(HEADER);

		ToggleGroup rateGroup = new ToggleGroup();
		for (int i = 1; i <= 5; i++) {
			RadioButton radio = new RadioButton(Integer.toString(i));
			radio.setToggleGroup(rateGroup);
			radio.setUserData(i);
			vbradio.getChildren().add(radio);
		}

		dialogContent.add(new Label(RATING_HEADER), 0, 0);
		dialogContent.add(vbradio, 1, 0);
		dialogContent.add(new Label(COMMENT_HEADER), 0, 2);
		dialogContent.add(comment, 1, 2);

		getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
		getDialogPane().setContent(dialogContent);

		final Button buttonOk = (Button) getDialogPane().lookupButton(ButtonType.OK);
		buttonOk.addEventFilter(ActionEvent.ACTION, event -> {
			try {
				guiManager.getGuiToProcessing().rateAndComment(
						Integer.parseInt(rateGroup.getSelectedToggle().getUserData().toString()), comment.getText());
			} catch (NumberFormatException | NullCommentException | NullUserNetworkException | BadRatingException
					| NetworkSocketException | CurrentUserIsGridOwner e) {
				new ExceptionAlert(e);
				event.consume();
			}
		});
		show();
	}
}
