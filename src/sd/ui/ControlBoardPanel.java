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
import sd.core.Move;
import sd.util.Constants;

public class ControlBoardPanel extends BGPanel {

	private static final long serialVersionUID = 1L;
	private GamePanel gamePanel;
	private CoreGame coreGame;
	private List<JButton> currentPlayer;
	private JLabel timeOfTurn;
	private JLabel round;
	private long countdown;
	private BufferedImage[][] exactDieFaces; /*
											 * facce col risultato esatto del
											 * dado
											 */
	private BufferedImage[] animationBuffer; /*
											 * facce da mostrare durante la
											 * rotazione del dado
											 */
	private JButton die;

	public ControlBoardPanel(GamePanel gamePanel, CoreGame coreGame) {
		super("images/desk.jpg");
		// this.setOpaque(true);
		this.setLayout(null);
		this.gamePanel = gamePanel;
		this.coreGame = coreGame;
		this.countdown = Constants.MAX_WAIT_FOR_TURN;
		
		JLabel colorIntro = new JLabel("Your color:");
		colorIntro.setBounds(10, 20, 185, 25);
		colorIntro.setFont(new java.awt.Font("Helvetica", Font.BOLD, 18));
		colorIntro.setForeground(Color.WHITE);
		this.add(colorIntro);
		
		JButton color = new JButton();
		color.setBounds(10, 50, 30, 30);
		color.setIcon(new javax.swing.ImageIcon(getClass().getResource(
				"images/box/on/" + this.coreGame.getCurrentPartecipant().getColor() + ".png")));
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
		this.setTimer();

		JLabel roundIntro = new JLabel("Round:");
		roundIntro.setBounds(10, 140, 185, 25);
		roundIntro.setFont(new java.awt.Font("Helvetica", Font.BOLD, 18));
		roundIntro.setForeground(Color.WHITE);
		this.add(roundIntro);

		round = new JLabel();
		round.setBounds(10, 170, 185, 25);
		round.setFont(new java.awt.Font("Helvetica", 0, 18));
		round.setForeground(Color.LIGHT_GRAY);
		this.add(round);
		this.setRound();

		JLabel playerConnectedIntro = new JLabel("Current player:");
		playerConnectedIntro.setBounds(10, 200, 185, 25);
		playerConnectedIntro.setFont(new java.awt.Font("Helvetica", Font.BOLD,
				18));
		playerConnectedIntro.setForeground(Color.WHITE);
		this.add(playerConnectedIntro);

		this.initRound();
		this.setPlayerConnected();

		final JPanel containerDie = new JPanel();
		containerDie.setBorder(BorderFactory.createTitledBorder(null,
				"Container die", 0, 0, null, new java.awt.Color(0, 0, 0)));
		containerDie.setBounds(10, 280, 185, 150);
		this.add(containerDie);

		die = new JButton("Launch die");
		die.setBounds(10, 450, 185, 25);
		die.addActionListener(new ActionListener() {
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
		die.setEnabled(false);
		this.add(die);

		JButton fold = new JButton("Fold");
		fold.setBounds(10, 480, 185, 25);
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
	
	public void enableTurn() {
		die.setEnabled(true);
	}

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

	private BufferedImage[] initAnimationBuffer() {

		BufferedImage[] result = new BufferedImage[Constants.ROTATIONS];

		int rowSprite, colSprite;
		Random random = new Random();

		for (int i = 0; i < Constants.ROTATIONS; i++) {
			rowSprite = random.nextInt(9);

			if (rowSprite == 0 || rowSprite == 8)
				colSprite = 0;
			else
				colSprite = random.nextInt(16);

			result[i] = DieSprite.getSprite(colSprite, rowSprite);
		}

		return result;

	}

	private void startAnimationDie(JPanel container) {
		int animationSpeed = 40;
		// These are animation states
		AnimationSprite move = new AnimationSprite(this.animationBuffer,
				animationSpeed);
		// This is the actual animation
		AnimationSprite animation = move;
		animation.start();
		for (int counter = 0; counter < animationSpeed * 100; counter++) {
			animation.update();
			paint(container.getGraphics(), animation.getSprite(), animation
					.getSprite().getWidth(), animation.getSprite().getHeight());
		}

		int launchResult = coreGame.getDie();
		
		System.out.println(launchResult);

		/* showing final face of the die, according to the launch result */
		AnimationSprite resultAnimation = new AnimationSprite(
				this.exactDieFaces[launchResult - 1], 6);
		resultAnimation.start();

		// for (int counter = 0; counter < animationSpeed * 100; counter++) {
		resultAnimation.update();
		paint(container.getGraphics(), resultAnimation.getSprite(),
				resultAnimation.getSprite().getWidth(), resultAnimation
						.getSprite().getHeight());
		// }
		
		this.executeTurn(launchResult);
		
	}

	private void initRound() {
		currentPlayer = new ArrayList<JButton>();
		for (int i = 0; i < Constants.COLOR.length; i++) {
			JButton button = new JButton();
			button.setBounds(5 + (i * 33), 235, 30, 30);
			button.setOpaque(true);
			button.setBorder(null);
			button.setFocusPainted(false);
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
			currentPlayer.add(button);
			this.add(button);
		}
	}

	private void setPlayerConnected() {
		for (int i = 0; i < currentPlayer.size(); i++) {
			if (i != this.coreGame.getRound()) {
				currentPlayer.get(i).setIcon(new javax.swing.ImageIcon(getClass().getResource(
						"images/box/off/" + Constants.COLOR[i] + ".png")));
			} else {
				currentPlayer.get(i).setIcon(new javax.swing.ImageIcon(getClass().getResource(
						"images/box/on/" + Constants.COLOR[i] + ".png")));
			}
		}
	}

	public void setRound() {
		round.setText(String.valueOf(this.coreGame.getRound()));
	}

	public void setTimer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (countdown > 0) {
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
				setRound();
				updateUI();
				// TODO Chiamata al prossimo
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
	
	private void executeTurn(int resultDie) {
		List<Move> possibleMoves = coreGame.initTurn(resultDie);
		System.out.println(possibleMoves.get(0).getDestination().getRow()+", "+possibleMoves.get(0).getDestination().getColumn());
		/* update GUI here showing possible moves passing the list above */
		this.gamePanel.setPossibleMovesStartingFrom(possibleMoves);
	}

}