package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;

/**
 * Class that launches the graphing calculator application.
 * 
 * @author Eitan Tuchin
 * @version 1.0
 * @since 1/11/2024
 */

public class GraphingApp extends Application {

	/**
	 * Sets the current scene that the user sees within the application.
	 */
	
	private Stage stage;

	/**
	 * Launches the application .
	 * @param args N/A
	 */
	
	public static void main(String[] args) {
		
		launch(args);
	}

	/**
	 * Creates the title of the application, opens the menu to the user, and places the icon for the app.
	 * @param primaryStage The base of the application.
	 * 
	 */
	
	@Override
	public void start(Stage primaryStage) {

		this.stage = primaryStage;
		
		stage.setTitle("Graphing Calculator");

		// Create the main menu scene
		openMenuScene();

		// Add app icon
		//---------------------------------------
		Image appIcon = new Image("appIcon.png");

		stage.getIcons().add(appIcon);
		//---------------------------------------


		stage.show();
	}

	/**
	 * Creates a new graph and graph scene and opens it.
	 */
	
	protected void openGraphScene() {
		
		Graph graph = new Graph(this);
		
		Scene graphScene = graph.createGraphScene();
		
		stage.setScene(graphScene);
	}

	/**
	 * Creates a new menu and menu scene and opens it.
	 */
	
	protected void openMenuScene() {
		
		Menu menu = new Menu(this);
		
		Scene menuScene = menu.createMenuScene();
		
		stage.setScene(menuScene);
	}
	
}
