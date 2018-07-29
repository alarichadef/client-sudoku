package fr.utc.gui;

import java.util.List;

import fr.utc.dataStructure.GridLocal;
import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.UserLocal;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.gui.tasks.DisplayGridTask;
import fr.utc.gui.tasks.UpdateConnectedUserListTask;
import fr.utc.gui.tasks.UpdateGridListTask;
import fr.utc.interfaces.ProcessingToGUI;
import javafx.application.Platform;

public class ProcessingToGUIImpl implements ProcessingToGUI {
	private UpdateConnectedUserListTask updateConnectedUserListTask;
	private UpdateGridListTask updateGridList;
	private DisplayGridTask displayGridTask;
	
	public ProcessingToGUIImpl(GUIManager guiManager) {
		updateConnectedUserListTask = new UpdateConnectedUserListTask(guiManager);
		updateGridList = new UpdateGridListTask(guiManager);
		displayGridTask = new DisplayGridTask(guiManager);
	}

	@Override
	public void updateGridList(List<GridNetwork> gridList) { 
		updateGridList.setGridListParameter(gridList);
		Platform.runLater(updateGridList);
	}

	@Override
	public void displayGridFromNetwork(GridLocal gridLocal) {
		displayGridTask.setGridLocalParameter(gridLocal);		
		Platform.runLater(displayGridTask);
	}

	@Override
	public void displayUser(UserLocal userLocal) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public void updateConnectedUserList(List<UserNetwork> userList) {
		updateConnectedUserListTask.setConnectedUserListParameter(userList);
		Platform.runLater(updateConnectedUserListTask);
	}
}
