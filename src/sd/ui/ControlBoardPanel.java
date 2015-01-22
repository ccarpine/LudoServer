package sd.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sd.core.CoreGame;
import sd.util.Constants;

public class ControlBoardPanel extends BGPanel {

	private static final long serialVersionUID = 1L;
	private CoreGame coreGame;
	private boolean dieLaunched;
	private JLabel playerConnected;
	private JLabel timeOfTurn;
	private JLabel round;
	private long countdown;
	
	public ControlBoardPanel(CoreGame coreGame) {
		super("images/desk.jpg");
		//this.setOpaque(true);
		this.setLayout(null);
		this.coreGame = coreGame;
		this.dieLaunched = false;
		this.countdown = Constants.MAX_WAIT_FOR_TURN;
		
		JLabel timeOfTurnIntro = new JLabel("Time of turn:");
		timeOfTurnIntro.setBounds(10, 20, 180, 25);
		timeOfTurnIntro.setFont(new java.awt.Font("Helvetica", Font.BOLD, 18));
		timeOfTurnIntro.setForeground(Color.WHITE);
		this.add(timeOfTurnIntro);
		
		timeOfTurn = new JLabel();
		timeOfTurn.setBounds(10, 50, 180, 25);
		timeOfTurn.setFont(new java.awt.Font("Helvetica", 0, 18));
		timeOfTurn.setForeground(Color.LIGHT_GRAY);
		this.add(timeOfTurn);
		this.setTimer();
		
		JLabel playerConnectedIntro = new JLabel("Current player:");
		playerConnectedIntro.setBounds(10, 100, 180, 25);
		playerConnectedIntro.setFont(new java.awt.Font("Helvetica", Font.BOLD, 18));
		playerConnectedIntro.setForeground(Color.WHITE);
		this.add(playerConnectedIntro);
		
		playerConnected = new JLabel("Player 1");
		playerConnected.setBounds(10, 130, 180, 25);
		playerConnected.setFont(new java.awt.Font("Helvetica", 0, 18));
		playerConnected.setForeground(Color.LIGHT_GRAY);
		this.add(playerConnected);
		
		JLabel roundIntro = new JLabel("Round:");
		roundIntro.setBounds(10, 160, 180, 25);
		roundIntro.setFont(new java.awt.Font("Helvetica", Font.BOLD, 18));
		roundIntro.setForeground(Color.WHITE);
		this.add(roundIntro);
		
		final JPanel containerDie = new JPanel();
		containerDie.setBorder(BorderFactory.createTitledBorder(null,
				"Container die", 0, 0, null, 
				new java.awt.Color(0, 0, 0)));
		containerDie.setBounds(10, 280, 180, 150);
		this.add(containerDie);
		
		JButton die = new JButton("Launch die");
		die.setBounds(10, 450, 180, 25);
		die.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!dieLaunched) {
					dieLaunched = true;
					new Thread(new Runnable() {
						@Override
						public void run() {
							startAnimationDie(containerDie);
						}
					}).start();
				} else {
					JOptionPane.showMessageDialog(null, "You have already launched the die.");
				}
			}
		});
		this.add(die);
		
		JButton fold = new JButton("Fold");
		fold.setBounds(10, 480, 180, 25);
		fold.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		this.add(fold);
	}
	
	private void startAnimationDie(JPanel container) {
		int animationSpeed = 40;
		BufferedImage[] animationBuffer = {DieSprite.getSprite(0, 1), DieSprite.getSprite(2, 1)};
		// These are animation states
		AnimationSprite move = new AnimationSprite(animationBuffer, animationSpeed);

		// This is the actual animation
		AnimationSprite animation = move;
		animation.start();
		for (int counter=0; counter<animationSpeed*100; counter++) {
			animation.update();
			paint(container.getGraphics(), animation.getSprite(), animation.getSprite().getWidth(), animation.getSprite().getHeight());
		}
		dieLaunched = false;
	}
	
	public void setPlayerConnected(String player) {
		this.playerConnected.setText(player);
	}
	
	public void setTimer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (countdown > 0) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					countdown--;
					int seconds = (int) countdown % 60;
					int minutes = (int) ((countdown / 60) % 60);
					timeOfTurn.setText(String.format("%02d", minutes)+":"+String.format("%02d", seconds));
				}
				// Chiamata al prossimo
			}
		}).start();
	}
	
	public void paint(Graphics g, BufferedImage dieSprite, int x, int y) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(dieSprite, x, y, this);
        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

}