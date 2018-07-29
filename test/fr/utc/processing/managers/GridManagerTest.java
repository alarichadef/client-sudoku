package fr.utc.processing.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import fr.utc.dataStructure.Comment;
import fr.utc.dataStructure.GridLocal;
import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.Group;
import fr.utc.dataStructure.UserNetwork;
import fr.utc.exceptions.BadRatingException;
import fr.utc.exceptions.IndexOutOfRangeException;
import fr.utc.exceptions.NullCommentException;
import fr.utc.exceptions.NullUserNetworkException;
import junit.framework.TestCase;

public class GridManagerTest extends TestCase {
	private GridManager gridM = new GridManager();
	
	private ArrayList<Integer> grid = new ArrayList<Integer>(
			Arrays.asList(
					1,2,3,4,5,6,7,8,9,
					2,3,4,5,6,7,8,9,1,
					3,4,5,6,7,8,9,1,2,
					4,5,6,7,8,9,1,2,3,
					5,6,7,8,9,1,2,3,4,
					6,7,8,9,1,2,3,4,5,
					7,8,9,1,2,3,4,5,6,
					8,9,1,2,3,4,5,6,7,
					9,1,2,3,4,5,6,7,8
			)
		);
	
	public GridManagerTest() throws NullUserNetworkException, BadRatingException{
		List<String> tags = new ArrayList<String>();
		List<Group> readGroup = new ArrayList<Group>();
		List<Group> commentGroup = new ArrayList<Group>();
		List<Group> playGroup = new ArrayList<Group>();
		
		GridLocal grid = new GridLocal(tags, readGroup, commentGroup, playGroup, "grid", null, "uuid");
		gridM.storeGrid(grid);
		GridLocal grid2 = new GridLocal(tags, readGroup, commentGroup, playGroup, "grid2", null, "uuid");
		gridM.storeGrid(grid2);
	}
	
	/**
	 * Test whether the method getGridsByName returns the grids with their name equals to the given string
	 */
	public void testGetGridsByName(){
		List<GridNetwork> grids = gridM.getGridsByName("grid");
		
		assertEquals(1, grids.size());
		assertEquals("grid", grids.get(0).getName());
	}
	
	/**
	 * Test whether the method addTag and getGridsByName work
	 */
	public void testGridTag(){
		gridM.getGridsByName("grid").get(0).addTag("tagTest");
		List<GridNetwork> grids = gridM.getGridsByTag("tagTest");
		
		assertEquals(1, gridM.getGridsByName("grid").get(0).getTags().size());
		
		assertEquals(2, grids.size());
		assertEquals("grid", grids.get(0).getName());
	}
	
	/**
	 * Test whether the methods addRating and getGridsByName work
	 * @throws BadRatingException 
	 * @throws NullUserNetworkException 
	 */
	public void testGridRating() throws NullUserNetworkException, BadRatingException{
		UserNetwork user = new UserNetwork("user", new Date(), "testLN", "testFN", null);
		gridM.getGridsByName("grid").get(0).addRating(user, 3);
		
		assertEquals(1, gridM.getGridsByName("grid").get(0).getRatings().size());
		
		List<GridNetwork> grids = gridM.getGridsByRating(3);
		
		assertEquals(1, grids.size());
		assertEquals("grid", grids.get(0).getName());
	}
	
