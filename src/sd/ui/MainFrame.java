package sd.ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;


public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	public MainFrame() {
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("sd/ui/images/icon.png")));
		this.setTitle("Ludo Game");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException| IllegalAccessException | UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				break;
			}
		this.setResizable(false);
		this.setSize(600, 500);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.resetFrame();
	}
	
	public void addPanel(JPanel panel, String borderLayout) {
		this.add(panel, borderLayout);
		this.revalidate();
		this.repaint();
	}
	
	public void resetFrame() {
		this.getContentPane().removeAll();
		this.setLayout(new BorderLayout());
	}
	
}