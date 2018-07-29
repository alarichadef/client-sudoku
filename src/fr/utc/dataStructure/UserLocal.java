package fr.utc.dataStructure;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import fr.utc.exceptions.NullGridLocalException;
import fr.utc.exceptions.NullGroupException;
import fr.utc.exceptions.NullUserNetworkException;

public class UserLocal extends UserNetwork {
	
	private static final long serialVersionUID = -6239919396001722962L;
	private transient List<Group> groups;
	private String password;
	private Set<URI> discoveredNodes;
	private Set<URI> userNodes;
	private List<GridLocal> localGrids;
	private List<GridLocal> playedGrids;

	public UserLocal() {
		super();
		discoveredNodes = new TreeSet<>();
		userNodes = new TreeSet<>();
	}

	
	public UserLocal(String login, Date bday, String lastname, String firstname,
			URI uri, String password){
		super(login, bday, lastname, firstname, uri);
		this.password = password;
		discoveredNodes = new TreeSet<>();
		userNodes = new TreeSet<>();
		this.localGrids = new ArrayList<>();
		this.playedGrids = new ArrayList<>();
	}
	public UserLocal(String login, Date bday, String lastname, String firstname, 
			 String password){
		super(login, bday, lastname, firstname);
		this.password = password;
		discoveredNodes = new TreeSet<>();
		userNodes = new TreeSet<>();
	}
	public UserLocal(List<Group> groups, String pwd, Set<URI> dNodes, Set<URI> uNodes,
			List<GridLocal> lGrids, List<GridLocal> pGrids) {
		this.groups = groups;
		this.password = pwd;
		this.discoveredNodes = dNodes;
		this.userNodes = uNodes;
		this.localGrids = lGrids;
		this.playedGrids = pGrids;
	}

	public Set<URI> getUserNodes(){
		return this.userNodes;
	}

	public List<Group> getAllGroups() {
		return this.groups;
	}

	public List<GridLocal> getLocalGrids() {
		return this.localGrids;
	}

	public List<GridLocal> getPlayedGrids() {
		return this.playedGrids;
	}

	public String getPassword() {
		return this.password;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	
	public Set<URI> getDiscoveredNodes(){
		return this.discoveredNodes;
	}
	
	/*
	 * Add a single grid to a list of played grids
	 * 
	 * @param GridLocal grid
	 * 
	 * @throws NullGridLocalException
	 */
	public void addGridToPlayedGrids(GridLocal grid) throws NullGridLocalException {
		if (grid != null) {
			this.playedGrids.add(grid);
		} else {
			throw new NullGridLocalException("The GridLocal you're trying to add is null");
		}
	}

	public void addDiscoveredNode(URI node) {
		if (node != null) {
			this.discoveredNodes.add(node);
		}
	}

	public void addUserNode(URI node) {
		if (node != null) {
			this.userNodes.add(node);
		}
	}

	/*
	 * Add the given user to the specified group
	 * 
	 * @param UserNetwork user
	 * 
	 * @param Group group
	 * 
	 * @throws NullUserNetworkException
	 * 
	 * @throws NullGroupException
	 */
	public void addUserToGroup(UserNetwork user, Group group) throws NullUserNetworkException, NullGroupException {
		if (user == null) {
			throw new NullUserNetworkException("The UserNetwork you're trying to add is null");
		} else if (group == null) {
			throw new NullGroupException("The Group you're trying to add is null");
		} else {

		}
	}

	// to keep or to merge with addGridToPlayedGrids ?
	public void saveGrid(GridLocal grid) throws NullGridLocalException {
		if (grid == null) {
			throw new NullGridLocalException("The GridLocal you're trying to add is null");
		} else {
			this.localGrids.add(grid);
		}
	}

	public void removeURIFromDiscoveredNodes(URI uri) {
		this.discoveredNodes.remove(uri);
	}

	public void removeURIFromUserNodes(URI uri) {
		this.userNodes.remove(uri);
	}
	
	public Set<URI> getAllNodes(){
		Set<URI> res = new HashSet<>();
		res.add(getUri());
		res.addAll(getDiscoveredNodes());
		return res;
	}
	
	@Override
	public String toStringCSV(){
		return super.toStringCSV() + ";" + password + "\n";
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((discoveredNodes == null) ? 0 : discoveredNodes.hashCode());
		result = prime * result + ((groups == null) ? 0 : groups.hashCode());
		result = prime * result + ((localGrids == null) ? 0 : localGrids.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((playedGrids == null) ? 0 : playedGrids.hashCode());
		result = prime * result + ((userNodes == null) ? 0 : userNodes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserLocal other = (UserLocal) obj;
		if (discoveredNodes == null) {
			if (other.discoveredNodes != null)
				return false;
		} else if (!discoveredNodes.equals(other.discoveredNodes))
			return false;
		if (groups == null) {
			if (other.groups != null)
				return false;
		} else if (!groups.equals(other.groups))
			return false;
		if (localGrids == null) {
			if (other.localGrids != null)
				return false;
		} else if (!localGrids.equals(other.localGrids))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (playedGrids == null) {
			if (other.playedGrids != null)
				return false;
		} else if (!playedGrids.equals(other.playedGrids))
			return false;
		if (userNodes == null) {
			if (other.userNodes != null)
				return false;
		} else if (!userNodes.equals(other.userNodes))
			return false;
		return true;
	}
}
