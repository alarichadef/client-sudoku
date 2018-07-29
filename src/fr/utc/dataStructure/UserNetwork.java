package fr.utc.dataStructure;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserNetwork implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8337496483698836479L;
	private UUID uuid;
	private String login;
	private Date birthday;
	private String lastName;
	private String firstName;
	private URI uri;
	private List<GridNetwork> sharableGrids;
	private transient Logger logger;

	public UserNetwork(){
		//empty
	}
	
	public UserNetwork(String login, Date bday, String lastname, String firstname, BufferedImage avatar,
			URI uri, List<GridNetwork> grids){
		super();
		this.uuid = UUID.randomUUID();
		this.login = login;
		this.birthday = bday;
		this.lastName = lastname;
		this.firstName = firstname;
		this.uri = uri;
		this.sharableGrids = grids;
		logger = Logger.getLogger(getClass().getName());
	}
	
	public UserNetwork(UserNetwork userNetwork) {
		this.uuid = userNetwork.getUuid();
		this.login = userNetwork.getLogin();
		this.birthday = userNetwork.getBirthday();
		this.lastName= userNetwork.getLastName();
		this.firstName= userNetwork.getFirstName();
		this.uri = userNetwork.getUri();
		this.sharableGrids = userNetwork.getSharableGrids();
	}
	
	public UserNetwork(String login, Date bday, String lastname, String firstname,
			URI uri){
		super();
		this.uuid = UUID.randomUUID();
		this.login = login;
		this.birthday = bday;
		this.lastName = lastname;
		this.firstName = firstname;
		this.uri = uri;
		this.sharableGrids = new ArrayList<>();
	}
	public UserNetwork(String login, Date bday, String lastname, String firstname){
		super();
		this.uuid = UUID.randomUUID();
		this.login = login;
		this.birthday = bday;
		this.lastName = lastname;
		this.firstName = firstname;
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
	        for (NetworkInterface netint : Collections.list(nets))
	        {
	        	Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
	            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
	            	if(inetAddress.toString().startsWith("/172"))
	            	{
	            		this.uri = new URI(InetAddress.getByName(inetAddress.getHostAddress()), 2000);
	            	}
	            }
	        }
		} catch (UnknownHostException | SocketException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		this.sharableGrids = new ArrayList<>();
	}
	
	
	public UUID getUuid() {
		return uuid;
	}

	public String toStringCSV(){
		
		String bday = "";
		if(birthday != null)
		{
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			bday = df.format(birthday);
		}
		return uuid + ";" +login +";"+lastName+";"+firstName+";"+bday+";"+"";
	}
	
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}

	public List<GridNetwork> getSharableGrids() {
		return sharableGrids;
	}

	public void setSharableGrids(List<GridNetwork> sharableGrids) {
		this.sharableGrids = sharableGrids;
	}

	public String getLogin(){
		return this.login;
	}
	
	public void setLogin(String login){
		this.login = login;
	}
	
	public String getLastName(){
		return this.lastName;
	}
	
	public void setLastName(String lastname){
		this.lastName = lastname;
	}
	
	public String getFirstName(){
		return this.firstName;
	}
	
	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}

	@Override
	public String toString() {
		return "UserNetwork [uuid=" + uuid + ", login=" + login + ", uri=" + uri + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserNetwork other = (UserNetwork) obj;
		return this.uuid.equals(other.getUuid());
	}
	
	public boolean addSharableGrids(GridNetwork grid){
		return this.sharableGrids.add(grid);
	}
	
}
