package sd.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sd.core.CoreGame;
import sd.core.player.UserPlayer;
import sd.util.Constants;

public class ControlBoardPanel extends BGPanel {

	private static final long serialVersionUID = 1L;
	private UserPlayer userPlayer;
	private CoreGame coreGame;
	private List<JButton> currentPlayer;
	private JLabel timeOfTurn;
	private JLabel round;
	private long countdown;
	private BufferedImage[][] exactDieFaces; // faces with the exact result of the die faces
	private BufferedImage[] animationBuffer; // faces to use during the rolling of the die
	private JButton die;

	/**
	 * 
	 * @param gamePanel
	 * @param coreGame
	 */
	public ControlBoardPanel(CoreGame coreGame, UserPlayer userPlayer) {
		super("images/desk.jpg");
		this.setLayout(null);
		this.coreGame = coreGame;
		this.userPlayer = userPlayer;
		this.countdown = Constants.MAX_WAIT_FOR_TURN;
		JLabel colorIntro = new JLabel("Your color:");
		colorIntro.setBounds(10, 20, 185, 25);
		colorIntro.setFont(new java.awt.Font("Helvetica", Font.BOLD, 18));
		colorIntro.setForeground(Color.WHITE);
		this.add(colorIntro);
		JButton color = new JButton();
		color.setBounds(10, 45, 30, 30);
		color.setIcon(new javax.swing.ImageIcon(ClassLoader.getSystemResource(
				"sd/ui/images/box/on/" + this.coreGame.getMyPartecipant().getColor() + ".png")));
		color.setBorder(null);
		color.setFocusPainted(false);
		color.setBorderPainted(false);
		color.setContentAreaFilled(false);
		this.add(color);
		JLabel timeOfTurnIntro = new JLabel("Time of turn:");
		timeOfTurnIntro.setBounds(10, 80, 185, 25);
		timeOfTurnIntro.setFont(new java.awt.Font("Helvetica", Font.BOLD, 18));
		timeOfTurnIntro.setForeground(Color.WHITE);
		this.add(timeOfTurnIntro);
		timeOfTurn = new JLabel();
		timeOfTurn.setBounds(10, 110, 185, 25);
		timeOfTurn.setFont(new java.awt.Font("Helvetica", 0, 18));
		timeOfTurn.setForeground(Color.LIGHT_GRAY);
		this.add(timeOfTurn);
		JLabel roundIntro = new JLabel("Round:");
		roundIntro.setBounds(10, 140, 185, 25);
		roundIntro.setFont(new java.awt.Font("Helvetica", Font.BOLD, 18));
		roundIntro.setForeground(Color.WHITE);
		this.add(roundIntro);
		round = new JLabel(String.valueOf(this.coreGame.getRound()));
		round.setBounds(10, 170, 185, 25);
		round.setFont(new java.awt.Font("Helvetica", 0, 18));
		round.setForeground(Color.LIGHT_GRAY);
		this.add(round);
		JLabel playerConnectedIntro = new JLabel("Current player:");
		playerConnectedIntro.setBounds(10, 200, 185, 25);
		playerConnectedIntro.setFont(new java.awt.Font("Helvetica", Font.BOLD,
				18));
		playerConnectedIntro.setForeground(Color.WHITE);
		this.add(playerConnectedIntro);
		this.initRound();
		final JPanel containerDie = new JPanel();
		containerDie.setBorder(BorderFactory.createTitledBorder(null,
				"Container die", 0, 0, null, new java.awt.Color(0, 0, 0)));
		containerDie.setBounds(10, 280, 185, 150);
		this.add(containerDie);
		this.die = new JButton("Launch die");
		this.die.setBounds(10, 440, 185, 25);
		this.die.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				die.setEnabled(false);
				new Thread(new Runnable() {
					@Override
					public void run() {
						startAnimationDie(containerDie);
					}
				}).start();
			}
		});
		this.die.setEnabled(false);
		this.add(die);
		JButton fold = new JButton("Fold");
		fold.setBounds(10, 470, 185, 25);
		fold.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		this.add(fold);
		this.animationBuffer = this.initAnimationBuffer();
		this.exactDieFaces = this.initExactDieFaces();
	}
	
	/**
	 * enable the button for launch die
	 */
	public void enableTurn() {
		System.out.println("Abilito il tasto per tirare il dado");
		this.die.setEnabled(true);
		this.setTimer();
	}

	/**
	 * Add the die in the panel in a specific position
	 * @param g 
	 * @param dieSprite, buffered image of the die
	 * @param x, orizontal position in the panel
	 * @param y, vertical position in the panel
	 */
	public void paint(Graphics g, BufferedImage dieSprite, int x, int y) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(dieSprite, x, y, this);
		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}
	
	/**
	 * 
	 * @return multidimensional array of buffered images for the 6 final image representing the die luaunch
	 */
	private BufferedImage[][] initExactDieFaces() {
		BufferedImage[][] result = new BufferedImage[6][1];
		result[0][0] = DieSprite.getSprite(0, 4); /* die face 1 */
		result[1][0] = DieSprite.getSprite(4, 4); /* die face 2 */
		result[2][0] = DieSprite.getSprite(0, 8); /* die face 3 */
		result[3][0] = DieSprite.getSprite(0, 0); /* die face 4 */
		result[4][0] = DieSprite.getSprite(12, 4); /* die face 5 */
		result[5][0] = DieSprite.getSprite(8, 4); /* die face 6 */
		return result;
	}

	/**
	 * 
	 * @return create array of buffered image with all possibile image for the die while is routing
	 */
	private BufferedImage[] initAnimationBuffer() {
		BufferedImage[] result = new BufferedImage[Constants.ROTATIONS];
		int rowSprite, colSprite;
		Random random = new Random();
		for (int i=0; i<Constants.ROTATIONS; i++) {
			rowSprite = random.nextInt(9);
			if (rowSprite == 0 || rowSprite == 8)
				colSprite = 0;
			else
				colSprite = random.nextInt(16);
			result[i] = DieSprite.getSprite(colSprite, rowSprite);
		}
		return result;
	}

	/**
	 * make the animation of launch die in a specific container and set the
	 * possible destination for the result in the game panel
	 * @param Jpanel, the container for the die animation 
	 */
	private void startAnimationDie(JPanel panel) {
		int animationSpeed = 40;
		// This is the actual animation
		AnimationSprite animation = new AnimationSprite(this.animationBuffer,animationSpeed);
		animation.start();
		for (int counter=0; counter<animationSpeed*100; counter++) {
			animation.update();
			paint(panel.getGraphics(), animation.getSprite(), animation.getSprite().getWidth(), animation.getSprite().getHeight());
		}
		int launchResult = coreGame.launchDie();
		/* showing final face of the die, according to the launch result */
		AnimationSprite resultAnimation = new AnimationSprite(this.exactDieFaces[launchResult-1], 6);
		resultAnimation.start();
		resultAnimation.update();
		paint(panel.getGraphics(), resultAnimation.getSprite(),
				resultAnimation.getSprite().getWidth(), resultAnimation
						.getSprite().getHeight());
		this.userPlayer.getGamePanel().makePossibleMoveFlash(launchResult);
	}

	/**
	 * set the icon for all the player
	 */
	private void initRound() {
		currentPlayer = new ArrayList<JButton>();
		for (int i=0; i<Constants.COLOR.length; i++) {
			JButton button = new JButton();
			button.setBounds(5 + (i * 33), 230, 30, 30);
			button.setOpaque(true);
			button.setBorder(null);
			button.setFocusPainted(false);
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
			currentPlayer.add(button);
			this.add(button);
		}
	}

	/**
	 * set the icon for the current player as on, the other as off
	 */
	public void setPlayerConnected() {
		for (int i=0; i<this.currentPlayer.size(); i++) {
			if (Constants.COLOR[i] != this.coreGame.getMyPartecipant().getColor()) {
				this.currentPlayer.get(i).setIcon(new javax.swing.ImageIcon(ClassLoader.getSystemResource("sd/ui/images/box/off/" + Constants.COLOR[i] + ".png")));
			} else {
				this.currentPlayer.get(i).setIcon(new javax.swing.ImageIcon(ClassLoader.getSystemResource("sd/ui/images/box/on/" + Constants.COLOR[i] + ".png")));
			}
		}
	}

	/**
	 * set the timer for the turn of single player. 
	 * When timer end the turn pass to the next player
	 */
	private void setTimer() {
		System.out.println("Abilito il timer");
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (countdown > 0 && coreGame.isTurnActive()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					countdown--;
					int seconds = (int) countdown % 60;
					int minutes = (int) ((countdown / 60) % 60);
					timeOfTurn.setText(String.format("%02d", minutes) + ":"
							+ String.format("%02d", seconds));
				}
				die.setEnabled(false);
				setPlayerConnected();
				if (coreGame.isTurnActive()) {
					coreGame.setTurnActive(false);
					userPlayer.getGamePanel().makePossibleMoveDisable();
					userPlayer.updateNext(coreGame.getPartecipants(), coreGame.getGameBoard(), coreGame.getMyPartecipant().getIp());
				}
			}
		}).start();
	}

}