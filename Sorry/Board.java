import java.util.HashMap;
import java.util.HashSet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

class Board extends JPanel {
	private HashMap<Color,HashSet<Pawn>> pawns;
	private Deck newCards;
	private Deck oldCards;
	private int playerTurn;
	private int turnState;
	private int remaining;
	private Pawn selectedPawn;
	private final static int DRAW               = 0;
	private final static int SELECT_PAWN        = 1;
	private final static int SELECT_SECOND      = 2;
	private final static int SELECT_PAWN_SWITCH = 3;
	private final static int SELECT_PAWN_DEST   = 4;
	private final static int SELECT_SORRY_INIT  = 5;
	private final static int SELECT_SORRY_DEST  = 6;

	public final static Color BACKGROUND  = new Color(204, 225, 204);
	public final static Color RED         = new Color(221, 0, 0);
	public final static Color DARK_RED    = new Color(187, 0, 0);
	public final static Color GREEN       = new Color(0, 221, 0);
	public final static Color DARK_GREEN  = new Color(0, 187, 0);
	public final static Color BLUE        = new Color(0, 0, 221);
	public final static Color DARK_BLUE   = new Color(0, 0, 187);
	public final static Color YELLOW      = new Color(221, 221, 0);
	public final static Color DARK_YELLOW = new Color(187, 187, 0);
	private final static Color[] COLORS   = {RED, BLUE, YELLOW, GREEN};
	public static Font comicSans, comicSansSmall, comicSansTiny;

