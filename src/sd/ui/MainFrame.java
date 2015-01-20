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
	private JPanel panel;
	
	public MainFrame() {
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("sd/ui/images/icon.png")));
		this.setTitle("Ludo Game");
		System.out.println("ptima");
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException| IllegalAccessException | UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				break;
			}
		//this.setResizable(false);
		System.out.println("due");
		this.setSize(600, 500);
		this.setLocationRelativeTo(null);
		this.setLayout(new BorderLayout());
		this.panel= new JPanel();
		System.out.println("tre");
		this.add(this.panel);
	}
	
	public void addPanel(JPanel panel) {
		this.panel.removeAll();
		this.panel = panel;
		this.panel.updateUI();
		this.setVisible(true);
	}
	
}