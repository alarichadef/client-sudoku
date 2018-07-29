package fr.utc.gui.tasks;

import java.util.List;

import fr.utc.dataStructure.UserNetwork;
import fr.utc.gui.GUIManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class UpdateConnectedUserListTask implements Runnable {
	private GUIManager guiManager;
	private List<UserNetwork> connectedUserListParameter;

	public UpdateConnectedUserListTask(GUIManager guiManager) {
		super();
		this.guiManager = guiManager;
	}

	public List<UserNetwork> getConnectedUserListParameter() {
		return connectedUserListParameter;
	}

	public void setConnectedUserListParameter(List<UserNetwork> connectedUserListParameter) {
		this.connectedUserListParameter = connectedUserListParameter;
	}

	@Override
	public void run() {
		ObservableList<UserNetwork> connectedUserList = FXCollections.observableArrayList(connectedUserListParameter);
		guiManager.getHomeController().getDefaultGroupList().setItems(connectedUserList);
		guiManager.getHomeController().getDefaultGroupList().refresh();
	}
}