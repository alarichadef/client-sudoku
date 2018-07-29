package fr.utc.gui;

import fr.utc.interfaces.GUIToProcessing;
import fr.utc.interfaces.ProcessingToGUI;

public class GUIManager {
	private GUIToProcessing guiToProcessing;
	private ProcessingToGUI processingToGUI;
	private SignInController signInController;
	private SignUpController signUpController;
	private HomeController homeController;
	private CreateGridController createGridController;
	private ConsultGridController consultGridController;
	private PlayGridController playGridController;
	
	public GUIManager() {
		this.processingToGUI = new ProcessingToGUIImpl(this);
	}

	public ConsultGridController getConsultGridController() {
		return consultGridController;
	}

	public void setConsultGridController(ConsultGridController consultGridController) {
		this.consultGridController = consultGridController;
	}

	public GUIToProcessing getGuiToProcessing() {
		return guiToProcessing;
	}

	public void setGuiToProcessing(GUIToProcessing guiToProcessing) {
		this.guiToProcessing = guiToProcessing;
	}

	public ProcessingToGUI getProcessingToGUI() {
		return processingToGUI;
	}

	public void setProcessingToGUI(ProcessingToGUI processingToGUI) {
		this.processingToGUI = processingToGUI;
	}
	
	public SignInController getSignInController() {
		return signInController;
	}

	public void setSignInController(SignInController signInController) {
		this.signInController = signInController;
	}
	
	public SignUpController getSignUpController() {
		return signUpController;
	}

	public void setSignUpController(SignUpController signUpController) {
		this.signUpController = signUpController;
	}

	public HomeController getHomeController() {
		return homeController;
	}

	public void setHomeController(HomeController homeController) {
		this.homeController = homeController;
	}

	public CreateGridController getCreateGridController() {
		return createGridController;
	}

	public void setCreateGridController(CreateGridController createGridController) {
		this.createGridController = createGridController;
	}
	
	public PlayGridController getPlayGridController() {
		return playGridController;
	}

	public void setPlayGridController(PlayGridController playGridController) {
		this.playGridController = playGridController;
	}
}
