package fr.utc.gui.tasks;

import fr.utc.dataStructure.GridLocal;
import fr.utc.gui.GUIManager;

public class DisplayGridTask implements Runnable {
	private GUIManager guiManager;
	private GridLocal gridLocalParameter;

	public DisplayGridTask(GUIManager guiManager) {
		super();
		this.guiManager = guiManager;
	}

	public GridLocal getGridLocalParameter() {
		return gridLocalParameter;
	}

	public void setGridLocalParameter(GridLocal gridLocalParameter) {
		this.gridLocalParameter = gridLocalParameter;
	}

	@Override
	public void run() {
		guiManager.getHomeController().consultGrid(gridLocalParameter);
	}
}