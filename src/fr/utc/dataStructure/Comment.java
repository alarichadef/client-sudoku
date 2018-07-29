package fr.utc.dataStructure;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable{
	private static final long serialVersionUID = -6131317461756102743L;
	private String comment; 
	private Date date;
	private UserNetwork author;
	
	public Comment(String message, UserNetwork author){
		this.comment = message;
		this.author = author;
	}
	
	public String getComment() {
		return comment;
	}
	
	public Date getDate() {
		return date;
	}
	
	public UserNetwork getAuthor() {
		return author;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setAuthor(UserNetwork author) {
		this.author = author;
	}
}
