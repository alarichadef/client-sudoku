package fr.utc.processing.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import fr.utc.dataStructure.GridLocal;
import fr.utc.dataStructure.GridNetwork;
import fr.utc.dataStructure.Group;
import fr.utc.dataStructure.UserLocal;
import fr.utc.exceptions.IndexOutOfRangeException;
import fr.utc.exceptions.NetworkSocketException;
import fr.utc.exceptions.NullGridLocalException;

public class GridManager {
	/**
	 * list of grids known to the current user
	 */
	private List<GridNetwork> grids;

	/**
	 * localGrid currently in use in the application
	 */
	private GridLocal currentGrid;

	private ProcessingManager processingManager;
	private Logger logger;
	private final String indexMessage = "The index you passed in parameter is not between 1 and 9";

	public GridManager() {
		super();
		logger = Logger.getLogger(getClass().getName());
		this.grids = new ArrayList<>();
		currentGrid = null;
	}

	/**
	 * Method wich returns all the local grids of the current user
	 * 
	 * @return List<GridNetwork> list of current user local grids
	 */
	public List<GridNetwork> getAllGrids() {
		List<GridNetwork> gridsNetwork = new ArrayList<>();
		for(GridNetwork grid : processingManager.getUserManager().getCurrentUser().getLocalGrids().stream().map(object -> (GridNetwork) object).collect(Collectors.toList())){
			gridsNetwork.add(grid);
		}
		gridsNetwork.addAll(grids);
		return gridsNetwork;
	}

	/**
	 * Method which returns the grid currently in use
	 * 
	 * @return GridLocal the grid currently in use
	 */
	public GridLocal getCurrentLocalGrid() {
		return this.currentGrid;
	}

	/**
	 * Method which set the new grid in use
	 * 
	 * @param GridLocal the new grid to set
	 */
	public void setCurrentLocalGrid(GridLocal grid) {
		this.currentGrid = grid;
	}

	public void setProcessingManager(ProcessingManager processingManager) {
		this.processingManager = processingManager;
	}

	/**
	 * Method which retrieves a particular grid with the given uuid
	 * 
	 * @param uuid the uuid of the grid to retrieve
	 * @return GridNetwork the grid to retrieve or null
	 */
	public GridNetwork getGridByUuid(UUID uuid) throws NetworkSocketException {
		if (uuid != null) {
			List<GridNetwork> allGrids = this.getAllGrids();
			
			GridNetwork gridNetwork = allGrids.stream().filter(grid -> grid.getUuid().equals(uuid)).findFirst().orElse(null);
			if(gridNetwork instanceof GridLocal) {
				this.setCurrentLocalGrid((GridLocal) gridNetwork);
			}
			return gridNetwork;
		} else {
			return null;
		}
	}

	public void addGrids(List<GridNetwork> grids) {
		if (grids != null && !grids.isEmpty()) {
			grids.forEach(grid -> {
				if (grid != null) {
					this.grids.add(grid);
				}
			});
		}
	}

	/**
	 * Create a new grid if the given information are correct
	 * 
	 * @param the name of the grid
	 * @param tags  the list of tags of the grid
	 * @param readOnlyGroup the list of Group granted reading rights
	 * @param playGroup the list of Group granted playing rights
	 * @param allRightsGroup the list of Group granted all rights
	 * @param grid the List of Integer in the actual grid 
	 * @return boolean true if the grid was successfuly created, false otherwise
	 */
	public boolean createGrid(String name, List<String> tags, List<Group> readOnlyGroup, List<Group> playGroup,
			List<Group> allRightsGroup, List<Integer> grid) throws NullGridLocalException {
		boolean result = false;

		if(!name.isEmpty() && !grid.isEmpty()) {
			GridLocal createdGrid = new GridLocal(tags, readOnlyGroup, playGroup, allRightsGroup, name, grid, processingManager.getUserManager().getCurrentUser().getUuid().toString());
			result = this.hasSolution(createdGrid.getGrid());

			if(result){
				this.setCurrentLocalGrid(createdGrid);
				this.processingManager.getUserManager().getCurrentUser().saveGrid(createdGrid);
				this.saveLocalGrid(createdGrid);
				this.processingManager.getProcessingToGUI().updateGridList(this.processingManager.getGridManager().getAllGrids());
				this.processingManager.getProcessingToNetworking().sendGridToNetwork(this.processingManager.getUserManager().getCurrentUser().getAllNodes(), createdGrid);
				this.processingManager.getUserManager().getCurrentUser().addSharableGrids(createdGrid);
			}
		}
		return result;
	}

