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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import sd.core.register.RegisterInterface;
import sd.util.Constants;
import sd.util.MyFont;
import sun.util.locale.provider.LocaleResources;

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
	public IntroPanel() {
		super("images/intro.jpg");
		this.setLayout(null);
		JLabel ipServer = new JLabel("IP Server");
		ipServer.setFont(new MyFont().getMyFont(Font.PLAIN, 12));
		ipServer.setForeground(Color.WHITE);
		ipServer.setBounds(425, 245, 90, 25);
		this.add(ipServer);
		final JTextField ipServerField = new JTextField(Constants.IP_SERVER);
		ipServerField.setBounds(390, 270, 150, 20);
		ipServerField.setHorizontalAlignment(JTextField.CENTER);
		this.add(ipServerField);
		final JButton goOnMatch = new javax.swing.JButton();
		goOnMatch.setBounds(380, 300, 170, 50);
		goOnMatch.setIcon(new javax.swing.ImageIcon(ClassLoader.getSystemResource("sd/ui/images/start.png")));
		goOnMatch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				goOnMatch.setEnabled(false);
				// try the connection to the server
				if (!startConnection(ipServerField.getText())) {
					JOptionPane.showMessageDialog(null,"Server at "+ipServerField.getText()+" is out of service. Try later!");
					goOnMatch.setEnabled(true);
				} else {
					startCountdown();
				}
			}
		});
		this.add(goOnMatch);
		JButton exit = new javax.swing.JButton();
		exit.setBounds(380, 360, 170, 50);
		exit.setIcon(new javax.swing.ImageIcon(ClassLoader.getSystemResource("sd/ui/images/exit.png")));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				exit(ipServerField.getText());
			}
		});
		this.add(exit);
		this.countdown = new JLabel();
		this.countdown.setBounds(50, 405, 300, 30);
		this.countdown.setFont(new MyFont().getMyFont(Font.PLAIN, 14));
		this.countdown.setForeground(Color.WHITE);
		this.countdown.setVisible(false);
		this.add(countdown);
		this.waitingLabel = new JLabel("Wait other players");
		this.waitingLabel.setFont(new MyFont().getMyFont(Font.PLAIN, 14));
		this.waitingLabel.setBounds(30, 420, 300, 50);
		this.waitingLabel.setForeground(Color.WHITE);
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
			Registry registry = LocateRegistry.getRegistry(6000);
			RegisterInterface server = (RegisterInterface) registry
					.lookup("rmi://" + serverIP + "/RMILudoServer");
			this.timeToStart = server.register(Inet4Address.getLocalHost()
					.getHostAddress());
			return true;
		} catch (RemoteException | /*MalformedURLException | */NotBoundException
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
					}
				}
				countdown.setText("Starting game!");
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
					}
				}
				waitingLabel.setVisible(false);
			}
		}).start();
	}
	
	private void exit(String ipServer) {
		try {
			RegisterInterface server = (RegisterInterface) Naming.lookup("rmi://" + ipServer + "/RMILudoServer");
			server.deletePartecipant(Inet4Address.getLocalHost().getHostAddress());
		} catch (RemoteException | MalformedURLException | UnknownHostException | NotBoundException exc) {
			exc.printStackTrace();
		} finally {
			System.exit(0);
		}
	}
	
}