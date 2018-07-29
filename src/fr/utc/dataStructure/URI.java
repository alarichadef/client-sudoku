package fr.utc.dataStructure;

import java.io.Serializable;
import java.net.InetAddress;

public class URI implements Comparable<URI>, Serializable{
	
	private static final long serialVersionUID = -1268341313035172055L;
	private InetAddress address;
	private int port;
	
	public URI(InetAddress address, int port) {
		super();
		this.address = address;
		this.port = port;
	}
	public InetAddress getAddress() {
		return address;
	}
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	@Override
	public int compareTo(URI o) {
		if(this.address.equals(o.getAddress()) && this.port == o.getPort())
			return 0;
		return -1;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URI address = (URI) obj;
		if(this.getAddress().equals(address.getAddress()) && this.getPort() == address.getPort()){
			return true; 
		}
		
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + port;
		return result;
	}
	
	@Override
	public String toString(){
		return address.toString()+":"+port;
	}
}
