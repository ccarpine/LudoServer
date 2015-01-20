package sd.ui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import sd.core.player.UserPlayer;
import sd.core.player.UserPlayerInterface;
import sd.core.register.RegisterInterface;

public class IntroPanel extends BGPanel {

	private static final long serialVersionUID = 1L;
	private JLabel waitingLabel;

	public IntroPanel(String serverIP) {
		super("images/startWallpaper.jpg");
		this.setLayout(null);
		JButton goOnMatch = new javax.swing.JButton();
		goOnMatch.setBounds(380, 250, 180, 60);
		goOnMatch.setIcon(new javax.swing.ImageIcon(getClass().getResource("images/start.jpg")));
		goOnMatch.addActionListener(new ActionListener() {
		
			@Override
			public void actionPerformed(ActionEvent e) {

				if (startConnection(serverIP)) {
					System.out.println("RICHIESTA INVIATA!");
	
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
		this.waitingLabel = new JLabel("Wait other players...");
		this.waitingLabel.setFont(new java.awt.Font("Helvetica", 0, 18));
		this.waitingLabel.setBounds(330, 420, 300, 50);
		this.waitingLabel.setVisible(false);
		this.add(waitingLabel);
		this.updateUI();

	}
	
	private boolean startConnection(String serverIP) {
		System.out.println("rmi://" + serverIP + "/RMILudoServer");
		try {
			RegisterInterface server = (RegisterInterface) Naming.lookup("rmi://" +serverIP + "/RMILudoServer");
			/* get the ip */
			String ipAddress;
			ipAddress = Inet4Address.getLocalHost().getHostAddress();
			long timeToStart = server.register(serverIP);
			return true;
		} catch (UnknownHostException | RemoteException | MalformedURLException | NotBoundException e) {
			e.printStackTrace();
			return false;
		}
		
	}

}