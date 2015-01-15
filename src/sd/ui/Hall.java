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

public class Hall extends JFrame {

	private static final long serialVersionUID = 1L;
	private JLabel waitingLabel;

	public Hall(final String serverIP) {
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
		
		this.setResizable(false);
		this.setSize(600, 500);

		

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

				startConnection(serverIP);
				System.out.println("RICHIESTA INVIATA!");

				waitingLabel.setVisible(true);
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (true) {
							try {
								Thread.sleep(500);
								waitingLabel.setVisible(!waitingLabel
										.isVisible());
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
		exit.setIcon(new
		javax.swing.ImageIcon(getClass().getResource("images/exit.jpg")));
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
		panel.updateUI();

	}
	
	private void startConnection(String serverIP) {
		try {
			UserPlayerInterface client = (UserPlayerInterface) new UserPlayer(this);
			/* get the ip */
			String ipAddress = Inet4Address.getLocalHost()
					.getHostAddress();
			Naming.rebind("//" + ipAddress + "/RMIGameClient", client);
			RegisterInterface server = (RegisterInterface) Naming
					.lookup("rmi://" + serverIP + "/RMILudoServer");

			long timeToStart = server.register(ipAddress);
			System.out.println("CLIENT ---- time to start:"
					+ timeToStart);
			System.out.println("CLIENT ---- Ip address:" + ipAddress);
			System.out.println("CLIENT ---- Ip address:" + ipAddress);
		} catch (NotBoundException | UnknownHostException
				| RemoteException | MalformedURLException exc) {
			exc.printStackTrace();
		}
	}

}
