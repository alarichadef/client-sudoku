package fr.utc.exceptions;

public class NullCommentException extends Exception {
	public NullCommentException(){
		super();
	}
	
	public NullCommentException(String message){
		super(message);
	}
}
