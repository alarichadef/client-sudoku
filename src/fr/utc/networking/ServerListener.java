package fr.utc.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ServerSocketFactory;

public class ServerListener extends Thread {

    private ServerSocket serverSocket;
    private NetworkManager networkManager;
    private Logger logger;

    public ServerListener(NetworkManager networkManager, int port) throws IOException {
        this.networkManager = networkManager;
    	serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
    	logger = Logger.getLogger(getClass().getName());
    }
    
   
    @Override
    /**
     * Start the server thread and open a new socket to a specific ip and port 
     */
    public void run() {
    	logger.log(Level.INFO, "Thread en cours "+this.getThreadGroup().activeCount());
    	
        while (true) {
            try {
                Socket socketToClient = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(networkManager, socketToClient);
                clientHandler.start();
            } catch (IOException e) {
            	logger.log(Level.SEVERE, e.toString(), e);
            }
        }
    }
}