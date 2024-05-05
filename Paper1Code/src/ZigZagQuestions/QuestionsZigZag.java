/*
    Skeleton Program for the AQA A Level Paper 1 Summer 2024 examination
    this code should be used in conjunction with the Preliminary Material
    written by the AQA Programmer Team
    developed in NetBeans IDE 12.6 environment
 */

package ZigZagQuestions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.Random;
import console.Console;

public class QuestionsZigZag
{
	public static void main(String[] args)
	{
		String again = "y";
		int score;

		while (again.equals("y"))
		{
			Console.write("Press Enter to start a standard puzzle or enter name of file to load: ");
			String filename = Console.readLine();
			Puzzle myPuzzle;

			if (filename.length() > 0)
			{
				myPuzzle = new Puzzle(filename + ".txt");

			}
			else
			{
				myPuzzle = new Puzzle(8 , (int) (8 * 8 * 0.6));

			}
			score = myPuzzle.attemptPuzzle();
			Console.writeLine("Puzzle finished. Your score was: " + score);
			Console.write("Do another puzzle? ");
			again = Console.readLine().toLowerCase();

		}
		Console.readLine();

	}

}

class Puzzle
{
	private int score;
	private int symbolsLeft;
	private int gridSize;
	private List <Cell> grid;
	private List <Pattern> allowedPatterns;
	private List <String> allowedSymbols;
	private static Random rng = new Random();

	private boolean bMatchedOnDouble = false;

	public Puzzle(String filename)
	{
		grid = new ArrayList <>();
		allowedPatterns = new ArrayList <>();
		allowedSymbols = new ArrayList <>();
		loadPuzzle(filename);

	}

	public Puzzle(int size , int startSymbols)
	{
		score = 0;
		symbolsLeft = startSymbols;
		gridSize = size;
		grid = new ArrayList <>();

		int iDoubleCells = 0;
		int[] iRandomCoords = new int[5];

		for (int i = 0 ; i < 5 ; i++)
		{
			iRandomCoords[i] = getRandomInt(1 , (gridSize * gridSize + 1));

		}

		Arrays.sort(iRandomCoords);

		for (int count = 1 ; count < gridSize * gridSize + 1 ; count++)
		{
			Cell c;

			if (iDoubleCells < 5 && iRandomCoords[iDoubleCells] == count)
			{
				c = new DoublePointCell();
				iDoubleCells++;

			}
			else if (getRandomInt(1 , 101) < 90)
			{
				c = new Cell();

			}
			else
			{
				c = new BlockedCell();

			}
			grid.add(c);

		}

		allowedPatterns = new ArrayList <>();
		allowedSymbols = new ArrayList <>();

		Pattern qPattern = new Pattern("Q" , "QQ**Q**QQ" , getRandomInt(1 , 4));
		allowedPatterns.add(qPattern);
		allowedSymbols.add("Q");
		Pattern xPattern = new Pattern("X" , "X*X*X*X*X" , getRandomInt(1 , 4));
		allowedPatterns.add(xPattern);
		allowedSymbols.add("X");
		Pattern tPattern = new Pattern("T" , "TTT**T**T" , getRandomInt(1 , 4));
		allowedPatterns.add(tPattern);
		allowedSymbols.add("T");

		Pattern cPattern = new Pattern("C" , "CCC*CCCC" , getRandomInt(1 , 4));
		allowedPatterns.add(cPattern);
		allowedSymbols.add("C");

	}

	private void loadPuzzle(String filename)
	{

		try
		{
			File myStream = new File(filename);
			Scanner scan = new Scanner(myStream);
			int noOfSymbols = Integer.parseInt(scan.nextLine());

			for (int count = 0 ; count < noOfSymbols ; count++)
			{
				allowedSymbols.add(scan.nextLine());

			}
			int noOfPatterns = Integer.parseInt(scan.nextLine());

			for (int count = 0 ; count < noOfPatterns ; count++)
			{
				String[] items = scan.nextLine().split("," , 2);
				Pattern p = new Pattern(items[0] , items[1] , getRandomInt(1 , 4));
				allowedPatterns.add(p);

			}
			gridSize = Integer.parseInt(scan.nextLine());

			for (int count = 1 ; count <= gridSize * gridSize ; count++)
			{
				Cell c;
				String[] items = scan.nextLine().split("," , 2);

				if (items[0].equals("@"))
				{
					c = new BlockedCell();

				}
				else
				{
					c = new Cell();
					c.changeSymbolInCell(items[0]);

					for (int currentSymbol = 1 ; currentSymbol < items.length ; currentSymbol++)
					{
						c.addToNotAllowedSymbols(items[currentSymbol]);

					}

				}
				grid.add(c);

			}
			score = Integer.parseInt(scan.nextLine());
			symbolsLeft = Integer.parseInt(scan.nextLine());

		}
		catch (Exception e)
		{
			Console.writeLine("Puzzle not loaded");

		}

	}