	/**
	 * Test whether the method addComment works
	 * @throws NullCommentException 
	 * @throws NullUserNetworkException 
	 */
	public void testGridComment() throws NullCommentException{
		UserNetwork user = new UserNetwork("user", new Date(), "testLN", "testFN", null);
		Comment com = new Comment("comment unit test", user);
		assertEquals(0, gridM.getGridsByName("grid").get(0).getComments().size());
		
		gridM.getGridsByName("grid").get(0).addComment(com);
		assertEquals(1, gridM.getGridsByName("grid").get(0).getComments().size());
	}

	
	public void testGetRowByIndexSuccess(){
		GridLocal gridLocal = new GridLocal(); 
		gridLocal.setGrid(this.grid);
		
		try {
			List<Integer> row1 = gridM.getRowByIndex(1, gridLocal);
			assertEquals(new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9)), row1);
			List<Integer> row2 = gridM.getRowByIndex(2, gridLocal);
			assertEquals(new ArrayList<Integer>(Arrays.asList(2,3,4,5,6,7,8,9,1)), row2);
			List<Integer> row3 = gridM.getRowByIndex(3, gridLocal);
			assertEquals(new ArrayList<Integer>(Arrays.asList(3,4,5,6,7,8,9,1,2)), row3);
		} catch (IndexOutOfRangeException e) {
			e.printStackTrace();
		}
	}
	
	public void testGetRowByIndexFailure(){
		GridLocal gridLocal = new GridLocal(); 
		gridLocal.setGrid(this.grid);
		
		try {
			gridM.getRowByIndex(14, gridLocal);
			gridM.getRowByIndex(0, gridLocal);
		} catch (IndexOutOfRangeException e) {
			assertTrue(true);
		}
	}
	
	public void testGetColumnByIndexSuccess(){
		GridLocal gridLocal = new GridLocal(); 
		gridLocal.setGrid(this.grid);
		
		try {
			List<Integer> column1 = gridM.getColumnByIndex(1, gridLocal);
			assertEquals(new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,7,8,9)), column1);
			List<Integer> column2 = gridM.getColumnByIndex(2, gridLocal);
			assertEquals(new ArrayList<Integer>(Arrays.asList(2,3,4,5,6,7,8,9,1)), column2);
			List<Integer> column3 = gridM.getColumnByIndex(3, gridLocal);
			assertEquals(new ArrayList<Integer>(Arrays.asList(3,4,5,6,7,8,9,1,2)), column3);
		} catch (IndexOutOfRangeException e) {
			e.printStackTrace();
		}
	}
	
	public void testGetColumnByIndexFailure(){
		GridLocal gridLocal = new GridLocal(); 
		gridLocal.setGrid(this.grid);
		
		try {
			gridM.getColumnByIndex(14, gridLocal);
			gridM.getColumnByIndex(0, gridLocal);
		} catch (IndexOutOfRangeException e) {
			assertTrue(true);
		}
	}
	
	public void testGetCaseSuccess(){
		GridLocal gridLocal = new GridLocal(); 
		gridLocal.setGrid(this.grid);
		
		try {
			int case1 = gridM.getCaseByRowAndByColumn(3, 6, gridLocal);
			assertEquals(8, case1);
			int case2 = gridM.getCaseByRowAndByColumn(8, 8, gridLocal);
			assertEquals(6, case2);
			int case3 = gridM.getCaseByRowAndByColumn(7, 4, gridLocal);
			assertEquals(1, case3);
		} catch (IndexOutOfRangeException e) {
			e.printStackTrace();
		}
	}
	
	public void testGetCaseFailure(){
		GridLocal gridLocal = new GridLocal(); 
		gridLocal.setGrid(this.grid);
		
		try {
			gridM.getCaseByRowAndByColumn(12, 6, gridLocal);
		} catch (IndexOutOfRangeException e) {
			assertTrue(true);
		}
	}
	
	public void testGetSquareByIndexFailure(){
		GridLocal gridLocal = new GridLocal(); 
		gridLocal.setGrid(this.grid);
		
		try {
			gridM.getSquareByIndex(0, gridLocal);
		} catch (IndexOutOfRangeException e) {
			assertTrue(true);
		}
	}
	
	public void testGetSquareByIndexSuccess(){
		GridLocal gridLocal = new GridLocal(); 
		gridLocal.setGrid(this.grid);
		
		try {
			List<Integer> square1 = gridM.getSquareByIndex(1, gridLocal);
			List<Integer> validSquare13 = new ArrayList<Integer>(Arrays.asList(1,2,3,2,3,4,3,4,5));
			assertEquals(square1, validSquare13);
			List<Integer> square2 = gridM.getSquareByIndex(4, gridLocal);
			List<Integer> validSquare2 = new ArrayList<Integer>(Arrays.asList(4,5,6,5,6,7,6,7,8));
			assertEquals(square2, validSquare2);
			List<Integer> square3 = gridM.getSquareByIndex(8, gridLocal);
			assertEquals(square3, validSquare13);
		} catch (IndexOutOfRangeException e) {
			e.printStackTrace();
		}
	}
	
	public void testSolveGridFailure(){
		GridLocal gridLocal = new GridLocal(); 
		gridLocal.setGrid(grid);
		
		boolean result;
		try {
			result = gridM.solve(gridLocal);
			assertFalse(result);
		} catch (IndexOutOfRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testSolveGridSuccess() {
		GridLocal gridLocal = new GridLocal(); 
		ArrayList<Integer> falseGrid = new ArrayList<Integer>(
				Arrays.asList(
						9, 6, 3, 1, 7, 4, 2, 5, 8,
						1, 7, 8, 3, 2, 5, 6, 4, 9,
						2, 5, 4, 6, 8, 9, 7, 3, 1,
						8, 2, 1, 4, 3, 7, 5, 9, 6,
						4, 9, 6, 8, 5, 2, 3, 1, 7,
						7, 3, 5, 9, 6, 1, 8, 2, 4,
						5, 8, 9, 7, 1, 3, 4, 6, 2,
						3, 1, 7, 2, 4, 6, 9, 8, 5,
						6, 4, 2, 5, 9, 8, 1, 7, 3
				)
			);
		gridLocal.setGrid(falseGrid);

		boolean result;
		try {
			result = gridM.solve(gridLocal);
			assertTrue(result);
		} catch (IndexOutOfRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void testOnlyOneSolutionTrue() {
		ArrayList<Integer> TrueGrid = new ArrayList<Integer>(
				Arrays.asList(
						9, 6, 3, 1, 7, 4, 2, 5, 8,
						1, 7, 8, 3, 2, 5, 6, 4, 9,
						2, 5, 4, 6, 8, 9, 7, 3, 1,
						8, 2, 1, 4, 3, 7, 5, 9, 6,
						4, 9, 6, 8, 5, 2, 3, 1, 7,
						7, 3, 5, 9, 6, 1, 8, 2, 4,
						5, 8, 9, 7, 1, 3, 4, 6, 2,
						3, 1, 7, 2, 4, 6, 9, 8, 5,
						6, 4, 2, 5, 9, 8, 1, 7, 3
				)
			);
		assertTrue(gridM.hasSolution(TrueGrid));
	}
	
	public void testOnHasNoSolutionTrue() {
		ArrayList<Integer> TrueGrid = new ArrayList<Integer>(
				Arrays.asList(
						9, 0, 0, 0, 0, 0, 0, 0, 0,
						9, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0
				)
			);
		assertFalse(gridM.hasSolution(TrueGrid));
	}
	
	public void testOnHasNotUniqueSolutionTrue() {
		ArrayList<Integer> TrueGrid = new ArrayList<Integer>(
				Arrays.asList(
						9, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0,
						0, 0, 0, 0, 0, 0, 0, 0, 0
				)
			);
		assertFalse(gridM.hasSolution(TrueGrid));
	}
	
	public void testGenerateValidGrid() throws IndexOutOfRangeException {
		GridLocal gridLocal = new GridLocal();
		gridM.generate(gridLocal);

		boolean result;
		try {
			assertNotNull(gridLocal.getGrid());
			result = gridM.solve(gridLocal);
			assertTrue(result);
		} catch (IndexOutOfRangeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
