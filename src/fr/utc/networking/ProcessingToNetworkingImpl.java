package fr.utc.networking;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.utc.dataStructure.GridLocal;
import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.URI;
import fr.utc.dataStructure.UserLocal;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.interfaces.ProcessingToNetworking;
import fr.utc.networking.requests.AllUsersRequest;
import fr.utc.networking.requests.CommentRequest;
import fr.utc.networking.requests.LeaveNetworkRequest;
import fr.utc.networking.requests.NewGridRequest;
import fr.utc.networking.requests.RatingRequest;

public class ProcessingToNetworkingImpl implements ProcessingToNetworking{

	private NetworkManager networkManager;
	private Logger logger;

	public ProcessingToNetworkingImpl(NetworkManager networkM){
		this.networkManager = networkM;
		this.logger=Logger.getLogger(getClass().getName());
	}

	@Override
	public void connect() throws NetworkSocketException {
		UserLocal userLocal = networkManager.getUserLocal();
		ServerListener server;
		
		logger.log(Level.INFO, "Connexion");

		try {
			server = new ServerListener(networkManager, userLocal.getUri().getPort());
			Thread t = new Thread(server);
			t.setDaemon(true);
			t.start();

		} catch (IOException e) {
			throw new NetworkSocketException("Impossible to launch the server. A service is already running on port "+userLocal.getUri().getPort());
		}

		for(URI uri: userLocal.getUserNodes()){
			logger.log(Level.INFO, "Connexion à  "+uri.toString());
			
			Request req = new AllUsersRequest(uri, userLocal.getAllNodes(), networkManager.getNetToProcess().getAllUsers());
			req.send();
		}

	}

	@Override 
	public void updateNetwork() throws NetworkSocketException {
		UserLocal userLocal = networkManager.getUserLocal();
		for(URI uri: userLocal.getUserNodes()){
			logger.log(Level.INFO, "Connexion à  "+uri.toString());
			
			Request req = new AllUsersRequest(uri, userLocal.getAllNodes(), networkManager.getNetToProcess().getAllUsers());
			req.send();
		}
	}
	
	@Override
	public void disconnect(Set<URI> URIs, UserNetwork user) {
		for(URI uri: URIs){
			if(uri != networkManager.getUserLocal().getUri() && user != null){
				logger.log(Level.INFO, "Déconnexion à  "+uri.toString());
				Request req = new LeaveNetworkRequest(uri, user);
				try {
					req.send();
				} catch (NetworkSocketException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void sendGridCommentToOwner(String comment, GridNetwork grid, UserNetwork user, URI receiver) throws NetworkSocketException {
		CommentRequest req = new CommentRequest(user, grid, comment, receiver);
		req.send();
	}

	@Override
	public void sendGridRatingToOwner(Integer grade, GridNetwork grid, UserNetwork user, URI receiver) throws NetworkSocketException {
		RatingRequest req = new RatingRequest(user, grid, grade, receiver);
		req.send();
	}

	@Override
	public void sendNewRights(Set<URI> uris, GridNetwork grid) {

	}

	@Override
	public void sendGridToNetwork(Set<URI> URIs, GridNetwork grid) {
		for(URI uri:URIs){
			ArrayList<GridNetwork> liste = new ArrayList<GridNetwork>();
			liste.add(grid);
			NewGridRequest req = new NewGridRequest(uri, liste);
			try {
				req.send();
			} catch (NetworkSocketException e) {
				e.printStackTrace();
			}
		}
	}

	public NetworkManager getNetworkManager() {
		return networkManager;
	}

	public void setNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
	}
}
