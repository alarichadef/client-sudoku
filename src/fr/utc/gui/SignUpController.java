package fr.utc.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class SignUpController extends AbstractController {
	
	public SignUpController () {
	}
	
	private SignInModel model;
	
	// r�f�rences vers la vue

	@FXML private TextField tf_username;
	@FXML private TextField tf_password;
	@FXML private TextField tf_password2;
	@FXML private TextField tf_firstname;
	@FXML private TextField tf_lastname;
	@FXML private Label lb_title;
	@FXML private DatePicker dp_dateOfBirth;
	@FXML private Button btn_avatar;
	@FXML private Button btn_create;
	
	private File avatarFile;
	/**
	 * Initialize called after the constructor
	 */
	
	/**
	 * Cr�ation du profil lors du clic sur le bouton Create
	 * @throws IOException
	 */
	public void create() throws IOException{
		// V�rification des champs
		String p1 = tf_password.getText();
		String p2 = tf_password2.getText();
		String firstName = tf_firstname.getText();
		String lastName = tf_lastname.getText();
		if (p1.isEmpty() || p2.isEmpty() || tf_username.getText().isEmpty() || lastName.isEmpty() || firstName.isEmpty()){
			lb_title.setText("Please fill all mandatory fields.");
			lb_title.setTextFill(Color.web("#f00"));
			return;
		}
		if (!p1.equals(p2)) {
			lb_title.setText("Passwords don't match.");
			lb_title.setTextFill(Color.web("#ff0"));
			return;
		}
		Stage stage = (Stage) btn_create.getScene().getWindow();
		Date dateOfBirth = null;
		LocalDate localDate = dp_dateOfBirth.getValue();
		if (localDate != null) {
			try {
				Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
				dateOfBirth = Date.from(instant);
			} catch (Exception e) {
				dateOfBirth = null;
				lb_title.setText("Madre mia ! La fecha introducida no es valid en Valladolid !");
				lb_title.setTextFill(Color.web("#0ff"));
			}
		}
        BufferedImage avatar = null;
		if (avatarFile != null) {
	        try {
	        	avatar = ImageIO.read(avatarFile);
	        }
	        catch (Exception e){
				lb_title.setText("Puta madre ! Este fichero no esta un pinche foto");
				lb_title.setTextFill(Color.web("#0ff"));
	        }
		}

        SignInController.getGuiManager().getGuiToProcessing().createUser(tf_username.getText(), tf_password.getText(), tf_firstname.getText(), tf_lastname.getText(), dateOfBirth, avatar);
		guiManager.setSignUpController(null);
        stage.close();
	}
	
	public void chooseAvatar(){
		FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File("."));
        File f = fileChooser.showOpenDialog(new Stage());
        if (f == null) {
        	avatarFile = null;
            btn_avatar.setText("Choose a picture");
        	return;
        }
        String name =(f.getName());
        btn_avatar.setText(name);
        avatarFile = f;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	              guiManager.setSignUpController(null);

	          }
	     });     
	}
	
	
}
