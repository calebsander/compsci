import java.io.FileReader;
import java.io.IOException;

class Run {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println("Correct usage:");
			System.out.println("\tjava Run puzzles.txt");
			return;
		}
		try {
			String[] rowStrings;
			char[] tempCharArray;
			Sudoku sudoku, lastSudoku;
			FileReader inputStream = new FileReader(args[0]);
			inputStream.read();
			for (int i = 0, j, k; i == 0 || inputStream.read() != ']'; i++) { //iterate over puzzles
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
				} while (!sudoku.equals(lastSudoku));
				sudoku.print();
				System.out.println();
			}
			inputStream.close();
		}
		catch (IOException e) {
			System.out.println("Error reading file");
		}
	}
}