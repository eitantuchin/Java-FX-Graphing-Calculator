package application;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Solves an equation that is provided by the user by compartmentalizing different pieces of the equation into different tokens firstly.
 * Next uses a stack of operators and numbers to allow for simpler computation. Has various helper methods to solve edge cases that can
 * appear in special types of equations. Any equation that is inputted by the user that is illegal ends up being recognized as an exception.
 * Checks for infinite loops throughout.
 * 
 * @author Eitan Tuchin
 *
 */

public class EquationSolver {
	
	/**
	 * A list of individual or multiple characters within the String provided by the user in order.
	 */
	
	private ArrayList<String> tokens;

	/**
	 * Keeps track of how operators position themselves relative to each other within the expression.
	 */
	
	private Stack<String> operators = new Stack<>();

	/**
	 * Keeps track of how operands position themselves relative to each other within the expression.
	 */
	
	private Stack<Double> numbers = new Stack<>();

	/**
	 * Counts how many open parentheses are in the expression.
	 */
	
	private int countOpenParen;
	
	/**
	 * Counts how many closed parentheses are in the expression.
	 */
	
	private int countClosedParen;

	/**
	 * The maximum amount of iterations allowed within a loop. Checks for infinite loops.
	 */
	
	private static final int MAX_ITERATIONS_ALLOWED = 50;

	/**
	 * Tokenizes the given equation, identifies special sequences of tokens, and evaluates the expression. 
	 * Checks for illegal expressions.
	 * @param equation The user inputted equation.
	 * @param x The curent x-axis value.
	 * @return result The evaluated number.
	 * @throws Exception All possible thrown exceptions at any stage within the evaluation process.
	 */
	
	public double parseEquation(String equation, double x) throws Exception {
		
		try {
			
			// Tokenize the expression
			
			tokens = tokenize(equation, x);

			countParentheses();

			if (countOpenParen != countClosedParen) {
				
				throw new Exception();
			}

			// find decimals and make sure they will operate correctly in this system
			
			identifyDecimals();

			// find fractions and make sure they will operate correctly in this system
			
			identifyFractions();

			// find negatives and make sure they will operate correctly in this system
			
			identifyNegatives();

			// Use a simple stack-based approach to evaluate the expression
			
			double result = evaluateExpression();
			
			return result;
		} 
		catch (Exception e) {
			
			// if any exception occurs we throw an exception to show that the user inputted an illegal expression
			
			e.printStackTrace();
			
			throw new Exception();

		}
	}

	/**
	 * Finds all sequences of tokens within the array that comprise a decimal number and updates the array accordingly.
	 * @throws Exception The index is out of bounds.
	 */
	
	private void identifyDecimals() throws Exception {
		
		for (int i = 0; i <= tokens.size() - 1; ++i) {
			
			try {
				
				// check for all possible combinations of decimals occuring within the expression
				
				if (tokens.get(i).equals(".") && isNumeric(tokens.get(i + 1))) {
					
					if (tokens.get(i).equals(".") && i == 0) {
						
						replace(i);
					} 
					
					else if (tokens.get(i).equals(".") && i != 0) {
						
						if (isNumeric(tokens.get(i - 1))) {
							
							// replace tokens with decimal number
							
							String replacement = tokens.get(i - 1) + "." + tokens.get(i + 1);
							
							tokens.remove(i - 1);
							
							tokens.remove(i - 1);
							
							tokens.remove(i - 1);
							
							tokens.add(i - 1, replacement);
						} 
						
						else {
							
							replace(i);
						}
					}
				} 
				
				// a decimal point must be followed by a integer
				
				else if (tokens.get(i).equals(".") && (!isNumeric(tokens.get(i + 1)))) {
					
					throw new Exception();
				}
			} 
			
			catch (IndexOutOfBoundsException e) {
				
				throw new Exception();
			}

		}
	}

	/**
	 * Finds all sequences of tokens within the array that comprise a fractional number and updates the array accordingly.
	 */
	
	private void identifyFractions() {
		
		for (int k = 1; k < tokens.size() - 1; ++k) {
			
			try {
				
				// checks if the tokens around index k form a fraction and takes into account precedence of other operators
				
				if (tokens.get(k).equals("/") && isNumeric(tokens.get(k - 1)) && isNumeric(tokens.get(k + 1))
						&& !hasPrecedence(tokens.get(k + 2), tokens.get(k))) {
					
					Double numerator = Double.parseDouble(tokens.get(k - 1));
					
					Double denominator = Double.parseDouble(tokens.get(k + 1));
					
					Double replacement = numerator / denominator;
					
					// replace tokens with fractional number
					
					tokens.remove(k - 1);
					
					tokens.remove(k);
					
					tokens.remove(k - 1);
					
					tokens.add(k - 1, String.valueOf(replacement));
				}
			} 
			
			catch (IndexOutOfBoundsException e) {
			}
		}
	}

