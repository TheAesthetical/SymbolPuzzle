/*
    Skeleton Program for the AQA A Level Paper 1 Summer 2024 examination
    this code should be used in conjunction with the Preliminary Material
    written by the AQA Programmer Team
    developed in NetBeans IDE 12.6 environment
 */

package SymbolPuzzle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import console.Console;

import java.util.Random;

//================================================================================================
// PuzzleSP
//================================================================================================

public class PuzzleSP
{
	//+++++++++++++++++++++++++++++++++++++++++++++++
	// Main
	//+++++++++++++++++++++++++++++++++++++++++++++++

	public static void main(String[] args)
	{
		// Sets the variable to indicate if the user wants to play the game or not
		String again = "y";
		// Variable to store the score in
		int score;

		// Weird choice of loop? As it runs at least once, checking at the end with a do while would make more sense
		// ??? Not case sensitive ???
		// Runs while the user chooses yes
		while (again.equals("y"))
		{
			// Prompting the user to either, enter for a normal puzzle or load a puzzle file
			Console.write("Press Enter to start a standard puzzle or enter name of file to load: ");
			String filename = Console.readLine();

			// New puzzle object
			Puzzle myPuzzle;

			// If the file length is greater than 0 (a valid string)
			if (filename.length() > 0)
			{
				// Make the puzzle the name of the file
				myPuzzle = new Puzzle(filename + ".txt");

			}
			// Otherwise, it makes the puzzle from scratch
			else
			{
				// Parsing in the default grid size of 8 and number of symbols
				myPuzzle = new Puzzle(8 , (int) (8 * 8 * 0.6));

			}

			// Plays the puzzle game, and returns the score at the end
			score = myPuzzle.attemptPuzzle();

			// Outputting the finished state and final score of the puzzle
			Console.writeLine("Puzzle finished. Your score was: " + score);

			// Asking the user if they want to play again
			Console.write("Do another puzzle? ");
			again = Console.readLine().toLowerCase();

		}

		// ???
		// Keeps the application running until you input something???
		Console.readLine();

	}

}

//================================================================================================
// Puzzle
//================================================================================================

class Puzzle
{
	// The game score
	private int score;
	// The number of symbols left to put their retrospective patterns onto the board
	private int symbolsLeft;
	// The size of the grid (both x and y axis)
	private int gridSize;

	// The grid contents, consisting of cells
	private List <Cell> grid;
	// The allowed patterns and symbols in the game
	private List <Pattern> allowedPatterns;
	private List <String> allowedSymbols;

	// Random object for the random number generator
	private static Random rng = new Random();

	public Puzzle(String filename)
	{
		// Initialising array lists
		grid = new ArrayList <>();
		allowedPatterns = new ArrayList <>();
		allowedSymbols = new ArrayList <>();

		// Loading the puzzle from the file name (assuming there is one)
		loadPuzzle(filename);

	}

	public Puzzle(int size , int startSymbols)
	{
		// Initialising beginning score
		score = 0;
		// Number of symbols left is the number of symbols to begin with
		symbolsLeft = startSymbols;
		// New grid size that has just been parsed
		gridSize = size;

		// Grid as array list
		grid = new ArrayList <>();

		// From a count of 1 to the grid size (2D) + 1.
		for (int count = 1 ; count < gridSize * gridSize + 1 ; count++)
		{

			// Cell template object
			Cell c;

			// Generates a number between 1 and 100, if it is greater than 90
			if (getRandomInt(1 , 101) < 90)
			{
				// Initialise a new, free cell
				c = new Cell();

			}
			// If it is not
			else
			{
				// Create the cell to be a blocked cell
				c = new BlockedCell();

			}

			// Add that cell to the grid
			grid.add(c);

		}

		// Allowed pattern array list initialisation
		allowedPatterns = new ArrayList <>();
		// Allowed symbols array list initialisation
		allowedSymbols = new ArrayList <>();

		// Patterns are done in a spiral pattern as shown below
		//		-------
		//		|1|2|3|
		//		-------
		//		|8|9|4|
		//		-------
		//		|7|6|5|
		//		-------

		// Q pattern
		Pattern qPattern = new Pattern("Q" , "QQ**Q**QQ");
		// Adding the Q pattern and the representative symbol
		allowedPatterns.add(qPattern);
		allowedSymbols.add("Q");

		// X pattern
		Pattern xPattern = new Pattern("X" , "X*X*X*X*X");
		// Adding the X pattern and the representative symbol
		allowedPatterns.add(xPattern);
		allowedSymbols.add("X");

		// T pattern
		Pattern tPattern = new Pattern("T" , "TTT**T**T");
		// Adding the T pattern and the representative symbol
		allowedPatterns.add(tPattern);
		allowedSymbols.add("T");

	}

