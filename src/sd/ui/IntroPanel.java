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
import javax.swing.JOptionPane;

import sd.core.register.RegisterInterface;
import sd.util.MyFont;

public class IntroPanel extends BGPanel {

	private static final long serialVersionUID = 1L;
	private JLabel waitingLabel;
	private JLabel countdown;
	private long timeToStart;

	/**
	 * create the intro panel
	 * 
	 * @param serverIP
	 *            , the ip of the server
	 */
	public IntroPanel(final String serverIP) {
		super("images/startWallpaper.jpg");
		this.setLayout(null);
		JLabel ludo2 = new JLabel();
		ludo2.setBounds(390, 30, 150, 220);
		ludo2.setIcon(new javax.swing.ImageIcon(ClassLoader
				.getSystemResource("sd/ui/images/angry2.png")));
		this.add(ludo2);
		JLabel ludo3 = new JLabel();
		ludo3.setBounds(115, 260, 200, 220);
		ludo3.setIcon(new javax.swing.ImageIcon(ClassLoader
				.getSystemResource("sd/ui/images/angry1.png")));
		this.add(ludo3);
		final JButton goOnMatch = new javax.swing.JButton();
		goOnMatch.setBounds(380, 280, 170, 50);
		goOnMatch.setIcon(new javax.swing.ImageIcon(ClassLoader.getSystemResource("sd/ui/images/start.png")));
		goOnMatch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goOnMatch.setEnabled(false);
				// try the connection to the server
				if (!startConnection(serverIP)) {
					JOptionPane.showMessageDialog(null,"Server is out of service. Try later!");
					System.exit(0);
				} else {
					startCountdown();
				}
			}
		});
		this.add(goOnMatch);
		JButton exit = new javax.swing.JButton();
		exit.setBounds(380, 340, 170, 50);
		exit.setIcon(new javax.swing.ImageIcon(ClassLoader.getSystemResource("sd/ui/images/exit.png")));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					RegisterInterface server = (RegisterInterface) Naming
							.lookup("rmi://" + serverIP + "/RMILudoServer");
					server.deletePartecipant(Inet4Address.getLocalHost()
							.getHostAddress());
				} catch (RemoteException | MalformedURLException
						| UnknownHostException | NotBoundException exc) {
					exc.printStackTrace();
				}
				System.exit(0);
			}
		});
		this.add(exit);
		this.countdown = new JLabel();
		this.countdown.setBounds(370, 405, 300, 30);
		this.countdown.setFont(new MyFont().getMyFont(Font.PLAIN, 18));
		this.countdown.setForeground(Color.BLACK);
		this.countdown.setVisible(false);
		this.add(countdown);
		this.waitingLabel = new JLabel("Wait other players");
		this.waitingLabel.setFont(new MyFont().getMyFont(Font.PLAIN, 18));
		this.waitingLabel.setBounds(340, 420, 300, 50);
		this.waitingLabel.setVisible(false);
		this.add(waitingLabel);
	}

	/**
	 * start the connection with the server
	 * 
	 * @param serverIP
	 *            , the ip of the server
	 * @return boolean, the result of the connection to the server
	 */
	private boolean startConnection(String serverIP) {
		try {
			RegisterInterface server = (RegisterInterface) Naming
					.lookup("rmi://" + serverIP + "/RMILudoServer");
			this.timeToStart = server.register(Inet4Address.getLocalHost()
					.getHostAddress());
			return true;
		} catch (RemoteException | MalformedURLException | NotBoundException
				| UnknownHostException e) {
			return false;
		}
	}

	/**
	 * launch a thread to show the countdown related to the start of the match
	 */
	private void startCountdown() {
		this.countdown.setVisible(true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (timeToStart > 0) {
					int seconds = (int) (timeToStart / 1000);
					countdown.setText("Start in "+String.valueOf(seconds)+" sec");
					try {
						Thread.sleep(1000);
						timeToStart -= 1000;

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (timeToStart > 0) {
					waitingLabel.setVisible(!waitingLabel.isVisible());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
}