	/**
	 * Finds all sequences of tokens within the array that comprise a negative number and updates the array accordingly.
	 */
	
	private void identifyNegatives() {

		// evaluates all possibilities of there being a negative number within the expression
		
		for (int i = 0; i < tokens.size() - 1; ++i) {
			
			String curr = tokens.get(i);
			
			if (tokens.size() > 2) {
				
				if (i == 0) {
					
					if (curr.equals("-")) {
						
						tokens.add(i, "0");
					}
				} 
				else {
					
					if (curr.equals("-") && !isNumeric(tokens.get(i - 1))) {
						
						curr += "1";
						
						tokens.remove(i);
						
						tokens.add(i, curr);
					}
				}

			} 
			else {
				
				if (curr.equals("-")) {
					
					replace(i);
				}
			}
		}
	}

	/**
	 * Replaces token at given index with 2 or more combined tokens.
	 * @param index The given index in the token array.
	 */
	
	private void replace(int index) {

		String replacement = tokens.get(index) + tokens.get(index + 1);
		
		if (replacement.contains("--")) {
			
			replacement = replacement.substring(2);
		}
		// for decimals
		else if (replacement.charAt(0) == '.') {
			
			replacement = "0" + replacement;
		}

		tokens.remove(index);
		
		tokens.remove(index);

		tokens.add(index, replacement);
	}

	/**
	 * Evaluates the expression by iterating through the token array and identifying each token individually. Uses helper methods 
	 * to perform things like order of operations, applying operators to selected operators and operands within their respective stacks,
	 * and some other ones. Handles implicit multiplication and identifies illegal expressions.
	 * @return number The operand that is left at the top and bottom of the stack.
	 * @throws Exception An infinite loop has been detected.
	 */
	
	private double evaluateExpression() throws Exception {
		
		// will hold numbers that are evaluated later because of location within sets of parentheses 
		
		ArrayList<Double> toBeEvaluated = new ArrayList<>();

		for (int i = 0; i < tokens.size(); ++i) {

			String token = tokens.get(i);

			if (isNumeric(token)) {
				
				numbers.push(Double.parseDouble(token));
			} 
			
			else if (isOperator(token)) {
				
				try {
					
					// begin to evaluate expression if order of operations holds
					
					while (!operators.isEmpty() && hasPrecedence(operators.peek(), token)) {
						
						applyOperator(numbers.elementAt(numbers.size() - 2),	
								numbers.elementAt(numbers.size() - 1), operators.pop(), numbers.size() - 2);
					}
					
					operators.push(token);
					
					// check again if operations need to be done after updated operator stack
					
					if (hasPrecedence(operators.peek(), operators.elementAt(operators.size() - 2))
							&& !operators.elementAt(operators.size() - 2).equals("(")) {
						
						numbers.push(Double.parseDouble(tokens.get(i + 1)));
						
						i++;
						
						applyOperator(numbers.elementAt(numbers.size() - 2),
								numbers.elementAt(numbers.size() - 1), operators.pop(), numbers.size() - 2);
					}
				} 
				
				catch (IndexOutOfBoundsException | EmptyStackException e) {
				}
			} 
			
			else if (token.equals("(")) {
				
				operators.push(token);
			} 
			
			else if (token.equals(")")) {
				
				// reverse nums stack for easier evaluation
				
				numbers = reverseNums();
				
				int index = findPosition();

				int k = 0;
				
				// operators between two sets of parentheses
				
				int operatorsBetween = numOperatorsBetween();
				
				while (k < operatorsBetween) {
					
					k++;
					
					toBeEvaluated.add(numbers.pop());
				}

				numbers = reverseNums();
				
				// apply order of operations starting from first number after first parentheses encountered

				orderOfOperations(operators.size() - index);

				for (int m = toBeEvaluated.size() - 1; m >= 0; --m) {
					
					numbers.add(0, toBeEvaluated.get(m));
				}
				
				toBeEvaluated.removeAll(toBeEvaluated);

			}
			
			// check for implicit multiplication
			
			if (i + 1 < tokens.size() && isImplicitMultiplication(tokens.get(i), tokens.get(i + 1))) {

				operators.push("*");
			}
		}

		try {
			
			int iterationNum = 0;
			
			while (!operators.isEmpty()) {
				
				// Check for infnite loop
				
				iterationNum++;
				
				if (iterationNum > MAX_ITERATIONS_ALLOWED) {
					
					throw new Exception();
				}
				
				// evaluate last bits left
				
				orderOfOperations(0);
			}
		} 
		
		catch (IndexOutOfBoundsException e) {
		}

		return numbers.pop();
	}

