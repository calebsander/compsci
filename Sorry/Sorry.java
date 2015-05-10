import javax.swing.JFrame;

class Sorry {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Sorry!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(new Board());
		frame.setSize(1044, 1074);
		frame.setVisible(true);
	}
}