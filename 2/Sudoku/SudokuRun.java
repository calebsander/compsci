import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class SudokuRun {
	public static void main(String[] args) {
		if (args.length != 1) { //must have exactly one argument
			System.out.println("Correct usage:");
			System.out.println("\tjava Run puzzles.txt");
			System.out.println("or\tjava Run --input");
			return;
		}
		if (args[0].equals("--input")) { //if inputting puzzle manually
			System.out.println("Enter puzzle:");
			Scanner scanner = new Scanner(System.in);
			scanner.useDelimiter("\n");
			String[] rowStrings = new String[9];
			for (int i = 0; i < 9; i++) rowStrings[i] = scanner.next(); //read in each line of the puzzle
			scanner.close();
			Puzzle puzzle = new Puzzle(rowStrings), lastPuzzle; //instantiate Puzzle from input, lastPuzzle stores the state of the puzzle from the last iteration
			do { //use each solving method
				lastPuzzle = puzzle.clone();
				puzzle.killPossibilities();
				puzzle.chooseUnique();
				puzzle.findContainedGroups();
				puzzle.guess();
			} while (!puzzle.equals(lastPuzzle)); //keep going until no change was made after running all the methods
			puzzle.print();
			System.out.print("Done: ");
			System.out.println(puzzle.done());
		}
		else { //if reading from file
			try {
				String[] rowStrings;
				char[] tempCharArray; //singleton array to be used to make a string
				Puzzle puzzle, lastPuzzle;
				ArrayList<Boolean> done = new ArrayList<Boolean>();
				FileReader inputStream = new FileReader(args[0]);
				inputStream.read();
				for (int i = 0, j, k; i == 0 || inputStream.read() != ']'; i++) { //iterate over puzzles
					System.out.println(i);
					inputStream.read();
					rowStrings = new String[9];
					for (j = 0; j < 9; j++) { //iterate over rows
						rowStrings[j] = "";
						inputStream.read();
						for (k = 0; k < 9; k++) { //iterate over squares
							tempCharArray = new char[1];
							tempCharArray[0] = (char)inputStream.read();
							rowStrings[j] += new String(tempCharArray);
							inputStream.read();
						}
						inputStream.read();
					}
					puzzle = new Puzzle(rowStrings); //see above
					do { //see above
						lastPuzzle = puzzle.clone();
						puzzle.killPossibilities();
						puzzle.chooseUnique();
						puzzle.findContainedGroups();
						puzzle.guess();
					} while (!puzzle.equals(lastPuzzle));
					puzzle.print();
					done.add(new Boolean(puzzle.done()));
					System.out.println();
				}
				inputStream.close();
				for (int i = 0; i < done.size(); i++) { //print whether each puzzle was solved
					System.out.print(i);
					System.out.print(" Done: ");
					System.out.println(done.get(i));
				}
			}
			catch (IOException e) { //if problem reading file, report it
				System.out.println("Error reading file");
				System.out.println("Correct usage:");
				System.out.println("\tjava Run puzzles.txt");
				System.out.println("or\tjava Run --input");
			}
		}
	}
}