	/**
	 * Performs order of operations based on a given starting position within the operator stack which can be interpreted
	 * as evaluating the expression up until the the the first open parentheses is found (in most cases). Uses a priority system to 
	 * first evaluate higher priority operators within the expression.
	 * @param startingPos The given starting position within the operator stack.
	 * @throws Exception An infinite loop has been detected.
	 */
	
	private void orderOfOperations(int startingPos) throws Exception {
		int iterationNum = 0;

		// start with highest priority possible
		
		int currentPriority = 3;
		
		while (numbers.size() > 1) {

			int i = startingPos;
			
			while (i < operators.size()) {
				
				// Check for infnite loop
				
				iterationNum++;
				
				if (iterationNum > MAX_ITERATIONS_ALLOWED) {
					
					throw new Exception();
				}

				int index = i - startingPos + 1;
				
				try {
					
					// apply the operator if priority matches 
					
					if (getPriority(operators.elementAt(i)) == currentPriority) {
						
						String currentOperator = operators.elementAt(i);
						
						applyOperator(numbers.elementAt(index - 1), numbers.elementAt(index), currentOperator, index - 1);
						
						operators.remove(i);
					} 
					
					else {
						
						i++;
					}
				} 
				
				catch (IndexOutOfBoundsException e) {
				}
			}
			
			currentPriority--;
		}
		
		// remove operator from the stack only if it is an open parentheses
		
		if (!operators.isEmpty() && operators.peek().equals("(")) {
			
			operators.pop();
		}
	}

	/**
	 * Counts the number of operators between two parentheses.
	 * @return count The number of operators.
	 */
	
	private int numOperatorsBetween() {
		
		int count = 0;
		
		for (int i = operators.size() - 1; i >= 0; --i) {
			
			if (operators.elementAt(i).equals("(")) {
				
				for (int j = i - 1; j >= 0; --j) {
					
					if (!operators.elementAt(j).equals("(")) {
						
						count++;
					} 
					
					else {
						
						break;
					}
				}
			}
		}
		
		return count;
	}

	/**
	 * Finds the position of the first open parentheses within the operator stack.
	 * @return index The index of that open parentheses.
	 */
	
	private int findPosition() {
		
		int index = 0;
		
		for (int i = operators.size() - 1; i >= 0; --i) {
			
			if (operators.elementAt(i).equals("(")) {
				
				return index;
			}
			
			index++;
		}
		
		return -1;
	}

	/**
	 * Counts how many parentheses of both types that are in the expression.
	 */
	
	private void countParentheses() {
		
		for (int i = 0; i < tokens.size(); ++i) {
			
			if (tokens.get(i).equals("(")) {
				
				countOpenParen++;
			} 
			
			else if (tokens.get(i).equals(")")) {
				
				countClosedParen++;
			}
		}
	}

	/**
	 * Reverses the numbers stack.
	 * @return retStk The reversed numbers stack.
	 */
	
	private Stack<Double> reverseNums() {
		
		Stack<Double> retStk = new Stack<>();
		
		while (!numbers.isEmpty()) {
			
			retStk.push(numbers.pop());
		
		}
		
		return retStk;
	}

	/**
	 * Checks for implicit multiplication between two tokens within the token array.
	 * @param currentToken The current token at index i within the evaluateExpression() method.
	 * @param nextToken The token at index i + 1 within the evaluateExpression() method.
	 * @return true/false If the relationship between the two tokens holds true for this case.
	 */
	
	private boolean isImplicitMultiplication(String currentToken, String nextToken) {
		
		// Check if implicit multiplication is needed between currentToken and nextToken
		
		return (isNumeric(currentToken) || currentToken.equals(")")) && (isNumeric(nextToken) || nextToken.equals("("));
	}

