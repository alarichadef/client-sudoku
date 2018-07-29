package fr.utc.processing.managers;

import java.util.UUID;

import fr.utc.dataStructure.Comment;
import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.URI;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.BadRatingException;
import fr.utc.exceptions.CurrentUserIsGridOwner;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.exceptions.NullCommentException;
import fr.utc.exceptions.NullUserNetworkException;
import fr.utc.interfaces.ProcessingToGUI;
import fr.utc.interfaces.ProcessingToNetworking;
import fr.utc.processing.GUIToProcessingImpl;
import fr.utc.processing.NetworkingToProcessingImpl;

public class ProcessingManager {
	private GridManager gridManager;
	private UserManager userManager;
	private GUIToProcessingImpl guiToProcessing;
	private NetworkingToProcessingImpl networkingToProcessing;
	private ProcessingToNetworking processingToNetworking;
	private ProcessingToGUI processingToGUI;

	public GUIToProcessingImpl getGuiToProcessing() {
		return guiToProcessing;
	}

	public void setGuiToProcessing(GUIToProcessingImpl guiToProcessing) {
		this.guiToProcessing = guiToProcessing;
	}

	public ProcessingToNetworking getProcessingToNetworking() {
		return processingToNetworking;
	}

	public void setProcessingToNetworking(ProcessingToNetworking processingToNetworking) {
		this.processingToNetworking = processingToNetworking;
	}

	public ProcessingToGUI getProcessingToGUI() {
		return processingToGUI;
	}

	public void setProcessingToGUI(ProcessingToGUI processingToGUI) {
		this.processingToGUI = processingToGUI;
	}

	public ProcessingManager() {
		guiToProcessing = new GUIToProcessingImpl(this);
		networkingToProcessing = new NetworkingToProcessingImpl(this);
		gridManager = new GridManager();
		gridManager.setProcessingManager(this);
		userManager = new UserManager();
		userManager.setProcessingManager(this);
	}

	public GridManager getGridManager() {
		return gridManager;
	}

	public void setGridManager(GridManager gridManager) {
		this.gridManager = gridManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public NetworkingToProcessingImpl getNetworkingToProcessing() {
		return networkingToProcessing;
	}

	public void setNetworkingToProcessing(NetworkingToProcessingImpl np) {
		networkingToProcessing = np;
	}
	
	public UserNetwork getGridOwner(GridNetwork grid) throws NetworkSocketException {
		return getUserManager().getUserByUuid(grid.getCreatorUuid());
	}
	
	/**
	 * Add a rating and a comment to the current grid
	 * @param mark
	 * @param comment
	 * @throws NullCommentException
	 * @throws NullUserNetworkException
	 * @throws BadRatingException
	 * @throws NetworkSocketException 
	 * @throws CurrentUserIsGridOwner 
	 */
	public void rateAndComment(int mark, String comment) throws NullCommentException, NullUserNetworkException, BadRatingException, NetworkSocketException, CurrentUserIsGridOwner {
		/* if the comment is not empty and if the user is not the grid's owner, he can comment it*/
		if (!comment.isEmpty() && !"".equals(comment) && !this.currentUserIsGridOwner()){
			for(URI receiver : this.getNetworkingToProcessing().getUserLocal().getAllNodes()){		
				this.processingToNetworking.sendGridRatingToOwner(mark, this.getGridManager().getCurrentLocalGrid(), this.getUserManager().getCurrentUser(), receiver);
				this.processingToNetworking.sendGridCommentToOwner(comment, this.getGridManager().getCurrentLocalGrid(), this.getUserManager().getCurrentUser(), receiver);
			}
		} else {
			throw new NullCommentException(
					"The comment you're trying to add is null");
		}
	}

	/**
	 * Check if current user is the grid owner
	 * @return true if current user is the grid owner, return false if current user is not the grid owner
	 * @throws NetworkSocketException
	 */
	public boolean currentUserIsGridOwner() throws NetworkSocketException {
		return this.userManager.getCurrentUser().getUuid().toString().equals(this.getGridManager().getCurrentLocalGrid().getCreatorUuid());
	}
}