	public int attemptPuzzle()
	{
		boolean finished = false;
		String szChoice;

		int row;
		int column;

		Stack <PreviousMove> previousMoves = new Stack <PreviousMove>();

		while (!finished)
		{
			PreviousMove previousCell = new PreviousMove();

			displayPuzzle();

			Console.writeLine();
			outputPatternCount();
			Console.writeLine();

			Console.writeLine("Current score: " + score);

			Console.writeLine("Symbols left: " + symbolsLeft);

			do
			{
				Console.write("Would you like to undo your previous move? y/n: ");
				szChoice = Console.readLine();

				if (szChoice.equalsIgnoreCase("y") && !previousMoves.isEmpty())
				{
					undoPreviousMove(previousMoves);

					Console.writeLine();
					displayPuzzle();
					Console.writeLine();

				}
				else if (szChoice.equalsIgnoreCase("y") && previousMoves.isEmpty())
				{
					Console.writeLine("There are no more previous actions to undo!");
					szChoice.equalsIgnoreCase("-");

				}

			} while (szChoice.equalsIgnoreCase("y"));

			Console.write("Would you like to remove a symbol? y/n: ");
			szChoice = Console.readLine();

			if (szChoice.equalsIgnoreCase("y"))
			{
				row = inputAxis("Enter the row number of the symbol you would like to remove: ");
				column = inputAxis("Enter the column number of the symbol you would like to remove: ");

				removeSymbol(row , column);

			}
			else
			{

				row = inputAxis("Enter row number: ");
				column = inputAxis("Enter column number: ");

				String symbol = getSymbolFromUser();
				symbolsLeft -= 1;
				Cell currentCell = getCell(row , column);

				if (currentCell.checkSymbolAllowed(symbol))
				{
					previousCell.setOldPreviousMove(currentCell.getSymbol() , currentCell.symbolsNotAllowed , row , column);

					previousMoves.push(previousCell);

					for (Pattern p : allowedPatterns)
					{

						if (symbol.charAt(0) == p.getSymbol())
						{

							if (p.getAttempts() == 0)
							{
								outputPatternWarning(p);

							}
							else
							{

								if (currentCell.isDouble())
								{
									bMatchedOnDouble = true;

								}

								currentCell.changeSymbolInCell(symbol);
								int amountToAddToScore = checkForMatchWithPattern(row , column);

								if (amountToAddToScore > 0)
								{
									score += amountToAddToScore;

									Console.write("Would you like to reshuffle all blocked cells? y/n: ");
									szChoice = Console.readLine();

									if (szChoice.equalsIgnoreCase("y"))
									{
										reShuffleBlockedCells();

									}

								}

							}

						}

					}

					previousCell.setPreviousMove(currentCell.getSymbol() , currentCell.symbolsNotAllowed , row , column);

				}

				if (symbolsLeft == 0)
				{
					finished = true;

				}

			}

			Console.write("Would you like to save your current game? y/n: ");
			szChoice = Console.readLine();

			if (szChoice.equalsIgnoreCase("y"))
			{
				Console.writeLine("Please input the filename: ");
				String szFilename = Console.readLine();

				try
				{
					savePuzzle(szFilename);

				}
				catch (IOException e)
				{
					e.printStackTrace();

				}

			}

		}

		Console.writeLine();
		Console.writeLine();
		displayPuzzle();
		outputPatternCount();
		Console.writeLine();
		Console.writeLine();
		return score;

	}

	private void reShuffleBlockedCells()
	{

		for (int i = 0 ; i < grid.size() ; i++)
		{

			if (grid.get(i).symbol.equals("@"))
			{
				int iRandIndex;

				do
				{
					iRandIndex = getRandomInt(1 , grid.size());

				} while (!grid.get(iRandIndex).getSymbol().equals("-"));

				grid.get(iRandIndex).changeSymbolInCell(grid.get(i).getSymbol());

				grid.get(i).changeSymbolInCell("");

			}

		}

	}

	private void undoPreviousMove(Stack <PreviousMove> moves)
	{
		PreviousMove toRestore = moves.pop();

		getCell(toRestore.iRow , toRestore.iCol).symbol = toRestore.symbol;
		getCell(toRestore.iRow , toRestore.iCol).symbolsNotAllowed = toRestore.symbolsNotAllowed;

		getCell(toRestore.iOldRow , toRestore.iOldCol).symbol = toRestore.szOldSymbol;
		getCell(toRestore.iOldRow , toRestore.iOldCol).symbolsNotAllowed = toRestore.oldSymbolsNotAllowed;

	}

