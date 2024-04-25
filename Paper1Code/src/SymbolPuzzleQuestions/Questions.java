/*
    Skeleton Program for the AQA A Level Paper 1 Summer 2024 examination
    this code should be used in conjunction with the Preliminary Material
    written by the AQA Programmer Team
    developed in NetBeans IDE 12.6 environment
 */

package SymbolPuzzleQuestions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;
import console.Console;

public class Questions {
	public static void main(String[] args) {
		String again = "y";
		int score;
		while (again.equals("y")) {
			Console.write("Press Enter to start a standard puzzle or enter name of file to load: ");
			String filename = Console.readLine();
			Puzzle myPuzzle;
			if (filename.length() > 0) {
				myPuzzle = new Puzzle(filename + ".txt");
			} else {
				myPuzzle = new Puzzle(8, (int)(8 * 8 * 0.6));
			}
			score = myPuzzle.attemptPuzzle();
			Console.writeLine("Puzzle finished. Your score was: " + score);
			Console.write("Do another puzzle? ");
			again = Console.readLine().toLowerCase();
		}
		Console.readLine();
	}
}

class Puzzle {
	private int score;
	private int symbolsLeft;
	private int gridSize;
	private List<Cell> grid;
	private List<Pattern> allowedPatterns;
	private List<String> allowedSymbols;
	private static Random rng = new Random();

	public Puzzle(String filename){
		grid = new ArrayList<>();
		allowedPatterns = new ArrayList<>();
		allowedSymbols = new ArrayList<>();
		loadPuzzle(filename);
	}

	public Puzzle(int size, int startSymbols) {
		score = 0;
		symbolsLeft = startSymbols;
		gridSize = size;
		grid = new ArrayList<>();
		for (int count = 1; count < gridSize * gridSize + 1; count++) {
			Cell c;
			if (getRandomInt(1, 101) < 90) {
				c = new Cell();
			} else {
				c = new BlockedCell();
			}
			grid.add(c);
		}
		allowedPatterns = new ArrayList<>();
		allowedSymbols = new ArrayList<>();
		Pattern qPattern = new Pattern("Q", "QQ**Q**QQ");
		allowedPatterns.add(qPattern);
		allowedSymbols.add("Q");
		Pattern xPattern = new Pattern("X", "X*X*X*X*X");
		allowedPatterns.add(xPattern);
		allowedSymbols.add("X");
		Pattern tPattern = new Pattern("T", "TTT**T**T");
		allowedPatterns.add(tPattern);
		allowedSymbols.add("T");

		Pattern cPattern = new Pattern("C" , "CCC*CCCC");
		allowedPatterns.add(cPattern);
		allowedSymbols.add("C");

	}

	private void loadPuzzle(String filename) {
		try {
			File myStream = new File(filename);
			Scanner scan = new Scanner(myStream);
			int noOfSymbols = Integer.parseInt(scan.nextLine());
			for (int count = 0; count < noOfSymbols; count++) {
				allowedSymbols.add(scan.nextLine());
			}
			int noOfPatterns = Integer.parseInt(scan.nextLine());
			for (int count = 0; count < noOfPatterns; count++) {
				String[] items = scan.nextLine().split(",", 2);
				Pattern p = new Pattern(items[0], items[1]);
				allowedPatterns.add(p);
			}
			gridSize = Integer.parseInt(scan.nextLine());
			for (int count = 1; count <= gridSize * gridSize; count++) {
				Cell c;
				String[] items = scan.nextLine().split(",", 2);
				if (items[0].equals("@")) {
					c = new BlockedCell();
				} else {
					c = new Cell();
					c.changeSymbolInCell(items[0]);
					for (int currentSymbol = 1; currentSymbol < items.length; currentSymbol++) {
						c.addToNotAllowedSymbols(items[currentSymbol]);
					}
				}
				grid.add(c);
			}
			score = Integer.parseInt(scan.nextLine());
			symbolsLeft = Integer.parseInt(scan.nextLine());
		} catch (Exception e) {
			Console.writeLine("Puzzle not loaded");
		}
	}

	public int attemptPuzzle() {
		boolean finished = false;
		while (!finished) {
			displayPuzzle();
			Console.writeLine("Current score: " + score);
			int row = -1;
			boolean valid = false;
			while (!valid) {
				Console.write("Enter row number: ");
				try {
					row = Integer.parseInt(Console.readLine());
					valid = true;
				} catch (Exception e) {
				}
			}
			int column = -1;
			valid = false;
			while (!valid) {
				Console.write("Enter column number: ");
				try {
					column = Integer.parseInt(Console.readLine());
					valid = true;
				} catch (Exception e) {
				}
			}
			String symbol = getSymbolFromUser();
			symbolsLeft -= 1;
			Cell currentCell = getCell(row, column);
			if (currentCell.checkSymbolAllowed(symbol)) {
				currentCell.changeSymbolInCell(symbol);
				int amountToAddToScore = checkForMatchWithPattern(row, column);
				if (amountToAddToScore > 0) {
					score += amountToAddToScore;
				}
			}
			if (symbolsLeft == 0) {
				finished = true;
			}
		}
		Console.writeLine();
		displayPuzzle();
		Console.writeLine();
		return score;
	}