	/**
	 * Method which generates a square list of 81 integers representing a grid 
	 * 
	 * @param int nbFilled the number of boxes already filled 
	 * @return List<Integer> square list of 9 numbers representing the grid
	 * @throws IndexOutOfRangeException
	 */
	public List<Integer> generateGrid(int nbFilled) throws IndexOutOfRangeException {
		GridLocal createdGrid = new GridLocal();
		//temporary, we need to move methods to avoid creating a new gridLocal each time
		this.generate(createdGrid);
		List<Integer> generatedList = new ArrayList<>();
		List<Integer> visList = this.randomizeDisplay(nbFilled);
		for(int i = 0; i < createdGrid.getGrid().size(); i++) {
			// the condition below tests if each particular member of the list is to be displayed
			if(visList.get(i) == 1) {
				generatedList.add(createdGrid.getGrid().get(i));
			} else {
				generatedList.add(null);
			}
		}
		this.setCurrentLocalGrid(createdGrid);

		return generatedList;
	}


	/**
	 * Method which check if the grid solution is correct
	 * 
	 * @param List<Integer> grid the list of integer representig the grid
	 * @return boolean true if the solution is correct, false otherwise
	 * @throws IndexOutOfRangeException
	 */
	public boolean checkGrid(List<Integer> grid) throws IndexOutOfRangeException {
		GridLocal tmp = new GridLocal(null, null, null, null, null, grid, null);
		return this.solve(tmp);
	}

	/**
	 * Method which retrieves all grid with the given name
	 * 
	 * @param String the name 
	 * @return List<GridNetwork> a list of GridNetwork
	 */
	public List<GridNetwork> getGridsByName(String name) {
		if(name.isEmpty()) {
			return getAllGrids();
		} else {
			return getAllGrids().stream().filter(grid -> grid.getName().contains(name)).collect(Collectors.toList());
		}
	}

	/**
	 * Method which retrieves all the grid with the given rating value 
	 * 
	 * @param Integer the rating
	 * @return List<GridNetwork> a list of GridNetwork
	 */
	public List<GridNetwork> getGridsByRating(Integer rating) {
		if(Integer.toString(rating).isEmpty()) {
			return getAllGrids();
		}
		if (rating != null) {
			return getAllGrids().stream().filter(grid -> grid.getAverageRating() >= rating).collect(Collectors.toList());
		} else{
			return new  ArrayList<>();
		}
	}

	/**
	 * Method which returns a List of GridNetwork that contains the tag given
	 * 
	 * @param String
	 *            tag
	 * @return List<GridNetwork> list of grids
	 */
	public List<GridNetwork> getGridsByTag(String tag) {
		List<GridNetwork> gridsToReturn = new ArrayList<>();
		
		if(tag.isEmpty()) {
			return getAllGrids();
		} else {
			for (GridNetwork grid : getAllGrids()) {
				for (String t : grid.getTags()) {
					if (t.equals(tag) && !gridsToReturn.contains(grid)) {
						gridsToReturn.add(grid);
					}
				}
			}
		}

		return gridsToReturn;
	}	

	public void storeGrid(GridLocal grid) {
		this.grids.add(grid);
	}

	/**
	 * Method which generates a list of 81 integers. Their value is either 1 if the boxe at the same position in a list is to be filled, 0 otherwise
	 * 
	 * @param int nbToDisplay number of boxes to be filled
	 * @return List<Integer> a list of integers (0 or 1)
	 */
	public void clear() {
		this.grids.clear();
	}

	private List<Integer> randomizeDisplay(int nbToDisplay) {
		List<Integer> displays = new ArrayList<>();
		int i = 0;
		// no need to test, GUI ensure the int value
		while (i < 81) {
			if (i < nbToDisplay) {
				displays.add(1);
			} else {
				displays.add(0);
			}
			i++;
		}
		Collections.shuffle(displays);
		return displays;
	}