	private void loadPuzzle(String filename)
	{
		try
		{
			// New file based off of the filename provided
			File myStream = new File(filename);
			// New scanner based on the file object created
			Scanner scan = new Scanner(myStream);

			// The number of symbols is the first integer in the file
			int noOfSymbols = Integer.parseInt(scan.nextLine());

			// For the number of symbols in the file
			for (int count = 0 ; count < noOfSymbols ; count++)
			{
				// Adds a new allowed symbol which is on the next concurrent lines (for how many symbols)
				allowedSymbols.add(scan.nextLine());

			}

			// The number of patterns is the integer on the next line (following the allowed symbols)
			int noOfPatterns = Integer.parseInt(scan.nextLine());

			// For the number of patterns
			for (int count = 0 ; count < noOfPatterns ; count++)
			{
				// The items on the line separated by a ',' are split into an array, with a max of 2 per line
				String[] items = scan.nextLine().split("," , 2);
				// A new pattern is created, with the type of pattern and the actual pattern itself
				Pattern p = new Pattern(items[0] , items[1]);

				// The pattern that has just been processed is added to the list of allowed patterns
				allowedPatterns.add(p);

			}

			// The grid size is then obtained from the file (as it is after the pattern denotes)
			gridSize = Integer.parseInt(scan.nextLine());

			// For the grid size, multiplied by the grid size (the 2D grid)
			for (int count = 1 ; count <= gridSize * gridSize ; count++)
			{
				// Cell object creation
				Cell c;

				// The items on the line separated by a ',' are split into an array, with a max of 2 per line
				String[] items = scan.nextLine().split("," , 2);

				// If the first item equals a '@' then it is a blocked cell
				if (items[0].equals("@"))
				{
					// Therefore it creates the blocked cell
					c = new BlockedCell();

				}
				else
				{
					// Otherwise it is a normal cell
					c = new Cell();
					// The symbol is changed in the cell to reflect this change
					c.changeSymbolInCell(items[0]);

					// For the current symbol number, from 1 to the number of items in the line
					for (int currentSymbol = 1 ; currentSymbol < items.length ; currentSymbol++)
					{
						// Adds the symbol to the symbols that are not allowed in the grid
						c.addToNotAllowedSymbols(items[currentSymbol]);

					}

				}
				// The grid adds the cell to it
				grid.add(c);

			}

			// The score is then on the 2nd to final line
			score = Integer.parseInt(scan.nextLine());
			// The symbols left is on the final line
			symbolsLeft = Integer.parseInt(scan.nextLine());

		}
		// The error case in which there is an invalid name or its not found
		catch (Exception e)
		{
			Console.writeLine("Puzzle not loaded");

		}

	}

	public int attemptPuzzle()
	{
		// State of which the puzzle is in
		boolean finished = false;

		// While it is not finished
		while (!finished)
		{
			// Display the puzzle and let the user know their score
			displayPuzzle();
			Console.writeLine("Current score: " + score);

			// Row is -1, as the array data structure is 0 indexed
			int row = -1;
			boolean valid = false;

			// While it is not valid
			while (!valid)
			{
				// Prompts the user to enter a row number to move to
				Console.write("Enter row number: ");

				try
				{
					// Gets the user to input a row value as an integer
					row = Integer.parseInt(Console.readLine());
					// If it is an integer then it is valid
					valid = true;

				}
				// If it is not a integer then catch the exception...
				catch (Exception e)
				{

				}

			}

			// Repeats exact same process, just with the column 

			int column = -1;
			valid = false;

			while (!valid)
			{
				Console.write("Enter column number: ");

				try
				{
					column = Integer.parseInt(Console.readLine());
					valid = true;

				}
				catch (Exception e)
				{

				}

			}

			// Gets a valid symbol from the user
			String symbol = getSymbolFromUser();

			// Decrements the symbols left in the pattern by 1
			symbolsLeft -= 1;

			// Gets the valid cell inputed by the user
			Cell currentCell = getCell(row , column);

			// If the inputed symbol is allowed
			if (currentCell.checkSymbolAllowed(symbol))
			{
				// Changes the symbol in the current cell to the symbol inputed by the user
				currentCell.changeSymbolInCell(symbol);

				// Returns the score (if any) for the turn
				// It returns a score if the symbol placed matches the pattern denoted by that symbols pattern
				int amountToAddToScore = checkForMatchWithPattern(row , column);

				// If the score that was gained is bigger than 0
				if (amountToAddToScore > 0)
				{
					// Add it to the overall score
					score += amountToAddToScore;

				}

			}

			// If there are no symbols left 
			if (symbolsLeft == 0)
			{
				// The puzzle is finished
				finished = true;

			}

		}

		// Displays the end of game puzzle
		Console.writeLine();
		displayPuzzle();
		Console.writeLine();

		// Returns the end of game score
		return score;

	}

