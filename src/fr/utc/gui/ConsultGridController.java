package fr.utc.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import fr.utc.dataStructure.Comment;
import fr.utc.dataStructure.GridLocal;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.gui.modals.ExceptionAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

public class ConsultGridController extends AbstractController {
	private static final String DISCONNECTED_USER = "Disconnected user";
	private GridLocal gridLocal;
	@FXML
	private Button markGrid;
	@FXML
	private Button playGrid;
	@FXML
	private GridPane gridPane;
	@FXML
	private Label name;
	@FXML
	private Label user;
	@FXML
	private Label tags;
	@FXML
	private Label mark;
	@FXML
	private ListView<Comment> commentList;

	public ConsultGridController(GridLocal gridLocal) {
		super();
		this.gridLocal = gridLocal;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Integer squareValue;

		int index = 0;
		for (Node node : gridPane.getChildren()) {
			if (node instanceof Label) {
				Label label = (Label) node;
				squareValue = gridLocal.getGrid().get(index);
				if (squareValue != null) {
					String tmp = squareValue.toString();
					label.setText(tmp);
				} else {
					squareValue = gridLocal.getSavedState().get(index);
					if (squareValue != null) {
						String tmp = squareValue.toString();
						label.setText(tmp);
						label.setTextFill(Paint.valueOf("#0096C9"));
					}
				}
			}
			index++;
		}

		try {
			UserNetwork gridOwner = guiManager.getGuiToProcessing().getGridOwner(gridLocal);
			if (gridOwner != null) {
				user.setText(gridOwner.getFirstName() + " " + gridOwner.getLastName());
			} else {
				user.setText(DISCONNECTED_USER);
			}
		} catch (NetworkSocketException e) {
			new ExceptionAlert(e);
		}
		name.setText(gridLocal.getName());
		tags.setText(gridLocal.getTags().toString());
		mark.setText(gridLocal.getAverageRating().toString());

		commentList.setCellFactory(param -> new ListCell<Comment>() {
			@Override
			protected void updateItem(Comment item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
					setTooltip(null);
				} else {
					setText(item.getComment());
					UserNetwork owner = item.getAuthor();
					if (owner != null) {
						setTooltip(new Tooltip(owner.getFirstName() + " " + owner.getLastName()));
					} else {
						setTooltip(new Tooltip(DISCONNECTED_USER));
					}
				}
			}
		});
		ObservableList<Comment> commentListObs = FXCollections.observableArrayList(gridLocal.getComments());
		commentList.setItems(commentListObs);
	}

	@FXML
	private void onPlayGrid(ActionEvent event) {
		try {
			PlayGridController playGridController = guiManager.getPlayGridController();
			if (playGridController == null) {
				playGridController = new PlayGridController(gridLocal);
			}
			playGridController.showScene(new Stage(), FXML_PLAYGRID, "Play " + gridLocal.getName());
			guiManager.setPlayGridController(playGridController);
			close();
		} catch (IOException e) {
			new ExceptionAlert(e);
		}
	}

	@Override
	public void close() {
		if (guiManager.getConsultGridController() != null) {
			guiManager.setConsultGridController(null);
		}
		stage.close();
	}
}
