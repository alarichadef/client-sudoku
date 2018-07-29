package fr.utc.dataStructure;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import fr.utc.exceptions.BadRatingException;
import fr.utc.exceptions.NullUserNetworkException;
import junit.framework.TestCase;

public class GridNetworkTest extends TestCase {
	private GridNetwork grid;
	
	public GridNetworkTest(){
		List<String> tags = new ArrayList<String>();
		List<Group> readGroup = new ArrayList<Group>();
		List<Group> commentGroup = new ArrayList<Group>();
		List<Group> playGroup = new ArrayList<Group>();
		HashMap<UserNetwork, Integer> ratings = new HashMap<UserNetwork, Integer>();
		List<Comment> comments = new ArrayList<Comment>();
		
		grid = new GridNetwork(tags, readGroup, commentGroup, playGroup, ratings, comments, "grid", "uuids");
	}
	
	/**
	 * Test which checks that the average rating returns the correct value
	 * @throws NullUserNetworkException
	 * @throws BadRatingException
	 */
	public void testGetAverageRatings() throws NullUserNetworkException, BadRatingException{
		UserNetwork user1 = new UserNetwork("testlogin", new Date(), "testName", "testFirstName", null);
		grid.addRating(user1, 3);
		UserNetwork user2 = new UserNetwork("testloginBis", new Date(), "testName", "testFirstName", null);
		grid.addRating(user2, 1);
		
		assertEquals(Double.valueOf(2), Double.valueOf(grid.getAverageRating()));
		
		UserNetwork user3 = new UserNetwork("testloginTer", new Date(), "testName", "testFirstName", null);
		grid.addRating(user3, 4);
		
		assertEquals(Double.valueOf(3), Double.valueOf(grid.getAverageRating()));
		
		
	}

}
