package sd.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class VictoryFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private MainFrame mainFrame;
	private String serverIP;

	public VictoryFrame(MainFrame mainFrame, String serverIP, String colorWinner) {
		super();
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("sd/ui/images/icon.png")));
		this.setTitle("Don't Be Angry");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		this.getContentPane().removeAll();
		this.setLayout(new BorderLayout());
		this.mainFrame = mainFrame;
		this.serverIP = serverIP;
		BGPanel panel = new BGPanel("images/sky.png");
		this.add(panel);
		panel.setLayout(null);
		JLabel winner = new JLabel();
		winner.setIcon(new ImageIcon(ClassLoader.getSystemResource("sd/ui/images/victory/"+colorWinner+".png")));
		winner.setBounds(0, -35, 300, 400);
		JButton newGame = new JButton();
		newGame.setBounds(220, 240, 170, 50);
		newGame.setIcon(new ImageIcon(ClassLoader.getSystemResource("sd/ui/images/reload.jpg")));
		newGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				newGame();
			}
		});
		panel.add(newGame);
		JLabel message = new JLabel("<html><center>"+colorWinner+"<br>is the<br>winner!</center></html>");
		message.setBounds(245, 0, 200, 250);
		message.setFont(new java.awt.Font("Helvetica", Font.BOLD, 30));
		message.setForeground(Color.WHITE);
		panel.add(message);
		JButton exit = new JButton();
		exit.setBounds(220, 300, 170, 50);
		exit.setIcon(new ImageIcon(ClassLoader.getSystemResource("sd/ui/images/exit.jpg")));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panel.add(exit);
		panel.add(winner);
		panel.updateUI();
	}

	private void newGame() {
		this.setVisible(false);
		this.mainFrame.resetFrame();
		this.mainFrame.setSize(600, 500);
		this.mainFrame.addPanel(new IntroPanel(serverIP), BorderLayout.CENTER);
		this.dispose();
	}
	
	@Override
	public void setSize(Dimension d) {
        Insets i = this.getInsets();
        this.setSize(d.width + i.left + i.right, d.height + i.top + i.bottom);
    }
	
}