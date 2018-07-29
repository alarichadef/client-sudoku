package fr.utc.networking;

import java.io.Serializable;

import fr.utc.dataStructure.UserLocal;
import fr.utc.interfaces.NetworkingToProcessing;
import fr.utc.interfaces.ProcessingToNetworking;

public class NetworkManager implements Serializable{
	
	private static final long serialVersionUID = 1470645217688273704L;
	private NetworkingToProcessing netToProcess;
	private ProcessingToNetworking processingToNetworking;
	
	public NetworkManager(){
		processingToNetworking = new ProcessingToNetworkingImpl(this);
	}
	
	/**
	 * @return the local user 
	 */
	public UserLocal getUserLocal(){
		return netToProcess.getUserLocal();
	}
	
	/** 
	 * @return the networkingToProcessing interface
	 */
	public NetworkingToProcessing getNetToProcess() {
		return netToProcess;
	}
	
	/**
	 * Set the netToProcess interface
	 * @param netToProcess
	 */
	public void setNetToProcess(NetworkingToProcessing netToProcess) {
		this.netToProcess = netToProcess;
	}
	
	/**
	 * get the ProcessingToNetworking interface
	 * @return ProcessingToNetworking interface
	 */
	public ProcessingToNetworking getProcessingToNetworking() {
		return processingToNetworking;
	}

	/**
	 * Set the ProcessingToNetworking interface
	 * @param sender
	 */
	public void setProcessingToNetworking(ProcessingToNetworking sender) {
		this.processingToNetworking = sender;
	}
}
