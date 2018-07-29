package fr.utc.interfaces;

import java.util.Set;

import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.URI;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.NetworkSocketException;

public interface ProcessingToNetworking {
			
	/**
	 * Start the server and launch the discover network procedure
	 * @throws NetworkSocketException
	 */
	public void connect() throws NetworkSocketException;
			
	/**
	 * Disconnect the user to the user network
	 * @param uris
	 * @param user
	 */
	public void disconnect(Set<URI> uris, UserNetwork user);
		
	/**
	 * Send a comment about a grid to the owner
	 * @param comment the comment to add
	 * @param grid the grid to comment
	 * @param user the user who add the comment
	 * @throws NetworkSocketException 
	 */
	void sendGridCommentToOwner(String comment, GridNetwork grid, UserNetwork user, URI receiver) throws NetworkSocketException;	
	/**
	 * Send a rating about a grid to the owner
	 * @param note the grade of the grid
	 * @param grid the grid to rate
	 * @param user the user who rate the grid
	 * @throws NetworkSocketException 
	 */
	void sendGridRatingToOwner(Integer grade, GridNetwork grid, UserNetwork user, URI receiver) throws NetworkSocketException;

	/**
	 * Add rights for a grid to a user
	 * @param URIs the uris of the users
	 * @param grid the grid to work on 
	 */
	public void sendNewRights(Set<URI> URIs, GridNetwork grid);
	
	/**
	 * Send the grid to the network
	 * @param URIs the URIs to notify
	 * @param grid the grid to share 
	 */
	public void sendGridToNetwork(Set<URI> URIs, GridNetwork grid);

	void updateNetwork() throws NetworkSocketException;



	
}