	/**
	 * Determines if the first given operator has higher or lower priority over the second operator.
	 * @param op1 The first operator.
	 * @param op2 The second operator.
	 * @return true/false Depending on op1's and op2's priorities.
	 */
	
	private boolean hasPrecedence(String op1, String op2) {
		
		int precedenceOp1 = getPriority(op1);
		
		int precedenceOp2 = getPriority(op2);

		return precedenceOp1 > precedenceOp2;
	}

	/**
	 * Gets the priority of the given operator using a switch.
	 * @param operator The given operator.
	 * @return priority An integer assigned to given operator.
	 */
	
	private int getPriority(String operator) {
		
		switch (operator) {
			
			case "^":
				
				return 3;
			
			case "*":
			case "/":
				
				return 2;
				
			case "+":
			case "-":
				
				return 1;
				
			default:
				
				return 0;
		}
	}

	/**
	 * Applies the operator given for the two numbers given at given index. Allows for subtraction, addition, multiplication,
	 *  division, and exponentiation operations.
	 * @param a The operand to the left of the operator.
	 * @param b The operand to the right of the operator.
	 * @param operator The given operator.
	 * @param index The index within the numbers stack.
	 */
	
	private void applyOperator(double a, double b, String operator, int index) {
		
		numbers.remove(index);
		
		numbers.remove(index);
		
		try {
			
			switch (operator) {
				
				case "+":
					
					numbers.add(index, a + b);
					
					break;
				
				case "-":
					numbers.add(index, a - b);
					
					break;
				
				case "*":
					
					numbers.add(index, a * b);
					
					break;
				
				case "/":
					
					numbers.add(index, a / b);
					
					break;
				
				case "^":
					
					numbers.add(index, Math.pow(a, b));
					
					break;
				
				default:
					
					throw new IllegalArgumentException("Invalid operator: " + operator);
			}
		} 
		catch (ArithmeticException e) {
		}
	}

	/**
	 * Evaluates whether or not a given token is numeric or not.
	 * @param str The given token.
	 * @return true/false If the given string represents a number.
	 */
	
	private boolean isNumeric(String str) {
		
		return str.matches("-?\\d+(\\.\\d+)?");
	}

	/**
	 * Evaluates whether or not a given token is an operator or not.
	 * @param str The given token.
	 * @return true/false If the given string represents an operator.
	 */
	
	
	private boolean isOperator(String str) {
		
		return str.matches("[+\\-*/^]");
	}

	/**
	 * Splits the expression into tokens. Replaces all variables with the current x-axis value. Properly identifies integers
	 * with 2 or more digits. Checks for illegal expressions.
	 * @param expression The given equation.
	 * @param x The current x-axis value.
	 * @return tokenList The array of tokens.
	 * @throws Exception An illegal character has been identified.
	 */
	
	private ArrayList<String> tokenize(String expression, double x) throws Exception {
		
		ArrayList<String> tokenList = new ArrayList<>();

		for (int i = 0; i < expression.length(); ++i) {
			
			String currToken = String.valueOf(expression.charAt(i));
			
			try {
				
				// mechanism to check if an integer has two digits at least and bind those tokens together if it is
				
				if (isNumeric(currToken) && isNumeric(String.valueOf(expression.charAt(i - 1)))) {
					
					String lastToken = tokenList.get(tokenList.size() - 1);
					
					if (isNumeric(lastToken)) {
						
						lastToken += expression.charAt(i);
						
						tokenList.remove(tokenList.size() - 1);
						
						tokenList.add(lastToken);
					} 
					
					else {
						
						tokenList.add(currToken);
					}
				} 
				
				// replace x with the it's current value
				
				else if (expression.charAt(i) == 'x') {
					
					tokenList.add(String.valueOf(x));
				}

				else if (isValidInput(expression.charAt(i))) {
					
					tokenList.add(currToken);
				} 
				
				else {
					
					// if none of the above are true then the user inputted an illegal character
					
					throw new Exception();
				}
			} 
			
			catch (IndexOutOfBoundsException e) {
				
				tokenList.add(currToken);
			}
		}
		
		return tokenList;
	}

	/**
	 * Checks if the user inputted a valid character into the expression or not.
	 * @param input The current character in the string expression.
	 * @return true/false The evaluated boolean depending on the input.
	 */
	
	private boolean isValidInput(char input) {
		
		return isNumeric(String.valueOf(input)) || input == '(' || input == ')' || input == '*' || input == '/'
				|| input == '-' || input == '+' || input == '.' || input == '^';
	}
}