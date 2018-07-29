package fr.utc.interfaces;


import java.util.List;

import fr.utc.dataStructure.GridLocal;
import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.URI;
import fr.utc.dataStructure.UserLocal;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.NetworkSocketException;

public interface NetworkingToProcessing {
	
	public boolean addNewUser(UserNetwork user);

	public List<GridNetwork> getAllGrids();

	public void addGrids(List<GridNetwork> grids);

	public void removeUserNetworkFromList(UserNetwork user);

	public List<UserNetwork> getAllUsers();

	public UserNetwork RequestProfilWithUser(UserNetwork user);

	public List<GridNetwork> getGridsByName(String name);

	public List<GridNetwork> getGridsByLevel(int level);

	public List<GridNetwork> getGridsByTag(String tag);

	public void addNewComment(GridNetwork grid, String comment, UserNetwork user) throws NetworkSocketException;
	
	public UserLocal getUserLocal();
	
	public void removeGridFromUser(UserNetwork user);
	
	public GridLocal getGridLocal(String Uuid);

	void addNewMark(GridNetwork grid, int mark, UserNetwork user) throws NetworkSocketException;

	URI getURIByGrid(GridNetwork grid) throws NetworkSocketException;

	void updateUsersList();

	void updateGridsList();
	
}
