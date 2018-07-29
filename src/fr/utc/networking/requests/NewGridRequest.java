package fr.utc.networking.requests;

import java.util.List;

import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.URI;
import fr.utc.networking.NetworkManager;
import fr.utc.networking.Request;

public class NewGridRequest extends Request {

	private static final long serialVersionUID = 6600970871950280605L;
	private List<GridNetwork> grids;
	
	public NewGridRequest(URI receiver, List<GridNetwork> grids) {
		super(receiver);
		this.grids=grids;
	}

	@Override
	public void doProcessing(NetworkManager networkManager) {
		networkManager.getNetToProcess().addGrids(grids);
	}

}
