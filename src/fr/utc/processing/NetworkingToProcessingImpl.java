package fr.utc.processing;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.utc.dataStructure.*;
import fr.utc.exceptions.BadRatingException;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.exceptions.NullCommentException;
import fr.utc.exceptions.NullUserNetworkException;
import fr.utc.interfaces.NetworkingToProcessing;
import fr.utc.processing.managers.ProcessingManager;

public class NetworkingToProcessingImpl implements NetworkingToProcessing {
	private ProcessingManager processingManager;
	private Logger logger;

	public NetworkingToProcessingImpl(ProcessingManager processManager) {
		processingManager = processManager;
		logger = Logger.getLogger(getClass().getName());
	}

	@Override
	public boolean addNewUser(UserNetwork user) {
		boolean existe = false;
		for(UserNetwork u : processingManager.getUserManager().getAllUsers())
			if(u.getUuid().equals(user.getUuid()))
				existe=true;

		// Adding the new user to the connectedUser of the current user
		if(!existe){
			processingManager.getUserManager().getAllUsers().add(user);
			// Updating the GUI list of connected users
			processingManager.getProcessingToGUI().updateConnectedUserList(processingManager.getUserManager().getAllUsers());
			processingManager.getProcessingToGUI().updateGridList(processingManager.getGridManager().getAllGrids());			
		}
		return existe;
	}

	@Override
	public List<GridNetwork> getAllGrids() {
		return processingManager.getGridManager().getAllGrids();
	}

	@Override
	public void addGrids(List<GridNetwork> grids) {
		for(GridNetwork newGrid: grids){
			boolean existe = false;
			for(GridNetwork grid : processingManager.getGridManager().getAllGrids()){
				if(grid.getUuid().equals(newGrid.getUuid()))
					existe=true;
			}
			// Adding the new grid to the gridNetwork of the current user
			if(!existe){
				List<Integer> initialGridState = ((GridLocal) newGrid).getGrid();
				((GridLocal) newGrid).setSavedState(initialGridState);
				processingManager.getGridManager().getNetworkGrids().add(newGrid);
			}
		}
		processingManager.getProcessingToGUI().updateGridList(processingManager.getGridManager().getAllGrids());
	}

	@Override
	public void removeUserNetworkFromList(UserNetwork user) {
		UserNetwork userToRemove = null;
		for(UserNetwork u:processingManager.getUserManager().getAllUsers())
			if(u.getUuid().equals(user.getUuid()))
				userToRemove = u;

		processingManager.getUserManager().getAllUsers().remove(userToRemove);
		processingManager.getProcessingToGUI().updateConnectedUserList(processingManager.getUserManager().getAllUsers());
	}

	@Override
	public List<UserNetwork> getAllUsers() {
		return processingManager.getUserManager().getAllUsers();
	}

	@Override
	public UserNetwork RequestProfilWithUser(UserNetwork user) {
		return null;
	}

	@Override
	public List<GridNetwork> getGridsByName(String name) {
		return processingManager.getGridManager().getGridsByName(name);
	}

	@Override
	public List<GridNetwork> getGridsByLevel(int level) {
		return processingManager.getGridManager().getGridsByRating(level);
	}

	@Override
	public List<GridNetwork> getGridsByTag(String tag) {
		return processingManager.getGridManager().getGridsByTag(tag);
	}

	@Override
	public void addNewComment(GridNetwork grid, String comment, UserNetwork user) throws NetworkSocketException {
		try {
			this.processingManager.getGridManager().getGridByUuid(grid.getUuid()).addComment(new Comment(comment, user));
		} catch (NullCommentException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}
	
	@Override
	public void addNewMark(GridNetwork grid, int mark, UserNetwork user) throws NetworkSocketException{
		try {
			this.processingManager.getGridManager().getGridByUuid(grid.getUuid()).addRating(user, mark);
			this.processingManager.getProcessingToGUI().updateGridList(this.processingManager.getGridManager().getAllGrids());
		} catch (NullUserNetworkException | BadRatingException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}
	
	@Override
	public void updateUsersList(){
		this.processingManager.getProcessingToGUI().updateConnectedUserList(this.processingManager.getUserManager().getAllUsers());
	}
	
	@Override
	public void updateGridsList(){
		this.processingManager.getProcessingToGUI().updateGridList(this.processingManager.getGridManager().getAllGrids());
	}
		
	@Override
	public URI getURIByGrid(GridNetwork grid) throws NetworkSocketException{
		return this.processingManager.getUserManager().getUserByUuid(grid.getCreatorUuid()).getUri();
	}

	@Override
	public UserLocal getUserLocal() {
		return processingManager.getUserManager().getCurrentUser();
	}

	@Override	
	public void removeGridFromUser(UserNetwork user){
		for(GridNetwork grid : user.getSharableGrids()){
			if(!gridIsPlayed(grid))
			{
				System.out.println("to delete "+grid.getUuid());
				System.out.println(processingManager.getGridManager().getNetworkGrids().remove(grid));
			}
		}
		processingManager.getProcessingToGUI().updateGridList(processingManager.getGridManager().getAllGrids());
	}

	@Override		
	public GridLocal getGridLocal(String uuid){
		return processingManager.getGridManager().getGridsByUuid(uuid);
	}
	
	private boolean gridIsPlayed(GridNetwork grid)
	{
		boolean gridIsPlayed = false;
		Iterator<GridLocal> it = processingManager.getUserManager().getCurrentUser().getPlayedGrids().iterator();
		
		while(it.hasNext())
		{
			GridLocal nextGrid = it.next();
			if(nextGrid.getUuid().equals(grid.getUuid()))
			{
				gridIsPlayed = true;
			}
		}
		return gridIsPlayed;
	}
}
