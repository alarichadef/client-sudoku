package fr.utc.processing;

import java.awt.image.BufferedImage;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.utc.dataStructure.GridLocal;
import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.Group;
import fr.utc.dataStructure.URI;
import fr.utc.dataStructure.UserLocal;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.BadRatingException;
import fr.utc.exceptions.CurrentUserIsGridOwner;
import fr.utc.exceptions.IndexOutOfRangeException;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.exceptions.NullCommentException;
import fr.utc.exceptions.NullGridLocalException;
import fr.utc.exceptions.NullGroupException;
import fr.utc.exceptions.NullUserNetworkException;
import fr.utc.processing.managers.ProcessingManager;

public class GUIToProcessingImpl implements fr.utc.interfaces.GUIToProcessing {

	private ProcessingManager processingManager;

	private Logger logger;
	public GUIToProcessingImpl(ProcessingManager processingManager)
	{
		this.processingManager = processingManager;
		logger = Logger.getLogger(getClass().getName());
	}

	@Override
	public GridLocal getLocalGrid(UUID uuid) throws NetworkSocketException {
		return (GridLocal)processingManager.getGridManager().getGridByUuid(uuid);
	}

	@Override
	public UserNetwork getGridOwner(GridNetwork grid) throws NetworkSocketException {
		return processingManager.getGridOwner(grid);
	}
	@Override
	public List<UserNetwork> getAllUsers() {
		return processingManager.getUserManager().getAllUsers();
	}

	@Override
	public List<Group> getAllGroups() {
		return processingManager.getUserManager().getCurrentUser().getAllGroups();
	}

	@Override
	public void addUserToGroup(UserNetwork user, Group group) {
		try {
			processingManager.getUserManager().getCurrentUser().addUserToGroup(user, group);
		} catch (NullUserNetworkException | NullGroupException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}

	}

	@Override
	public List<GridNetwork> getAllGrids() {
		return processingManager.getGridManager().getAllGrids();
	}

	@Override
	public void createUser(String login, String password, String firstname, String lastname, Date birthday,
			BufferedImage picture) {
		processingManager.getUserManager().createUser(login, password, firstname, lastname, birthday);
	}

