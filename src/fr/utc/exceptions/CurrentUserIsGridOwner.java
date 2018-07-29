package fr.utc.exceptions;

public class CurrentUserIsGridOwner extends Exception {
	public CurrentUserIsGridOwner(){
		super();
	}
	
	public CurrentUserIsGridOwner(String message){
		super(message);
	}
}
