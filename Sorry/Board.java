import java.util.ArrayList;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

class Board extends JPanel {
	private ArrayList<Pawn> pawns;
	private final static Color BACKGROUND = new Color(204, 225, 204);
	private final static Color RED = new Color(221, 0, 0);
	private final static Color DARK_RED = new Color(187, 0, 0);
	private final static Color GREEN = new Color(0, 221, 0);
	private final static Color DARK_GREEN = new Color(0, 187, 0);
	private final static Color BLUE = new Color(0, 0, 221);
	private final static Color DARK_BLUE = new Color(0, 0, 187);
	private final static Color YELLOW = new Color(221, 221, 0);
	private final static Color DARK_YELLOW = new Color(187, 187, 0);

	Board() {
		this.pawns = new ArrayList<Pawn>();
	}

	public void paintComponent(Graphics gA) {
		super.paintComponent(gA);
		Graphics2D g = (Graphics2D)gA;
		this.setBackground(BACKGROUND);
		int i;
		for (i = 0; i < 16; i++) {
			g.setColor(Color.WHITE);
			g.fillRect(i * 64, 0, 64, 64);
			g.fillRect(i * 64, 960, 64, 64);
			g.setColor(Color.BLACK);
			g.drawRect(i * 64 - 1, 0, 64, 64);
			g.drawRect(i * 64 - 1, 960, 64, 64);
		}
		for (i = 1; i < 15; i++) {
			g.setColor(Color.WHITE);
			g.fillRect(0, i * 64, 64, 64);
			g.fillRect(960, i * 64, 64, 64);
			g.setColor(Color.BLACK);
			g.drawRect(-1, i * 64, 64, 64);
			g.drawRect(959, i * 64, 64, 64);
		}
		this.drawRect(g, DARK_RED, 1, 0, 3, 0);
		this.drawCircle(g, RED, 4, 0);
		this.drawRect(g, DARK_RED, 9, 0, 4, 0);
		this.drawCircle(g, RED, 13, 0);
		this.drawRect(g, DARK_BLUE, 15, 1, 0, 3);
		this.drawCircle(g, BLUE, 15, 4);
		this.drawRect(g, DARK_BLUE, 15, 9, 0, 4);
		this.drawCircle(g, BLUE, 15, 13);
		this.drawRect(g, DARK_YELLOW, 2, 15, 4, 0);
		this.drawCircle(g, YELLOW, 2, 15);
		this.drawRect(g, DARK_YELLOW, 11, 15, 3, 0);
		this.drawCircle(g, YELLOW, 11, 15);
		this.drawRect(g, DARK_GREEN, 0, 2, 0, 4);
		this.drawCircle(g, GREEN, 0, 2);
		this.drawRect(g, DARK_GREEN, 0, 11, 0, 3);
		this.drawCircle(g, GREEN, 0, 11);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{256, 512, 768, 512}, new int[]{512, 256, 512, 768}, 4);
		this.drawCardBase(g, 368, 288);
		this.drawCardBase(g, 368, 544);
	}
	private void drawRect(Graphics2D g, Color color, int x, int y, int width, int height) {
		g.setColor(color);
		g.fillRect(x * 64 + 16, y * 64 + 16, width * 64 + 32, height * 64 + 32);
		g.setColor(Color.BLACK);
		g.drawRect(x * 64 + 16, y * 64 + 16, width * 64 + 32, height * 64 + 32);

	}
	private void drawCircle(Graphics2D g, Color color, int x, int y) {
		g.setColor(color);
		g.fillOval(x * 64 + 8, y * 64 + 8, 48, 48);
		g.setColor(Color.BLACK);
		g.drawOval(x * 64 + 8, y * 64 + 8, 48, 48);
	}
	private void drawCardBase(Graphics2D g, int x, int y) {
		g.setColor(Color.BLACK);
		g.drawRect(x, y, 288, 192);
		g.setColor(BACKGROUND);
		g.fillRect(x + 1, y + 1, 287, 191);
	}
}