	private void savePuzzle(String szName) throws IOException
	{

		try
		{
			File saveFile = new File(szName + ".txt");
			FileWriter Stream = new FileWriter(saveFile);

			// Symbol numbers and types
			saveFile.createNewFile();

			Stream.write(allowedSymbols.size() + "\n");

			for (int i = 0 ; i < allowedSymbols.size() ; i++)
			{
				Stream.write(allowedSymbols.get(i) + "\n");

			}

			// Pattern numbers and types
			Stream.write(allowedPatterns.size() + "\n");

			for (int i = 0 ; i < allowedPatterns.size() ; i++)
			{
				Stream.write(allowedSymbols.get(i) + "," + allowedPatterns.get(i).getPatternSequence() + "\n");

			}

			// Grid size
			Stream.write(gridSize + "\n");

			// Grid Contents
			for (int i = 0 ; i < grid.size() ; i++)
			{

				if (grid.get(i).getSymbol().equals("-"))
				{
					Stream.write(",\n");

				}
				else
				{
					Stream.write(grid.get(i).getSymbol() + "\n");

				}

			}

			Stream.write(score + "\n");
			Stream.write(Integer.toString(symbolsLeft));

			Stream.close();

		}
		catch (IOException e)
		{
			e.printStackTrace();

		}

	}

	private void removeSymbol(int iRow , int iCol)
	{
		boolean bValidRemove = false;

		for (int i = 0 ; i < allowedSymbols.size() ; i++)
		{

			if (getCell(iRow , iCol).symbol.charAt(0) == allowedSymbols.get(i).charAt(0) && !getCell(iRow , iCol).symbolsNotAllowed.contains(getCell(iRow , iCol).symbol))
			{
				bValidRemove = true;
				break;

			}

		}

		if (bValidRemove == true)
		{
			getCell(iRow , iCol).changeSymbolInCell("");
			symbolsLeft -= 1;

		}
		else
		{
			Console.writeLine("Invalid removal of symbol!");

		}

	}

	private int inputAxis(String szMessage)
	{
		int axis = -1;
		boolean valid = false;

		while (!valid)
		{
			Console.write(szMessage);

			try
			{
				axis = Integer.parseInt(Console.readLine());
				valid = true;

			}
			catch (Exception e)
			{

			}

		}

		return axis;

	}

	private Cell getCell(int row , int column)
	{
		return grid.get((gridSize - row) * gridSize + column - 1);

	}

	public int checkForMatchWithPattern(int row , int column)
	{
		boolean bOffGrid = false;

		for (int startRow = row + 2 ; startRow >= row ; startRow--)
		{

			for (int startColumn = column - 2 ; startColumn <= column ; startColumn++)
			{

				try
				{

					if (getCell(startRow , startColumn).symbolsNotAllowed.contains(getCell(startRow , startColumn).getSymbol()))
					{
						return (0);

					}

					String patternString = "";

					if (bOffGrid == true)
					{
						patternString += null;

					}
					else
					{
						patternString += getCell(startRow , startColumn).getSymbol();
						patternString += getCell(startRow , startColumn + 1).getSymbol();
						patternString += getCell(startRow , startColumn + 2).getSymbol();
						patternString += getCell(startRow - 1 , startColumn + 2).getSymbol();
						patternString += getCell(startRow - 2 , startColumn + 2).getSymbol();
						patternString += getCell(startRow - 2 , startColumn + 1).getSymbol();
						patternString += getCell(startRow - 2 , startColumn).getSymbol();
						patternString += getCell(startRow - 1 , startColumn).getSymbol();
						patternString += getCell(startRow - 1 , startColumn + 1).getSymbol();

					}

					for (Pattern p : allowedPatterns)
					{
						String currentSymbol = getCell(row , column).getSymbol();

						if (p.matchesPattern(patternString , currentSymbol))
						{

							getCell(startRow , startColumn).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow , startColumn + 1).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow , startColumn + 2).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 1 , startColumn + 2).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 2 , startColumn + 2).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 2 , startColumn + 1).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 2 , startColumn).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 1 , startColumn).addToNotAllowedSymbols(currentSymbol);
							getCell(startRow - 1 , startColumn + 1).addToNotAllowedSymbols(currentSymbol);

							p.usePattern();

							if (bMatchedOnDouble = true)
							{
								bMatchedOnDouble = false;
								return (10 * 2);

							}
							else
							{
								return 10;

							}

						}

					}

				}
				catch (Exception e)
				{

				}

				if ((startColumn + 1) == gridSize)
				{
					bOffGrid = true;

				}

			}

		}
		return 0;

	}

	private String getSymbolFromUser()
	{
		String symbol = "";

		while (!allowedSymbols.contains(symbol))
		{
			Console.write("Enter symbol: ");
			symbol = Console.readLine();

		}
		return symbol;

	}

	private void outputPatternCount()
	{

		for (int i = 0 ; i < allowedPatterns.size() ; i++)
		{
			Console.write("The attempts you have left on pattern " + allowedPatterns.get(i).getSymbol() + " is " + allowedPatterns.get(i).getAttempts());
			Console.writeLine();

		}

	}

	private void outputPatternWarning(Pattern p)
	{
		Console.writeLine();
		Console.write("You have used all allowed pattern " + p.getSymbol() + "'s up!");
		Console.writeLine();

	}

	private String createHorizontalLine()
	{
		String line = "  ";

		for (int count = 0 ; count <= (gridSize * 2) ; count++)
		{
			line += '-';

		}
		return line;

	}

	public void displayPuzzle()
	{
		Console.writeLine();

		if (gridSize < 10)
		{
			Console.write("  ");

			for (int count = 1 ; count <= gridSize ; count++)
			{
				Console.write(" " + count);

			}

		}
		Console.writeLine();
		Console.writeLine(createHorizontalLine());

		for (int count = 0 ; count < grid.size() ; count++)
		{

			if (count % gridSize == 0 && gridSize < 10)
			{
				Console.write((gridSize - ((count + 1) / gridSize)) + " ");

			}
			Console.write("|" + grid.get(count).getSymbol());

			if ((count + 1) % gridSize == 0)
			{
				Console.writeLine("|");
				Console.writeLine(createHorizontalLine());

			}

		}

	}

	private int getRandomInt(int min , int max)
	{
		return min + rng.nextInt(max - min);

	}

}

