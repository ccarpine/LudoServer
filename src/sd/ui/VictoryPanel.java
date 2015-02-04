package sd.ui;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class VictoryPanel extends BGPanel {

	public VictoryPanel(String path) {
		super("images/A4.png");
		this.setLayout(null);
		JButton b = new JButton();
		b.setBounds(50, 300, 200, 60);
		b.setIcon(new ImageIcon(ClassLoader.getSystemResource("sd/ui/images/start.jpg")));
		this.add(b);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