	public GridLocal getGridsByUuid(String uuid) {
		if (uuid != null && !uuid.isEmpty()) {
			return this.processingManager.getUserManager().getCurrentUser().getLocalGrids().stream().filter(grid -> grid.getUuid().toString().equals(uuid)).findFirst().orElse(null);
		} else {
			return null;
		}
	}

	public List<GridNetwork> getNetworkGrids(){
		return this.grids;
	}

	/**
	 * Method which returns a row from a given index between 1 and 9 A row is
	 * composed by 9 numbers
	 * 
	 * @param index
	 *            is the index of the row
	 * @return a list of 9 numbers which represents the row of the grid
	 */
	public List<Integer> getRowByIndex(int index, GridLocal  grid) throws IndexOutOfRangeException {
		List<Integer> row = new ArrayList<>();

		if (index < 1 || index > 9)
			throw new IndexOutOfRangeException("The index you passed in parameter is not between 1 and 9");
		else {
			for (int i = 9 * (index - 1); i < 9 * index; i++) {
				row.add(grid.getGrid().get(i));
			}
		}

		return row;
	}

	/**
	 * Method which returns a column from a given index between 1 and 9 A row in
	 * composed by 9 numbers
	 * 
	 * @param index
	 *            is the index of the row
	 * @return a list of 9 numbers which represents the column of the grid
	 * @throws IndexOutOfRangeException
	 */
	public List<Integer> getColumnByIndex(int index, GridLocal grid) throws IndexOutOfRangeException {
		List<Integer> column = new ArrayList<>();

		if (index < 1 || index > 9)
			throw new IndexOutOfRangeException(indexMessage);
		else {
			for (int i = index; i <= grid.getGrid().size() - 9 + index; i += 9) {
				column.add(grid.getGrid().get(i - 1));
			}
		}

		return column;
	}

	/**
	 * Method which retrieve a particular case of the grid from the row and
	 * column numbers
	 * 
	 * @param row
	 *            is the row number of the case that we want
	 * @param column
	 *            is the column number of the case that we want
	 * @return is an integer with the value of the case
	 * @throws IndexOutOfRangeException
	 */
	public Integer getCaseByRowAndByColumn(int row, int column, GridLocal grid) throws IndexOutOfRangeException {
		if (row < 0 || row > 9 || column < 0 || column > 9)
			throw new IndexOutOfRangeException(indexMessage);

		int index = (9 * row) + column;
		return grid.getGrid().get(index);
	}

	/**
	 * Method which retrieves a particular square of the grid from the index
	 * 
	 * @param int index index of the wanted square
	 * @return List<Integer> square list of 9 numbers representing the square
	 * @throws IndexOutOfRangeException
	 */
	public List<Integer> getSquareByIndex(int index, GridLocal grid) throws IndexOutOfRangeException {
		List<Integer> square = new ArrayList<>();
		if(index < 1 || index > 9){
			throw new IndexOutOfRangeException(indexMessage);
		} else {
			int maxLines;
			int minLines;
			int maxCol; 
			int minCol;
			//initialize lines indexes
			if(index>=1 && index<=3){
				minLines = 0;
				maxLines = 2;
			} else if (index > 3 && index <= 6){
				minLines = 3;
				maxLines = 5;
			} else {
				minLines = 6;
				maxLines = 8;
			}
			//initialize columns indexes
			if(index == 1 || index == 4 || index == 7){
				minCol = 0;
				maxCol = 2;
			} else if (index == 2 || index == 5 || index == 8){
				minCol = 3;
				maxCol = 5;
			} else {
				minCol = 6;
				maxCol = 8;
			}

			for (int l = minLines; l <= maxLines; l ++){
				for (int col = minCol; col <= maxCol; col++){
					square.add(grid.getGrid().get(l*9+col));
				}
			}
		}
		return square;
	}

