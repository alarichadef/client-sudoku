package fr.utc.gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import fr.utc.dataStructure.GridLocal;
import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.UserLocal;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.gui.modals.DisplayUserAlert;
import fr.utc.gui.modals.EditUserDialog;
import fr.utc.gui.modals.ExceptionAlert;
import fr.utc.gui.modals.IpManagementDialog;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

public class HomeController extends AbstractController {
	private static final String DISCONNECTED_USER = "Disconnected user";
	@FXML
	private ListView<UserNetwork> defaultGroupList;
	@FXML
	private TableView<GridNetwork> gridTableView;
	@FXML
	private TableColumn<GridNetwork, String> gridColumnName;
	@FXML
	private TableColumn<GridNetwork, String> gridColumnOwner;
	@FXML
	private TableColumn<GridNetwork, String> gridColumnTags;
	@FXML
	private TableColumn<GridNetwork, String> gridColumnRating;
	@FXML
	private TextField gridSearchText;
	@FXML
	private ToggleGroup searchGroup;
	@FXML
	private RadioButton searchByNameRadio;
	@FXML
	private RadioButton searchByTagRadio;
	@FXML
	private RadioButton searchByMarkRadio;

	public ListView<UserNetwork> getDefaultGroupList() {
		return defaultGroupList;
	}

	public void setDefaultGroupList(ListView<UserNetwork> defaultGroupList) {
		this.defaultGroupList = defaultGroupList;
	}

	public TableView<GridNetwork> getGridTableView() {
		return gridTableView;
	}

	public void setGridTableView(TableView<GridNetwork> gridTableView) {
		this.gridTableView = gridTableView;
	}

	public TableColumn<GridNetwork, String> getGridColumnName() {
		return gridColumnName;
	}

	public void setGridColumnName(TableColumn<GridNetwork, String> gridColumnName) {
		this.gridColumnName = gridColumnName;
	}

	public TableColumn<GridNetwork, String> getGridColumnOwner() {
		return gridColumnOwner;
	}

	public void setGridColumnOwner(TableColumn<GridNetwork, String> gridColumnOwner) {
		this.gridColumnOwner = gridColumnOwner;
	}

	public TableColumn<GridNetwork, String> getGridColumnTags() {
		return gridColumnTags;
	}

	public void setGridColumnTags(TableColumn<GridNetwork, String> gridColumnTags) {
		this.gridColumnTags = gridColumnTags;
	}

	public TableColumn<GridNetwork, String> getGridColumnRating() {
		return gridColumnRating;
	}

