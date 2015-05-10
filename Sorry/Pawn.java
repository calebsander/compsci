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

	public void move(int spaces) {

	}
	public void bump() {

	}
	public boolean canMove(Card card) {
		if (this.pos == 0) return card.getValue() < 3;
		return false;
	}
	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.fillRect(x - 24, y - 24, 48, 48);
		g.setColor(Color.BLACK);
		g.drawRect(x - 24, y - 24, 48, 48);
		g.setColor(this.color);
		g.fillRect(x - 16, y - 16, 32, 32);
	}
}