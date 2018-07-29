package fr.utc.exceptions;

public class NullUserNetworkException extends Exception {
	public NullUserNetworkException(){
		super();
	}
	
	public NullUserNetworkException(String message){
		super(message);
	}
}