	// Transforms reference by a row and column to an array index
	private Cell getCell(int row , int column)
	{
		// (gridSize - row) is done to have the rows going in ascending order (1 to 8)
		// * gridSize is done to get the row
		// + column - 1 is done to get the column, - 1 because it is 0 indexed.
		return grid.get((gridSize - row) * gridSize + column - 1);

	}

	public int checkForMatchWithPattern(int row , int column)
	{
		// For startRow, which equals row + 2 to the row being greater than or equal to the start row
		// Does row + 2 to start from the top down, adjusted for the boards weird layout
		// Decrementing 
		for (int startRow = row + 2 ; startRow >= row ; startRow--)
		{
			// For startColumn, which equals column - 2 to the column being less than or equal to the start column
			// Incrementing 
			// Does this so it moves from left to right
			for (int startColumn = column - 2 ; startColumn <= column ; startColumn++)
			{

				try
				{
					// String to hold completed pattern in
					String patternString = "";

					// Manipulates startRow and startColumn to go in the spiral pattern of:
					//		-------
					//		|1|2|3|
					//		-------
					//		|8|9|4|
					//		-------
					//		|7|6|5|
					//		-------
					// There are 9 getCell concatenation calls because there are 9 cells
					// Then gets the symbol at that cell and adds it to the current pattern cell
					patternString += getCell(startRow , startColumn).getSymbol();
					patternString += getCell(startRow , startColumn + 1).getSymbol();
					patternString += getCell(startRow , startColumn + 2).getSymbol();
					patternString += getCell(startRow - 1 , startColumn + 2).getSymbol();
					patternString += getCell(startRow - 2 , startColumn + 2).getSymbol();
					patternString += getCell(startRow - 2 , startColumn + 1).getSymbol();
					patternString += getCell(startRow - 2 , startColumn).getSymbol();
					patternString += getCell(startRow - 1 , startColumn).getSymbol();
					patternString += getCell(startRow - 1 , startColumn + 1).getSymbol();

					// For each allowed patterns
					for (Pattern p : allowedPatterns)
					{
						// The current symbol is the symbol in the current cell
						String currentSymbol = getCell(row , column).getSymbol();

						// If the pattern matches the pattern string and symbol posed by this particular input
						if (p.matchesPattern(patternString , currentSymbol))
						{
							// Get each cell in the spiral pattern
							// Add it to the symbols that are not allowed in that cell (to not overwrite)
							getCell(startRow , startColumn).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow , startColumn + 1).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow , startColumn + 2).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 1 , startColumn + 2).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 2 , startColumn + 2).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 2 , startColumn + 1).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 2 , startColumn).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 1 , startColumn).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 1 , startColumn + 1).addToNotAllowedSymbols(currentSymbol);

							// Returns a score of 10
							return 10;

						}

					}

				}
				catch (Exception e)
				{

				}

			}

		}

		// Otherwise, return that they have gotten no points
		return 0;

	}

	private String getSymbolFromUser()
	{
		// Stores the symbol
		String symbol = "";

		// While the inputed symbol doesn't contain one of the allowed symbols
		while (!allowedSymbols.contains(symbol))
		{
			// Get the user to input a new symbol and store it in symbol
			Console.write("Enter symbol: ");
			symbol = Console.readLine();

		}

		// Returns the accepted symbol
		return symbol;

	}

	private String createHorizontalLine()
	{
		// Initialises new line 
		String line = "  ";

		// For 0 to less than or equal to 2 * the grid size 
		for (int count = 0 ; count <= (gridSize * 2) ; count++)
		{
			// Concatenates a subtraction sign onto the end of the string
			line += '-';

		}

		// Returns the string back
		return line;

	}

	public void displayPuzzle()
	{
		// Write a new line to the console
		Console.writeLine();

		// If the grid size is larger than 10
		if (gridSize < 10)
		{
			// Write spaces for the top left of the board
			Console.write("  ");

			// For 1 to less than or equal to the grid size 
			for (int count = 1 ; count <= gridSize ; count++)
			{
				//Write a space and the column number
				Console.write(" " + count);

			}

		}

		// Write a new line and a horizontal line
		Console.writeLine();
		Console.writeLine(createHorizontalLine());

		// For 0 to less than the grid size 
		for (int count = 0 ; count < grid.size() ; count++)
		{
			// If count is 0 (the start of the line for the number)
			// AND 
			// The grid size is less than 10
			if (count % gridSize == 0 && gridSize < 10)
			{
				// Write the number to the side, and spacing
				// gridSize - ((count + 1) is done to have descending numbers instead of ascending
				Console.write((gridSize - ((count + 1) / gridSize)) + " ");

			}

			// Write the cell border and the symbol in that board cell
			Console.write("|" + grid.get(count).getSymbol());

			// If the next count is going to be 0, it must be the end of the row line
			if ((count + 1) % gridSize == 0)
			{
				// Close off the end of the row
				Console.writeLine("|");
				// Write a horizontal line underneath the completed row
				Console.writeLine(createHorizontalLine());

			}

		}

	}

	private int getRandomInt(int min , int max)
	{
		// Returns a random integer between minimum and maximum
		return min + rng.nextInt(max - min);

	}

}

