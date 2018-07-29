package fr.utc.networking;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;

import fr.utc.dataStructure.URI;
import fr.utc.exceptions.NetworkSocketException;

public abstract class Request implements Serializable{
	private static final long serialVersionUID = 4159490744621594898L;
	protected URI receiver;

	public Request(URI receiver) {
		this.receiver = receiver;
	}

	/**
	 * 
	 * @return the receiver URI
	 */
	public URI getReceiver() {
		return receiver;
	}

	/**
	 * Set the receiver URI
	 * @param receiver
	 */
	public void setReceiver(URI receiver) {
		this.receiver = receiver;
	}

	/**
	 * Launch the request processing on the local application
	 * @param networkManager
	 * @throws NetworkSocketException 
	 */
	public abstract void doProcessing(NetworkManager networkManager) throws NetworkSocketException;

	/**
	 * Send to receiver URI the request
	 * @throws NetworkSocketException
	 */
	public void send() throws NetworkSocketException{
		try {
			Socket socketToServer = new Socket();		
			socketToServer.connect(new InetSocketAddress(receiver.getAddress(), receiver.getPort()), 3000);
			ObjectOutputStream outStream = new ObjectOutputStream(socketToServer.getOutputStream());
			outStream.writeObject(this);
			socketToServer.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
			throw new NetworkSocketException("Impossible to reach "+receiver.getAddress()+" on port "+receiver.getPort()+"\n"+e.getMessage());
		}
	}
}
