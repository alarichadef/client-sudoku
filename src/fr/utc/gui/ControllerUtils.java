package fr.utc.gui;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ControllerUtils {
	private ControllerUtils() {

	}

	public static List<Integer> getFilledGrid(GridPane gCase) {
		List<Integer> filledGrid = new ArrayList<>();
		for (Node node : gCase.getChildren()) {
			if (node instanceof TextField) {
				TextField tField = (TextField) node;
				if (tField.getText().isEmpty()) {
					filledGrid.add(null);
				} else {
					filledGrid.add(Integer.parseInt(tField.getText()));
				}
			}
		}
		return filledGrid;
	}
}
