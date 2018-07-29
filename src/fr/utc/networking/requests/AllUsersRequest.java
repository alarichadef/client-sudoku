package fr.utc.networking.requests;

import java.util.List;
import java.util.Set;

import fr.utc.dataStructure.URI;
import fr.utc.dataStructure.UserLocal;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.networking.NetworkManager;
import fr.utc.networking.Request;

/**
 * Récupération des utilisateurs et des URI distantes sur les clients 
 * @author Antoine
 *
 */
public class AllUsersRequest extends Request{

	private static final long serialVersionUID = -4359817167409506878L;
	private Set<URI> uris;
	private List<UserNetwork> users;

	public AllUsersRequest(URI uri, Set<URI> allNodes, List<UserNetwork> allUsers) {
		super(uri);
		this.uris = allNodes;
		this.users = allUsers;	
	}

	@Override
	public void doProcessing(NetworkManager localNetworkManager) {
		UserLocal user = localNetworkManager.getUserLocal();
		System.out.println("AllUsersRequest");
		System.out.println(user.getFirstName()+" a reçu les URI:"+ uris );
		System.out.println(user.getFirstName()+" a reçu les USERS:"+ users );

		
		//Add uri on local nodes
		for(URI uri:uris){
			if(!uri.equals(user.getUri())){
				user.addDiscoveredNode(uri);
			}
		}
		
		for(UserNetwork u:users){
			localNetworkManager.getNetToProcess().addNewUser(u);
			localNetworkManager.getNetToProcess().addGrids(u.getSharableGrids());
		}

		//Update the entire network with the new nodes
		for(URI uri: user.getDiscoveredNodes()){
			System.out.println("Update "+uri);

			Request req = new UpdateNetworkRequest(uri, user.getAllNodes(), localNetworkManager.getNetToProcess().getAllUsers());
			try {
				req.send();
			} catch (NetworkSocketException e) {
				e.printStackTrace();
			}
		}
	}
}
