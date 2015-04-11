import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;

class Square {
	private HashSet<Integer> possibilities;
	private int value;

	Square(int value) {
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

	public void removePossibility(int possibility) {
		if (this.possibilities.remove(Integer.valueOf(possibility)) && this.possibilities.size() == 1) this.value = this.possibilities.iterator().next().intValue();
	}
	public boolean hasPossibility(int possibility) {
		return this.possibilities.contains(possibility);
	}
	public HashSet<Integer> getPossibilities() {
		return (HashSet<Integer>)this.possibilities.clone();
	}
	public void select(int value) {
		for (Iterator<Integer> i = this.possibilities.iterator(); i.hasNext();) {
			if (i.next().intValue() != value) i.remove();
		}
		this.value = value;
	}
	public int getValue() {
		return this.value;
	}
	public boolean empty() {
		return this.getValue() == 0;
	}
	public boolean equals(Square other) {
		if (this.possibilities.size() != other.possibilities.size()) return false;
		for (Integer i : this.possibilities) {
			if (!other.possibilities.contains(i)) return false;
		}
		return true;
	}
	public Square clone() {
		Square newSquare = new Square(this.value);
		newSquare.possibilities = (HashSet<Integer>)this.possibilities.clone();
		return newSquare;
	}
	public String possibilitiesRowString(int level) {
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
}