class Pattern
{
	private String symbol;
	private String patternSequence;

	private int patternCount;

	public Pattern(String symbolToUse , String patternString , int iAttempts)
	{
		symbol = symbolToUse;
		patternSequence = patternString;

		patternCount = iAttempts;

	}

	public boolean matchesPattern(String patternString , String symbolPlaced)
	{

		if (!symbolPlaced.equals(symbol))
		{
			return false;

		}
		else
		{

			for (int count = 0 ; count < patternSequence.length() ; count++)
			{

				if (patternSequence.charAt(count) == symbol.charAt(0) && patternString.charAt(count) != symbol.charAt(0))
				{
					return false;

				}

			}

		}
		return true;

	}

	public String getPatternSequence()
	{
		return patternSequence;

	}

	public char getSymbol()
	{
		return symbol.charAt(0);

	}

	public void usePattern()
	{
		patternCount--;

	}

	public int getAttempts()
	{
		return patternCount;

	}

}

class PreviousMove extends Cell
{

	protected int iRow;
	protected int iCol;

	protected int iOldRow;
	protected int iOldCol;

	protected String szOldSymbol;

	protected List <String> oldSymbolsNotAllowed;

	public PreviousMove()
	{
		super();

	}

	public void setPreviousMove(String szSymbol , List <String> szSymbolsNotAllowed , int row , int col)
	{
		symbol = szSymbol;
		symbolsNotAllowed = szSymbolsNotAllowed;

		iRow = row;
		iCol = col;

	}

	public void setOldPreviousMove(String szSymbol , List <String> szOldSymbolsNotAllowed , int row , int col)
	{
		iOldRow = row;
		iOldCol = col;

		szOldSymbol = szSymbol;
		oldSymbolsNotAllowed = szOldSymbolsNotAllowed;

	}

}

class Cell
{
	protected String symbol;
	protected List <String> symbolsNotAllowed;

	public Cell()
	{
		symbol = "";
		symbolsNotAllowed = new ArrayList <>();

	}

	public String getSymbol()
	{

		if (isEmpty())
		{
			return "-";

		}
		else
		{
			return symbol;

		}

	}

	public boolean isEmpty()
	{

		if (symbol.length() == 0)
		{
			return true;

		}
		else
		{
			return false;

		}

	}

	public void changeSymbolInCell(String newSymbol)
	{
		symbol = newSymbol;

	}

	public boolean checkSymbolAllowed(String symbolToCheck)
	{

		for (String item : symbolsNotAllowed)
		{

			if (item.equals(symbolToCheck))
			{
				return false;

			}

		}
		return true;

	}

	public void addToNotAllowedSymbols(String symbolToAdd)
	{
		symbolsNotAllowed.add(symbolToAdd);

	}

	public void updateCell()
	{

	}

	public boolean isDouble()
	{
		return false;

	}

}

class DoublePointCell extends Cell
{
	public DoublePointCell()
	{
		super();
		symbol = "D";

	}

	@Override
	public boolean isDouble()
	{
		return true;

	}

}

class BlockedCell extends Cell
{

	public BlockedCell()
	{
		super();
		symbol = "@";

	}

	@Override
	public boolean checkSymbolAllowed(String symbolToCheck)
	{
		return false;

	}

}