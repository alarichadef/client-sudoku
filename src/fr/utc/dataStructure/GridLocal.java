package fr.utc.dataStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridLocal extends GridNetwork{
	private static final String OUTOFRANGE = "The index you passed in parameter is not between 1 and 9";
	private List<Integer> grid;
	private List<Integer> savedState;
	
	public GridLocal() {
		// very convenient despite being ugly
	}

	public GridLocal(List<String> tags, List<Group> readGroup, List<Group> commentGroup,
			List<Group> playGroup, String name, List<Integer> grid, List<Integer> savedState, String creatorUuid) {
		super(tags, readGroup, commentGroup, playGroup, name, creatorUuid);
		this.grid = grid;
		this.savedState = savedState;
	}

	public GridLocal(List<String> tags, List<Group> readGroup, List<Group> commentGroup,
			List<Group> playGroup, String name, List<Integer> grid, String creatorUuid) {
		super(tags, readGroup, commentGroup, playGroup, name, creatorUuid);
		this.grid = grid;
		this.savedState = grid;
	}

	public List<Integer> getGrid() {
		return grid;
	}

	public void setGrid(List<Integer> grid) {
		this.grid = grid;
	}

	public List<Integer> getSavedState() {
		return savedState;
	}

	public void setSavedState(List<Integer> savedState) {
		this.savedState = savedState;
	}

}
