package sd.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import sd.core.Cell;
import sd.core.CoreGame;
import sd.core.player.UserPlayer;
import sd.util.Constants;

public class ControlBoardPanel extends BGPanel {

	private static final long serialVersionUID = 1L;
	private UserPlayer userPlayer;
	private CoreGame coreGame;
	private List<CellButton> currentPlayer;
	private JLabel timeOfTurn;
	private long countdown;
	private JButton die;

	/**
	 * 
	 * @param gamePanel
	 * @param coreGame
	 */
	public ControlBoardPanel(CoreGame coreGame, UserPlayer userPlayer) {
		super("images/desk.jpg");
		this.setOpaque(true);
		this.setLayout(null);
		this.coreGame = coreGame;
		this.userPlayer = userPlayer;
		this.countdown = Constants.MAX_TIME_FOR_TURN;
		this.drawControlBoardGUI(false);
	}

	public void drawControlBoardGUI(boolean isDoubleTurn) {
		this.removeAll();
		this.updateUI();
		JLabel colorIntro = new JLabel("You");
		colorIntro.setBounds(10, 10, 185, 25);
		colorIntro.setFont(new java.awt.Font("Helvetica", 0, 16));
		colorIntro.setForeground(Color.WHITE);
		this.add(colorIntro);
		JButton color = new JButton();
		color.setBounds(10, 35, 30, 30);
		color.setIcon(new javax.swing.ImageIcon(ClassLoader
				.getSystemResource("sd/ui/images/turnMarkers/on/SMALL_"
						+ this.coreGame.getMyPartecipant().getColor() + ".png")));
		color.setBorder(null);
		color.setFocusPainted(false);
		color.setBorderPainted(false);
		color.setContentAreaFilled(false);
		this.add(color);
		this.timeOfTurn = new JLabel("Wait...");
		this.timeOfTurn.setBounds(120, 20, 185, 25);
		this.timeOfTurn.setFont(new java.awt.Font("Helvetica", 0, 18));
		this.timeOfTurn.setForeground(Color.WHITE);
		this.add(timeOfTurn);
		JLabel playerConnectedIntro = new JLabel("Player");
		playerConnectedIntro.setBounds(10, 75, 185, 25);
		playerConnectedIntro.setFont(new java.awt.Font("Helvetica", 0, 16));
		playerConnectedIntro.setForeground(Color.WHITE);
		this.add(playerConnectedIntro);
		final JPanel containerDie = new JPanel();
		containerDie.setBorder(BorderFactory.createTitledBorder(null,
				"Container die", 0, 0, null, new java.awt.Color(0, 0, 0)));
		containerDie.setBounds(10, 245, 170, 130);
		this.add(containerDie);
		this.die = new JButton("Launch die");
		this.die.setBounds(12, 385, 180, 50);
		this.die.setIcon(new javax.swing.ImageIcon(ClassLoader
				.getSystemResource("sd/ui/images/launch.png")));
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
		fold.setIcon(new javax.swing.ImageIcon(ClassLoader
				.getSystemResource("sd/ui/images/exit.jpg")));
		fold.setBounds(12, 440, 180, 50);
		fold.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		this.add(fold);
		this.setPlayerConnected(isDoubleTurn);
		this.updateUI();
	}

	/**
	 * enable the button for launch die
	 */
	public void enableTurn() {
			System.out.println("Sono dentro enableTurn()");
			this.die.setEnabled(true);
			this.setTimer();
	}

	/**
	 * Add the die in the panel in a specific position
	 * 
	 * @param g
	 * @param dieSprite
	 *            , buffered image of the die
	 * @param x
	 *            , orizontal position in the panel
	 * @param y
	 *            , vertical position in the panel
	 */
	/*
	 * public void paint(Graphics g, BufferedImage dieSprite, int x, int y) {
	 * super.paintComponents(g); Graphics2D g2d = (Graphics2D) g;
	 * g2d.drawImage(dieSprite, x, y, this); Toolkit.getDefaultToolkit().sync();
	 * g.dispose(); }
	 */

	/**
	 * 
	 * @return multidimensional array of buffered images for the 6 final image
	 *         representing the die luaunch
	 */
	private BufferedImage[][] initExactDieFaces() {
		BufferedImage[][] result = new BufferedImage[6][1];
		result[0][0] = DieSprite.getSprite(0, 0); /* die face 1 */
		result[1][0] = DieSprite.getSprite(1, 1); /* die face 2 */
		result[2][0] = DieSprite.getSprite(2, 2); /* die face 3 */
		result[3][0] = DieSprite.getSprite(3, 3); /* die face 4 */
		result[4][0] = DieSprite.getSprite(4, 4); /* die face 5 */
		result[5][0] = DieSprite.getSprite(5, 5); /* die face 6 */
		return result;
	}

	/**
	 * 
	 * @return create array of buffered image with all possibile image for the
	 *         die while is routing
	 */
	private BufferedImage[] initAnimationBuffer() {
		BufferedImage[] result = new BufferedImage[Constants.ROTATIONS];
		int rowSprite, colSprite;
		Random random = new Random();
		for (int i = 0; i < Constants.ROTATIONS; i++) {
			rowSprite = 1 + random.nextInt(5);
			colSprite = 1 + random.nextInt(5);
			result[i] = DieSprite.getSprite(colSprite, rowSprite);
		}
		return result;
	}

