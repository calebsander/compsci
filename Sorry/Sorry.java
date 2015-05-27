import javax.swing.JFrame;

class Sorry {
	private final static int WIDTH = 768;
	public static void main(String[] args) {
		JFrame frame = new JFrame("Sorry!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(new Board(WIDTH));
		frame.setSize(WIDTH + 20, WIDTH + 50);
		frame.setVisible(true);
	}
}