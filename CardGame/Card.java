class Card {
	private String suit;
	private String value;

	Card(String suit, String value) {
		this.suit = suit;
		this.value = value;
	}

	public String getSuit() {
		return this.suit;
	}
	public int getValue() {
		switch (value) {
			case "2":
				return 2;
			case "3":
				return 3;
			case "4":
				return 4;
			case "5":
				return 5;
			case "6":
				return 6;
			case "7":
				return 7;
			case "8":
				return 8;
			case "9":
				return 9;
			case "10":
				return 10;
			case "J":
				return 11;
			case "Q":
				return 12;
			case "K":
				return 13;
			case "A":
				return 14;
			default:
				return 0;
		}
	}
	public boolean getRed() {
		if (this.suit.equals("hearts") || this.suit.equals("diamonds")) return true;
		return false;
	}
	public String toString() {
		return value + " of " + suit;
	}
	public boolean equals(Card card) {
		return this.suit.equals(card.suit) && this.value.equals(card.value);
	}
	private String numberSpace() {
		if (this.value.equals("10")) return "10";
		else return this.value + " ";
	}
	public String[] graphic() {
		switch (this.suit) {
			case "spades":
				String[] toReturns = {
					"+-----+",
					"|S   S|",
					"|     |",
					"|  " + this.numberSpace() + " |",
					"|     |",
					"|S   S|",
					"+-----+"
				};
				return toReturns;
			case "hearts":
				String[] toReturnh = {
					"+-----+",
					"|H   H|",
					"|     |",
					"|  " + this.numberSpace() + " |",
					"|     |",
					"|H   H|",
					"+-----+"
				};
				return toReturnh;
			case "diamonds":
				String[] toReturnd = {
					"+-----+",
					"|D   D|",
					"|     |",
					"|  " + this.numberSpace() + " |",
					"|     |",
					"|D   D|",
					"+-----+"
				};
				return toReturnd;
			case "clubs":
				String[] toReturnc = {
					"+-----+",
					"|C   C|",
					"|     |",
					"|  " + this.numberSpace() + " |",
					"|     |",
					"|C   C|",
					"+-----+"
				};
				return toReturnc;
		}
		return new String[0];
	}

	public final static String[] SUITS = {"clubs", "diamonds", "spades", "hearts"};
	public final static String[] VALUES = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
}