	Board() {
		Font[] systemFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		boolean foundFont = false;
		for (Font font : systemFonts) {
			if (font.getFontName().equals("Comic Sans MS")) {
				this.comicSans = font.deriveFont(60.0F);
				foundFont = true;
				break;
			}
		}
		if (!foundFont) comicSans = new Font("SansSerif", Font.PLAIN, 60);
		comicSansSmall = comicSans.deriveFont(30.0F);
		comicSansTiny = comicSans.deriveFont(10.0F);

		this.pawns = new HashMap<Color,HashSet<Pawn>>();
		for (Color color : COLORS) {
			this.pawns.put(color, new HashSet<Pawn>());
			for (int j = 0; j < 4; j++) this.pawns.get(color).add(new Pawn(color, j));
		}
		this.newCards = new Deck(true);
		this.newCards.shuffle();
		this.oldCards = new Deck(false);
		this.playerTurn = 0;
		this.turnState = DRAW;
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				switch (Board.this.turnState) {
					case Board.DRAW:
						if (e.getX() > 368 && e.getX() < 656 && e.getY() > 288 && e.getY() < 480) {
							if (Board.this.newCards.size() == 0) {
								Board.this.newCards = new Deck(true);
								Board.this.newCards.shuffle();
								Board.this.oldCards = new Deck(false);
							}
							Board.this.oldCards.add(Board.this.newCards.deal());
							boolean canMove;
							if (Board.this.oldCards.last().getValue() == 0) { //Sorry! card
								boolean anyFriendAtStart = false;
								for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
									if (pawn.getPos() == 0) {
										anyFriendAtStart = true;
										break;
									}
								}
								boolean anyOpponentInPlay = false;
								for (Color player : Board.COLORS) {
									if (player.equals(Board.COLORS[Board.this.playerTurn])) continue; //only iterate over other players
									for (Pawn pawn : Board.this.pawns.get(player)) {
										if (pawn.getPos() != 0 && (pawn.getPos() < 60 || pawn.getPos() == 100)) {
											anyOpponentInPlay = true;
											break;
										}
									}
								}
								canMove = anyFriendAtStart && anyOpponentInPlay;
							}
							else {
								canMove = false;
								for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
									if (pawn.canMove(Board.this.oldCards.last()) || (Board.this.oldCards.last().getValue() == 10 && pawn.canTenBackwards())) {
										canMove = true;
										break;
									}
								}
							}
							if (canMove) {
								switch (Board.this.oldCards.last().getValue()) {
									case 0: //Sorry! card
										Board.this.turnState = Board.SELECT_SORRY_INIT;
										Board.this.displayMessage();
										break;
									case 11:
										int choice = JOptionPane.showConfirmDialog(Board.this, "Switch places? (Yes for yes, No for advancing 11 spaces, Cancel to forfeit turn)");
										switch (choice) {
											case JOptionPane.YES_OPTION:
												Board.this.turnState = Board.SELECT_PAWN_SWITCH;
												Board.this.displayMessage();
												break;
											case JOptionPane.NO_OPTION:
												Board.this.turnState = Board.SELECT_PAWN;
												Board.this.displayMessage();
												break;
											case JOptionPane.CANCEL_OPTION:
												Board.this.nextTurn();
												Board.this.displayMessage();
										}
										break;
									default:
										Board.this.turnState = Board.SELECT_PAWN;
										Board.this.displayMessage();
								}
							}
							else {
								if (Board.this.oldCards.last().getValue() == 11) {
									boolean canSwitch = false;
									for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
										if (pawn.getPos() != 0 && (pawn.getPos() < 60 || pawn.getPos() == 100)) {
											canSwitch = true;
											break;
										}
									}
									if (canSwitch) {
										int choice = JOptionPane.showConfirmDialog(Board.this, "Switch places?", "Select an Option", JOptionPane.YES_NO_OPTION);
										if (choice == JOptionPane.YES_OPTION) {
											Board.this.turnState = Board.SELECT_PAWN_SWITCH;
											Board.this.displayMessage();
										}
										else {
											Board.this.nextTurn();
											Board.this.displayMessage();
										}
									}
									else {
										Board.this.nextTurn();
										Board.this.displayMessage();
									}
								}
								else {
									if (Board.this.oldCards.last().getValue() != 2) Board.this.nextTurn();
									Board.this.displayMessage();
								}
							}
						}
						break;
					case Board.SELECT_PAWN:
						for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
							if (pawn.clickedBy(e.getX(), e.getY()) && (pawn.canMove(Board.this.oldCards.last()) || (Board.this.oldCards.last().getValue() == 10 && pawn.canTenBackwards()))) {
								if (Board.this.oldCards.last().getValue() == 7) { //7 card
									pawn.select();
									int distance = 8;
									while ((distance < 1 || distance > 7) || (pawn.getPos() != 100 && pawn.getPos() + distance > 65)) {
										try {
											distance = Integer.valueOf(JOptionPane.showInputDialog(Board.this, "How many squares to move (1-7)?")).intValue();
										}
										catch (NumberFormatException err) {}
									}
									pawn.move(distance);
									HashSet<Pawn> moves = pawn.checkSlide();
									moves.add(pawn);
									for (Color player : Board.COLORS) {
										for (Pawn otherPawn : Board.this.pawns.get(player)) {
											for (Pawn passedPosition : moves) {
												if (!otherPawn.equals(pawn) && otherPawn.sameSquare(passedPosition)) {
													otherPawn.bump();
													break;
												}
											}
										}
									}
									pawn.deselect();
									boolean allHome = true;
									for (Pawn friendlyPawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
										if (!friendlyPawn.isHome()) {
											allHome = false;
											break;
										}
									}
									if (allHome) Board.this.endGame(Board.COLORS[Board.this.playerTurn]);
									if (distance == 7) {
										Board.this.turnState = Board.DRAW;
										Board.this.nextTurn();
										Board.this.displayMessage();
									}
									else {
										Board.this.remaining = 7 - distance;
										boolean canMove = false;
										for (Pawn otherPawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
											if (otherPawn.getPos() != 0 && otherPawn.getPos() + Board.this.remaining < 66) {
												canMove = true;
												break;
											}
										}
										if (canMove) {
											Board.this.turnState = Board.SELECT_SECOND;
											Board.this.displayMessage();
										}
										else {
											Board.this.turnState = Board.DRAW;
											Board.this.nextTurn();
											Board.this.displayMessage();
										}
									}
								}
								else if (Board.this.oldCards.last().getValue() == 10) { //10 card
									if (pawn.canMove(Board.this.oldCards.last())) {
										if (pawn.canTenBackwards()) {
											pawn.select();
											boolean moveForwards = JOptionPane.showConfirmDialog(Board.this, "Go forwards?", "Select an Option", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
											if (moveForwards) pawn.move(10);
											else pawn.move(-1);
										}
										else pawn.move(10);
									}
									else pawn.move(-1);
									HashSet<Pawn> moves = pawn.checkSlide();
									moves.add(pawn);
									for (Color player : Board.COLORS) {
										for (Pawn otherPawn : Board.this.pawns.get(player)) {
											for (Pawn passedPosition : moves) {
												if (!otherPawn.equals(pawn) && otherPawn.sameSquare(passedPosition)) {
													otherPawn.bump();
													break;
												}
											}
										}
									}
									pawn.deselect();
									boolean allHome = true;
									for (Pawn friendlyPawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
										if (!friendlyPawn.isHome()) {
											allHome = false;
											break;
										}
									}
									if (allHome) Board.this.endGame(Board.COLORS[Board.this.playerTurn]);
									Board.this.turnState = Board.DRAW;
									Board.this.nextTurn();
									Board.this.displayMessage();
								}
								else { //"normal" movement card - not 7 or 10 or Sorry!
									pawn.move(Board.this.oldCards.last());
									HashSet<Pawn> moves = pawn.checkSlide();
									moves.add(pawn);
									for (Color player : Board.COLORS) {
										for (Pawn otherPawn : Board.this.pawns.get(player)) {
											for (Pawn passedPosition : moves) {
												if (!otherPawn.equals(pawn) && otherPawn.sameSquare(passedPosition)) {
													otherPawn.bump();
													break;
												}
											}
										}
									}
									boolean allHome = true;
									for (Pawn friendlyPawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
										if (!friendlyPawn.isHome()) {
											allHome = false;
											break;
										}
									}
									if (allHome) Board.this.endGame(Board.COLORS[Board.this.playerTurn]);
									Board.this.turnState = Board.DRAW;
									if (Board.this.oldCards.last().getValue() != 2) Board.this.nextTurn();
									Board.this.displayMessage();
								}
								break;
							}
						}
						break;
					case Board.SELECT_SECOND:
						for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
							if (pawn.clickedBy(e.getX(), e.getY()) && pawn.getPos() != 0 && pawn.getPos() + Board.this.remaining < 66) {
								pawn.move(Board.this.remaining);
								HashSet<Pawn> moves = pawn.checkSlide();
								moves.add(pawn);
								for (Color player : Board.COLORS) {
									for (Pawn otherPawn : Board.this.pawns.get(player)) {
										for (Pawn passedPosition : moves) {
											if (!otherPawn.equals(pawn) && otherPawn.sameSquare(passedPosition)) {
												otherPawn.bump();
												break;
											}
										}
									}
								}
								boolean allHome = true;
								for (Pawn friendlyPawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
									if (!friendlyPawn.isHome()) {
										allHome = false;
										break;
									}
								}
								if (allHome) Board.this.endGame(Board.COLORS[Board.this.playerTurn]);
								Board.this.turnState = Board.DRAW;
								Board.this.nextTurn();
								Board.this.displayMessage();
								break;
							}
						}
						break;
					case Board.SELECT_PAWN_SWITCH:
						for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
							if (pawn.clickedBy(e.getX(), e.getY()) && pawn.getPos() != 0 && (pawn.getPos() < 60 || pawn.getPos() == 100)) {
								Board.this.selectedPawn = pawn;
								pawn.select();
								Board.this.turnState = Board.SELECT_PAWN_DEST;
								Board.this.displayMessage();
								break;
							}
						}
						break;
					case Board.SELECT_PAWN_DEST:
						boolean quitLookingLoop = false;
						for (Color player : Board.COLORS) {
							if (player.equals(Board.COLORS[Board.this.playerTurn])) {
								for (Pawn pawn : Board.this.pawns.get(player)) {
									if (pawn.clickedBy(e.getX(), e.getY())) {
										Board.this.selectedPawn.deselect();
										Board.this.turnState = Board.DRAW;
										Board.this.nextTurn();
										Board.this.displayMessage();
										quitLookingLoop = true;
										break;
									}
								}
							}
							else {
								for (Pawn pawn : Board.this.pawns.get(player)) {
									if (pawn.clickedBy(e.getX(), e.getY()) && pawn.getPos() != 0 && (pawn.getPos() < 60 || pawn.getPos() == 100)) {
										Pawn cloneOfSwitching = Board.this.selectedPawn.clone();
										for (int pos = 1; pos < 101; pos++) {
											if (pos == 60) pos = 100;
											cloneOfSwitching.setPos(pos);
											if (cloneOfSwitching.sameSquare(pawn)) {
												cloneOfSwitching = Board.this.selectedPawn.clone(); //keep track of where the pawn used to be so the other one can be switched to it
												Board.this.selectedPawn.setPos(pos);
												Board.this.selectedPawn.deselect();
												Pawn cloneOfSwitched = pawn.clone();
												for (int secondPos = 1; secondPos < 60; secondPos++) {
													if (secondPos == 60) secondPos = 100;
													cloneOfSwitched.setPos(secondPos);
													if (cloneOfSwitched.sameSquare(cloneOfSwitching)) {
														pawn.setPos(secondPos);
														break;
													}
												}
												Board.this.turnState = Board.DRAW;
												Board.this.nextTurn();
												Board.this.displayMessage();
												break;
											}
										}
										quitLookingLoop = true;
										break;
									}
								}
							}
							if (quitLookingLoop) break;
						}
						break;
					case Board.SELECT_SORRY_INIT:
						for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
							if (pawn.clickedBy(e.getX(), e.getY()) && pawn.getPos() == 0) {
								Board.this.selectedPawn = pawn;
								pawn.select();
								Board.this.turnState = Board.SELECT_SORRY_DEST;
								Board.this.displayMessage();
								break;
							}
						}
						break;
					case Board.SELECT_SORRY_DEST:
						boolean quitLooking = false;
						for (Color player : Board.COLORS) {
							if (player.equals(Board.COLORS[Board.this.playerTurn])) continue;
							for (Pawn pawn : Board.this.pawns.get(player)) {
								if (pawn.clickedBy(e.getX(), e.getY()) && pawn.getPos() != 0 && (pawn.getPos() < 60 || pawn.getPos() == 100)) {
									Pawn cloneOfSwitching = Board.this.selectedPawn.clone();
									for (int pos = 1; pos < 101; pos++) {
										if (pos == 60) pos = 100;
										cloneOfSwitching.setPos(pos);
										if (cloneOfSwitching.sameSquare(pawn)) {
											Board.this.selectedPawn.setPos(pos);
											Board.this.selectedPawn.deselect();
											pawn.setPos(0);
											Board.this.turnState = Board.DRAW;
											Board.this.nextTurn();
											Board.this.displayMessage();
											break;
										}
									}
									quitLooking = true;
									break;
								}
							}
							if (quitLooking) break;
						}
				}
			}
		});
		this.displayMessage();
	}

	public void paintComponent(Graphics gA) {
		super.paintComponent(gA);

		Graphics2D g = (Graphics2D)gA;
		g.setFont(comicSans);
		g.drawString("SORRY!", 400, 536);
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
		g.fillOval(191, 64, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(191, 64, 192, 192);

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
		g.fillOval(767, 192, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(767, 192, 192, 192);

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
		g.fillOval(639, 768, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(639, 768, 192, 192);

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
		g.fillOval(63, 640, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(63, 640, 192, 192);

		g.setColor(RED);
		g.fillRect(127, 64, 64, 384);
		g.setColor(Color.BLACK);
		g.drawRect(127, 64, 64, 64);
		g.drawRect(127, 128, 64, 64);
		g.drawRect(127, 192, 64, 64);
		g.drawRect(127, 256, 64, 64);
		g.drawRect(127, 320, 64, 64);
		g.fillRect(127, 384, 65, 64);
		g.setColor(RED);
		g.fillOval(64, 384, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(64, 384, 192, 192);

		g.setColor(BLUE);
		g.fillRect(576, 128, 383, 64);
		g.setColor(Color.BLACK);
		g.drawRect(896, 128, 63, 64);
		g.drawRect(832, 128, 64, 64);
		g.drawRect(768, 128, 64, 64);
		g.drawRect(704, 128, 64, 64);
		g.drawRect(640, 128, 64, 64);
		g.fillRect(576, 128, 64, 65);
		g.setColor(BLUE);
		g.fillOval(448, 64, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(448, 64, 192, 192);

		g.setColor(YELLOW);
		g.fillRect(831, 576, 64, 384);
		g.setColor(Color.BLACK);
		g.drawRect(831, 896, 64, 64);
		g.drawRect(831, 832, 64, 64);
		g.drawRect(831, 768, 64, 64);
		g.drawRect(831, 704, 64, 64);
		g.drawRect(831, 640, 64, 64);
		g.fillRect(831, 576, 65, 64);
		g.setColor(YELLOW);
		g.fillOval(767, 448, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(767, 448, 192, 192);

		g.setColor(GREEN);
		g.fillRect(63, 832, 384, 64);
		g.setColor(Color.BLACK);
		g.drawRect(63, 832, 64, 64);
		g.drawRect(127, 832, 64, 64);
		g.drawRect(191, 832, 64, 64);
		g.drawRect(255, 832, 64, 64);
		g.drawRect(319, 832, 64, 64);
		g.fillRect(383, 832, 64, 65);
		g.setColor(GREEN);
		g.fillOval(383, 768, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(383, 768, 192, 192);

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

		for (Color player : COLORS) {
			for (Pawn pawn : this.pawns.get(player)) pawn.draw(g);
		}
		g.setColor(COLORS[this.playerTurn]);
		g.fillRect(296, 480, 64, 64);
		g.setFont(comicSansTiny);
		g.setColor(Color.BLACK);
		g.drawString("CURRENT", 304, 512);
		g.drawString("TURN", 312, 528);

		g.setFont(comicSansSmall);
		g.setColor(Color.BLACK);
		g.drawString("START", 224, 288);
		g.drawString("HOME", 96, 608);
		g.drawString("START", 800, 416);
		g.drawString("HOME", 616, 96);
		g.drawString("START", 672, 760);
		g.drawString("HOME", 800, 448);
		g.drawString("START", 96, 640);
		g.drawString("HOME", 300, 824);
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
	private void nextTurn() {
		this.playerTurn = (this.playerTurn + 1) % 4;
	}
	private void displayMessage() {
		switch (this.turnState) {
			case DRAW:
				JOptionPane.showMessageDialog(this, "Draw a card");
				break;
			case SELECT_PAWN:
				JOptionPane.showMessageDialog(this, "Select pawn to move");
				break;
			case SELECT_SECOND:
				JOptionPane.showMessageDialog(this, "Select pawn to move the remaining " + Integer.valueOf(this.remaining) + " space(s)");
				break;
			case SELECT_PAWN_SWITCH:
				JOptionPane.showMessageDialog(this, "Select your pawn to switch");
				break;
			case SELECT_PAWN_DEST:
				JOptionPane.showMessageDialog(this, "Select pawn to switch with (select one of your own to cancel switching)");
				break;
			case SELECT_SORRY_INIT:
				JOptionPane.showMessageDialog(this, "Select your pawn to leave Start");
				break;
			case SELECT_SORRY_DEST:
				JOptionPane.showMessageDialog(this, "Select pawn to send back to Start");
		}
	}
	private void endGame(Color winnerColor) {
		if (winnerColor.equals(RED)) JOptionPane.showMessageDialog(Board.this, "Red wins!");
		else if (winnerColor.equals(BLUE)) JOptionPane.showMessageDialog(Board.this, "Blue wins!");
		else if (winnerColor.equals(YELLOW)) JOptionPane.showMessageDialog(Board.this, "Yellow wins!");
		else if (winnerColor.equals(GREEN)) JOptionPane.showMessageDialog(Board.this, "Green wins!");
		System.exit(0);
	}
}