import java.util.ArrayList;

class Sudoku {
	private Square[][] puzzle;

	Sudoku(Square[][] puzzle) {
		this.puzzle = puzzle;
	}
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

	private Square getByRow(int row, int pos) {
		return this.puzzle[row][pos];
	}
	private Square getByCol(int col, int pos) {
		return this.puzzle[pos][col];
	}
	private Square getByBox(int box, int pos) {
		return this.puzzle[(box / 3) * 3 + pos / 3][(box % 3) * 3 + pos % 3];
	}
	private static ArrayList<ArrayList<Integer>> allCombinations(ArrayList<Integer> possibilities, int length) {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		if (length == 0) result.add(new ArrayList<Integer>());
		else {
			Integer thisPick;
			ArrayList<Integer> nextPossibilities;
			ArrayList<ArrayList<Integer>> nextCombinations;
			ArrayList<Integer> possibility, possibilityStart;
			for (int i = 0, j; i < possibilities.size() - length + 1; i++) { //iterate over possible additions to the set
				thisPick = possibilities.get(i);
				nextPossibilities = (ArrayList<Integer>)possibilities.clone();
				for (j = 0; j < i + 1; j++) nextPossibilities.remove(0);
				nextCombinations = allCombinations(nextPossibilities, length - 1);
				possibilityStart = new ArrayList<Integer>(1);
				possibilityStart.add(thisPick);
				for (j = 0; j < nextCombinations.size(); j++) {
					possibility = (ArrayList<Integer>)possibilityStart.clone();
					possibility.addAll(nextCombinations.get(j));
					result.add(possibility);
				}
			}
		}
		return result;
	}
	public void killPossibilities() { //eliminates possiblities if that number exists already in the row, column, or box
		int value;
		int box, pos;
		for (int i = 0, j, k; i < 9; i++) { //iterate over rows
			for (j = 0; j < 9; j++) { //iterate over columns
				value = this.puzzle[i][j].getValue();
				if (value != 0) {
					for (k = 0; k < 9; k++) { //iterate over row
						if (j != k) this.getByRow(i, k).removePossibility(value);
					}
					for (k = 0; k < 9; k++) { //iterate over column
						if (i != k) this.getByCol(j, k).removePossibility(value);
					}
					box = (i / 3) * 3 + (j / 3);
					pos = (i % 3) * 3 + (j % 3);
					for (k = 0; k < 9; k++) { //iterate over box
						if (pos != k) this.getByBox(box, k).removePossibility(value);
					}
				}
			}
		}
	}
	public void chooseUnique() { //if only one square can be a specific number in the row, column, or box, it must be that number
		int tally;
		int index = 0;
		for (int i = 1, j, k; i < 10; i++) { //iterate over numbers
			for (j = 0; j < 9; j++) { //iterate over rows
				tally = 0;
				for (k = 0; k < 9; k++) { //iterate over row
					if (this.getByRow(j, k).hasPossibility(i)) {
						tally++;
						index = k;
					}
				}
				if (tally == 1) this.getByRow(j, index).select(i);
			}
			for (j = 0; j < 9; j++) { //iterate over columns
				tally = 0;
				for (k = 0; k < 9; k++) { //iterate over column
					if (this.getByCol(j, k).hasPossibility(i)) {
						tally++;
						index = k;
					}
				}
				if (tally == 1) this.getByCol(j, index).select(i);
			}
			for (j = 0; j < 9; j++) { //iterate over boxes
				tally = 0;
				for (k = 0; k < 9; k++) { //iterate over box
					if (this.getByBox(j, k).hasPossibility(i)) {
						tally++;
						index = k;
					}
				}
				if (tally == 1) this.getByBox(j, index).select(i);
			}
		}
	}
	public void findContainedGroups() { //if the size of the union of possibilities for some number of squares in a row, column, or box is that number, then no other squares in that row, column, or box can have any of those values
		int i, j, k, l, m;
		ArrayList<Integer> unknownSquares;
		ArrayList<ArrayList<Integer>> combinations;
		ArrayList<Integer> totalPossibilities, squarePossibilities;
		for (i = 0; i < 9; i++) { //iterate over rows
			unknownSquares = new ArrayList<Integer>();
			for (j = 0; j < 9; j++) { //iterate over row
				if (this.getByRow(i, j).empty()) unknownSquares.add(new Integer(j));
			}
			for (j = 2; j < unknownSquares.size(); j++) { //iterate over lengths of possible combinations
				combinations = allCombinations(unknownSquares, j);
				for (k = 0; k < combinations.size(); k++) { //iterate over each combination
					totalPossibilities = new ArrayList<Integer>();
					for (l = 0; l < combinations.get(k).size(); l++) { //iterate over each square in the combination
						squarePossibilities = this.getByRow(i, combinations.get(k).get(l)).getPossibilities();
						for (m = 0; m < squarePossibilities.size(); m++) { //iterate over each possibility for the square
							if (!totalPossibilities.contains(squarePossibilities.get(m))) totalPossibilities.add(squarePossibilities.get(m));
						}
					}
					if (totalPossibilities.size() == j) { //f the possibilities are contained in those squares
						for (l = 0; l < 9; l++) { //iterate over squares in the row
							if (!combinations.get(k).contains(new Integer(l))) { //if not one of the squares in the combination
								for (m = 0; m < totalPossibilities.size(); m++) this.getByRow(i, l).removePossibility(totalPossibilities.get(m).intValue()); //remove every used possibility
							}
						}
					}
				}
			}
		}
		for (i = 0; i < 9; i++) { //iterate over columns
			unknownSquares = new ArrayList<Integer>();
			for (j = 0; j < 9; j++) { //iterate over column
				if (this.getByCol(i, j).empty()) unknownSquares.add(new Integer(j));
			}
			for (j = 2; j < unknownSquares.size(); j++) { //iterate over lengths of possible combinations
				combinations = allCombinations(unknownSquares, j);
				for (k = 0; k < combinations.size(); k++) { //iterate over each combination
					totalPossibilities = new ArrayList<Integer>();
					for (l = 0; l < combinations.get(k).size(); l++) { //iterate over each square in the combination
						squarePossibilities = this.getByCol(i, combinations.get(k).get(l)).getPossibilities();
						for (m = 0; m < squarePossibilities.size(); m++) { //iterate over each possibility for the square
							if (!totalPossibilities.contains(squarePossibilities.get(m))) totalPossibilities.add(squarePossibilities.get(m));
						}
					}
					if (totalPossibilities.size() == j) { //f the possibilities are contained in those squares
						for (l = 0; l < 9; l++) { //iterate over squares in the column
							if (!combinations.get(k).contains(new Integer(l))) { //if not one of the squares in the combination
								for (m = 0; m < totalPossibilities.size(); m++) this.getByCol(i, l).removePossibility(totalPossibilities.get(m).intValue()); //remove every used possibility
							}
						}
					}
				}
			}
		}
		for (i = 0; i < 9; i++) { //iterate over boxes
			unknownSquares = new ArrayList<Integer>();
			for (j = 0; j < 9; j++) { //iterate over box
				if (this.getByBox(i, j).empty()) unknownSquares.add(new Integer(j));
			}
			for (j = 2; j < unknownSquares.size(); j++) { //iterate over lengths of possible combinations
				combinations = allCombinations(unknownSquares, j);
				for (k = 0; k < combinations.size(); k++) { //iterate over each combination
					totalPossibilities = new ArrayList<Integer>();
					for (l = 0; l < combinations.get(k).size(); l++) { //iterate over each square in the combination
						squarePossibilities = this.getByBox(i, combinations.get(k).get(l)).getPossibilities();
						for (m = 0; m < squarePossibilities.size(); m++) { //iterate over each possibility for the square
							if (!totalPossibilities.contains(squarePossibilities.get(m))) totalPossibilities.add(squarePossibilities.get(m));
						}
					}
					if (totalPossibilities.size() == j) { //f the possibilities are contained in those squares
						for (l = 0; l < 9; l++) { //iterate over squares in the box
							if (!combinations.get(k).contains(new Integer(l))) { //if not one of the squares in the combination
								for (m = 0; m < totalPossibilities.size(); m++) this.getByBox(i, l).removePossibility(totalPossibilities.get(m).intValue());
							}
						}
					}
				}
			}
		}
	}
	public void guess() { //if picking a possibility creates a contradiction, discard it
		ArrayList<Integer> possibilities;
		Sudoku guessSudoku, lastGuessSudoku;
		for (int i = 0, j, k; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				possibilities = this.puzzle[i][j].getPossibilities();
				for (k = 0; k < possibilities.size(); k++) {
					guessSudoku = this.clone();
					guessSudoku.puzzle[i][j].select(possibilities.get(k).intValue());
					lastGuessSudoku = guessSudoku.clone();
					do {
						lastGuessSudoku = guessSudoku.clone();
						guessSudoku.killPossibilities();
						guessSudoku.chooseUnique();
						guessSudoku.findContainedGroups();
					} while (!guessSudoku.equals(lastGuessSudoku));
					if (guessSudoku.error()) this.puzzle[i][j].removePossibility(possibilities.get(k).intValue());
				}
			}
		}
	}
	private boolean error() {
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				if (this.puzzle[i][j].getPossibilities().size() == 0) return true;
			}
		}
		return false;
	}
	public boolean equals(Sudoku other) {
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				if (!this.puzzle[i][j].equals(other.puzzle[i][j])) return false;
			}
		}
		return true;
	}
	public boolean done() {
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				if (this.puzzle[i][j].empty()) return false;
			}
		}
		return true;
	}
	public Sudoku clone() {
		Square[][] newPuzzle = new Square[9][9];
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) newPuzzle[i][j] = this.puzzle[i][j].clone();
		}
		return new Sudoku(newPuzzle);
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