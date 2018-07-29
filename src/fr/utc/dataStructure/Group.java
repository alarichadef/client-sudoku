/**
 * 
 */
package fr.utc.dataStructure;

import java.util.List;
import java.util.UUID;

import fr.utc.exceptions.NullUserNetworkException;

public class Group {
	
	private String name;
	private UUID uuid;
	private List<UserNetwork> users;
	
	public Group() {
	
	}

	public Group(String name, UUID uuid, List<UserNetwork> users) {
		this.name = name;
		this.uuid = uuid;
		this.users = users;
	}
	
	/**
	 * Method to add an user to the group
	 * @param UserNetwork the user to add
	 * @throws NullUserNetworkException
	 */
	public void addUserToGroup(UserNetwork user) throws NullUserNetworkException {
		if(user != null && !user.getUuid().toString().isEmpty() && !user.getLogin().isEmpty()){
			users.add(user);
		} else {
			throw new NullUserNetworkException("The userNetwork you're trying to add is null or empty");
		}
	}
	
	
	/**
	 * Method to remove an user to the group
	 * @param UserNetwork the user to remove
	 * @throws NullUserNetworkException
	 */
	public void removeUserFromGroup(UserNetwork user) throws NullUserNetworkException {
		if (user != null && !user.getLogin().isEmpty()) {
			this.users.removeIf(u -> u.equals(user));
		} else {
			throw new NullUserNetworkException("The userNetwork you're trying to remove is null or empty");
		}
	}
	
	/**
	 * Method which returns the list of users in the group
	 * @return ArrayList<UserNetwork> the list of users
	 */
	public List<UserNetwork> getUsers() {
		return this.users;
		
	}
	
	public String getName() {
		return this.name;
	}
	
	public UUID getUuid() {
		return this.uuid;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setUsers(List<UserNetwork> users) {
		this.users = users;
	}
	
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((users == null) ? 0 : users.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (users == null) {
			if (other.users != null)
				return false;
		} else if (!users.equals(other.users))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}