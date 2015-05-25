class Card {
	private int value;
	private final static String[] CAPTIONS = {
		"Move any one pawn from Start to a square occupied by any opponent, sending that pawn back to its own Start. If there are no pawns on the player's Start, or no opponent's pawns on any squares, the turn is lost. If an enemy's pawn is swapped while it is in front of your Home, your pawn is switched exactly where your enemy's pawn is, not at your Home.",
		"Move a pawn from Start or move a pawn one space forward",
		"Move a pawn from Start or move a pawn two spaces forward. Drawing a two entitles the player to draw again at the end of his or her turn. If you cannot use two, you can still draw again.",
		"Move a pawn three spaces forward",
		"Move a pawn four spaces backwards",
		"Move a pawn five spaces forward",
		"",
		"Move one pawn seven spaces forward or split the seven spaces between two pawns (such as four spaces for one pawn and three for another). The seven cannot be split into a six and one or a five and two for the purposes of moving out of Start.",
		"Move a pawn eight spaces forward",
		"",
		"Move a pawn ten spaces forward or one space backward. If a player cannot go forward ten spaces, then one pawn must go back one space.",
		"Move eleven spaces forward or switch places with one opposing pawn. A player that cannot move eleven spaces is not forced to switch and instead can forfeit the turn.",
		"Move a pawn twelve spaces forward"
	};

	Card(int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}
	public String getTitle() {
		if (this.value == 0) return "Sorry!";
		return Integer.valueOf(this.value).toString();
	}
	public String getMessage() {
		return CAPTIONS[this.value];
	}
}