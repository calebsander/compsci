import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class Board extends JPanel {
	private HashMap<Color,HashSet<Pawn>> pawns;
	private Deck newCards;
	private Deck oldCards;
	private int playerTurn;
	private int turnState;
	private final static int DRAW = 0;
	private final static int SELECTPAWN = 1;
	private final static int SELECTEND = 2;

	public final static Color BACKGROUND = new Color(204, 225, 204);
	public final static Color RED = new Color(221, 0, 0);
	public final static Color DARK_RED = new Color(187, 0, 0);
	public final static Color GREEN = new Color(0, 221, 0);
	public final static Color DARK_GREEN = new Color(0, 187, 0);
	public final static Color BLUE = new Color(0, 0, 221);
	public final static Color DARK_BLUE = new Color(0, 0, 187);
	public final static Color YELLOW = new Color(221, 221, 0);
	public final static Color DARK_YELLOW = new Color(187, 187, 0);
	private final static Color[] COLORS = {RED, BLUE, YELLOW, GREEN};

	Board() {
		this.pawns = new HashMap<Color,HashSet<Pawn>>();
		for (Color color : COLORS) {
			pawns.put(color, new HashSet<Pawn>());
			for (int j = 0; j < 4; j++) pawns.get(color).add(new Pawn(color, j));
		}
		this.newCards = new Deck(true);
		this.newCards.shuffle();
		this.oldCards = new Deck(false);
		this.playerTurn = 0;
		this.turnState = DRAW;
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getX() > 368 && e.getX() < 656 && e.getY() > 288 && e.getY() < 480 && Board.this.turnState == DRAW) {
					if (Board.this.newCards.size() == 0) {
						Board.this.newCards = new Deck(true);
						Board.this.newCards.shuffle();
						Board.this.oldCards = new Deck(false);
					}
					Board.this.oldCards.add(Board.this.newCards.deal());
					boolean canMove = false;
					for (HashSet<Pawn> player : Board.this.pawns.values()) {
						for (Pawn pawn : player) {
							if (pawn.canMove(Board.this.oldCards.last())) {
								canMove = true;
								break;
							}
						}
					}
					if (canMove) {
						Window parentWindow = SwingUtilities.windowForComponent(Board.this);
						Frame parentFrame = null;
						if (parentWindow instanceof Frame) parentFrame = (Frame)parentWindow;
						JOptionPane.showMessageDialog(Board.this, "Select pawn to move");
						Board.this.turnState = SELECTPAWN;
					}
					else Board.this.playerTurn = (Board.this.playerTurn + 1) % 4;
				}
			}
		});
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
		g.setColor(RED);
		g.fillPolygon(new int[]{72, 96, 72}, new int[]{8, 32, 56}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{72, 96, 72}, new int[]{8, 32, 56}, 3);
		this.drawCircle(g, RED, 4, 0);
		this.drawRect(g, DARK_RED, 9, 0, 4, 0);
		g.setColor(RED);
		g.fillPolygon(new int[]{584, 608, 584}, new int[]{8, 32, 56}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{584, 608, 584}, new int[]{8, 32, 56}, 3);
		this.drawCircle(g, RED, 13, 0);
		g.setColor(RED);
		g.fillOval(192, 64, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(192, 64, 192, 192);

		this.drawRect(g, DARK_BLUE, 15, 1, 0, 3);
		g.setColor(BLUE);
		g.fillPolygon(new int[]{968, 992, 1016}, new int[]{72, 96, 72}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{968, 992, 1016}, new int[]{72, 96, 72}, 3);
		this.drawCircle(g, BLUE, 15, 4);
		this.drawRect(g, DARK_BLUE, 15, 9, 0, 4);
		g.setColor(BLUE);
		g.fillPolygon(new int[]{968, 992, 1016}, new int[]{584, 608, 584}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{968, 992, 1016}, new int[]{584, 608, 584}, 3);
		this.drawCircle(g, BLUE, 15, 13);
		g.setColor(BLUE);
		g.fillOval(768, 192, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(768, 192, 192, 192);

		this.drawRect(g, DARK_YELLOW, 2, 15, 4, 0);
		g.setColor(YELLOW);
		g.fillPolygon(new int[]{440, 416, 440}, new int[]{968, 992, 1016}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{440, 416, 440}, new int[]{968, 992, 1016}, 3);
		this.drawCircle(g, YELLOW, 2, 15);
		this.drawRect(g, DARK_YELLOW, 11, 15, 3, 0);
		g.setColor(YELLOW);
		g.fillPolygon(new int[]{952, 928, 952}, new int[]{968, 992, 1016}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{952, 928, 952}, new int[]{968, 992, 1016}, 3);
		this.drawCircle(g, YELLOW, 11, 15);
		g.setColor(YELLOW);
		g.fillOval(640, 768, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(640, 768, 192, 192);

		this.drawRect(g, DARK_GREEN, 0, 2, 0, 4);
		g.setColor(GREEN);
		g.fillPolygon(new int[]{8, 32, 56}, new int[]{440, 416, 440}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{8, 32, 56}, new int[]{440, 416, 440}, 3);
		this.drawCircle(g, GREEN, 0, 2);
		this.drawRect(g, DARK_GREEN, 0, 11, 0, 3);
		g.setColor(GREEN);
		g.fillPolygon(new int[]{8, 32, 56}, new int[]{952, 928, 952}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{8, 32, 56}, new int[]{952, 928, 952}, 3);
		this.drawCircle(g, GREEN, 0, 11);
		g.setColor(GREEN);
		g.fillOval(64, 640, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(64, 640, 192, 192);

		Font[] systemFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		for (Font font : systemFonts) {
			if (font.getFontName().equals("Comic Sans MS")) g.setFont(font.deriveFont(60.0F));
		}
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{256, 512, 768, 512}, new int[]{512, 256, 512, 768}, 4);
		this.drawCardBase(g, 368, 288);
		this.drawCardBase(g, 368, 544);
		g.setColor(Color.WHITE);
		g.fillRect(376, 296, 272, 176);
		g.setColor(GREEN);
		g.fillRect(384, 304, 256, 160);
		g.setColor(DARK_GREEN);
		g.fillRect(448, 320, 128, 128);
		this.oldCards.display(g, 368, 544);
		g.setColor(Color.BLACK);
		g.drawString("SORRY!", 400, 536);

		for (HashSet<Pawn> player : this.pawns.values()) {
			for (Pawn pawn : player) pawn.draw(g);
		}
		switch (this.playerTurn) {
			case 0:
				g.setColor(RED);
				break;
			case 1:
				g.setColor(BLUE);
				break;
			case 2:
				g.setColor(YELLOW);
				break;
			case 3:
				g.setColor(GREEN);
		}
		g.fillRect(480, 128, 64, 64);
		try {
			Thread.sleep(50);
		}
		catch (InterruptedException e) {}
		this.repaint();
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