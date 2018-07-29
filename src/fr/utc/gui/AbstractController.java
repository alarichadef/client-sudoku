package fr.utc.gui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public abstract class AbstractController extends Application implements Initializable {
	public static final String ICON = "sudoku_icon.png";
	protected static final String FXML_HOME = "home.fxml";
	protected static final String FXML_CONSULTGRID = "consult_grid.fxml";
	protected static final String FXML_CREATEGRID = "create_grid.fxml";
	protected static final String FXML_PLAYGRID = "play_grid.fxml";
	protected static GUIManager guiManager;
	protected static Logger logger;
	protected Stage stage;

	public AbstractController() {
		logger = Logger.getLogger(getClass().getName());
	}

	public void showScene(Stage stage, String scenePath, String title) throws IOException {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource(scenePath));
		loader.setController(this);

		this.stage = stage;

		try {
			Parent root = loader.load();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle(title);
			stage.getIcons().add(new Image(getClass().getResourceAsStream(ICON)));
			stage.setOnCloseRequest(event -> close());
			stage.show();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

	}

	public void close() {
		logger.log(Level.WARNING, "Controller has been closed before removal in GuiManager.");
		stage.close();
	}
}
