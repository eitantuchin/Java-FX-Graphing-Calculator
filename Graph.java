package application;

import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;

import javafx.event.EventHandler;

import java.util.ArrayList;

import java.util.Arrays;

import java.util.HashMap;

import java.util.List;

import java.util.Random;

import javafx.beans.value.ChangeListener;

import javafx.geometry.Insets;

import javafx.scene.Node;

import javafx.scene.Scene;

import javafx.scene.chart.LineChart;

import javafx.scene.chart.NumberAxis;

import javafx.scene.chart.XYChart;

import javafx.scene.control.Button;

import javafx.scene.control.Label;

import javafx.scene.control.ScrollPane;

import javafx.scene.control.ScrollPane.ScrollBarPolicy;

import javafx.scene.control.TextField;

import javafx.scene.layout.HBox;

import javafx.scene.layout.VBox;

import javafx.scene.paint.Color;

import javafx.scene.shape.Circle;

import javafx.scene.text.Font;

/**
 * Assembles the graph scene of the graphing calculator which allows for many different useful functions. This includes
 * the abulity to plot multiple lines each with consistent coloring, smooth zooming in and out of the desired portion of the graph,
 * error messaging if the user inputs an illegal expression, color indicators for each equation, and the ability to delete any equation
 * of your choosing from the graph.
 * 
 * @author Eitan Tuchin
 *
 */

public class Graph {

	/**
	 * The lower bound of both axes.
	 */
	
	final static int LOWER_BOUND = -25;

	/**
	 * The upper bound of both axes.
	 */
	
	final static int UPPER_BOUND = 25;

	/**
	 * A copy of the base app.
	 */
	
	private GraphingApp app;

	/**
	 * The x-axis of the graph.
	 */
	
	private NumberAxis xAxis = new NumberAxis(LOWER_BOUND, UPPER_BOUND, 1);

	/**
	 * The y-axis of the graph.
	 */
	
	private NumberAxis yAxis = new NumberAxis(LOWER_BOUND, UPPER_BOUND, 1);

	/**
	 * The graph constructed with both axes.
	 */
	
	private LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

	/**
	 * Stores all colors that have already been used within the graph.
	 */
	
	private ArrayList<Color> alreadyChosen = new ArrayList<>();
	
	/**
	 * Assigns every new equation with a textfield to keep track of user input changes.
	 */
	
	private HashMap<TextField, ArrayList<XYChart.Series<Number, Number>>> equationMap = new HashMap<>();

	/**
	 * Assigns a color with a textfield to keep track of which color belongs to which equation.
	 */
	
	private HashMap<TextField, Color> colorMap = new HashMap<>();

	/**
	 * Assigns a label, namely a label that contains an error and assigns it to a textfield to keep track of 
	 * which textfield contains invalid input.
	 */
	
	private HashMap<TextField, Label> errorMap = new HashMap<>();

	/**
	 * Keeps track of changes to the bounds of the graph from zooming operations.
	 */
	
	private double newXUpper = 0, newXLower = 0, newYUpper = 0, newYLower = 0;

	/**
	 * Initializes the graph.
	 * @param app The base app.
	 */
	
	public Graph(GraphingApp app) {

		this.app = app;

	}

	/**
	 * Applies an operator to the given range to decide whether to zoom in or out.
	 * @param range The range of the gvien axis.
	 * @param op The given operator.
	 * @return range The calculated change.
	 */
	
	private double doOp(double range, char op) {

		if (op == '*') {

			return range;

		}

		else {

			return range / 4;

		}

	}
	
	/**
	 * Zooms in or out of the graph depending on user interaction with the mousepad.
	 * @param x The mouse's x-axis position within the graph.
	 * @param y The mouse's y-axis position within the graph.
	 * @param op The given operator.
	 */

	private void zoomInOrOut(double x, double y, char op) {

		double xRange = xAxis.getUpperBound() - xAxis.getLowerBound();

		double yRange = yAxis.getUpperBound() - yAxis.getLowerBound();

		newXUpper = x + doOp(xRange, op);

		newYUpper = y + doOp(yRange, op);

		newYLower = x - doOp(xRange, op);

		newXLower = y - doOp(yRange, op);

		// Set new axis ranges

		xAxis.setLowerBound(Math.max(newXLower, LOWER_BOUND));

		xAxis.setUpperBound(Math.min(newXUpper, UPPER_BOUND));

		yAxis.setLowerBound(Math.max(newYLower, LOWER_BOUND));

		yAxis.setUpperBound(Math.min(newYUpper, UPPER_BOUND));

	}

