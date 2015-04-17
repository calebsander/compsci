import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;

class Square {
	private HashSet<Integer> possibilities;
	private int value;

	Square(int value) { //initializes the square - 0 means empty, any other number indicates what number is in that box
		if (value == 0) {
			this.possibilities = new HashSet<Integer>(9);
			for (int i = 1; i < 10; i++) this.possibilities.add(new Integer(i));
		}
		else {
			this.possibilities = new HashSet<Integer>(1);
			this.possibilities.add(value);
		}
		this.value = value;
	}

	public void removePossibility(int possibility) { //eliminates a certain possibility, and sets the value of the square
		if (this.possibilities.remove(Integer.valueOf(possibility)) && this.possibilities.size() == 1) this.value = this.possibilities.iterator().next().intValue();
	}
	public boolean hasPossibility(int possibility) { //returns whether or not the square has a certain possibility
		return this.possibilities.contains(possibility);
	}
	public HashSet<Integer> getPossibilities() { //returns a cloned copy of the possibilities set
		return (HashSet<Integer>)this.possibilities.clone();
	}
	public void select(int value) { //sets the value to a certain value and removes all other possibilities
		this.value = value;
		for (Iterator<Integer> i = this.possibilities.iterator(); i.hasNext();) { //iterates over each possibility
			if (i.next().intValue() != value) i.remove(); //if the possibility is not the desired one, remove it
		}
	}
	public int getValue() {
		return this.value;
	}
	public boolean empty() { //returns whether the square has not yet been determined
		return this.getValue() == 0;
	}
	public boolean equals(Square other) { //two squares are equal iff they have the same set of possibilities
		return this.possibilities.equals(other.possibilities);
	}
	public Square clone() {
		Square newSquare = new Square(this.value);
		newSquare.possibilities = this.getPossibilities();
		return newSquare;
	}
	public String possibilitiesRowString(int level) { //if value has not yet been determined, returns a string the levelth set of 3 possibilities; if value has been determined, put the number in the middle of the middle level
		if (this.value == 0) {
			String returnString = "";
			ArrayList<Integer> sortedPossibilities = new ArrayList<Integer>(this.possibilities);
			java.util.Collections.sort(sortedPossibilities);
			int size = sortedPossibilities.size();
			int index;
			for (int i = 0; i < 3; i++) {
				index = level * 3 + i;
				if (size > index) returnString += sortedPossibilities.get(index).toString();
				else returnString += " ";
			}
			return returnString;
		}
		else  {
			if (level == 1) return " " + Integer.valueOf(this.value).toString() + " ";
			else return "   ";
		}
	}
}