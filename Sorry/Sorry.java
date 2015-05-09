import javax.swing.JFrame;

class Sorry {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Sorry");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.add(new Board());
		frame.setSize(1024, 1024);
		frame.setVisible(true);
	}
}