	/**
	 * make the animation of launch die in a specific container and set the
	 * possible destination for the result in the game panel
	 * 
	 * @param Jpanel
	 *            , the container for the die animation
	 */
	private void startAnimationDie(JPanel panel) {
		BufferedImage[] animationBuffer = this.initAnimationBuffer();
		BufferedImage[][] exactDieFaces = this.initExactDieFaces();
		int animationSpeed = 40;
		// int launchResult = coreGame.getCurrentDie();
		int launchResult = Integer.parseInt(JOptionPane.showInputDialog(null,
				"What's your name?"));// coreGame.launchDie();

		/*
		 * I become the current partecipant only AFTER having launched the die,
		 * because of the call to initTurn()
		 */
		this.coreGame.getMyPartecipant().setLastLaunch(launchResult);
		// This is the actual animation
		AnimationSprite animation = new AnimationSprite(animationBuffer,
				animationSpeed);
		animation.start();
		JLabel resultDie = new JLabel();
		resultDie.setBounds(60, 265, Constants.DIE_SIZE, Constants.DIE_SIZE);
		for (int counter = 0; counter < animationSpeed * 100; counter++) {
			animation.update();
			panel.removeAll();
			panel.updateUI();
			// TODO start

			resultDie.setIcon(new ImageIcon(animationBuffer[counter
					% Constants.ROTATIONS]));
			panel.add(resultDie);
			// System.out.println("OK 1");
			// TODO end
			// paint(panel.getGraphics(), animation.getSprite(),
			// animation.getSprite().getWidth(),
			// animation.getSprite().getHeight());
		}
		// showing final face of the die, according to the launch result
		panel.removeAll();
		panel.updateUI();
		AnimationSprite resultAnimation = new AnimationSprite(
				exactDieFaces[launchResult - 1], 6);
		resultAnimation.start();
		resultAnimation.update();
		// TODO start
		resultDie.setIcon(new ImageIcon(exactDieFaces[launchResult - 1][0]));
		resultDie.setBounds(60, 265, Constants.DIE_SIZE, Constants.DIE_SIZE);
		panel.add(resultDie);
		// System.out.println("OK 2");
		// TODO end
		// paint(panel.getGraphics(), resultAnimation.getSprite(),
		// resultAnimation.getSprite().getWidth(),
		// resultAnimation.getSprite().getHeight());
		this.userPlayer.getGamePanel().makePossibleMoveFlash();
	}

	/**
	 * set the icon for all the player
	 */
	// TODO sbaglia a settare dado
	private void initRound() {
		this.currentPlayer = new ArrayList<CellButton>();
		for (int i = 0; i < this.coreGame.getPartecipants().size(); i++) {

			/* a player might haven exited during the building of the GUI */
			CellButton button = new CellButton(0, 0, "images/turnMarkers/on/"
					+ Constants.COLOR[i] + ".png", new Cell(Constants.COLOR[i],
					0, 0));
			button.setBounds(5 + (i * 33), 100, 20, 20);
			this.currentPlayer.add(button);
			this.add(button);
			JLabel lastDie = new JLabel();
			lastDie.setBounds(5 + (i * 33), 125, 20, 20);
			int lastLaunch = this.coreGame.getPartecipants().get(i)
					.getLastLaunch();
			if (lastLaunch > 0) {
				lastDie.setIcon(new javax.swing.ImageIcon(
						ClassLoader
								.getSystemResource("sd/ui/images/dice/"
										+ this.coreGame.getPartecipants()
												.get(i).getColor() + "_"
										+ lastLaunch + ".png")));

				this.add(lastDie);
			}
		}
	}

	/**
	 * set the icon for the current player as on, the other as off
	 */
	private void setPlayerConnected(boolean isDoubleTurn) {
		this.initRound();
		/*
		 * l'istruzione seguente illumina il giocatore corrente nel caso il
		 * turno sia maggiore di zero. Tale giocatore è il seguente di quello
		 * che è arrivato con l'aggiornamento il quale proprio adesso sta
		 * giocando.
		 */
		String color = this.coreGame.getNextActivePartecipant(
				this.coreGame.getCurrentPartecipant().getIp()).getColor();
		if (this.coreGame.getTurn() == 0) {
			color = this.coreGame.getPartecipants()
					.get(this.coreGame.getFirstActiveIndex()).getColor();
		} else if (isDoubleTurn) {
			color = this.coreGame.getCurrentPartecipant().getColor();
		}
		for (int i = 0; i < this.coreGame.getPartecipants().size(); i++) {
			if (this.coreGame.getPartecipants().get(i).isStatusActive()) {
				if (!Constants.COLOR[i].equals(color)) {

					this.currentPlayer
							.get(i)
							.setIcon(
									new javax.swing.ImageIcon(
											ClassLoader
													.getSystemResource("sd/ui/images/turnMarkers/off/"
															+ Constants.COLOR[i]
															+ ".png")));
				} else {
					this.currentPlayer.get(i).changeState();
				}
			}

			else {
				this.currentPlayer.get(i).setVisible(false);
			}
		}

	}

	/**
	 * set the timer for the turn of single player. When timer end the turn pass
	 * to the next player
	 */
	private void setTimer() {
		this.countdown = Constants.MAX_TIME_FOR_TURN;
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
					int seconds = (int) (countdown/1000) % 60;
					int minutes = (int) (( (countdown/1000) / 60) % 60);
					timeOfTurn.setText(String.format("%02d", minutes) + ":"+ String.format("%02d", seconds));
				}
				die.setEnabled(false);
				System.out.println("Faccio l'update next se is turn active " + coreGame.isTurnActive());
				if (coreGame.isTurnActive()) {
					coreGame.setTurnActive(false);
					userPlayer.getGamePanel().makePossibleMoveDisable();
					System.out.println("Faccio l'update next");
					userPlayer.updateNext(coreGame.getPartecipants(), coreGame
							.getGameBoard(), coreGame.getCurrentPartecipant()
							.getIp(), coreGame.isDoubleTurn(), coreGame
							.getTurn());
				}
			}
		}).start();
	}

}
