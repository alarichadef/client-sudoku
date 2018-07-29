package fr.utc.networking.requests;
import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.URI;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.networking.NetworkManager;
import fr.utc.networking.Request;

public class RatingRequest extends Request {
	
	private static final long serialVersionUID = -9192451193768951389L;
	private UserNetwork userNetwork;
	private GridNetwork grid;
	private int rate;
	
	public RatingRequest(UserNetwork userNetwork, GridNetwork grid, int rate, URI receiver) {
		super(receiver);
		this.userNetwork = userNetwork;
		this.grid = grid;
		this.rate = rate;
	}


	@Override
	public void doProcessing(NetworkManager networkManager) throws NetworkSocketException {
		networkManager.getNetToProcess().addNewMark(grid, rate, userNetwork);
	}

}
