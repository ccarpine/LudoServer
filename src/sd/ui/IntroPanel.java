package sd.ui;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JLabel;

import sd.core.register.RegisterInterface;

public class IntroPanel extends BGPanel {

	private static final long serialVersionUID = 1L;
	private JLabel waitingLabel;
	private JLabel timer;
	private JLabel countdown;
	private long timeToStart;

	public IntroPanel(final String serverIP) {
		super("images/startWallpaper.jpg");
		this.setLayout(null);
		
		JLabel ludo1 = new JLabel();
		ludo1.setBounds(300, 30, 150, 200);
		ludo1.setIcon(new javax.swing.ImageIcon(getClass().getResource("images/angry1.png")));
		this.add(ludo1);
		
		JLabel ludo2 = new JLabel();
		ludo2.setBounds(440, 30, 150, 200);
		ludo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("images/angry2.png")));
		this.add(ludo2);
		
		JLabel ludo3 = new JLabel();
		ludo3.setBounds(10, 280, 200, 200);
		ludo3.setIcon(new javax.swing.ImageIcon(getClass().getResource("images/angry3.png")));
		this.add(ludo3);
		
		final JButton goOnMatch = new javax.swing.JButton();
		goOnMatch.setBounds(380, 250, 180, 60);
		goOnMatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("images/start.jpg")));
		goOnMatch.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {

				goOnMatch.setEnabled(false);
				
				if (startConnection(serverIP)) {
					System.out.println("RICHIESTA INVIATA!");
					timer.setVisible(true);
					startCountdown();
					startWaiting();
				}
			}
		});
		this.add(goOnMatch);
		JButton exit = new javax.swing.JButton();
		exit.setBounds(380, 320, 180, 60);
		exit.setIcon(new
		javax.swing.ImageIcon(getClass().getResource("images/exit.jpg")));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		this.add(exit);
		
		timer = new JLabel("Start in:");
		timer.setBounds(330, 405, 100, 30);
		timer.setFont(new java.awt.Font("Helvetica", 0, 18));
		timer.setForeground(Color.BLACK);
		timer.setVisible(false);
		this.add(timer);
		
		countdown = new JLabel();
		countdown.setBounds(410, 405, 100, 30);
		countdown.setFont(new java.awt.Font("Helvetica", 0, 18));
		countdown.setForeground(Color.BLACK);
		countdown.setVisible(false);
		this.add(countdown);
		
		this.waitingLabel = new JLabel("Wait other players...");
		this.waitingLabel.setFont(new java.awt.Font("Helvetica", 0, 18));
		this.waitingLabel.setBounds(330, 420, 300, 50);
		this.waitingLabel.setVisible(false);
		this.add(waitingLabel);
		
	}
	
	private boolean startConnection(String serverIP) {
		try {
			RegisterInterface server = (RegisterInterface) Naming.lookup("rmi://" +serverIP + "/RMILudoServer");
			timeToStart = server.register(Inet4Address.getLocalHost().getHostAddress());
			System.out.println("0 REGISTER --> ");
			return true;
		} catch ( RemoteException | MalformedURLException | NotBoundException | UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void startCountdown() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				countdown.setVisible(true);
				while (timeToStart > 0) {
					int seconds = (int) (timeToStart / 1000) % 60;
					int minutes = (int) ((timeToStart / 60000) % 60);
					countdown.setText(String.format("%02d", minutes)+":"+String.format("%02d", seconds));
					try {
						Thread.sleep(1000);
						timeToStart -= 1000;
						waitingLabel.setVisible(!waitingLabel.isVisible());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private void startWaiting() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				waitingLabel.setVisible(true);
				while (timeToStart > 0) {
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

}