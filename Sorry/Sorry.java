import javax.swing.JFrame;

class Sorry {
	private final static int WINDOW_SIZE = 768;

	public static void main(String[] args) {
		JFrame frame = new JFrame("Sorry!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(new Board(WINDOW_SIZE));
		frame.setSize(WINDOW_SIZE + 20, WINDOW_SIZE + 50);
		frame.setVisible(true);
	}
}