package fr.utc.networking.requests;

import fr.utc.dataStructure.URI;
import fr.utc.dataStructure.UserLocal;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.networking.NetworkManager;
import fr.utc.networking.Request;

public class LeaveNetworkRequest extends Request {
	
	private static final long serialVersionUID = 4844795360434332559L;
	private UserNetwork userNetwork;

	public LeaveNetworkRequest(URI receiver, UserNetwork userNetwork) {
		super(receiver);
		this.userNetwork=userNetwork;
	}

	@Override
	public void doProcessing(NetworkManager networkManager) {
		UserLocal user = networkManager.getUserLocal();
		System.out.println("Suppression");
		
		networkManager.getNetToProcess().removeUserNetworkFromList(userNetwork);
		networkManager.getNetToProcess().removeGridFromUser(userNetwork);

		user.getDiscoveredNodes().remove(userNetwork.getUri());
		
		System.out.println("Nouvelle Liste "+networkManager.getNetToProcess().getAllUsers());
	}
}