//================================================================================================
// Pattern
//================================================================================================

class Pattern
{
	// The symbol that represents the pattern
	private String symbol;
	// Stores a string that is representative of the pattern of symbols needed to represent it on the board
	private String patternSequence;

	public Pattern(String symbolToUse , String patternString)
	{
		// The symbol and actual pattern to use itself is initialised
		symbol = symbolToUse;
		patternSequence = patternString;

	}

	public boolean matchesPattern(String patternString , String symbolPlaced)
	{
		// If the symbol placed does not equal the objects symbol
		if (!symbolPlaced.equals(symbol))
		{
			// It is therefore false and not valid
			return false;

		}
		// Otherwise if it does
		else
		{
			// For 0 to count being less than the pattern sequence length
			for (int count = 0 ; count < patternSequence.length() ; count++)
			{
				// If the pattern sequence character at the current position equals the symbol 
				// AND 
				// The pattern string character at the current position does not equal the symbol
				if (patternSequence.charAt(count) == symbol.charAt(0) && patternString.charAt(count) != symbol.charAt(0))
				{
					// It is therefore not valid
					return false;

				}

			}

		}

		// Otherwise it is valid
		return true;

	}

	// Getter for the pattern
	public String getPatternSequence()
	{
		return patternSequence;

	}

}


//================================================================================================
// Cell
//================================================================================================

class Cell
{
	// Stores the symbol that represents the thing that is in the cell
	protected String symbol;

	// Symbols that are not allowed in that cell
	protected List <String> symbolsNotAllowed;

	public Cell()
	{
		// Initialising variables 
		symbol = "";
		symbolsNotAllowed = new ArrayList <>();

	}

	public String getSymbol()
	{
		// If it is empty (returns true)
		if (isEmpty())
		{
			return "-";

		}
		// Otherwise, it is not and must hold a symbol
		else
		{
			return symbol;

		}

	}

	public boolean isEmpty()
	{
		// If there is no symbol string
		if (symbol.length() == 0)
		{
			// It is empty
			return true;

		}
		else
		{
			// Otherwise it holds a value, and therefore full
			return false;

		}

	}

	public void changeSymbolInCell(String newSymbol)
	{
		// Assigns a new symbol to the cell object
		symbol = newSymbol;

	}

	public boolean checkSymbolAllowed(String symbolToCheck)
	{
		// For all of the items in the symbols not allowed (held in the string variable item)
		for (String item : symbolsNotAllowed)
		{
			// If the symbol that is not allowed is equal to the parsed symbolToCheck
			if (item.equals(symbolToCheck))
			{
				// Then it is not valid
				return false;

			}

		}

		// Otherwise, it can be considered as valid
		return true;

	}

	public void addToNotAllowedSymbols(String symbolToAdd)
	{
		// Adds to the symbols that are not allowed in the cell
		symbolsNotAllowed.add(symbolToAdd);

	}

	// Blank???
	public void updateCell()
	{

	}

}

//================================================================================================
// Blocked Cell
//================================================================================================

class BlockedCell extends Cell
{
	// Constructor - calls parent and immediately sets symbol
	public BlockedCell()
	{
		super();

		symbol = "@";

	}

	// Obviously as its a blocked cell, that a new overwrote symbol is not allowed
	@Override
	public boolean checkSymbolAllowed(String symbolToCheck)
	{
		return false;

	}

}