	@Override
	public boolean createGrid(
			String name, 
			List<String> tags, 
			List<Group> readOnlyGroup, 
			List<Group> playGroup, 
			List<Group> allRightsGroup, 
			List<Integer> grid) {
		boolean result = false;
		try {
			result = processingManager.getGridManager().createGrid(name, tags, readOnlyGroup, playGroup, allRightsGroup, grid);
		} catch (NullGridLocalException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		return result;
	}

	@Override
	public List<Integer> generateGrid(int nbFilled) throws IndexOutOfRangeException {
		List<Integer> generatedList = processingManager.getGridManager().generateGrid(nbFilled);
		while (!processingManager.getGridManager().hasSolution(generatedList)){
			generatedList = processingManager.getGridManager().generateGrid(nbFilled);
		}
		return generatedList;
	}

	@Override
	public boolean checkGrid(List<Integer> grid) throws IndexOutOfRangeException {
		return processingManager.getGridManager().checkGrid(grid);
	}

	@Override
	public void storeGrid(GridLocal grid) {
		processingManager.getGridManager().storeGrid(grid);
	}

	@Override
	public void rateAndComment(int mark, String comment) throws NullCommentException, NullUserNetworkException, BadRatingException, NetworkSocketException, CurrentUserIsGridOwner {
		//public void rateAndComment(int mark, String comment) 
		processingManager.rateAndComment(mark, comment);
	}

	@Override
	public boolean currentUserIsGridOwner() throws NetworkSocketException {
		return processingManager.currentUserIsGridOwner();
	}

	@Override
	public boolean authentication(String login, String password) {

		boolean connected = false;

		UserLocal user;
		user = processingManager.getUserManager().readUserCSV(login, password);
		if(user != null)
		{
			processingManager.getUserManager().setUserLocal(user);
			connected = true;
			processingManager.getUserManager().setUserLocal(user);
			try {
				processingManager.getUserManager().addUserToList(user);
				// Now get the local address, but not the loopback one
				Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
				for (NetworkInterface netint : Collections.list(nets))
				{
					Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
					for (InetAddress inetAddress : Collections.list(inetAddresses)) {

						if(inetAddress instanceof Inet4Address && ! inetAddress.isLoopbackAddress())
						{
							user.setUri(new URI(InetAddress.getByName(inetAddress.getHostAddress()), 2000));
						}
					}
				}
				processingManager.getUserManager().loadUris();
				processingManager.getGridManager().loadSavedGrids();
				startServer();
			} catch (NullUserNetworkException | UnknownHostException | SocketException e) {
				logger.log(Level.SEVERE, e.toString(), e);
			}
		}

		return connected;
	}

	@Override
	public void disconnect() {
		UserNetwork currentUser = new UserNetwork(processingManager.getUserManager().getCurrentUser());
		Set<URI> uris = processingManager.getUserManager().getCurrentUser().getDiscoveredNodes();
		processingManager.getProcessingToNetworking().disconnect(uris, currentUser);

		//Save Grid 
		for(GridLocal grid: processingManager.getNetworkingToProcessing().getUserLocal().getLocalGrids()){
			processingManager.getGridManager().saveLocalGrid(grid);
		}
		
		//When we disconnect, we clear all the managers
		processingManager.getUserManager().clear();
		processingManager.getGridManager().clear();
	}

	@Override
	public void modifyUser(String login, String firstname, String lastname, Date birthday, BufferedImage picture) {
		UserLocal user = processingManager.getUserManager().getCurrentUser();
		user.setLogin(login);
		user.setFirstName(firstname);
		user.setLastName(lastname);
		user.setBirthday(birthday);
		ArrayList<UserNetwork> liste = (ArrayList<UserNetwork>)processingManager.getUserManager().getAllUsers();
		processingManager.getUserManager().updateUserCsv(user);
		processingManager.getProcessingToGUI().updateConnectedUserList(liste);
	}

	@Override
	public void discoverNetwork(List<UserNetwork> users) {
		try {
			processingManager.getProcessingToNetworking().connect();
		} catch (NetworkSocketException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	@Override
	public List<GridNetwork> getGridsByName(String name) {
		return processingManager.getGridManager().getGridsByName(name);
	}

	@Override
	public List<GridNetwork> getGridsByTag(String tag) {
		return processingManager.getGridManager().getGridsByTag(tag);
	}

	@Override
	public List<GridNetwork> getGridsByMark(int rating) {
		return processingManager.getGridManager().getGridsByRating(rating);
	}

	@Override
	public void startServer() {
		try {
			processingManager.getProcessingToNetworking().connect();
		} catch (NetworkSocketException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	@Override
	public UserLocal getLocalUser(String uuid) throws NetworkSocketException {
		return (UserLocal)processingManager.getUserManager().getUserByUuid(uuid);
	}

	@Override
	public UserLocal getCurrentUser() {
		return processingManager.getUserManager().getCurrentUser();
	}

	@Override
	public Integer getCaseByRowAndByColumn(int row, int column, GridLocal grid) throws IndexOutOfRangeException {
		try {
			return processingManager.getGridManager().getCaseByRowAndByColumn(row, column, grid);
		} catch (IndexOutOfRangeException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		return null;
	}

	@Override
	public void saveFileGrid(GridLocal grid) {
		processingManager.getGridManager().saveLocalGrid(grid);		
	}

	@Override
	public void addUrisToUser(String uris) throws NetworkSocketException {	
		UserLocal user = processingManager.getUserManager().getCurrentUser();

		if("".equals(uris))
		{
			user.getUserNodes().clear();
		}
		else
		{
			String[] uriLines = uris.split("\\r?\\n");

			user.getUserNodes().clear();

			for(int i=0; i<uriLines.length; i++)
			{
				String[] uri = uriLines[i].split(":");
				String adress = uri[0];
				int port = Integer.parseInt(uri[1]);
				try {
					user.addUserNode(new URI(InetAddress.getByName(adress), port));
				} catch (UnknownHostException e) {
					logger.log(Level.SEVERE, e.toString(), e);
				}
			}
		}

		processingManager.getUserManager().saveUris();
		processingManager.getProcessingToNetworking().updateNetwork();
	}

	@Override
	public String getUrisFromUser() {
		UserLocal user = processingManager.getUserManager().getCurrentUser();
		Set<URI> uris = user.getUserNodes();

		StringBuilder builder= new StringBuilder();

		Iterator<URI> it = uris.iterator();
		while(it.hasNext())
		{
			URI uri = it.next();
			builder.append(uri.getAddress().getHostAddress());
			builder.append(":");
			builder.append(uri.getPort());
			builder.append("\n");
		}
		return builder.toString();
	}
	
	@Override
	public void savePlayedGrid(List<Integer> gridState)
	{
		GridLocal currentGrid = processingManager.getGridManager().getCurrentLocalGrid();
		currentGrid.setSavedState(gridState);
		
		processingManager.getGridManager().savePlayedGrid(currentGrid);
	}

}



