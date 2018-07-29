package fr.utc.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import fr.utc.dataStructure.Group;
import fr.utc.exceptions.IndexOutOfRangeException;
import fr.utc.gui.modals.ExceptionAlert;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class CreateGridController extends AbstractController {
	private static final int DIFFICULTY_EASY = 40;
	private static final int DIFFICULTY_MEDIUM = 32;
	private static final int DIFFICULTY_HARD = 27;
	private static final String ALERT_UNSOLVABLE_CONTENT = "Please check your grid for errors.";
	private static final String ALERT_UNSOLVABLE_TITLE = "Unsolvable grid";
	private static final String ALERT_MISSING_CONTENT = "Please set mandatory information.\nName and tags are required.";
	private static final String ALERT_MISSING_TITLE = "Missing required fields";
	private static final String ALERT_GENERATE_TITLE = "Choose grid level";
	private static final String ALERT_GENERATE_CONTENT = "Please, select your grid level.";
	private static final String EMPTY_STRING = "";
	private static final String READ = "Read";
	private static final String PLAY = "Read & Play";
	private static final String MARK = "Read, Play, Mark & Comment";
	private List<TextField> gridList;
	@FXML
	private ToggleGroup defaultGroup;
	@FXML
	private ToggleGroup friendsGroup;
	@FXML
	private ToggleGroup coworkersGroup;
	@FXML
	private ToggleGroup familyGroup;
	@FXML
	private GridPane gridCase;
	@FXML
	private TextField name;
	@FXML
	private TextField tags;
	@FXML
	private RadioButton dRead;
	@FXML
	private RadioButton dPlay;
	@FXML
	private RadioButton dMark;
	@FXML
	private RadioButton frRead;
	@FXML
	private RadioButton frPlay;
	@FXML
	private RadioButton frMark;
	@FXML
	private RadioButton cRead;
	@FXML
	private RadioButton cPlay;
	@FXML
	private RadioButton cMark;
	@FXML
	private RadioButton faRead;
	@FXML
	private RadioButton faPlay;
	@FXML
	private RadioButton faMark;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		gridList = new ArrayList<>();
		for (Node node : gridCase.getChildren()) {
			if (node instanceof TextField) {
				TextField field = (TextField) node;
				gridList.add(field);
				field.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
					if (!newValue.matches("[1-9]|$")) {
						field.setText(oldValue);
					}
				});
			}
		}
	}

	@FXML
	private void onClickReset(ActionEvent event) {
		List<Integer> generatedGrid = new ArrayList<>();
		try {
			generatedGrid = guiManager.getGuiToProcessing().generateGrid(0);
		} catch (IndexOutOfRangeException e) {
			new ExceptionAlert(e);
		}
		fillGrid(generatedGrid);
	}

	@FXML
	private void onClickGenerate(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(ALERT_GENERATE_TITLE);
		alert.setHeaderText(null);
		alert.setContentText(ALERT_GENERATE_CONTENT);

		ButtonType buttonEasy = new ButtonType("Easy");
		ButtonType buttonMedium = new ButtonType("Medium");
		ButtonType buttonHard = new ButtonType("Hard");

		alert.getButtonTypes().setAll(buttonEasy, buttonMedium, buttonHard, ButtonType.CANCEL);

		Optional<ButtonType> result = alert.showAndWait();
		List<Integer> generatedGrid = new ArrayList<>();

		if (result.isPresent()) {
			try {
				if (result.get() == buttonEasy) {
					generatedGrid = guiManager.getGuiToProcessing().generateGrid(DIFFICULTY_EASY);
				} else if (result.get() == buttonMedium) {
					generatedGrid = guiManager.getGuiToProcessing().generateGrid(DIFFICULTY_MEDIUM);
				} else if (result.get() == buttonHard) {
					generatedGrid = guiManager.getGuiToProcessing().generateGrid(DIFFICULTY_HARD);
				}
				fillGrid(generatedGrid);
			} catch (IndexOutOfRangeException e) {
				new ExceptionAlert(e);
			}
		}
	}

	public void fillGrid(List<Integer> generatedGrid) {
		int i = 0;
		for (TextField field : gridList) {
			if (generatedGrid.get(i) != null) {
				field.setText(generatedGrid.get(i).toString());
			} else {
				field.setText(EMPTY_STRING);
			}
			i++;
		}
	}

	private void missingFields() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(ALERT_MISSING_TITLE);
		alert.setHeaderText(null);
		alert.setContentText(ALERT_MISSING_CONTENT);
		alert.getButtonTypes().setAll(ButtonType.CLOSE);
		alert.showAndWait();
	}

	private void unsolvableGrid() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(ALERT_UNSOLVABLE_TITLE);
		alert.setHeaderText(null);
		alert.setContentText(ALERT_UNSOLVABLE_CONTENT);
		alert.getButtonTypes().setAll(ButtonType.CLOSE);
		alert.showAndWait();
	}

	@Deprecated
	private void fillGroupRights(ArrayList<Group> readOnlyGroup, ArrayList<Group> playGroup,
			ArrayList<Group> allRightsGroup) {
		RadioButton dGroupRights = (RadioButton) defaultGroup.getSelectedToggle();
		RadioButton frGroupRights = (RadioButton) friendsGroup.getSelectedToggle();
		RadioButton cGroupRights = (RadioButton) coworkersGroup.getSelectedToggle();
		RadioButton faGroupRights = (RadioButton) familyGroup.getSelectedToggle();

		Group gDefault = null;
		Group gFriends = null;
		Group gCoworkers = null;
		Group gFamily = null;

		List<Group> allGroups = guiManager.getGuiToProcessing().getAllGroups();
		for (Group g : allGroups) {
			if ("Default".equals(g.getName())) {
				gDefault = g;
			} else if ("Friends".equals(g.getName())) {
				gFriends = g;
			} else if ("Coworkers".equals(g.getName())) {
				gCoworkers = g;
			} else if ("Family".equals(g.getName())) {
				gFamily = g;
			}
		}

		switch (dGroupRights.getText()) {
		case READ:
			readOnlyGroup.add(gDefault);
			break;
		case PLAY:
			playGroup.add(gDefault);
			break;
		case MARK:
			allRightsGroup.add(gDefault);
			break;
		default:
			break;
		}

		switch (frGroupRights.getText()) {
		case READ:
			readOnlyGroup.add(gFriends);
			break;
		case PLAY:
			playGroup.add(gFriends);
			break;
		case MARK:
			allRightsGroup.add(gFriends);
			break;
		default:
			break;
		}

		switch (cGroupRights.getText()) {
		case READ:
			readOnlyGroup.add(gCoworkers);
			break;
		case PLAY:
			playGroup.add(gCoworkers);
			break;
		case MARK:
			allRightsGroup.add(gCoworkers);
			break;
		default:
			break;
		}

		switch (faGroupRights.getText()) {
		case READ:
			readOnlyGroup.add(gFamily);
			break;
		case PLAY:
			playGroup.add(gFamily);
			break;
		case MARK:
			allRightsGroup.add(gFamily);
			break;
		default:
			break;
		}
	}

	@FXML
	private void onClickSubmit(ActionEvent event) {
		if (name.getText().isEmpty() || tags.getText().isEmpty()) {
			missingFields();
		} else {
			String[] lTags = tags.getText().trim().split(" ");

			List<Group> readOnlyGroup = new ArrayList<>();
			List<Group> playGroup = new ArrayList<>();
			List<Group> allRightsGroup = new ArrayList<>();

			boolean solvable = guiManager.getGuiToProcessing().createGrid(name.getText(), Arrays.asList(lTags),
					readOnlyGroup, playGroup, allRightsGroup, ControllerUtils.getFilledGrid(gridCase));

			if (!solvable) {
				unsolvableGrid();
			} else {
				close();
			}
		}
	}

	@Override
	public void close() {
		if (guiManager.getCreateGridController() != null) {
			guiManager.setCreateGridController(null);
		}
		stage.close();
	}
}
