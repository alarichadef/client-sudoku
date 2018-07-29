package fr.utc.networking.requests;


import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.utc.dataStructure.URI;
import fr.utc.dataStructure.UserLocal;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.networking.NetworkManager;
import fr.utc.networking.Request;

public class UpdateNetworkRequest extends Request {

	private static final long serialVersionUID = -3719589690927477168L;
	private Set<URI> uris;
	private List<UserNetwork> users;

	public UpdateNetworkRequest(URI receiver, Set<URI> uris, List<UserNetwork> users) {
		super(receiver);
		this.uris=uris;
		this.users=users;
	}

	@Override
	public void doProcessing(NetworkManager networkManager) {
		UserLocal user = networkManager.getUserLocal();
		
		System.out.println("UpdateNetworkRequest");
		System.out.println("UpdateNetwork "+users);

		//Add uri on local nodes
		for(URI uri:uris){
			if(!uri.equals(user.getUri())){
				user.addDiscoveredNode(uri);
			}
		}

		for(UserNetwork u:users){
			boolean addUser = networkManager.getNetToProcess().addNewUser(u);
			networkManager.getNetToProcess().addGrids(u.getSharableGrids());
			
			if(!addUser){
				Request req = new UpdateNetworkRequest(u.getUri(), user.getAllNodes(), networkManager.getNetToProcess().getAllUsers());
				try {
					req.send();
				} catch (NetworkSocketException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
}
