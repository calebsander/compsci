class Sudoku {
	private Square[][] puzzle;

	Sudoku(String[] rowStrings) {
		this.puzzle = new Square[9][9];
		String squareString;
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				squareString = rowStrings[i].substring(j, j + 1);
				if (squareString.equals(" ")) puzzle[i][j] = new Square(0);
				else puzzle[i][j] = new Square(new Integer(squareString).intValue());
			}
		}
	}

	public void killPossibilities() { //eliminates possiblities if that number exists already in the row, column, or box
		int value;
		int baseI, baseJ, newI, newJ;
		for (int i = 0, j, k; i < 9; i++) { //iterate over rows
			for (j = 0; j < 9; j++) { //iterate over columns
				value = this.puzzle[i][j].getValue();
				if (value != 0) {
					for (k = 0; k < 9; k++) { //iterate over row
						if (i != k) this.puzzle[k][j].removePossibility(value);
					}
					for (k = 0; k < 9; k++) { //iterate over column
						if (j != k) this.puzzle[i][k].removePossibility(value);
					}
					baseI = (i / 3) * 3;
					baseJ = (j / 3) * 3;
					for (k = 0; k < 9; k++) { //iterate over box
						newI = baseI + k % 3;
						newJ = baseJ + k / 3;
						if (newI != i && newJ != j) this.puzzle[newI][newJ].removePossibility(value);
					}
				}
			}
		}
	}
	public void chooseUnique() { //if only one square can be a specific number in the row, column, or box, it must be it
		int tally, index = 0;
		int baseI, baseJ, newI, newJ;
		for (int i = 1, j, k; i < 10; i++) { //iterate over numbers
			for (j = 0; j < 9; j++) { //iterate over rows
				tally = 0;
				for (k = 0; k < 9; k++) { //iterate over row
					if (this.puzzle[j][k].hasPossibility(i) && this.puzzle[j][k].getValue() == 0) {
						tally++;
						index = k;
					}
				}
				if (tally == 1) this.puzzle[j][index].select(i);
			}
			for (j = 0; j < 9; j++) { //iterate over columns
				tally = 0;
				for (k = 0; k < 9; k++) { //iterate over column
					if (this.puzzle[k][j].hasPossibility(i) && this.puzzle[k][j].getValue() == 0) {
						tally++;
						index = k;
					}
				}
				if (tally == 1) this.puzzle[index][j].select(i);
			}
			for (j = 0; j < 9; j++) { //iterate over boxes
				tally = 0;
				baseI = (j % 3) * 3;
				baseJ = (j / 3) * 3;
				for (k = 0; k < 9; k++) { //iterate over box
					newI = baseI + k % 3;
					newJ = baseJ + k / 3;
					if (this.puzzle[newI][newJ].hasPossibility(i) && this.puzzle[newI][newJ].getValue() == 0) {
						tally++;
						index = k;
					}
				}
				if (tally == 1) this.puzzle[baseI + index % 3][baseJ + index / 3].select(i);
			}
		}
	}
	public boolean equals(Sudoku other) {
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				if (!this.puzzle[i][j].equals(other.puzzle[i][j])) return false;
			}
		}
		return true;
	}
	public Sudoku clone() {
		String[] rowStrings = new String[9];
		for (int i = 0, j; i < 9; i++) {
			rowStrings[i] = "";
			for (j = 0; j < 9; j++) rowStrings[i] += new Integer(this.puzzle[i][j].getValue()).toString();
		}
		return new Sudoku(rowStrings);
	}
	public void print() {
		for (int i = 0, j, k, l, m; i < 3; i++) { //iterates over rows of boxes
			for (j = 0; j < 3; j++) { //iterates over individual rows
				for (k = 0; k < 3; k++) { //iterates over 3 lines of possibilities
					for (l = 0; l < 3; l++) { //iterates over columns of boxes
						for (m = 0; m < 3; m++) { //iterates over individual columns
							System.out.print(this.puzzle[i * 3 + j][l * 3 + m].possibilitiesRowString(k));
							if (m != 2) System.out.print("|");
						}
						if (l != 2) System.out.print("||");
					}
					System.out.println();
				}
				if (j != 2) System.out.println("---+---+---++---+---+---++---+---+---");
			}
			if (i != 2) {
				System.out.println("---+---+---++---+---+---++---+---+---");
				System.out.println("---+---+---++---+---+---++---+---+---");
			}
		}
	}
}