	/**
	 * Creates a new scrollable graph scene that contains a line chart with an equation box and options to add equations, 
	 * return back to the menu, remove an equation, or make a new graph scene. Allows for the graph to update dynamically. (Real time
	 * graph updates.)
	 * @return scene A new graph scene.
	 */
	
	protected Scene createGraphScene() {

		// allow use to zoom in and out depening on mouse location within the chart
		// -------------------------------------------------------------------------
		lineChart.setOnMousePressed( e -> {

			Number dataX = xAxis.getValueForDisplay(e.getX());

			Number dataY = yAxis.getValueForDisplay(e.getY());

			if (e.isPrimaryButtonDown() && e.getClickCount() >= 2) {

				// one finger to zoom-in with double click

				zoomInOrOut((Double) dataX, (Double) dataY, '/');

			}

			else if (e.isSecondaryButtonDown()) {

				// two fingers to zoom-out

				zoomInOrOut((Double) dataX, (Double) dataY, '*');

			}

		});
		// -------------------------------------------------------------------------

		lineChart.setPrefSize(800, 600);

		// Make scene scrollable

		ScrollPane rootPane = new ScrollPane();

		// Create a VBox to hold the graph and bottom panel

		
		// creating the graph layout containing the chart, initial equation box, and buttons with useful functionality
		// ----------------------------------------------------------------------------------------------------------
		VBox mainVBox = new VBox();

		mainVBox.setPrefWidth(1180);

		VBox bottomPanelVBox = new VBox();

		bottomPanelVBox.setPadding(new Insets(20));

		Label equationLabel = new Label("Enter Equation(s):");

		equationLabel.setFont(new Font(25));

		VBox finalEquationBox = new VBox();

		HBox initialEquationBox = new HBox(); 

		Circle circle = new Circle(20, Color.WHITE); // will indicate what color is assigned to what equation

		// outline circle in black
		
		circle.setStroke(Color.BLACK);

		circle.setStrokeWidth(2.0);

		Label yLabel = new Label("y = ");

		yLabel.setFont(new Font(24));

		yLabel.setTranslateX(10);

		TextField initf = new TextField();

		initf.setPrefSize(900, 40);

		initf.setFont(new Font(18));

		initialEquationBox.setSpacing(10);

		initialEquationBox.setPadding(new Insets(20, 0, 20, 0));

		initialEquationBox.getChildren().addAll(circle, yLabel, initf);

		Button addEquationButton = createOptionButton("Add Equation", "limegreen", 60);

		addEquationButton.setOnAction(e -> {

			VBox newEquationBox = addEquationBox(); // allows for more equation to be plotted simultaenousely

			bottomPanelVBox.getChildren().add(newEquationBox);

		});

		Button menuButton = createOptionButton("Menu", "lightblue", 365);

		menuButton.setOnAction(e -> app.openMenuScene()); // returns you back to the menu

		Button newGraphButton = createOptionButton("New Graph", "orange", 670);

		newGraphButton.setOnAction(e -> app.openGraphScene()); // creates a fresh graph scene

		HBox buttonBox = new HBox();

		buttonBox.getChildren().addAll(addEquationButton, menuButton, newGraphButton);
		
		finalEquationBox.getChildren().addAll(equationLabel, initialEquationBox);

		bottomPanelVBox.getChildren().add(finalEquationBox);

		// Add the VBox containing all the equations to the main VBox

		mainVBox.getChildren().addAll(lineChart, buttonBox, bottomPanelVBox);

		// Add the main VBox to the rootPane

		rootPane.setContent(mainVBox);

		// Configure scrollbar policies

		rootPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);

		rootPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
		// ----------------------------------------------------------------------------------------------------------

