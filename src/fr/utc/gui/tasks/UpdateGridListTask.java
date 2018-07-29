package fr.utc.gui.tasks;

import java.util.List;

import fr.utc.dataStructure.GridNetwork;
import fr.utc.gui.GUIManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UpdateGridListTask implements Runnable {
	private GUIManager guiManager;
	private List<GridNetwork> gridListParameter;

	public UpdateGridListTask(GUIManager guiManager) {
		super();
		this.guiManager = guiManager;
	}

	public List<GridNetwork> getGridListParameter() {
		return gridListParameter;
	}

	public void setGridListParameter(List<GridNetwork> gridListParameter) {
		this.gridListParameter = gridListParameter;
	}

	@Override
	public void run() {
		ObservableList<GridNetwork> gridListObservable = FXCollections.observableArrayList(gridListParameter);
		guiManager.getHomeController().getGridTableView().setItems(gridListObservable);
		guiManager.getHomeController().getGridTableView().refresh();
	}
}