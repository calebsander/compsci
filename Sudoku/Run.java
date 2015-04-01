import java.util.Scanner;

class Run {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		scanner.useDelimiter("\n");
		String[] rowStrings = new String[9];
		for (int i = 0; i < 9; i++) rowStrings[i] = scanner.next();
		scanner.close();
		Sudoku sudoku = new Sudoku(rowStrings);
		Sudoku lastSudoku = new Sudoku(rowStrings);
		do {
			lastSudoku = sudoku.clone();
			sudoku.killPossibilities();
			sudoku.chooseUnique();
		} while (!sudoku.equals(lastSudoku));
		sudoku.print();
	}
}