	private Cell getCell(int row, int column) {
		return grid.get((gridSize - row) * gridSize + column - 1);
	}

	public int checkForMatchWithPattern(int row, int column) {
		for (int startRow = row + 2; startRow >= row; startRow--) {
			for (int startColumn = column - 2; startColumn <= column; startColumn++) {
				try {
					String patternString = "";
					patternString += getCell(startRow, startColumn).getSymbol();
					patternString += getCell(startRow, startColumn + 1).getSymbol();
					patternString += getCell(startRow, startColumn + 2).getSymbol();
					patternString += getCell(startRow - 1, startColumn + 2).getSymbol();
					patternString += getCell(startRow - 2, startColumn + 2).getSymbol();
					patternString += getCell(startRow - 2, startColumn + 1).getSymbol();
					patternString += getCell(startRow - 2, startColumn).getSymbol();
					patternString += getCell(startRow - 1, startColumn).getSymbol();
					patternString += getCell(startRow - 1, startColumn + 1).getSymbol();
					for (Pattern p : allowedPatterns) {
						String currentSymbol = getCell(row, column).getSymbol();
						if (p.matchesPattern(patternString, currentSymbol)) {
							getCell(startRow, startColumn).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow, startColumn + 1).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow, startColumn + 2).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 1, startColumn + 2).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 2, startColumn + 2).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 2, startColumn + 1).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 2, startColumn).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 1, startColumn).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 1, startColumn + 1).addToNotAllowedSymbols(currentSymbol);
							return 10;
						}
					}
				} catch (Exception e) {
				}
			}
		}
		return 0;
	}

	private String getSymbolFromUser() {
		String symbol = "";
		while (!allowedSymbols.contains(symbol)) {
			Console.write("Enter symbol: ");
			symbol = Console.readLine();
		}
		return symbol;
	}

	private String createHorizontalLine() {
		String line = "  ";
		for (int count = 0; count <= (gridSize * 2); count++) {
			line += '-';
		}
		return line;
	}

	public void displayPuzzle() {
		Console.writeLine();
		if (gridSize < 10) {
			Console.write("  ");
			for(int count = 1; count <= gridSize; count++) {
				Console.write(" " + count);
			}
		}
		Console.writeLine();
		Console.writeLine(createHorizontalLine());
		for (int count = 0; count < grid.size(); count++) {
			if (count % gridSize == 0 && gridSize < 10) {
				Console.write((gridSize - ((count + 1) / gridSize)) + " ");
			}
			Console.write("|" + grid.get(count).getSymbol());
			if ((count + 1) % gridSize == 0) {
				Console.writeLine("|");
				Console.writeLine(createHorizontalLine());
			}
		}
	}

	private int getRandomInt(int min, int max) {
		return min + rng.nextInt(max - min);
	}
}

class Pattern {
	private String symbol;
	private String patternSequence;

	public Pattern(String symbolToUse, String patternString) {
		symbol = symbolToUse;
		patternSequence = patternString;
	}

	public boolean matchesPattern(String patternString, String symbolPlaced) {
		if (!symbolPlaced.equals(symbol)) {
			return false;
		} else {
			for (int count = 0; count < patternSequence.length(); count++) {
				if (patternSequence.charAt(count) == symbol.charAt(0) && patternString.charAt(count) != symbol.charAt(0)) {
					return false;
				}
			}
		}
		return true;
	}

	public String getPatternSequence() {
		return patternSequence;
	}
}

class Cell {
	protected String symbol;
	protected List<String> symbolsNotAllowed;

	public Cell() {
		symbol = "";
		symbolsNotAllowed = new ArrayList<>();
	}

	public String getSymbol() {
		if (isEmpty()) {
			return "-";
		} else {
			return symbol;
		}
	}

	public boolean isEmpty() {
		if (symbol.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public void changeSymbolInCell(String newSymbol) {
		symbol = newSymbol;
	}

	public boolean checkSymbolAllowed(String symbolToCheck) {
		for (String item : symbolsNotAllowed) {
			if (item.equals(symbolToCheck)) {
				return false;
			}
		}
		return true;
	}

	public void addToNotAllowedSymbols(String symbolToAdd) {
		symbolsNotAllowed.add(symbolToAdd);
	}

	public void updateCell() {
	}
}

class BlockedCell extends Cell {

	public BlockedCell() {
		super();
		symbol = "@";
	}

	@Override
	public boolean checkSymbolAllowed(String symbolToCheck) {
		return false;
	}
}