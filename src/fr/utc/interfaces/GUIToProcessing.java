package fr.utc.interfaces;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import fr.utc.dataStructure.GridLocal;
import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.Group;
import fr.utc.dataStructure.UserLocal;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.BadRatingException;
import fr.utc.exceptions.CurrentUserIsGridOwner;
import fr.utc.exceptions.IndexOutOfRangeException;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.exceptions.NullCommentException;
import fr.utc.exceptions.NullUserNetworkException;

public interface GUIToProcessing {

	public GridLocal getLocalGrid(UUID uuid) throws NetworkSocketException;
	
	public UserNetwork getGridOwner(GridNetwork grid) throws NetworkSocketException;
	
	public List<UserNetwork> getAllUsers();

	public List<Group> getAllGroups();

	public void addUserToGroup(UserNetwork user, Group group);

	public List<GridNetwork> getAllGrids();

	public void createUser(String login, String password, String firstanme, String lastname, Date birthday,
			BufferedImage picture);
	
	public boolean createGrid(
			String name, 
			List<String> tags, 
			List<Group> readOnlyGroup, 
			List<Group> playGroup, 
			List<Group> allRightsGroup, 
			List<Integer> grid);

	public List<Integer> generateGrid(int nbFilled) throws IndexOutOfRangeException;
	
	public boolean checkGrid(List<Integer> grid) throws IndexOutOfRangeException;

	public void storeGrid(GridLocal grid);
	
	public void rateAndComment(int mark, String comment) throws NullCommentException, NullUserNetworkException, BadRatingException, NetworkSocketException, CurrentUserIsGridOwner;

	public boolean authentication(String login, String password);

	public void disconnect();

	public void modifyUser(String login, String firstname, String lastname, Date birthday, BufferedImage picture);

	public void discoverNetwork(List<UserNetwork> users);

	public List<GridNetwork> getGridsByName(String name);

	public List<GridNetwork> getGridsByTag(String tag);

	public List<GridNetwork> getGridsByMark(int mark);
	
	public void startServer();
	
	public void saveFileGrid(GridLocal grid);

	public UserLocal getLocalUser(String uuid) throws NetworkSocketException;

	public UserLocal getCurrentUser();

	public Integer getCaseByRowAndByColumn(int row, int column, GridLocal grid) throws IndexOutOfRangeException;

	public void addUrisToUser(String uris) throws NetworkSocketException;
	
	public String getUrisFromUser();

	boolean currentUserIsGridOwner() throws NetworkSocketException;
	
	public void savePlayedGrid(List<Integer> gridState);
}
