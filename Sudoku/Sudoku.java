import java.util.ArrayList;

class Sudoku {
	private Square[][] puzzle;

	private Sudoku(Square[][] puzzle) {
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

	private static int[] boxCoords(int box, int pos) {
		int[] returnCoords = new int[2];
		returnCoords[0] = (box % 3) * 3 + pos % 3;
		returnCoords[1] = (box / 3) * 3 + pos / 3;
		return returnCoords;
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
	public void chooseUnique() { //if only one square can be a specific number in the row, column, or box, it must be that number
		int tally;
		int index = 0;
		int[] boxCoords;
		for (int i = 1, j, k; i < 10; i++) { //iterate over numbers
			for (j = 0; j < 9; j++) { //iterate over rows
				tally = 0;
				for (k = 0; k < 9; k++) { //iterate over row
					if (this.puzzle[j][k].hasPossibility(i)) {
						tally++;
						index = k;
					}
				}
				if (tally == 1) this.puzzle[j][index].select(i);
			}
			for (j = 0; j < 9; j++) { //iterate over columns
				tally = 0;
				for (k = 0; k < 9; k++) { //iterate over column
					if (this.puzzle[k][j].hasPossibility(i)) {
						tally++;
						index = k;
					}
				}
				if (tally == 1) this.puzzle[index][j].select(i);
			}
			for (j = 0; j < 9; j++) { //iterate over boxes
				tally = 0;
				for (k = 0; k < 9; k++) { //iterate over box
					boxCoords = boxCoords(j, k);
					if (this.puzzle[boxCoords[0]][boxCoords[1]].hasPossibility(i)) {
						tally++;
						index = k;
					}
				}
				if (tally == 1) {
					boxCoords = boxCoords(j, index);
					this.puzzle[boxCoords[0]][boxCoords[1]].select(i);
				}
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
				if (this.puzzle[i][j].empty()) unknownSquares.add(new Integer(j));
			}
			for (j = 2; j < unknownSquares.size(); j++) { //iterate over lengths of possible combinations
				combinations = allCombinations(unknownSquares, j);
				for (k = 0; k < combinations.size(); k++) { //iterate over each combination
					totalPossibilities = new ArrayList<Integer>();
					for (l = 0; l < combinations.get(k).size(); l++) { //iterate over each square in the combination
						squarePossibilities = this.puzzle[i][combinations.get(k).get(l)].getPossibilities();
						for (m = 0; m < squarePossibilities.size(); m++) { //iterate over each possibility for the square
							if (!totalPossibilities.contains(squarePossibilities.get(m))) totalPossibilities.add(squarePossibilities.get(m));
						}
					}
					if (totalPossibilities.size() == j) { //f the possibilities are contained in those squares
						for (l = 0; l < 9; l++) { //iterate over squares in the row
							if (!combinations.get(k).contains(new Integer(l))) { //if not one of the squares in the combination
								for (m = 0; m < totalPossibilities.size(); m++) this.puzzle[i][l].removePossibility(totalPossibilities.get(m).intValue()); //remove every used possibility
							}
						}
					}
				}
			}
		}
		for (i = 0; i < 9; i++) { //iterate over columns
			unknownSquares = new ArrayList<Integer>();
			for (j = 0; j < 9; j++) { //iterate over column
				if (this.puzzle[j][i].empty()) unknownSquares.add(new Integer(j));
			}
			for (j = 2; j < unknownSquares.size(); j++) { //iterate over lengths of possible combinations
				combinations = allCombinations(unknownSquares, j);
				for (k = 0; k < combinations.size(); k++) { //iterate over each combination
					totalPossibilities = new ArrayList<Integer>();
					for (l = 0; l < combinations.get(k).size(); l++) { //iterate over each square in the combination
						squarePossibilities = this.puzzle[combinations.get(k).get(l)][i].getPossibilities();
						for (m = 0; m < squarePossibilities.size(); m++) { //iterate over each possibility for the square
							if (!totalPossibilities.contains(squarePossibilities.get(m))) totalPossibilities.add(squarePossibilities.get(m));
						}
					}
					if (totalPossibilities.size() == j) { //f the possibilities are contained in those squares
						for (l = 0; l < 9; l++) { //iterate over squares in the column
							if (!combinations.get(k).contains(new Integer(l))) { //if not one of the squares in the combination
								for (m = 0; m < totalPossibilities.size(); m++) this.puzzle[l][i].removePossibility(totalPossibilities.get(m).intValue()); //remove every used possibility
							}
						}
					}
				}
			}
		}
		int[] boxCoords;
		for (i = 0; i < 9; i++) { //iterate over boxes
			unknownSquares = new ArrayList<Integer>();
			for (j = 0; j < 9; j++) { //iterate over box
				boxCoords = boxCoords(i, j);
				if (this.puzzle[boxCoords[0]][boxCoords[1]].empty()) unknownSquares.add(new Integer(j));
			}
			for (j = 2; j < unknownSquares.size(); j++) { //iterate over lengths of possible combinations
				combinations = allCombinations(unknownSquares, j);
				for (k = 0; k < combinations.size(); k++) { //iterate over each combination
					totalPossibilities = new ArrayList<Integer>();
					for (l = 0; l < combinations.get(k).size(); l++) { //iterate over each square in the combination
						boxCoords = boxCoords(i, combinations.get(k).get(l));
						squarePossibilities = this.puzzle[boxCoords[0]][boxCoords[1]].getPossibilities();
						for (m = 0; m < squarePossibilities.size(); m++) { //iterate over each possibility for the square
							if (!totalPossibilities.contains(squarePossibilities.get(m))) totalPossibilities.add(squarePossibilities.get(m));
						}
					}
					if (totalPossibilities.size() == j) { //f the possibilities are contained in those squares
						for (l = 0; l < 9; l++) { //iterate over squares in the box
							if (!combinations.get(k).contains(new Integer(l))) { //if not one of the squares in the combination
								for (m = 0; m < totalPossibilities.size(); m++) { //remove every used possibility
									boxCoords = boxCoords(i, l);
									this.puzzle[boxCoords[0]][boxCoords[1]].removePossibility(totalPossibilities.get(m).intValue());
								}
							}
						}
					}
				}
			}
		}
	}
	//TODO: If all possibilities for a certain number in a row or column lie in the same box, no other number in that box can have that value. Similarly, if all the possibilities for a certain number in a box lie in the same row or column, no other number in that row or column can have that value.
	public void translateExclusivity() {
		ArrayList<Integer> occurences;
		int firstOccurence;
		boolean inSameBox;
		int box;
		int[] boxCoords;
		for (int i = 1, j, k; i < 10; i++) { //iterate over numbers
			for (j = 0; j < 9; i++) { //iterate over rows
				occurences = new ArrayList<Integer>();
				for (k = 0; k < 9; k++) { //iterate over row
					if (this.puzzle[j][k].hasPossibility(i)) occurences.add(k / 3);
				}
				firstOccurence = occurences.get(0).intValue();
				inSameBox = true;
				for (k = 1; k < occurences.size(); k++) { //iterate over occurences
					if (occurences.get(k).intValue() != firstOccurence) {
						inSameBox = false;
						break;
					}
				}
				if (inSameBox) { //if all occurences are in the same box, remove all other occurences from the box
					box = (j / 3) * 3 + firstOccurence;
					for (k = 0; k < 9; k++) { //iterate over box
						boxCoords = boxCoords(box, k);
						if (boxCoords[0] != j) this.puzzle[boxCoords[0]][boxCoords[1]].removePossibility(i);
					}
				}
			}
			for (j = 0; j < 9; i++) { //iterate over columns
				occurences = new ArrayList<Integer>();
				for (k = 0; k < 9; k++) { //iterate over column
					if (this.puzzle[k][j].hasPossibility(i)) occurences.add(k / 3);
				}
				firstOccurence = occurences.get(0).intValue();
				inSameBox = true;
				for (k = 1; k < occurences.size(); k++) { //iterate over occurences
					if (occurences.get(k).intValue() != firstOccurence) {
						inSameBox = false;
						break;
					}
				}
				if (inSameBox) { //if all occurences are in the same box, remove all other occurences from the box
					box = (firstOccurence) * 3 + j / 3;
					for (k = 0; k < 9; k++) { //iterate over box
						boxCoords = boxCoords(box, k);
						if (boxCoords[1] != j) this.puzzle[boxCoords[0]][boxCoords[1]].removePossibility(i);
					}
				}
			}
		}
	}
	//TODO: If a set of squares in a row, column, or box contains the only ones that can have certain numbers (and there are as many of these numbers as squares), those squares cannot be anything else
	public boolean equals(Sudoku other) {
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				if (!this.puzzle[i][j].equals(other.puzzle[i][j])) return false;
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
	public static ArrayList<ArrayList<Integer>> allCombinations(ArrayList<Integer> possibilities, int length) {
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
}