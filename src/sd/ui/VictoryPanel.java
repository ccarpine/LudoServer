package sd.ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class VictoryPanel extends JFrame {

	private static final long serialVersionUID = 1L;
	private MainFrame mainFrame;
	private String serverIP;

	public VictoryPanel(MainFrame mainFrame, String serverIP, String colorWinner) {
		super();
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("sd/ui/images/icon.png")));
		this.setTitle("Ludo Game");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.getContentPane().removeAll();
		this.setLayout(null);
		this.mainFrame = mainFrame;
		this.serverIP = serverIP;
		JLabel winner = new JLabel();
		winner.setIcon(new ImageIcon(ClassLoader.getSystemResource("sd/ui/images/victory/"+colorWinner+".png")));
		winner.setBounds(10, 10, 300, 400);
		this.add(winner);
		JButton newGame = new JButton();
		newGame.setBounds(50, 240, 190, 60);
		newGame.setIcon(new ImageIcon(ClassLoader.getSystemResource("sd/ui/images/reload.jpg")));
		newGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		this.add(newGame);
		JButton exit = new JButton();
		exit.setBounds(50, 320, 190, 60);
		exit.setIcon(new ImageIcon(ClassLoader.getSystemResource("sd/ui/images/exit.jpg")));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		this.add(exit);
		
	}

	private void newGame() {
		this.setVisible(false);
		this.mainFrame.resetFrame();
		this.mainFrame.addPanel(new IntroPanel(serverIP), BorderLayout.CENTER);
		this.dispose();
	}
	
}