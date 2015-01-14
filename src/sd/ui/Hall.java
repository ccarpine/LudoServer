package sd.ui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

public class Hall extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JLabel waitingLabel;

	public Hall() {
		this.setIconImage(Toolkit.getDefaultToolkit().createImage(ClassLoader.getSystemResource("sd/ui/images/icon.png")));
		this.setTitle("Ludo Game");
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (ClassNotFoundException | InstantiationException
						| IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
				}
				break;
			}
		this.setSize(600, 500);
		this.setResizable(false);
		this.setLocationRelativeTo(null);
		BGPanel panel = new BGPanel("images/startWallpaper.jpg");
		panel.setLayout(null);
		this.add(panel);
		JButton goOnMatch = new javax.swing.JButton();
        goOnMatch.setBounds(380, 250, 180, 60);
        goOnMatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("images/start.jpg")));
        goOnMatch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				waitingLabel.setVisible(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							try {
								Thread.sleep(500);
								waitingLabel.setVisible(!waitingLabel.isVisible());
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
			}
		});
        panel.add(goOnMatch);
        JButton exit = new javax.swing.JButton();
        exit.setBounds(380, 320, 180, 60);
        exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("images/exit.jpg")));
        exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		panel.add(exit);
		this.waitingLabel = new JLabel("Wait other players...");
		this.waitingLabel.setFont(new java.awt.Font("Helvetica", 0, 18));
		this.waitingLabel.setBounds(330, 420, 300, 50);
		this.waitingLabel.setVisible(false);
		panel.add(waitingLabel);
		
	}
	
	public static void main(String[] args) {
		new Hall();
	}

}
