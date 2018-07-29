package fr.utc.gui.modals;

import java.util.Optional;

import fr.utc.exceptions.NetworkSocketException;
import fr.utc.gui.AbstractController;
import fr.utc.gui.GUIManager;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

public class IpManagementDialog extends Dialog<Pair<String, String>> {
	private static final String TITLE = "Manage IPs";
	private static final String HEADER = "Insert nodes you want to connect to.";
	private static final String NODESIP_HEADER = "Nodes IPs";
	private TextArea nodesIp;
	private GridPane dialogContent;

	public IpManagementDialog(GUIManager guiManager) {
		super();

		Stage stage = (Stage) getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(AbstractController.class.getResourceAsStream(AbstractController.ICON)));

		nodesIp = new TextArea();
		dialogContent = new GridPane();
		dialogContent.setHgap(5);
		dialogContent.setVgap(5);

		setTitle(TITLE);
		setHeaderText(HEADER);

		String uris = guiManager.getGuiToProcessing().getUrisFromUser();
		nodesIp.setText(uris);

		setResultConverter(dialogButton -> {
			if (dialogButton == ButtonType.OK) {
				return new Pair<>(NODESIP_HEADER, nodesIp.getText());
			}
			return null;
		});

		dialogContent.add(new Label(NODESIP_HEADER), 0, 1);
		dialogContent.add(nodesIp, 1, 1);

		getDialogPane().getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
		getDialogPane().setContent(dialogContent);

		Optional<Pair<String, String>> result = showAndWait();
		result.ifPresent(event -> {
			try {
				guiManager.getGuiToProcessing().addUrisToUser(event.getValue().toString());
			} catch (NetworkSocketException e) {
				new ExceptionAlert(e);
			}
		});
	}
}
