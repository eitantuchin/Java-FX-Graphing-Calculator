
package application;

import javafx.geometry.Insets;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Assembles the menu of the graphing calculator application.
 * @author Eitan Tuchin
 *
 */

public class Menu {
	
	/**
	 * A copy of the base app.
	 */
	
	private GraphingApp app;

	/**
	 * Initializes the menu.
	 * @param app The base app.
	 */
	
	public Menu(GraphingApp app) {
		this.app = app;

	}

	/**
	 * Creates a new menu scene that contains the title, instructional information, and the option to create a graph.
	 * @return A new menu scene.
	 */
	
	protected Scene createMenuScene() {

		// title of app
		// ------------------------------------------------------------------------
		Text title = new Text("Graphing Calculator\n    By: Eitan Tuchin");
		title.setFont(Font.font(100));
		title.setFill(Color.GREEN);

		Rectangle titleRect = new Rectangle(900, 250);
		titleRect.setFill(Color.AQUA);
		titleRect.setStroke(Color.RED);
		titleRect.setStrokeWidth(6.0);
		titleRect.setArcWidth(20);
		titleRect.setArcHeight(20);

		StackPane titlePane = new StackPane(titleRect, title);
		StackPane.setAlignment(title, Pos.TOP_CENTER);
		titlePane.setTranslateY(30);
		// ------------------------------------------------------------------------

		// button for a new graph
		// --------------------------------------------------------------------------------------------------------------------
		Button newGraphButton = createSquareButton("New Graph", Color.BLUE);
		newGraphButton.setStyle("-fx-background-color: orange; -fx-border-color: red; -fx-border-width: 6px; "
				+ "-fx-border-radius: 20px; -fx-background-radius: 20px;");
		newGraphButton.setOnMouseEntered(
				e -> newGraphButton.setStyle("-fx-background-color: lightgreen; -fx-border-color: red; "
						+ " -fx-border-width: 6px; -fx-border-radius: 20px; -fx-background-radius: 20px;"));
		newGraphButton
				.setOnMouseExited(e -> newGraphButton.setStyle("-fx-background-color: orange; -fx-border-color: red; "
						+ "-fx-border-width: 6px; -fx-border-radius: 20px; -fx-background-radius: 20px;"));
		newGraphButton.setOnAction(event -> app.openGraphScene());
		newGraphButton.setTranslateX(80);
		newGraphButton.setTranslateY(-30);
		// --------------------------------------------------------------------------------------------------------------------

		// how to section
		// --------------------------------------------------------------------------------------------------------------------
		Rectangle howToSection = new Rectangle(400, 400);
		howToSection.setFill(Color.ORANGE);
		howToSection.setStroke(Color.RED);
		howToSection.setStrokeWidth(6.0);
		howToSection.setArcWidth(20);
		howToSection.setArcHeight(20);

		Text howToTextTitle = createMenuText("HOW TO:", 24, 150, 25);

		Text howToTextBody = createMenuText("\n - Hover over “New Graph” to create a new graph"
				+ "\n - Use the “Add Equation” button to graph as many \n\t lines as you want"
				+ "\n - The “Menu” button will return you back here"
				+ "\n - To reset the graph without having to go back to the \n\t menu, use the “New Graph” button in the graph menu"
				+ "\n - Hit the “X” next to any of the equations to delete \n\t any equation from the graph of your choosing"
				+ "\n - Double-click the graph with your touchpad or mouse \n\t to zoom in, click with two fingers to zoom out"
				+ "\n - Supported characters for an equation include \n\t all numbers from 0 - 9, /, *, ^, -, +, ., and ()."
				+ "\n - Have a try at it!", 16, 10, 15);
		
		howToTextBody.setTextAlignment(TextAlignment.LEFT);
		
		howToTextBody.setLineSpacing(7);
	

		VBox howToBox = new VBox();
		
		howToBox.getChildren().addAll(howToTextTitle, howToTextBody);
		
		StackPane howToPane = new StackPane(howToSection, howToBox);
		
		howToPane.setAlignment(Pos.CENTER);
		
		howToPane.setPadding(new Insets(20));
		
		howToPane.setTranslateY(-30);
		// --------------------------------------------------------------------------------------------------------------------

		// background of menu
		// ------------------------------------------------------------------------------------
		Pane backgroundPane = new Pane();
		
		backgroundPane.setMinSize(1200, 794);
		
		Image img = new Image("backgroundImage.jpg");
		
		BackgroundImage backgroundImg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.CENTER, new BackgroundSize(1200, 794, true, true, true, true));

		backgroundPane.setBackground(new Background(backgroundImg));
		// ------------------------------------------------------------------------------------

		// menu layout
		// -------------------------------------------------------------------------------
		HBox functionBox = new HBox();
		
		functionBox.setSpacing(200);
		
		functionBox.setAlignment(Pos.CENTER);
		
		functionBox.getChildren().addAll(newGraphButton, howToPane);

		VBox menuLayout = new VBox();
		
		menuLayout.getChildren().addAll(titlePane, functionBox);
		
		menuLayout.setSpacing(100);
		
		menuLayout.setAlignment(Pos.CENTER);

		StackPane rootPane = new StackPane();
		
		rootPane.getChildren().addAll(backgroundPane, menuLayout);
		// -------------------------------------------------------------------------------

		// Create and return the menu scene
		return new Scene(rootPane, 1200, 794);
	}

	/**
	 * Helper method that takes in different properties of a Text object and applies them accordingly.
	 * @param words Information to be displayed.
	 * @param fontSize Size of letters.
	 * @param deltaX Position of text relative to the x-axis.
	 * @param deltaY Position of text relative to the y-axis.
	 * @return text The updated Text object.
	 */
	
	private Text createMenuText(String words, int fontSize, int deltaX, int deltaY) {
		
		Text text = new Text(words);
		
		text.setFont(Font.font(fontSize));
		
		text.setFill(Color.BLUE);
		
		text.setTranslateX(deltaX);
		
		text.setTranslateY(deltaY);
		
		return text;
	}
	
	/**
	 * Helper method that takes in different properties of a Button object and applies them accordingly.
	 * @param text The information to go onto the button.
	 * @param color The color of the button.
	 * @return button The updated Button object.
	 */
	
	private Button createSquareButton(String text, Color color) {
		
		Button button = new Button(text);
		
		button.setMinSize(300, 300); 
		
		button.setFont(Font.font("System Bold", 40));
		
		button.setTextFill(color);
		
		return button;
	}
}