		// dynamically update graph
		// ----------------------------------------------------------------------------------------------------------
		initf.textProperty().addListener(new ChangeListener<String>() {

			@Override

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if (newValue.isEmpty()) { // nothing is typed in that particular textfield
					
					// remove the error if any and the line from the chart
					
					lineChart.getData().removeAll(equationMap.get(initf));

					equationMap.remove(initf);

					Circle c = (Circle) initialEquationBox.getChildren().get(0);

					c.setFill(Color.WHITE);

					VBox parentVBox = (VBox) initialEquationBox.getParent();

					parentVBox.getChildren().remove(errorMap.get(initf));

					errorMap.remove(initf);

				}

				else {

					plotEquation(initialEquationBox, newValue);

				}

			}

		});
		// ----------------------------------------------------------------------------------------------------------

		return new Scene(rootPane, 1200, 794);

	}
	
	/**
	 * Helper method that takes in different properties of a Button object and applies them accordingly.
	 * @param title The title of the button.
	 * @param color The color of the button.
	 * @param deltaX The x-axis position of the button.
	 * @return button The newly updated button.
	 */
	
	private Button createOptionButton(String title, String color, int deltaX) {
		
		Button button = new Button(title);
		
		button.setTranslateX(deltaX);
		
		button.setStyle("-fx-background-color: " + color + ";");
		
		button.setPrefSize(150, 40);
		
		button.setFont(Font.font("System Bold", 20));
		
		button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: lightgreen;"));
		
		button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + ";"));
		
		return button;
	}

	/**
	 * Method that does the magic. Plots data points at intervals of 1/25 over the interval from LOWER_BOUND
	 * to UPPER_BOUND and identifies asymptotes by creating a new XYChart.Series object to continue plotting the equation.
	 * Catches illegal user input.
	 * @param equationBox The given equation box.
	 * @param equation The current user inputted expression to plot.
	 */
	
	private void plotEquation(HBox equationBox, String equation) {

		Circle c = (Circle) equationBox.getChildren().get(0);

		TextField tf = (TextField) equationBox.getChildren().get(2);

		try {
			
			// use an equation solver object to plot the line
			
			EquationSolver EQ = new EquationSolver();

			XYChart.Series<Number, Number> series = new XYChart.Series<>();

			ArrayList<XYChart.Series<Number, Number>> seriesArr = equationMap.get(tf);
			
			// if the line has been plotted before we create a new line
			
			if (seriesArr == null) {

				seriesArr = new ArrayList<>();

				equationMap.put(tf, seriesArr);

			}
			
			// remove all lines from the chart

			lineChart.getData().removeAll(seriesArr);

			// logic for creating the graph for given equation

			// -----------------------------------------------

			seriesArr.clear();

			// Create a new series for each valid range of x values

			for (int i = 0; i <= (UPPER_BOUND - LOWER_BOUND) * 25; i++) {

				double x = LOWER_BOUND + i * 0.04;

				double y = EQ.parseEquation(equation, x);

				// Check if y is a valid number before adding to the series

				if (!(y == Double.POSITIVE_INFINITY) && !(y == Double.NEGATIVE_INFINITY) && !(Double.isNaN(y))) {

					series.getData().add(new XYChart.Data<>(x, y));

				}

				else {

					seriesArr.add(series);

					series = new XYChart.Series<>();

				}

			}

			// add the last part of the line to the chart
			
			seriesArr.add(series);

			// -----------------------------------------------

			equationMap.values().forEach(lineChart.getData()::addAll); // Add all series from the HashMa
			
			lineChart.setCreateSymbols(false); // ensures that every individual point that was plotted is shown

			lineChart.setLegendVisible(false);

			lineChart.setAnimated(false);

			Color color = assignColor(seriesArr, tf);

			c.setFill(color);

			// removes any error because line was able to be plotted
			// ------------------------------------------------------
			VBox parentVBox = (VBox) equationBox.getParent();

			parentVBox.getChildren().remove(errorMap.get(tf));

			errorMap.remove(tf);
			// ------------------------------------------------------

		}

		// displays an error message if the line inputted by user was wrong in any way
		// ----------------------------------------------------------------------------
		catch (Exception e) {

			c.setFill(Color.WHITE);

			displayErrorMessage("Not a valid equation.", tf);

		}
		// ----------------------------------------------------------------------------

	}

	/**
	 * Adds an equation box to the bottom panel VBox. The equation box has all the same attributes of the initial equation box
	 * apart from the ability to be removed via the remove button. The equation box contains a textfield, a color identifier, 
	 * and a remove button. Allows for dynamic updates of the graph.
	 * 
	 * @return finalEquationBox The VBox that contains the equation box that allows for error messages to appear below that 
	 * particular equation box if needed.
	 */
	
	private VBox addEquationBox() {

		// setting up initial properies of the equation box
		// -------------------------------------------------
		VBox finalEquationBox = new VBox();

		HBox equationBox = new HBox(); 

		Circle circle = new Circle(20, Color.WHITE);

		circle.setStroke(Color.BLACK); 

		circle.setStrokeWidth(2.0);

		Label yLabel = new Label("y = ");

		yLabel.setFont(new Font(24));

		yLabel.setTranslateX(10);

		TextField tf = new TextField();

		tf.setPrefSize(900, 40);

		tf.setFont(new Font(18));
		// -------------------------------------------------
		
		// dynamically update the graph for that selected text field

		tf.textProperty().addListener(new ChangeListener<String>() {

			@Override

			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if (newValue.isEmpty()) {

					lineChart.getData().removeAll(equationMap.get(tf));

					equationMap.remove(tf);

					// remove color from circle

					Circle c = (Circle) equationBox.getChildren().get(0);

					c.setFill(Color.WHITE);

					VBox parentVBox = (VBox) equationBox.getParent();

					parentVBox.getChildren().remove(errorMap.get(tf));

					errorMap.remove(tf);

				}

				else {

					plotEquation(equationBox, newValue);

				}

			}

		});

		equationBox.setSpacing(10);

		equationBox.setPadding(new Insets(20, 0, 20, 0));

		Button removeButton = new Button("X");

		removeButton.setStyle("-fx-background-color: red; -fx-text-fill: black;");

		removeButton.setFont(new Font(25));

		removeButton.setPrefSize(40, 40);

		// removes that equation box from the bottom panel box if pressed
		// -----------------------------------------------------------------------
		removeButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override

			public void handle(ActionEvent event) {

				TextField thistf = (TextField) equationBox.getChildren().get(2);

				try {

					lineChart.getData().removeAll(equationMap.get(thistf));

					colorMap.remove(thistf);

				}

				catch (NullPointerException e) {

				}

				equationMap.remove(thistf);

				VBox bottomPanelVBox = (VBox) equationBox.getParent().getParent();
				
				bottomPanelVBox.getChildren().remove(equationBox.getParent());
				

			}

		});
		// -----------------------------------------------------------------------

		equationBox.getChildren().addAll(circle, yLabel, tf, removeButton);

		finalEquationBox.getChildren().add(equationBox);

		return finalEquationBox;

	}

	/**
	 * Shows that user has inputted an illegal expression by adding a Label object beneath the equation box with the error.
	 * @param error The error to display.
	 * @param tf The given textfield.
	 */
	
	private void displayErrorMessage(String error, TextField tf) {

		Label errorLabel = null;

		// if error not assigned to line yet

		if (errorMap.get(tf) == null) {

			errorLabel = new Label(error);

			errorMap.put(tf, errorLabel);

			errorLabel.setStyle("-fx-text-fill: #FF0000;");

			errorLabel.setFont(new Font(15)); 

			errorLabel.setTranslateX(100);

			errorLabel.setTranslateY(-20);

			// Position the error message below the equation box

			VBox parentVBox = (VBox) tf.getParent().getParent(); 

			parentVBox.getChildren().add(errorLabel);

		}

	}

	/**
	 * Assigns a random color to the equation.
	 * @param seriesArr An array of series objects that represent one plotted equation.
	 * @param tf The textfield assigned to that equation.
	 * @return seriesColor The random color that has been chosen and applied to the line.
	 */
	
	private Color assignColor(ArrayList<XYChart.Series<Number, Number>> seriesArr, TextField tf) {

		Color seriesColor = null;

		// if color not assigned to line yet

		if (colorMap.get(tf) == null) {

			seriesColor = getRandomLineColor(new Random());

			colorMap.put(tf, seriesColor);

		}

		else {

			seriesColor = colorMap.get(tf);

		}

		// make color the same for every series in the equation

		for (XYChart.Series<Number, Number> s : seriesArr) {

			Node currLine = s.getNode().lookup(".chart-series-line");

			String rgb = String.format("%d, %d, %d",

					(int) (seriesColor.getRed() * 255),

					(int) (seriesColor.getGreen() * 255),

					(int) (seriesColor.getBlue() * 255));

			currLine.setStyle("-fx-stroke: rgba(" + rgb + ", 1.0);");

		}

		return seriesColor;

	}

	/**
	 * Gets a random color from a list of colors and does so until all colors from the list have been chosen at which point
	 * all colors can be picked at random once again.
	 * @param rand A new Random object.
	 * @return randColor The random color picked.
	 */
	
	private Color getRandomLineColor(Random rand) {

		List<Color> colorList = Arrays.asList(Color.AQUA, Color.CHOCOLATE, Color.MEDIUMPURPLE, Color.DEEPPINK,

				Color.DARKCYAN, Color.INDIGO, Color.DARKMAGENTA, Color.MEDIUMBLUE, Color.LIGHTPINK, Color.DARKGREEN,
				
				Color.LIGHTSALMON, Color.TOMATO, Color.RED, Color.LIGHTSEAGREEN, Color.FIREBRICK, Color.PLUM);

		int randIndex = rand.nextInt(colorList.size());

		while (true) {

			Color randColor = colorList.get(randIndex);

			if (!alreadyChosen.contains(randColor)) {

				alreadyChosen.add(randColor);

				return randColor;

			}

			else {

				randIndex = rand.nextInt(colorList.size());

			}
			
			// all colors have been chosen
			if (alreadyChosen.size() == colorList.size()) {

				alreadyChosen.clear();

			}

		}

	}
}