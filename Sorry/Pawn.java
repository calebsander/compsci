import java.util.HashSet;

import java.awt.Color;
import java.awt.Graphics2D;

class Pawn {
	private Color color;
	private int id;
	private int pos;
	private int x, y;

	Pawn(Color color, int id) {
		this.color = color;
		this.id = id;
		this.pos = 0;
		this.setPos(0);
	}

	public void move(Card card) {
		int value = card.getValue();
		int diff = 0;
		if (this.pos == 0) diff = 1;
		else if (value == 4) diff = -4;
		else diff = value;
		this.setPos(this.pos + diff);
	}
	public void move(int spaces) {
		this.setPos(this.pos + spaces);
	}
	public void bump() {
		this.setPos(0);
	}
	public HashSet<Pawn> setPos(int pos) {
		HashSet<Pawn> moves = new HashSet<Pawn>();
		Pawn tempClone;
		for (int intermediatePos = this.pos + 1; intermediatePos < pos + 1; intermediatePos++) {
			tempClone = this.clone();
			tempClone.pos = intermediatePos;
			tempClone.setPos(intermediatePos);
			moves.add(tempClone);
		}
		this.pos = pos;
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
		else {
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
		return moves;
	}
	public boolean canMove(Card card) {
		if (this.pos == 65) return false;
		if (card.getValue() == 4) return this.pos > 4;
		if (card.getValue() == 7) return this.pos != 0 && this.pos < 65;
		if (this.pos + card.getValue() > 65) return false;
		if (this.pos == 0) return card.getValue() == 1 || card.getValue() == 2;
		return true;
	}
	public boolean canTenBackwards() {
		return this.pos > 1;
	}
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(this.x - 24, this.y - 24, 48, 48);
		g.setColor(Color.BLACK);
		g.drawRect(this.x - 24, this.y - 24, 48, 48);
		g.setColor(this.color);
		g.fillRect(this.x - 16, this.y - 16, 32, 32);
	}
	public boolean clickedBy(int eX, int eY) {
		return Math.abs(this.x - eX) < 24 && Math.abs(this.y - eY) < 24;
	}
	public boolean equals(Pawn otherPawn) {
		return this.color.equals(otherPawn.color) && this.id == otherPawn.id;
	}
	public boolean sameSquare(Pawn otherPawn) {
		return this.x == otherPawn.x && this.y == otherPawn.y;
	}
	public HashSet<Pawn> checkSlide() {
		if (this.pos == 6 || this.pos == 21 || this.pos == 36 || this.pos == 51) return this.setPos(this.pos + 4);
		if (this.pos == 13 || this.pos == 28 || this.pos == 43) return this.setPos(this.pos + 3);
		if (this.pos == 58) return this.setPos(1);
		return new HashSet<Pawn>();
	}
	public boolean isHome() {
		return this.pos == 65;
	}
	protected Pawn clone() {
		Pawn newPawn = new Pawn(this.color, this.id);
		newPawn.setPos(this.pos);
		return newPawn;
	}
	public int getPos() {
		return this.pos;
	}
}