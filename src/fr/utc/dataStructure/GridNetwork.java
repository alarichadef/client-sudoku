package fr.utc.dataStructure;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import fr.utc.exceptions.BadRatingException;
import fr.utc.exceptions.NullCommentException;
import fr.utc.exceptions.NullGroupException;
import fr.utc.exceptions.NullUserNetworkException;

public class GridNetwork implements Serializable {

	private static final long serialVersionUID = 1L;
	private UUID uuid;
	private List<String> tags;
	private transient List<Group> readGroup;
	private transient List<Group> commentGroup;
	private transient List<Group> playGroup;
	private String name;
	private Map<UserNetwork,Integer> ratings;
	private List<Comment> comments;
	private String creatorUuid; 

	public GridNetwork(){
		//empty
	}
	
	public GridNetwork(List<String> tags, List<Group> readGroup, List<Group> commentGroup,
			List<Group> playGroup, Map<UserNetwork, Integer> ratings, List<Comment> comments, String name, String creatorUuid) {
		super();
		this.uuid = UUID.randomUUID();
		this.tags = tags;
		this.readGroup = readGroup;
		this.commentGroup = commentGroup;
		this.playGroup = playGroup;
		this.ratings = ratings;
		this.comments = comments;
		this.name = name;
		this.creatorUuid = creatorUuid;
	}
	

	public GridNetwork(List<String> tags, List<Group> readGroup, List<Group> commentGroup,
			List<Group> playGroup, String name, String creatorUuid) {
		super();
		this.uuid = UUID.randomUUID();
		this.tags = tags;
		this.readGroup = readGroup;
		this.commentGroup = commentGroup;
		this.playGroup = playGroup;
		this.name = name;
		this.ratings = new HashMap<>();
		this.comments = new ArrayList<>();
		this.creatorUuid = creatorUuid;
	}
	
	/**
	 * Add a single comment to the grid's list of comments
	 * @param comment is the comment to add in the list
	 * @throws NullCommentException 
	 */
	public void addComment(Comment comment) throws NullCommentException{
		if(comment != null && !comment.getComment().isEmpty()){
			comments.add(comment);
		} else {
			throw new NullCommentException("The comment you're trying to add is null or empty");
		}
	}
	
	/**
	 * Add a group which has the right to read the grid
	 * @param group is the group of users
	 * @throws NullGroupException 
	 */
	public void addGroupToReadRights(Group group) throws NullGroupException{
		if(group != null){
			readGroup.add(group);
		} else {
			throw new NullGroupException("The group you're trying to add to read rights is null");
		}
	}
	
	/**
	 * Add a group which has the right to comment the grid
	 * @param group is the group of users
	 * @throws NullGroupException 
	 */
	public void addGroupToCommentRights(Group group) throws NullGroupException{
		if(group != null){
			commentGroup.add(group);
		} else {
			throw new NullGroupException("The group you're trying to add to comment rights is null");
		}
	}
	
	/**
	 * Add a group which has the right to play the grid
	 * @param group is the group of users
	 * @throws NullGroupException 
	 */
	public void addGroupToPlayRight(Group group) throws NullGroupException{
		if(group != null){
			playGroup.add(group);
		} else {
			throw new NullGroupException("The group you're trying to play rights is null");
		}
	}
	
	/**
	 * Add a rating on the grid
	 * @param user is the user who gave the rating
	 * @param rating is the mark between 0 and 5
	 * @throws NullUserNetworkException 
	 * @throws BadRatingException 
	 */
	public void addRating(UserNetwork user, int rating) throws NullUserNetworkException, BadRatingException{
		if(user == null) {
			throw new NullUserNetworkException("The UserNetwork you're trying to add is null");
		} else if(rating <= 0 && rating >= 5) {
			throw new BadRatingException("The rating you're trying to add is not between 0 and 5");
		} else {
			ratings.put(user, rating);
		}
	}
	
	/**
	 * Add a tag on the grid
	 * @param tag is the string corresponding to the tag
	 */
	public void addTag(String tag){
		if(tag != null && !tag.isEmpty()){
			tags.add(tag);
		}
	}
	
	public Group findGroupByName(String name) {
		return Stream.concat(this.readGroup.stream(), Stream.concat(this.commentGroup.stream(), this.playGroup.stream()))
				.distinct()
				.filter(group -> name.equals(group.getName()))
				.findFirst()
				.orElse(null);
	}

	public UUID getUuid() {
		return uuid;
	}
	
	public String getName(){
		return name;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<Group> getReadGroup() {
		return readGroup;
	}

	public void setReadGroup(List<Group> readGroup) {
		this.readGroup = readGroup;
	}

	public List<Group> getCommentGroup() {
		return commentGroup;
	}

	public void setCommentGroup(List<Group> commentGroup) {
		this.commentGroup = commentGroup;
	}

	public List<Group> getPlayGroup() {
		return playGroup;
	}

	public void setPlayGroup(List<Group> playGroup) {
		this.playGroup = playGroup;
	}

	public Map<UserNetwork, Integer> getRatings() {
		return ratings;
	}

	public void setRatings(Map<UserNetwork, Integer> ratings) {
		this.ratings = ratings;
	}
	
	public Integer getAverageRating(){
		double sum = 0;
		int counter = 0;
		Long average;
		for (HashMap.Entry<UserNetwork, Integer> entry : ratings.entrySet()) {
		    int value = entry.getValue().intValue();
		    sum = sum + value;
		    counter ++;
		}
		//round to closest long and convert to int
		if (counter != 0){
			average = Math.round(sum/counter);
		} else {
			average = (long) 0;
		}
		
		return average.intValue();
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public void setName(String name){
		this.name = name;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((commentGroup == null) ? 0 : commentGroup.hashCode());
		result = prime * result + ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((playGroup == null) ? 0 : playGroup.hashCode());
		result = prime * result + ((ratings == null) ? 0 : ratings.hashCode());
		result = prime * result + ((readGroup == null) ? 0 : readGroup.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GridNetwork other = (GridNetwork) obj;
		if (commentGroup == null) {
			if (other.commentGroup != null)
				return false;
		} else if (!commentGroup.equals(other.commentGroup))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (playGroup == null) {
			if (other.playGroup != null)
				return false;
		} else if (!playGroup.equals(other.playGroup))
			return false;
		if (ratings == null) {
			if (other.ratings != null)
				return false;
		} else if (!ratings.equals(other.ratings))
			return false;
		if (readGroup == null) {
			if (other.readGroup != null)
				return false;
		} else if (!readGroup.equals(other.readGroup))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	public String getCreatorUuid() {
		return creatorUuid;
	}

	public void setCreatorUuid(String creatorUuid) {
		this.creatorUuid = creatorUuid;
	}
}
