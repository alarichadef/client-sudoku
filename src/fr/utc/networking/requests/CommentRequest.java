package fr.utc.networking.requests;



import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.URI;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.networking.NetworkManager;
import fr.utc.networking.Request;

public class CommentRequest extends Request {
	
	private static final long serialVersionUID = -9192451193768951389L;
	private UserNetwork userNetwork;
	private GridNetwork grid;
	private String comment;
	
	public CommentRequest(UserNetwork userNetwork, GridNetwork grid, String comment, URI receiver) {
		super(receiver);
		this.userNetwork = userNetwork;
		this.grid = grid;
		this.comment = comment;
	}


	@Override
	public void doProcessing(NetworkManager networkManager) throws NetworkSocketException {
		networkManager.getNetToProcess().addNewComment(grid, comment, userNetwork);
	}

}