	/**
	 * Method which indicates if a grid was solved or not
	 * 
	 * @return a boolean, true if the grid is solved, false if the grid isn't
	 * @throws IndexOutOfRangeException
	 */
	public boolean solve(GridLocal grid) throws IndexOutOfRangeException {
		// check for each line
		for (int l = 1; l < 10; l++) {
			List<Integer> line = this.getRowByIndex(l, grid);
			// test each number from 1 to 9
			for (int i = 1; i < 10; i++) {
				if (Collections.frequency(line, i) != 1) {
					return false;
				}
			}
		}

		// check columns
		for (int col = 1; col < 10; col++) {
			// build column list
			List<Integer> column = this.getColumnByIndex(col, grid);
			// test each number from 1 to 9
			for (int i = 1; i < 10; i++) {
				if (Collections.frequency(column, i) != 1) {
					return false;
				}
			}
		}
		// check squares
		for (int sq = 1; sq < 10; sq++) {
			// build square list
			List<Integer> square = this.getSquareByIndex(sq, grid);
			for (int i = 1; i < 10; i++) {
				if (Collections.frequency(square, i) != 1) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Method which generates a random grid
	 * 
	 * @return a list of 81 numbers which represents the entire grid
	 * @throws IndexOutOfRangeException 
	 */
	public boolean generate(GridLocal grid) throws IndexOutOfRangeException {
		List<Integer> newGrid = new ArrayList<>();
		int[] row = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };

		// generate a correct grid
		for (int nbLines = 0; nbLines < 9; nbLines++) {
			for (int nbCells = 0; nbCells < 9; nbCells++) {
				int index = nbCells + nbLines;
				if (index >= 9) {
					index = index - 9;
				}
				newGrid.add(nbLines*9 + nbCells, row[index]);
			}
		}

		// randomize grid by permutations
		do {
			newGrid = randomizeGrid(newGrid);
			grid.setGrid(newGrid);
		} while (!this.solve(grid));
		return true;
	}

	/**
	 * Method which interchange lines and columns
	 * 
	 * @param List<Integer>
	 *            grid grid to randomize
	 * 
	 * @return List<Integer> randomized grid
	 */
	private List<Integer> randomizeGrid(List<Integer> grid) {
		// interchange rows and columns to create a random grid
		List<Integer> newGrid = grid;
		for (int byLine = 0; byLine < 2; byLine++) {
			int firstItem;
			int secondItem;
			int max = 8;
			int min = 0;
			Random r = new Random();
			for (int i = 0; i < 9; i++) {
				firstItem = r.nextInt(max - min + 1) + min;
				do {
					secondItem = r.nextInt(max - min + 1) + min;
				} while (secondItem == firstItem);

				if (byLine == 0) {
					newGrid = interChangeRows(firstItem, secondItem, grid);
				} else if (byLine == 1) {
					newGrid = interChangeColumns(firstItem, secondItem, grid);
				}
			}
		}

		return newGrid;
	}

	/**
	 * Method which interchange two lines cell by cell
	 * 
	 * @param int
	 *            line1 first line to interchange
	 * @param int
	 *            line2 second line to interchange
	 * @param List<Integer>
	 *            grid grid in which interchange both lines
	 * @return a list of 81 numbers which represents the entire grid with the
	 *         two lines interchanged
	 */
	private List<Integer> interChangeRows(int line1, int line2, List<Integer> grid) {
		int tmp;
		for (int i = 0; i < 9; i++) {
			tmp = grid.get(line1 * 9 + i);
			grid.set(line1 * 9 + i, grid.get(line2 * 9 + i));
			grid.set(line2 * 9 + i, tmp);
		}
		return grid;
	}

	/**
	 * Method which interchange two columns cell by cell
	 * 
	 * @param int
	 *            col1 first column to interchange
	 * @param int
	 *            col2 second column to interchange
	 * @param List<Integer> grid grid in which interchange both columns
	 * @return a list of 81 numbers which represents the entire grid with the
	 *         two columns interchanged
	 */
	private List<Integer> interChangeColumns(int col1, int col2, List<Integer> grid) {
		int tmp;
		for (int i = 0; i < 9; i++) {
			tmp = grid.get(col1 + i * 9);
			grid.set(col1 + i * 9, grid.get(col2 + i * 9));
			grid.set(col2 + i * 9, tmp);
		}
		return grid;
	}

	/**
	 * Method witch return true if the grid has only one solution
	 * 
	 * @param List<Integer> 
	 * 				list witch contain the generated grid 
	 * @return true if only one solution false else 
	 */
	public boolean hasSolution(List<Integer> grid) {
		Model sudokuResolution = new Model();
		IntVar[] vars = new IntVar[81];
		for(int i =0;i<81;++i){
			if(grid.get(i)==null) 
				vars[i] = sudokuResolution.intVar("v"+i,1,9,false);
			else
				vars[i] = sudokuResolution.intVar("v"+i,grid.get(i));
		}
		for(int i=0;i<9;++i){
			IntVar[] ligne = new IntVar[9];
			IntVar[] collone = new IntVar[9];
			for(int j=0;j<9;++j){
				ligne[j] = vars[i*9+j];
				collone[j] = vars[j*9+i];
			}
			sudokuResolution.allDifferent(ligne).post();
			sudokuResolution.allDifferent(collone).post();
		}
		IntVar[] block1 = new IntVar[9];
		IntVar[] block2 = new IntVar[9];
		IntVar[] block3 = new IntVar[9];
		IntVar[] block4 = new IntVar[9];
		IntVar[] block5 = new IntVar[9];
		IntVar[] block6 = new IntVar[9];
		IntVar[] block7= new IntVar[9];
		IntVar[] block8 = new IntVar[9];
		IntVar[] block9 = new IntVar[9];
		int a;
		for(int i = 0;i<9;++i){
			a = (i-i%3)/3;
			block1[i]= vars[i%3 + a*9];
			block2[i]= vars[3+i%3 + a*9];
			block3[i]= vars[6+i%3 + a*9];
			block4[i]= vars[i%3 + (a+3)*9];
			block5[i]= vars[3+i%3 + (a+3)*9];
			block6[i]= vars[6+i%3 + (a+3)*9];
			block7[i]= vars[i%3+(a+6)*9];
			block8[i]= vars[3+i%3+(a+6)*9];
			block9[i]= vars[6+i%3+(a+6)*9];
		}
		sudokuResolution.allDifferent(block1).post();
		sudokuResolution.allDifferent(block2).post();
		sudokuResolution.allDifferent(block3).post();
		sudokuResolution.allDifferent(block4).post();
		sudokuResolution.allDifferent(block5).post();
		sudokuResolution.allDifferent(block6).post();
		sudokuResolution.allDifferent(block7).post();
		sudokuResolution.allDifferent(block8).post();
		sudokuResolution.allDifferent(block9).post();

		Solver solver = sudokuResolution.getSolver();
		int numberofResult = 0;
		while(solver.solve()){
			numberofResult++;
			if(numberofResult > 1)
				return false;
		}

		return numberofResult == 1;
	}
	
	public void savePlayedGrid(GridLocal grid) {
		UserLocal currentUser = processingManager.getUserManager().getCurrentUser();
		
		if(grid.getCreatorUuid().equals(currentUser.getUuid().toString()))
		{
			saveLocalGrid(grid);
		}
		else
		{
			saveDistantGrid(grid);
			
			boolean newGrid = true;
			
			Iterator<GridLocal> it = currentUser.getPlayedGrids().iterator();
			while(it.hasNext())
			{
				GridLocal nextGrid = it.next();
				
				if(nextGrid.getUuid().equals(grid.getUuid()))
				{
					nextGrid.setSavedState(grid.getSavedState());
					newGrid = false;
				}
			}
			
			if(newGrid)
			{
				try {
					currentUser.addGridToPlayedGrids(grid);
				} catch (NullGridLocalException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Method which persists the data of a grid on the computer of the user
	 * 
	 * @param GridLocal the grid to save
	 */
	public void saveLocalGrid(GridLocal grid) {
		String fileDirectory = System.getProperty("user.home")+"/sudoku_AI12/";
		File file = new File(fileDirectory);
		if(!file.exists() || !file.isDirectory())
		{
			file.mkdir();
		}

		try {
			UserLocal userLocal = processingManager.getUserManager().getCurrentUser();
			String fileName = fileDirectory+userLocal.getUuid()+".grids";
			
			File gridsFile = new File(fileName);
			
			if(gridsFile.exists() && !gridsFile.isDirectory())
			{
				FileInputStream fis = new FileInputStream(gridsFile);
				ObjectInputStream ois = new ObjectInputStream(fis);

				@SuppressWarnings("unchecked")
				List<GridLocal> allGrids = (List<GridLocal>)ois.readObject();
				ois.close();
				fis.close();
				boolean gridFound = false;

				Iterator<GridLocal> it = allGrids.iterator();

				while (it.hasNext() && gridFound == false) {
					GridLocal myGrid = it.next();
					if (myGrid.getUuid().toString().equals(grid.getUuid().toString())) {
						myGrid.setGrid(grid.getGrid());
						myGrid.setComments(grid.getComments());
						myGrid.setRatings(grid.getRatings());
						myGrid.setSavedState(grid.getSavedState());
						gridFound = true;
					}
				}
				if(!gridFound)
				{
					allGrids.add(grid);
				}
				FileOutputStream  fos = new FileOutputStream(gridsFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				oos.writeObject(allGrids);
				oos.close();
				fos.close();

			}
			else
			{
				List<GridLocal> allGrids = new ArrayList<>();
				allGrids.add(grid);
				FileOutputStream fos = new FileOutputStream(gridsFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				oos.writeObject(allGrids);
				oos.close();
				fos.close();
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void saveDistantGrid(GridLocal grid) {
		String fileDirectory = System.getProperty("user.home")+"/sudoku_AI12/";
		File file = new File(fileDirectory);
		if(!file.exists() || !file.isDirectory())
		{
			file.mkdir();
		}

		try {
			UserLocal userLocal = processingManager.getUserManager().getCurrentUser();
			String fileName = fileDirectory+userLocal.getUuid()+".playedgrids";
			File gridsFile = new File(fileName);
			if(gridsFile.exists() && !gridsFile.isDirectory())
			{
				FileInputStream fis = new FileInputStream(gridsFile);
				ObjectInputStream ois = new ObjectInputStream(fis);

				List<GridLocal> allGrids = (List<GridLocal>)ois.readObject();
				ois.close();
				fis.close();
				boolean gridFound = false;

				Iterator<GridLocal> it = allGrids.iterator();

				while (it.hasNext() && gridFound == false) {
					GridLocal myGrid = it.next();
					if (myGrid.getUuid().toString().equals(grid.getUuid().toString())) {
						myGrid.setGrid(grid.getGrid());
						myGrid.setComments(grid.getComments());
						myGrid.setRatings(grid.getRatings());
						myGrid.setSavedState(grid.getSavedState());
						gridFound = true;
					}
				}
				if(!gridFound)
				{
					allGrids.add(grid);
				}
				FileOutputStream  fos = new FileOutputStream(gridsFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				oos.writeObject(allGrids);
				oos.close();
				fos.close();

			}
			else
			{
				List<GridLocal> allGrids = new ArrayList<>();
				allGrids.add(grid);
				FileOutputStream fos = new FileOutputStream(gridsFile);
				ObjectOutputStream oos = new ObjectOutputStream(fos);

				oos.writeObject(allGrids);
				oos.close();
				fos.close();
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} 
	}
	
	public void loadSavedGrids()
	{
		String fileDirectory = System.getProperty("user.home")+"/sudoku_AI12/";
		UserLocal userLocal = processingManager.getUserManager().getCurrentUser();
		String fileName = fileDirectory+userLocal.getUuid()+".grids";
		
		loadGridFile(fileName, true);
		
		fileName = fileDirectory+userLocal.getUuid()+".playedgrids";
		loadGridFile(fileName, false);
		
	}
	
	private void loadGridFile(String fileName, boolean isMyGrids)
	{
		String fileDirectory = System.getProperty("user.home")+"/sudoku_AI12/";
		File file = new File(fileDirectory);
		if(!file.exists() || !file.isDirectory())
		{
			file.mkdir();
		}
		
		try {
			File gridsFile = new File(fileName);
			if(gridsFile.exists() && !gridsFile.isDirectory())
			{
				FileInputStream fis = new FileInputStream(gridsFile);
				ObjectInputStream ois = new ObjectInputStream(fis);

				List<GridLocal> allGrids = (List<GridLocal>)ois.readObject();
				ois.close();
				fis.close();

				Iterator<GridLocal> it = allGrids.iterator();

				while (it.hasNext()) {
					GridLocal grid = it.next();

					this.setCurrentLocalGrid(grid);
					this.processingManager.getUserManager().getCurrentUser().saveGrid(grid);
					this.processingManager.getProcessingToGUI().updateGridList(this.processingManager.getGridManager().getAllGrids());
					if(isMyGrids)
					{
						this.processingManager.getUserManager().getCurrentUser().addSharableGrids(grid);
					}
					else
					{
						this.processingManager.getUserManager().getCurrentUser().addGridToPlayedGrids(grid);
					}
				}
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		} catch (NullGridLocalException e) {
			e.printStackTrace();
		} 
	}
}