	public void setGridColumnRating(TableColumn<GridNetwork, String> gridColumnRating) {
		this.gridColumnRating = gridColumnRating;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		searchByMarkRadio.setUserData("rate");
		searchByNameRadio.setUserData("name");
		searchByTagRadio.setUserData("tag");

		setupDefaultGroupList();
		setupGridTableView();

		gridColumnName.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
		gridColumnTags.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getTags().toString()));
		gridColumnRating
				.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getAverageRating().toString()));

		List<UserNetwork> connectedUserList = guiManager.getGuiToProcessing().getAllUsers();
		guiManager.getProcessingToGUI().updateConnectedUserList(connectedUserList);
		List<GridNetwork> gridList = guiManager.getGuiToProcessing().getAllGrids();
		guiManager.getProcessingToGUI().updateGridList(gridList);
	}

	private void setupDefaultGroupList() {
		defaultGroupList.setCellFactory(param -> {
			ListCell<UserNetwork> cell = new ListCell<UserNetwork>() {
				@Override
				protected void updateItem(UserNetwork item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null || empty) {
						setText(null);
						setTooltip(null);
					} else {
						setText(item.getFirstName() + " " + item.getLastName());
						setTooltip(new Tooltip(item.getLogin() + "\n" + item.getUri()));
					}
				}
			};
			cell.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !cell.isEmpty()) {
					UserNetwork rowData = cell.getItem();
					UserLocal userLocal;
					try {
						userLocal = guiManager.getGuiToProcessing().getLocalUser(rowData.getUuid().toString());
						if (userLocal != null) {
							consultUser(userLocal);
						}
					} catch (NetworkSocketException e) {
						new ExceptionAlert(e);
					}
				}
			});
			return cell;
		});
	}

	private void setupGridTableView() {
		gridTableView.setRowFactory(param -> {
			TableRow<GridNetwork> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					GridNetwork rowData = row.getItem();
					GridLocal gridLocal;
					try {
						gridLocal = guiManager.getGuiToProcessing().getLocalGrid(rowData.getUuid());
						if (gridLocal != null) {
							consultGrid(gridLocal);
						}
					} catch (NetworkSocketException e) {
						new ExceptionAlert(e);
					}
				}
			});
			return row;
		});

		gridColumnOwner.setCellValueFactory(param -> {
			UserNetwork owner;
			try {
				owner = guiManager.getGuiToProcessing().getGridOwner(param.getValue());
				if (owner != null) {
					return new SimpleStringProperty(owner.getFirstName() + " " + owner.getLastName());
				} else {
					return new SimpleStringProperty(DISCONNECTED_USER);
				}
			} catch (NetworkSocketException e) {
				new ExceptionAlert(e);
			}
			return null;
		});
	}

	public void consultGrid(GridLocal gridLocal) {
		PlayGridController playGridController = guiManager.getPlayGridController();
		if (playGridController == null) {
			ConsultGridController consultGridController = guiManager.getConsultGridController();
			if (consultGridController != null) {
				consultGridController.close();
			}
			try {
				consultGridController = new ConsultGridController(gridLocal);
				consultGridController.showScene(new Stage(), FXML_CONSULTGRID, "Consult " + gridLocal.getName());
				guiManager.setConsultGridController(consultGridController);
			} catch (IOException e) {
				logger.log(Level.SEVERE, e.toString(), e);
			}
		}
	}

	public void consultUser(UserLocal userLocal) {
		new DisplayUserAlert(userLocal);
	}

	@FXML
	private void onClickLogout(ActionEvent event) {
		close();
	}

	@FXML
	private void onCreateGrid(ActionEvent event) {
		try {
			CreateGridController createGridController = guiManager.getCreateGridController();
			if (createGridController == null) {
				createGridController = new CreateGridController();
				createGridController.showScene(new Stage(), FXML_CREATEGRID, "New grid");
				guiManager.setCreateGridController(createGridController);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	@FXML
	private void onModifyProfile(ActionEvent event) {
		new EditUserDialog(guiManager);
	}

	@FXML
	private void onOpenIpManagement(ActionEvent event) {
		new IpManagementDialog(guiManager);
	}

	public void searchGrids() {
		String searchText = gridSearchText.getText();
		if (!searchText.trim().isEmpty()) {
			String selectedToggle = searchGroup.getSelectedToggle().getUserData().toString();
			switch (selectedToggle) {
			case "name":
				guiManager.getProcessingToGUI()
						.updateGridList(guiManager.getGuiToProcessing().getGridsByName(searchText));
				break;
			case "tag":
				guiManager.getProcessingToGUI()
						.updateGridList(guiManager.getGuiToProcessing().getGridsByTag(searchText));
				break;
			case "rate":
				if (searchText.matches("[0-5]|$")) {
					guiManager.getProcessingToGUI().updateGridList(
							guiManager.getGuiToProcessing().getGridsByMark(Integer.parseInt(searchText)));
				}
				break;
			default:
				break;
			}
		} else {
			guiManager.getProcessingToGUI().updateGridList(guiManager.getGuiToProcessing().getAllGrids());
		}
	}

	@Override
	public void close() {
		ConsultGridController consultGridController = guiManager.getConsultGridController();
		if (consultGridController != null) {
			consultGridController.close();
		}
		CreateGridController createGridController = guiManager.getCreateGridController();
		if (createGridController != null) {
			createGridController.close();
		}
		PlayGridController playGridController = guiManager.getPlayGridController();
		if (playGridController != null) {
			playGridController.close();
		}
		guiManager.getGuiToProcessing().disconnect();
		Platform.exit();
	}
}
