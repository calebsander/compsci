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
	private HashMap<Color,HashSet<Pawn>> pawns; //all the pawns, grouped by team color
	private Deck newCards; //draw pile
	private Deck oldCards; //discard pile
	private int playerTurn; //corresponds to the position in COLORS of the current turn color
	private int remaining; //remaining spaces to go for the second pawn chosen for a 7 card
	private Pawn selectedPawn; //keeps track of pawn being switched for 11 or Sorry! card

	private int turnState; //current part of turn; see constants below
	//Constants for current part of turn
	private final static int DRAW               = 0;
	private final static int SELECT_PAWN        = 1;
	private final static int SELECT_SECOND      = 2;
	private final static int SELECT_PAWN_SWITCH = 3;
	private final static int SELECT_PAWN_DEST   = 4;
	private final static int SELECT_SORRY_INIT  = 5;
	private final static int SELECT_SORRY_DEST  = 6;

	//Useful colors and an array of the team colors
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
	public static Font comicSans, comicSansSmall, comicSansTiny; //stores the best font ever in three different sizes

	Board() {
		//Go through a list of all the system fonts to find Comic Sans
		Font[] systemFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		boolean foundFont = false;
		for (Font font : systemFonts) {
			if (font.getFontName().equals("Comic Sans MS")) {
				comicSans = font.deriveFont(60.0F);
				foundFont = true;
				break;
			}
		}
		if (!foundFont) comicSans = new Font("SansSerif", Font.PLAIN, 60); //if no Comic Sans could be found, use generic Sans-Serif - this isn't sized exactly the same, but it will suffice
		comicSansSmall = comicSans.deriveFont(30.0F);
		comicSansTiny = comicSans.deriveFont(10.0F);

		//Assemble Map of Team->Set of Pawns
		this.pawns = new HashMap<Color,HashSet<Pawn>>();
		for (Color color : COLORS) {
			this.pawns.put(color, new HashSet<Pawn>());
			for (int j = 0; j < 4; j++) this.pawns.get(color).add(new Pawn(color, j));
		}

		this.replenish(); //create decks

		this.playerTurn = 0;
		this.turnState = DRAW;

		//Add click listener - this handles almost all the game logic
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				switch (Board.this.turnState) { //do different things based on the current part of the turn
					case Board.DRAW: //if drawing a card
						if (e.getX() > 368 && e.getX() < 656 && e.getY() > 288 && e.getY() < 480) { //if clicking on the draw pile
							if (Board.this.newCards.empty()) Board.this.replenish(); //replenish deck if necessary
							Board.this.oldCards.add(Board.this.newCards.deal()); //put drawn card in the discard pile
							boolean canMove; //whether or not a typical move is possible
							boolean canSwitch = false; //whether or not at least one of the player's pawns is out of Start and not in the home stretch
							boolean canBeSwitched = false; //whether or not at least one of the opponents' pawns is out of Start and not in the home stretch
							if (Board.this.oldCards.last().getValue() == 0) { //Sorry! card - needs extra special calculations for figuring out if moving is allowed
								boolean anyFriendAtStart = false;
								for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) { //check to see if any of the player's pawns are at Start
									if (pawn.isAtStart()) {
										anyFriendAtStart = true;
										break;
									}
								}
								boolean anyOpponentInPlay = false;
								for (Color player : Board.COLORS) { //check to see if any opponents' pawns are out of Start and not in the home stretch
									if (player.equals(Board.COLORS[Board.this.playerTurn])) continue; //only iterate over other players
									for (Pawn pawn : Board.this.pawns.get(player)) {
										if (pawn.isSwitchable()) {
											anyOpponentInPlay = true;
											break;
										}
									}
								}
								canMove = anyFriendAtStart && anyOpponentInPlay; //only allowed to move if those two conditions are met
							}
							else { //if a more normal card
								if (Board.this.oldCards.last().getValue() == 11) {
									for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
										if (pawn.isSwitchable()) {
											canSwitch = true;
											break;
										}
									}
									for (Color player : Board.COLORS) { //check to see if any opponents' pawns are out of Start and not in the home stretch
										if (player.equals(Board.COLORS[Board.this.playerTurn])) continue; //only iterate over other players
										for (Pawn pawn : Board.this.pawns.get(player)) {
											if (pawn.isSwitchable()) {
												canBeSwitched = true;
												break;
											}
										}
									}
								}
								canMove = false;
								for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) { //check to see whether any pawn can move
									if (pawn.canMove(Board.this.oldCards.last()) || (Board.this.oldCards.last().getValue() == 10 && pawn.canTenBackwards())) {
										canMove = true;
										break;
									}
								}
							}
							if (canMove) {
								switch (Board.this.oldCards.last().getValue()) {
									case 0: //Sorry! card - go to Sorry! case
										Board.this.turnState = Board.SELECT_SORRY_INIT;
										Board.this.displayMessage();
										break;
									case 11: //11 card - advancing is definitely possible, but not sure about switching
										if (canSwitch && canBeSwitched) { //if switching is possible, give the option to switch or advance or do nothing
											int choice = JOptionPane.showConfirmDialog(Board.this, "Switch places? (Yes for yes, No to advance 11 spaces instead, Cancel to forfeit turn)");
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
										}
										else { //if switching is not possible, give the option to advance
											int choice = JOptionPane.showConfirmDialog(Board.this, "Advance 11 spaces?", "Select an Option", JOptionPane.YES_NO_OPTION);
											if (choice == JOptionPane.YES_OPTION) {
												Board.this.turnState = Board.SELECT_PAWN;
												Board.this.displayMessage();
											}
											else {
												Board.this.nextTurn();
												Board.this.displayMessage();
											}
										}
										break;
									default: //most cards, go to pawn selection state
										Board.this.turnState = Board.SELECT_PAWN;
										Board.this.displayMessage();
								}
							}
							else {
								if (Board.this.oldCards.last().getValue() == 11) { //advancing is definitely not possible, but not sure about switching
									if (canSwitch && canBeSwitched) { //if switching is possible, give the option to switch
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
									else { //neither switching not advancing is possible, so go on to the next turn
										Board.this.nextTurn();
										Board.this.displayMessage();
									}
								}
								else { //no action is possible
									if (Board.this.oldCards.last().getValue() != 2) Board.this.nextTurn(); //2 card allows a second turn
									Board.this.displayMessage();
								}
							}
						}
						break;
					case Board.SELECT_PAWN: //if picking a pawn to move
						for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) { //iterate over all the current player's pawns
							if (pawn.clickedBy(e.getX(), e.getY()) && (pawn.canMove(Board.this.oldCards.last()) || (Board.this.oldCards.last().getValue() == 10 && pawn.canTenBackwards()))) { //if the pawn was clicked and moving is possible
								if (Board.this.oldCards.last().getValue() == 7) { //7 card
									pawn.select();
									int distance = 8;
									while ((distance < 1 || distance > 7) || (pawn.getPos() != 100 && pawn.getPos() + distance > 65)) { //keep going until the requested amount to move is valid
										try {
											distance = Integer.valueOf(JOptionPane.showInputDialog(Board.this, "How many squares to move (1-7)?")).intValue();
										}
										catch (NumberFormatException err) {} //just so program doesn't crash on input that isn't an integer
									}
									pawn.move(distance);
									HashSet<Pawn> moves = pawn.checkSlide(); //get a list of all places the pawn passed in sliding
									moves.add(pawn); //add the current position if the pawn didn't slide
									for (Color player : Board.COLORS) { //iterate over all the pawns, and move any (others) that are in the same location
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
									for (Pawn friendlyPawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) { //check to see if all the pawns on this team have reached Home
										if (!friendlyPawn.isHome()) {
											allHome = false;
											break;
										}
									}
									if (allHome) Board.this.endGame(Board.COLORS[Board.this.playerTurn]);
									if (distance == 7) { //if all 7 squares were used up on the first pawn, go to the next turn
										Board.this.turnState = Board.DRAW;
										Board.this.nextTurn();
										Board.this.displayMessage();
									}
									else { //otherwise, pick the next pawn
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
										else { //if it isn't possible to move another pawn the remaining distance, go to the next turn
											Board.this.turnState = Board.DRAW;
											Board.this.nextTurn();
											Board.this.displayMessage();
										}
									}
								}
								else if (Board.this.oldCards.last().getValue() == 10) { //10 card
									if (pawn.canMove(Board.this.oldCards.last())) {
										if (pawn.canTenBackwards()) { //if can move both forwards and backwards, give the option
											pawn.select();
											boolean moveForwards = JOptionPane.showConfirmDialog(Board.this, "Go forwards?", "Select an Option", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
											if (moveForwards) pawn.move(10);
											else pawn.move(-1);
										}
										else pawn.move(10);
									}
									else pawn.move(-1);
									//See annotation for the 7 card
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
									pawn.move(Board.this.oldCards.last()); //let Pawn class calculate how far to move
									//See annotations above for 7
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
									if (Board.this.oldCards.last().getValue() != 2) Board.this.nextTurn(); //2 card gets to go again
									Board.this.displayMessage();
								}
								break;
							}
						}
						break;
					case Board.SELECT_SECOND: //if choosing the second pawn to move on a 7 card
						for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
							if (pawn.clickedBy(e.getX(), e.getY()) && !pawn.isAtStart() && pawn.getPos() + Board.this.remaining < 66) { //if a pawn on the team was clicking and it was out of Start and it can move the distance
								pawn.move(Board.this.remaining);
								//See annotations for 7 card in case Board.SELECT_PAWN
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
					case Board.SELECT_PAWN_SWITCH: //if selecting the pawn to switch
						for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) {
							if (pawn.clickedBy(e.getX(), e.getY()) && pawn.isSwitchable()) { //if one of the player's pawns is clicking and is switchable
								Board.this.selectedPawn = pawn; //record that this one should be switched
								pawn.select();
								Board.this.turnState = Board.SELECT_PAWN_DEST; //select the receiver of the switch
								Board.this.displayMessage();
								break;
							}
						}
						break;
					case Board.SELECT_PAWN_DEST: //if selecting the pawn to be switched with
						boolean quitLookingLoop = false;
						for (Color player : Board.COLORS) {
							if (player.equals(Board.COLORS[Board.this.playerTurn])) continue; //can't switch with own team
							for (Pawn pawn : Board.this.pawns.get(player)) {
								if (pawn.clickedBy(e.getX(), e.getY()) && pawn.isSwitchable()) { //if this pawn is clicked and is switchable
									//The strategy here is to check to see what position on the pawn being switched would put it where the destination pawn is - this is necessary because of how the position of the pawns is kept as relative to their own Start spaces
									Pawn cloneOfSwitching = Board.this.selectedPawn.clone();
									for (int pos = 1; pos < 101; pos++) { //iterate over every possible position (0-59 and 100)
										if (pos == 60) pos = 100;
										cloneOfSwitching.setPos(pos);
										if (cloneOfSwitching.sameSquare(pawn)) { //if putting the pawn at this position would put it at the same position as the destination, switch them
											cloneOfSwitching = Board.this.selectedPawn.clone(); //keep track of where the pawn used to be so the other one can be switched to it
											Board.this.selectedPawn.setPos(pos);
											Board.this.selectedPawn.deselect();
											Pawn cloneOfSwitched = pawn.clone();
											for (int secondPos = 1; secondPos < 60; secondPos++) { //do the same thing on the other pawn to figure out where it should be placed
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
							if (quitLookingLoop) break; //continuing to scan after a switch has been made has weird results like sometimes switching back
						}
						break;
					case Board.SELECT_SORRY_INIT: //if selecting the pawn to move out of Start for the Sorry! card
						for (Pawn pawn : Board.this.pawns.get(Board.COLORS[Board.this.playerTurn])) { //iterate over the pawns on this team
							if (pawn.clickedBy(e.getX(), e.getY()) && pawn.isAtStart()) { //if the pawn was clicked and is at Start, it is elligible to be switched
								Board.this.selectedPawn = pawn;
								pawn.select();
								Board.this.turnState = Board.SELECT_SORRY_DEST; //go to selecting the target pawn
								Board.this.displayMessage();
								break;
							}
						}
						break;
					case Board.SELECT_SORRY_DEST: //if selecting the pawn to be sent back to Start for the Sorry! card
						boolean quitLooking = false;
						for (Color player : Board.COLORS) {
							if (player.equals(Board.COLORS[Board.this.playerTurn])) continue; //can't switch with own team
							for (Pawn pawn : Board.this.pawns.get(player)) {
								if (pawn.clickedBy(e.getX(), e.getY()) && pawn.isSwitchable()) {
									//See annotation for case Board.SELET_PAWN_DEST
									Pawn cloneOfSwitching = Board.this.selectedPawn.clone();
									for (int pos = 1; pos < 101; pos++) {
										if (pos == 60) pos = 100;
										cloneOfSwitching.setPos(pos);
										if (cloneOfSwitching.sameSquare(pawn)) {
											Board.this.selectedPawn.setPos(pos);
											Board.this.selectedPawn.deselect();
											pawn.bump(); //move the other pawn back to Start
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
							if (quitLooking) break; //see annotation above for case Board.SELECT_PAWN_DEST
						}
				}
			}
		});
		this.displayMessage();
	}

	public void paintComponent(Graphics gA) { //paints everything on the board
		super.paintComponent(gA);

		Graphics2D g = (Graphics2D)gA;
		g.setFont(comicSans);
		g.drawString("SORRY!", 400, 536);
		this.setBackground(BACKGROUND);

		//Spaces
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

		//Red side without home stretch
		drawRect(g, DARK_RED, 1, 0, 3, 0);
		g.setColor(RED);
		g.fillPolygon(new int[]{72, 96, 72}, new int[]{8, 32, 56}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{72, 96, 72}, new int[]{8, 32, 56}, 3);
		drawCircle(g, RED, 4, 0);
		drawRect(g, DARK_RED, 9, 0, 4, 0);
		g.setColor(RED);
		g.fillPolygon(new int[]{584, 608, 584}, new int[]{8, 32, 56}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{584, 608, 584}, new int[]{8, 32, 56}, 3);
		drawCircle(g, RED, 13, 0);
		g.setColor(RED);
		g.fillOval(191, 64, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(191, 64, 192, 192);

		//Blue side without home stretch
		drawRect(g, DARK_BLUE, 15, 1, 0, 3);
		g.setColor(BLUE);
		g.fillPolygon(new int[]{968, 992, 1016}, new int[]{72, 96, 72}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{968, 992, 1016}, new int[]{72, 96, 72}, 3);
		drawCircle(g, BLUE, 15, 4);
		drawRect(g, DARK_BLUE, 15, 9, 0, 4);
		g.setColor(BLUE);
		g.fillPolygon(new int[]{968, 992, 1016}, new int[]{584, 608, 584}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{968, 992, 1016}, new int[]{584, 608, 584}, 3);
		drawCircle(g, BLUE, 15, 13);
		g.setColor(BLUE);
		g.fillOval(767, 192, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(767, 192, 192, 192);

		//Yellow side without home stretch
		drawRect(g, DARK_YELLOW, 2, 15, 4, 0);
		g.setColor(YELLOW);
		g.fillPolygon(new int[]{440, 416, 440}, new int[]{968, 992, 1016}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{440, 416, 440}, new int[]{968, 992, 1016}, 3);
		drawCircle(g, YELLOW, 2, 15);
		drawRect(g, DARK_YELLOW, 11, 15, 3, 0);
		g.setColor(YELLOW);
		g.fillPolygon(new int[]{952, 928, 952}, new int[]{968, 992, 1016}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{952, 928, 952}, new int[]{968, 992, 1016}, 3);
		drawCircle(g, YELLOW, 11, 15);
		g.setColor(YELLOW);
		g.fillOval(639, 768, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(639, 768, 192, 192);

		//Green side without home stretch
		drawRect(g, DARK_GREEN, 0, 2, 0, 4);
		g.setColor(GREEN);
		g.fillPolygon(new int[]{8, 32, 56}, new int[]{440, 416, 440}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{8, 32, 56}, new int[]{440, 416, 440}, 3);
		drawCircle(g, GREEN, 0, 2);
		drawRect(g, DARK_GREEN, 0, 11, 0, 3);
		g.setColor(GREEN);
		g.fillPolygon(new int[]{8, 32, 56}, new int[]{952, 928, 952}, 3);
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{8, 32, 56}, new int[]{952, 928, 952}, 3);
		drawCircle(g, GREEN, 0, 11);
		g.setColor(GREEN);
		g.fillOval(63, 640, 192, 192);
		g.setColor(Color.BLACK);
		g.drawOval(63, 640, 192, 192);

		//Red home stretch
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

		//Blue home stretch
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

		//Yellow home stretch
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

		//Green home stretch
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

		//Centerpiece
		g.setColor(Color.BLACK);
		g.drawPolygon(new int[]{256, 512, 768, 512}, new int[]{512, 256, 512, 768}, 4);
		drawCardBase(g, 368, 288);
		drawCardBase(g, 368, 544);
		g.setColor(Color.WHITE);
		g.fillRect(376, 296, 272, 176);
		g.setColor(GREEN);
		g.fillRect(384, 304, 256, 160);
		g.setColor(DARK_GREEN);
		g.fillRect(448, 320, 128, 128);
		this.oldCards.display(g, 368, 544); //draw the top of the discard pile
		g.setColor(Color.BLACK);

		//Draw each pawn
		for (Color player : COLORS) {
			for (Pawn pawn : this.pawns.get(player)) pawn.draw(g);
		}

		//Draw color of current turn
		g.setColor(COLORS[this.playerTurn]);
		g.fillRect(296, 480, 64, 64);
		g.setFont(comicSansTiny);
		g.setColor(Color.BLACK);
		g.drawString("CURRENT", 304, 512);
		g.drawString("TURN", 312, 528);

		//Draw Start and Home labels
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

		//Draw the board every 50 milliseconds - it is useful to update the background while a dialog message is in process, which would block it everywhere else
		try {
			Thread.sleep(50);
		}
		catch (InterruptedException e) {}
		this.repaint();
	}
	//Makes a new draw deck and discard deck
	private void replenish() {
		this.newCards = new Deck(true);
		this.newCards.shuffle();
		this.oldCards = new Deck(false);
	}
	//Draws the outline of a slide rectangle
	private static void drawRect(Graphics2D g, Color color, int x, int y, int width, int height) {
		g.setColor(color);
		g.fillRect(x * 64 + 16, y * 64 + 16, width * 64 + 32, height * 64 + 32);
		g.setColor(Color.BLACK);
		g.drawRect(x * 64 + 16, y * 64 + 16, width * 64 + 32, height * 64 + 32);
	}
	//Draws the circle at the end of a slide rectangle
	private static void drawCircle(Graphics2D g, Color color, int x, int y) {
		g.setColor(color);
		g.fillOval(x * 64 + 8, y * 64 + 8, 48, 48);
		g.setColor(Color.BLACK);
		g.drawOval(x * 64 + 8, y * 64 + 8, 48, 48);
	}
	//Draws the black outline around a deck base
	private static void drawCardBase(Graphics2D g, int x, int y) {
		g.setColor(Color.BLACK);
		g.drawRect(x, y, 288, 192);
		g.setColor(BACKGROUND);
		g.fillRect(x + 1, y + 1, 287, 191);
	}
	//Goes to the next turn
	private void nextTurn() {
		this.playerTurn = (this.playerTurn + 1) % 4;
	}
	//Displays instructions for the current turnState in the form of a popup window
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
				JOptionPane.showMessageDialog(this, "Select pawn to switch with");
				break;
			case SELECT_SORRY_INIT:
				JOptionPane.showMessageDialog(this, "Select your pawn to leave Start");
				break;
			case SELECT_SORRY_DEST:
				JOptionPane.showMessageDialog(this, "Select pawn to send back to Start");
		}
	}
	//Alert which player won and end the program
	private void endGame(Color winnerColor) {
		if (winnerColor.equals(RED)) JOptionPane.showMessageDialog(this, "Red wins!");
		else if (winnerColor.equals(BLUE)) JOptionPane.showMessageDialog(this, "Blue wins!");
		else if (winnerColor.equals(YELLOW)) JOptionPane.showMessageDialog(this, "Yellow wins!");
		else if (winnerColor.equals(GREEN)) JOptionPane.showMessageDialog(this, "Green wins!");
		System.exit(0);
	}
}