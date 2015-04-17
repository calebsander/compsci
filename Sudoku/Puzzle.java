import java.util.ArrayList;
import java.util.HashSet;

class Puzzle {
	private Square[][] puzzle;

	Puzzle(Square[][] puzzle) { //only used for internal cloning
		this.puzzle = puzzle;
	}
	Puzzle(String[] rowStrings) { //creates a puzzle where each string represents a row and each character should either be a number or a space
		this.puzzle = new Square[9][9];
		String squareString;
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				squareString = rowStrings[i].substring(j, j + 1);
				if (squareString.equals(" ")) puzzle[i][j] = new Square(0);
				else puzzle[i][j] = new Square(Integer.valueOf(squareString).intValue());
			}
		}
	}

	private Square getByRow(int row, int pos) { //utility function for getting a certain element of a certain row
		return this.puzzle[row][pos];
	}
	private Square getByCol(int col, int pos) { //utility function for getting a certain element of a certain column
		return this.puzzle[pos][col];
	}
	private Square getByBox(int box, int pos) { //utility function for getting a certain element of a certain box
		return this.puzzle[(box / 3) * 3 + pos / 3][(box % 3) * 3 + pos % 3];
	}
	private static HashSet<HashSet<Integer>> allCombinations(ArrayList<Integer> possibilities, int length) { //possibilities is really a set, but keeping a specific order is handy to avoid double-counting
		HashSet<HashSet<Integer>> result = new HashSet<HashSet<Integer>>();
		if (length == 0) result.add(new HashSet<Integer>()); //only one way to pick 0 elements: the empty set
		else { //otherwise, for each element a, find the possible ways to pick the remaining elements from the elements that come after a, add a to each of those sets, then add that set to the list of possibilities
			Integer thisPick;
			ArrayList<Integer> nextPossibilities;
			HashSet<HashSet<Integer>> nextCombinations;
			HashSet<Integer> possibility, possibilityStart;
			for (int i = 0, j; i < possibilities.size() - length + 1; i++) { //iterate over possible additions to the set
				thisPick = possibilities.get(i);
				nextPossibilities = (ArrayList<Integer>)possibilities.clone();
				for (j = 0; j < i + 1; j++) nextPossibilities.remove(0); //remove all elements up to and including the current one
				nextCombinations = allCombinations(nextPossibilities, length - 1); //find ways to pick the rest of the members of the combination from the remaining numbers
				possibilityStart = new HashSet<Integer>(1); //stores the current possibility as a set to which to append more members
				possibilityStart.add(thisPick);
				for (HashSet<Integer> nextCombination : nextCombinations) { //iterate over possible ways to pick from the remaining numbers
					possibility = (HashSet<Integer>)possibilityStart.clone();
					possibility.addAll(nextCombination); //union the selected combination with the current number
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
		HashSet<HashSet<Integer>> combinations;
		HashSet<Integer> totalPossibilities;
		HashSet<Integer> squarePossibilities;
		for (i = 0; i < 9; i++) { //iterate over rows
			unknownSquares = new ArrayList<Integer>();
			for (j = 0; j < 9; j++) { //iterate over row
				if (this.getByRow(i, j).empty()) unknownSquares.add(Integer.valueOf(j));
			}
			for (j = 2; j < unknownSquares.size(); j++) { //iterate over lengths of possible combinations
				combinations = allCombinations(unknownSquares, j);
				for (HashSet<Integer> combination : combinations) { //iterate over each combination
					totalPossibilities = new HashSet<Integer>();
					for (Integer square : combination) { //iterate over each square in the combination
						squarePossibilities = this.getByRow(i, square.intValue()).getPossibilities();
						totalPossibilities.addAll(squarePossibilities);
					}
					if (totalPossibilities.size() == j) { //f the possibilities are contained in those squares
						for (k = 0; k < 9; k++) { //iterate over squares in the row
							if (!combination.contains(Integer.valueOf(k))) { //if not one of the squares in the combination
								for (Integer possibility : totalPossibilities) this.getByRow(i, k).removePossibility(possibility.intValue()); //remove every used possibility
							}
						}
					}
				}
			}
		}
		for (i = 0; i < 9; i++) { //iterate over columns
			unknownSquares = new ArrayList<Integer>();
			for (j = 0; j < 9; j++) { //iterate over column
				if (this.getByCol(i, j).empty()) unknownSquares.add(Integer.valueOf(j));
			}
			for (j = 2; j < unknownSquares.size(); j++) { //iterate over lengths of possible combinations
				combinations = allCombinations(unknownSquares, j);
				for (HashSet<Integer> combination : combinations) { //iterate over each combination
					totalPossibilities = new HashSet<Integer>();
					for (Integer square : combination) { //iterate over each square in the combination
						squarePossibilities = this.getByCol(i, square.intValue()).getPossibilities();
						totalPossibilities.addAll(squarePossibilities);
					}
					if (totalPossibilities.size() == j) { //f the possibilities are contained in those squares
						for (k = 0; k < 9; k++) { //iterate over squares in the column
							if (!combination.contains(Integer.valueOf(k))) { //if not one of the squares in the combination
								for (Integer possibility : totalPossibilities) this.getByCol(i, k).removePossibility(possibility.intValue()); //remove every used possibility
							}
						}
					}
				}
			}
		}
		for (i = 0; i < 9; i++) { //iterate over boxes
			unknownSquares = new ArrayList<Integer>();
			for (j = 0; j < 9; j++) { //iterate over box
				if (this.getByBox(i, j).empty()) unknownSquares.add(Integer.valueOf(j));
			}
			for (j = 2; j < unknownSquares.size(); j++) { //iterate over lengths of possible combinations
				combinations = allCombinations(unknownSquares, j);
				for (HashSet<Integer> combination : combinations) { //iterate over each combination
					totalPossibilities = new HashSet<Integer>();
					for (Integer square : combination) { //iterate over each square in the combination
						squarePossibilities = this.getByBox(i, square.intValue()).getPossibilities();
						totalPossibilities.addAll(squarePossibilities);
					}
					if (totalPossibilities.size() == j) { //f the possibilities are contained in those squares
						for (k = 0; k < 9; k++) { //iterate over squares in the box
							if (!combination.contains(Integer.valueOf(k))) { //if not one of the squares in the combination
								for (Integer possibility : totalPossibilities) this.getByBox(i, k).removePossibility(possibility.intValue()); //remove every used possibility
							}
						}
					}
				}
			}
		}
	}
	public void guess() { //if picking a possibility creates a contradiction, discard it
		HashSet<Integer> possibilities;
		Puzzle guessPuzzle, lastGuessPuzzle;
		for (int i = 0, j, k; i < 9; i++) { //iterate over squares by iterating the two address variables
			for (j = 0; j < 9; j++) {
				possibilities = this.puzzle[i][j].getPossibilities();
				for (Integer possibility : possibilities) { //iterate over possibilities for the square
					guessPuzzle = this.clone(); //make a copy of this puzzle
					guessPuzzle.puzzle[i][j].select(possibility.intValue()); //chose the possibility
					lastGuessPuzzle = guessPuzzle.clone();
					do { //solve the puzzle as much as possible
						lastGuessPuzzle = guessPuzzle.clone();
						guessPuzzle.killPossibilities();
						guessPuzzle.chooseUnique();
						guessPuzzle.findContainedGroups();
					} while (!guessPuzzle.equals(lastGuessPuzzle));
					if (guessPuzzle.error()) this.puzzle[i][j].removePossibility(possibility.intValue()); //check for puzzle being unsolvable; if so, remove this possibility from this square of the original puzzle
				}
			}
		}
	}
	private boolean error() { //if any of the squares have no possibilities, puzzle is unsolvable.
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				if (this.puzzle[i][j].getPossibilities().size() == 0) return true;
			}
		}
		return false;
	}
	public boolean equals(Puzzle other) { //two puzzles are equal if each square is equal
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				if (!this.puzzle[i][j].equals(other.puzzle[i][j])) return false;
			}
		}
		return true;
	}
	public boolean done() { //returns whether every square has been solved
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) {
				if (this.puzzle[i][j].empty()) return false;
			}
		}
		return true;
	}
	public Puzzle clone() { //clones each square individualy into a new square matrix, then wraps it in a Puzzle class
		Square[][] newPuzzle = new Square[9][9];
		for (int i = 0, j; i < 9; i++) {
			for (j = 0; j < 9; j++) newPuzzle[i][j] = this.puzzle[i][j].clone();
		}
		return new Puzzle(newPuzzle);
	}
	public void print() { //prints out the current state of the puzzle
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