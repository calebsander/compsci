import java.util.ArrayList;

class Square {
	private ArrayList<Integer> possibilities;
	private int value;

	Square(int value) {
		if (value == 0) {
			this.possibilities = new ArrayList<Integer>(9);
			for (int i = 1; i < 10; i++) this.possibilities.add(new Integer(i));
		}
		else {
			this.possibilities = new ArrayList<Integer>(1);
			this.possibilities.add(value);
		}
		this.value = value;
	}

	public void removePossibility(int possibility) {
		int index = this.possibilities.indexOf(new Integer(possibility));
		if (index != -1) {
			this.possibilities.remove(index);
			if (this.possibilities.size() == 1) this.value = this.possibilities.get(0);
		}
	}
	public boolean hasPossibility(int possibility) {
		return this.possibilities.contains(possibility);
	}
	public ArrayList<Integer> getPossibilities() {
		return this.possibilities;
	}
	public void select(int value) {
		for (int i = 0; i < this.possibilities.size(); i++) {
			if (this.possibilities.get(i).intValue() != value) {
				this.possibilities.remove(i);
				i--;
			}
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
		for (int i = 0; i < this.possibilities.size(); i++) {
			if (!other.possibilities.contains(this.possibilities.get(i))) return false;
		}
		return true;
	}
	public Square clone() {
		Square newSquare = new Square(this.value);
		newSquare.possibilities = (ArrayList<Integer>)this.possibilities.clone();
		return newSquare;
	}
	public String possibilitiesRowString(int level) {
		String returnString = "";
		int size = this.possibilities.size();
		int index;
		for (int i = 0; i < 3; i++) {
			index = level * 3 + i;
			if (size > index) returnString += this.possibilities.get(index).toString();
			else returnString += " ";
		}
		return returnString;
	}
}