package main;

import java.util.Locale;

import fr.utc.gui.GUIManager;
import fr.utc.gui.SignInController;
import fr.utc.networking.NetworkManager;
import fr.utc.processing.managers.ProcessingManager;

public class main {

	public static void main(String[] args) {
		NetworkManager network = new NetworkManager();
		ProcessingManager data = new ProcessingManager();
		GUIManager gui = new GUIManager();

		network.setNetToProcess(data.getNetworkingToProcessing()); // network asks processing its interface
		
		gui.setGuiToProcessing(data.getGuiToProcessing());
		
		data.setProcessingToNetworking(network.getProcessingToNetworking()); // Processing asks to networking its interface
		data.setProcessingToGUI(gui.getProcessingToGUI()); // Processing asks to GUI its interface
		
		Locale.setDefault(Locale.ENGLISH);

		SignInController.startApplication(gui);
	}

}
