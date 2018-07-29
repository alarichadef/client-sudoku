package fr.utc.networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import fr.utc.exceptions.NetworkSocketException;

public class ClientHandler extends Thread{
	private Socket socket;
	private NetworkManager networkManager;
	private ObjectInputStream inputStream;

	ClientHandler(NetworkManager networkManager, Socket socket) throws IOException {
		this.socket = socket;
		inputStream = new ObjectInputStream(socket.getInputStream());
		this.networkManager=networkManager;
	}

	@Override
	/**
	 * Launch the processing of the request on the local application
	 */
	public void run(){
		try {
			Request o = (Request) inputStream.readObject();
			o.doProcessing(networkManager);
			socket.close();
		} 
		catch (ClassNotFoundException | IOException | NetworkSocketException e) {
			Thread.currentThread().interrupt();
		}
	}
}