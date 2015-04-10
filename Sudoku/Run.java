import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

class Run {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Correct usage:");
			System.out.println("\tjava Run puzzles.txt");
			System.out.println("or\tjava Run --input");
			return;
		}
		if (args[0].equals("--input")) {
		    System.out.println("Enter puzzle:");
		    Scanner scanner = new Scanner(System.in);
		    scanner.useDelimiter("\n");
		    String[] rowStrings = new String[9];
		    for (int i = 0; i < 9; i++) rowStrings[i] = scanner.next();
		    scanner.close();
		    Sudoku sudoku = new Sudoku(rowStrings), lastSudoku = new Sudoku(rowStrings);
			do {
				lastSudoku = sudoku.clone();
				sudoku.killPossibilities();
				sudoku.chooseUnique();
				sudoku.findContainedGroups();
				sudoku.guess();
			} while (!sudoku.equals(lastSudoku));
			sudoku.print();
			System.out.println(sudoku.done());
			return;
		}
		try {
			String[] rowStrings;
			char[] tempCharArray;
			Sudoku sudoku, lastSudoku;
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
				sudoku = new Sudoku(rowStrings);
				lastSudoku = new Sudoku(rowStrings);
				do {
					lastSudoku = sudoku.clone();
					sudoku.killPossibilities();
					sudoku.chooseUnique();
					sudoku.findContainedGroups();
					sudoku.guess();
				} while (!sudoku.equals(lastSudoku));
				sudoku.print();
				done.add(new Boolean(sudoku.done()));
				System.out.println();
			}
			inputStream.close();
			for (int i = 0; i < done.size(); i++) System.out.println(done.get(i));
		}
		catch (IOException e) {
			System.out.println("Error reading file");
			System.out.println("Correct usage:");
			System.out.println("\tjava Run puzzles.txt");
			System.out.println("or\tjava Run --input");
		}
	}
}
