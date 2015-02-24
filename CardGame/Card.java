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
}