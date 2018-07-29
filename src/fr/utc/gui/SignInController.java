package fr.utc.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SignInController extends AbstractController {
	private SignInModel model;

	// r�f�rences vers la vue

	@FXML
	private TextField tf_username;
	@FXML
	private TextField tf_password;
	@FXML
	private Label lb_title;
	@FXML
	private Button btn_signIn;
	@FXML
	private Button btn_signUp;
	
	public void signIn() {
		String usr = tf_username.getText();
		String pwd = tf_password.getText();		
		if (usr.isEmpty() || pwd.isEmpty()){
			lb_title.setText("Please fill all fields.");
			lb_title.setTextFill(Color.web("#f00"));
			return;
		}
		if (guiManager.getGuiToProcessing().authentication(usr, pwd)) {
			System.out.println("SignedIn");
			// TODO envoyer la sauce au groupe GUI
			try {
				HomeController homeController = new HomeController();
				homeController.showScene(new Stage(), "home.fxml", "Home");
				guiManager.setHomeController(homeController);
			    Stage stage = (Stage) tf_username.getScene().getWindow();
			    stage.close();
			} catch (IOException e) {
				logger.severe(e.toString());
			}
		}
		else {
			lb_title.setText("Please enter corectly your credentials !");
			lb_title.setTextFill(Color.web("#f00"));
		}
	}

	public void signUp() throws IOException {
		if (guiManager.getSignUpController() == null) {
			SignUpController signUpController = new SignUpController();
			signUpController.showScene(new Stage(), "signUpView.fxml", "Sign up");
			guiManager.setSignUpController(signUpController);
		}
	}
	
	public static GUIManager getGuiManager() {
		return guiManager;
	}

	public static void startApplication(GUIManager guiManager) {
		SignInController.guiManager = guiManager;
		launch();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		EventHandler<KeyEvent> handleEvent = new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent keyEvent) {
		        if (keyEvent.getCode() == KeyCode.ENTER)  {
		            signIn();
		        }
		    }
		};
		tf_username.setOnKeyPressed(handleEvent);
		tf_password.setOnKeyPressed(handleEvent);
		btn_signIn.setOnKeyPressed(handleEvent);
	}
	
	

	@Override
	public void start(Stage stage) throws Exception {
		guiManager.setSignInController(this);
		this.showScene(stage, "SignInView.fxml", "Connection");
	}


}
