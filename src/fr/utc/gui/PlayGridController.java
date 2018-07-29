package fr.utc.gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import fr.utc.dataStructure.GridLocal;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.IndexOutOfRangeException;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.gui.modals.ExceptionAlert;
import fr.utc.gui.modals.RateCommentDialog;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class PlayGridController extends AbstractController {
	private static final String ALERT_UNSOLVED_CONTENT = "Check your grid!\nYou have done some mistakes.";
	private static final String ALERT_UNSOLVED_TITLE = "Unsolved grid";
	private static final String DISCONNECTED_USER = "Disconnected user";
	private GridLocal gridLocal;
	@FXML
	private Button reset;
	@FXML
	private Button save;
	@FXML
	private Button submit;
	@FXML
	private GridPane gridCase = new GridPane();
	@FXML
	private Label gridNameLabel;
	@FXML
	private Label ownerNameLabel;

	public PlayGridController(GridLocal gridLocal) {
		super();
		this.gridLocal = gridLocal;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		reset.setOnAction(this::onReset);
		save.setOnAction(this::onSave);
		submit.setOnAction(this::onSubmit);

		try {
			UserNetwork gridOwner = guiManager.getGuiToProcessing().getGridOwner(gridLocal);
			if (gridOwner != null) {
				ownerNameLabel.setText(gridOwner.getFirstName() + " " + gridOwner.getLastName());
			} else {
				ownerNameLabel.setText(DISCONNECTED_USER);
			}
			gridNameLabel.setText(gridLocal.getName());
		} catch (NetworkSocketException e) {
			new ExceptionAlert(e);
		}

		drawGrid();
		drawSavedState();
	}

	private void drawGrid() {
		List<Integer> gridList = gridLocal.getGrid();
		int index = 0;
		for (Node node : gridCase.getChildren()) {
			if (node instanceof TextField) {
				TextField field = (TextField) node;
				if (gridList.get(index) != null) {
					field.setText(gridList.get(index).toString());
					field.setDisable(true);
				} else {
					field.setText("");
					field.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
						if (!newValue.matches("[1-9]|$")) {
							field.setText(oldValue);
						}
					});
				}
				index++;
			}
		}
	}

	private void drawSavedState() {
		List<Integer> savedState = gridLocal.getSavedState();
		int index = 0;
		for (Node node : gridCase.getChildren()) {
			if (node instanceof TextField) {
				TextField field = (TextField) node;
				if (savedState.get(index) != null) {
					field.setText(savedState.get(index).toString());
				}
				index++;
			}
		}
	}

	private void unsolvedGrid() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(ALERT_UNSOLVED_TITLE);
		alert.setHeaderText(null);
		alert.setContentText(ALERT_UNSOLVED_CONTENT);
		alert.getButtonTypes().setAll(ButtonType.CLOSE);
		alert.showAndWait();
	}

	private void solvedGrid() {
		try {
			if (!guiManager.getGuiToProcessing().currentUserIsGridOwner()) {
				new RateCommentDialog(guiManager);
			}
		} catch (NetworkSocketException e) {
			new ExceptionAlert(e);
		}
	}

	@FXML
	private void onReset(ActionEvent event) {
		drawGrid();
	}

	@FXML
	private void onSubmit(ActionEvent event) {
		boolean isSolved = false;
		try {
			List<Integer> filledGrid = ControllerUtils.getFilledGrid(gridCase);
			isSolved = guiManager.getGuiToProcessing().checkGrid(filledGrid);
		} catch (IndexOutOfRangeException e) {
			new ExceptionAlert(e);
		}

		if (isSolved) {
			solvedGrid();
			close();
		} else {
			unsolvedGrid();
		}
	}

	@FXML
	private void onSave(ActionEvent event) {
		guiManager.getGuiToProcessing().savePlayedGrid(ControllerUtils.getFilledGrid(gridCase));
		close();
	}

	@Override
	public void close() {
		if (guiManager.getPlayGridController() != null) {
			guiManager.setPlayGridController(null);
		}
		stage.close();
	}
}
