package fr.utc.interfaces;

import java.util.List;

import fr.utc.dataStructure.GridLocal;
import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.UserLocal;
import fr.utc.dataStructure.UserNetwork;

public interface ProcessingToGUI {

	public void updateConnectedUserList(List<UserNetwork> userList);

	public void updateGridList(List<GridNetwork> gridList);

	public void displayGridFromNetwork(GridLocal gridLocal);

	public void displayUser(UserLocal userLocal);

}
