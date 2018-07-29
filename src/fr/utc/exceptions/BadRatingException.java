package fr.utc.exceptions;

public class BadRatingException extends Exception {
	public BadRatingException(){
		super();
	}
	
	public BadRatingException(String message){
		super(message);
	}
}
