package fr.utc.exceptions;

public class NetworkSocketException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public NetworkSocketException(){
		super();
	}
	
	public NetworkSocketException(String message){
		super(message);
	}
}
