import java.util.HashSet;

import java.awt.Color;
import java.awt.Graphics2D;

class Pawn {
	private Color color; //team color
	private int id; //used to position the pawns on the same team so they don't overlap in the Start and Home areas
	private int pos; //keeps track of the number of squares travelled - 0 represents Start and 65 represents Home - except 100 is used for the square between the home stretch and Start
	private int x, y; //keeps track of the x and y positions of the piece - used for rendering and checking for overlapping another piece
	private boolean selected; //keeps track of whether or not to draw a black circle representing that the pawn has been selected

	Pawn(Color color, int id) {
		this.color = color;
		this.id = id;
		this.pos = 0;
		this.setPos(0); //sets x and y values too
		this.selected = false;
	}

	public void move(Card card) { //used to move for cards that aren't ambiguous
		int diff;
		if (this.pos == 0) diff = 1;
		else if (card.getValue() == 4) diff = -4;
		else diff = card.getValue();
		if (this.pos == 100) { //if position is set to 100 (see annotation for the attribute), pretend it is either 60 or 0 depending on the direction it is moving
			if (diff < 0) this.pos = 60;
			else this.pos = 0;
		}
		if (this.pos + diff < 1) {
			if (this.pos + diff == 0) diff += 100; //if ending up on the 100 square, go to it
			else diff += 60; //otherwise, go another rotation around the board to have a positive position
		}
		this.setPos(this.pos + diff);
	}
	public void move(int spaces) { //basically identical to move(Card) except position is calculated automatically
		if (this.pos == 100) {
			if (spaces < 0) this.pos = 60;
			else this.pos = 0;
		}
		if (this.pos + spaces < 1) {
			if (this.pos + spaces == 0) spaces += 100;
			else spaces += 60;
		}
		this.setPos(this.pos + spaces);
	}
	public void bump() { //moves the pawn back to Start
		this.setPos(0);
	}
	public HashSet<Pawn> setPos(int pos) { //sets the position to a new position and recalculates the x and y attributes
		HashSet<Pawn> moves = new HashSet<Pawn>();
		Pawn tempClone;
		for (int intermediatePos = this.pos + 1; intermediatePos < pos; intermediatePos++) { //assembles a set of Pawns at all the intermediate positions passed
			tempClone = this.clone();
			tempClone.pos = intermediatePos;
			tempClone.setPos(intermediatePos);
			moves.add(tempClone);
		}
		this.pos = pos;
		//Basically, figure out what side (or Start, Home, home stretch, or 100) the Pawn is on and set the x and y attributes based on the Pawn's color
		if (this.pos == 0) {
			if (this.color.equals(Board.RED)) {
				this.x = 256;
				this.y = 128;
			}
			else if (this.color.equals(Board.BLUE)) {
				this.x = 832;
				this.y = 256;
			}
			else if (this.color.equals(Board.YELLOW)) {
				this.x = 704;
				this.y = 832;
			}
			else if (this.color.equals(Board.GREEN)) {
				this.x = 128;
				this.y = 704;
			}
			this.x += (id % 2) * 64;
			this.y += (id / 2) * 64;
		}
		else if (this.pos < 13) {
			if (this.color.equals(Board.RED)) {
				this.x = 288 + (this.pos - 1) * 64;
				this.y = 32;
			}
			else if (this.color.equals(Board.BLUE)) {
				this.x = 992;
				this.y = 288 + (this.pos - 1) * 64;
			}
			else if (this.color.equals(Board.YELLOW)) {
				this.x = 736 - (this.pos - 1) * 64;
				this.y = 992;
			}
			else if (this.color.equals(Board.GREEN)) {
				this.x = 32;
				this.y = 736 - (this.pos - 1) * 64;
			}
		}
		else if (this.pos < 28) {
			if (this.color.equals(Board.RED)) {
				this.x = 992;
				this.y = 32 + (this.pos - 12) * 64;
			}
			else if (this.color.equals(Board.BLUE)) {
				this.x = 992 - (this.pos - 12) * 64;
				this.y = 992;
			}
			else if (this.color.equals(Board.YELLOW)) {
				this.x = 32;
				this.y = 992 - (this.pos - 12) * 64;
			}
			else if (this.color.equals(Board.GREEN)) {
				this.x = 32 + (this.pos - 12) * 64;
				this.y = 32;
			}
		}
		else if (this.pos < 43) {
			if (this.color.equals(Board.RED)) {
				this.x = 992 - (this.pos - 27) * 64;
				this.y = 992;
			}
			else if (this.color.equals(Board.BLUE)) {
				this.x = 32;
				this.y = 992 - (this.pos - 27) * 64;
			}
			else if (this.color.equals(Board.YELLOW)) {
				this.x = 32 + (this.pos - 27) * 64;
				this.y = 32;
			}
			else if (this.color.equals(Board.GREEN)) {
				this.x = 992;
				this.y = 32 + (this.pos - 27) * 64;
			}
		}
		else if (this.pos < 58) {
			if (this.color.equals(Board.RED)) {
				this.x = 32;
				this.y = 992 - (this.pos - 42) * 64;
			}
			else if (this.color.equals(Board.BLUE)) {
				this.x = 32 + (this.pos - 42) * 64;
				this.y = 32;
			}
			else if (this.color.equals(Board.YELLOW)) {
				this.x = 992;
				this.y = 32 + (this.pos - 42) * 64;
			}
			else if (this.color.equals(Board.GREEN)) {
				this.x = 992 - (this.pos - 42) * 64;
				this.y = 992;
			}
		}
		else if (this.pos < 60) {
			if (this.color.equals(Board.RED)) {
				this.x = 32 + (this.pos - 57) * 64;
				this.y = 32;
			}
			else if (this.color.equals(Board.BLUE)) {
				this.x = 992;
				this.y = 32 + (this.pos - 57) * 64;
			}
			else if (this.color.equals(Board.YELLOW)) {
				this.x = 992 - (this.pos - 57) * 64;
				this.y = 992;
			}
			else if (this.color.equals(Board.GREEN)) {
				this.x = 32;
				this.y = 992 - (this.pos - 57) * 64;
			}
		}
		else if (this.pos < 65) {
			if (this.color.equals(Board.RED)) {
				this.x = 160;
				this.y = 32 + (this.pos - 59) * 64;
			}
			else if (this.color.equals(Board.BLUE)) {
				this.x = 992 - (this.pos - 59) * 64;
				this.y = 160;
			}
			else if (this.color.equals(Board.YELLOW)) {
				this.x = 864;
				this.y = 992 - (this.pos - 59) * 64;
			}
			else if (this.color.equals(Board.GREEN)) {
				this.x = 32 + (this.pos - 59) * 64;
				this.y = 864;
			}
		}
		else if (this.pos == 65) {
			if (this.color.equals(Board.RED)) {
				this.x = 128;
				this.y = 448;
			}
			else if (this.color.equals(Board.BLUE)) {
				this.x = 512;
				this.y = 128;
			}
			else if (this.color.equals(Board.YELLOW)) {
				this.x = 832;
				this.y = 512;
			}
			else if (this.color.equals(Board.GREEN)) {
				this.x = 448;
				this.y = 832;
			}
			this.x += (id % 2) * 64;
			this.y += (id / 2) * 64;
		}
		else if (this.pos == 100) {
			if (this.color.equals(Board.RED)) {
				this.x = 224;
				this.y = 32;
			}
			else if (this.color.equals(Board.BLUE)) {
				this.x = 992;
				this.y = 224;
			}
			else if (this.color.equals(Board.YELLOW)) {
				this.x = 800;
				this.y = 992;
			}
			else if (this.color.equals(Board.GREEN)) {
				this.x = 32;
				this.y = 800;
			}
		}
		return moves;
	}
	public boolean canMove(Card card) { //returns whether a move is possible for a certain card (not used for Sorry! or 11's switching function)
		if (this.pos == 65) return false;
		if (this.pos == 100) return true;
		if (this.pos == 0) return card.getValue() == 1 || card.getValue() == 2;
		if (card.getValue() == 4) return !this.isAtStart();
		if (card.getValue() == 7) return !this.isAtStart() && !this.isHome();
		if (this.pos + card.getValue() > 65) return false;
		return true;
	}
	public boolean canTenBackwards() { //whether the pawn can go back one square
		return !this.isAtStart() && !this.isHome();
	}
	public void draw(Graphics2D g) { //draws the pawn onto a Graphics object relative to its x and y attributes
		g.setColor(Color.WHITE);
		g.fillRect(this.x - 24, this.y - 24, 48, 48);
		g.setColor(Color.BLACK);
		g.drawRect(this.x - 24, this.y - 24, 48, 48);
		g.setColor(this.color);
		g.fillRect(this.x - 16, this.y - 16, 32, 32);
		if (selected) { //draw a black circle in the middle if it is selected
			g.setColor(Color.BLACK);
			g.fillOval(this.x - 8, this.y - 8, 16, 16);
		}
	}
	public boolean clickedBy(int eX, int eY) { //whether or not a pawn was clicked if the click was at the specified coordinates
		return Math.abs(this.x - eX) < 24 && Math.abs(this.y - eY) < 24;
	}
	public boolean equals(Pawn otherPawn) { //used to tell two pawns apart
		return this.color.equals(otherPawn.color) && this.id == otherPawn.id;
	}
	public boolean sameSquare(Pawn otherPawn) { //used to see if two pawns (likely of different colors) are on the same space
		return this.x == otherPawn.x && this.y == otherPawn.y;
	}
	public HashSet<Pawn> checkSlide() { //slides the pawn forward and returns a set of Pawns at the spaces it passed through
		if (this.pos == 6 || this.pos == 21 || this.pos == 36 || this.pos == 51) return this.setPos(this.pos + 4);
		if (this.pos == 13 || this.pos == 28 || this.pos == 43) return this.setPos(this.pos + 3);
		if (this.pos == 58) {
			HashSet<Pawn> passedPositions = new HashSet<Pawn>(2);
			Pawn clone = this.clone();
			clone.setPos(59);
			passedPositions.add(clone);
			clone = this.clone();
			clone.setPos(100);
			passedPositions.add(clone);
			this.setPos(1);
			return passedPositions;
		}
		return new HashSet<Pawn>();
	}
	public boolean isAtStart() {
		return this.pos == 0;
	}
	public boolean isHome() {
		return this.pos == 65;
	}
	public boolean isSwitchable() { //returns whether the pawn is out of Start and not in the home stretch
		return !this.isAtStart() && (this.pos < 60 || this.pos == 100);
	}
	protected Pawn clone() {
		Pawn newPawn = new Pawn(this.color, this.id);
		newPawn.setPos(this.pos);
		return newPawn;
	}
	public int getPos() {
		return this.pos;
	}
	public void select() {
		this.selected = true;
	}
	public void deselect() {
		this.selected = false;
	}
}