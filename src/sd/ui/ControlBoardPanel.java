package sd.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import sd.core.Cell;
import sd.core.CoreGame;
import sd.core.player.UserPlayer;
import sd.util.Constants;
import sd.util.MyFont;

public class ControlBoardPanel extends BGPanel {

	private static final long serialVersionUID = 1L;
	private UserPlayer userPlayer;
	private CoreGame coreGame;
	private List<CellButton> currentPlayer;
	private JLabel timeOfTurn;
	private long countdown;
	private JButton die;
	private BufferedImage[] animationBuffer;
	private BufferedImage[][] exactDieFaces;

	/**
	 * @param gamePanel
	 * @param coreGame
	 */
	public ControlBoardPanel(CoreGame coreGame, UserPlayer userPlayer) {
		super("images/table2.jpg");
		this.setOpaque(true);
		this.setLayout(null);
		this.coreGame = coreGame;
		this.userPlayer = userPlayer;
		this.countdown = Constants.MAX_TIME_FOR_TURN;
		this.initAnimationBuffer();
		this.initExactDieFaces();
	}

	/***
	 * draw the right part of the windows, 
	 * enable or disable the correct button and show how is the current player
	 * @param isDoubleTurn, boolean help to choose the current partecipant 
	 */
	public void drawControlBoardGUI(boolean isDoubleTurn) {
		this.removeAll();
		this.updateUI();
		JLabel colorIntro = new JLabel("You");
		colorIntro.setBounds(20, 20, 185, 25);
		colorIntro.setFont(new MyFont().getMyFont(Font.BOLD, 16));
		colorIntro.setForeground(Color.WHITE);
		this.add(colorIntro);
		JButton color = new JButton();
		color.setBounds(20, 48, 30, 30);
		color.setIcon(new javax.swing.ImageIcon(ClassLoader
				.getSystemResource("sd/ui/images/turnMarkers/on/SMALL_"
						+ this.coreGame.getMyPartecipant().getColor() + ".png")));
		color.setBorder(null);
		color.setFocusPainted(false);
		color.setBorderPainted(false);
		color.setContentAreaFilled(false);
		this.add(color);
		this.timeOfTurn = new JLabel("Waiting");
		this.timeOfTurn.setBounds(75, 53, 185, 25);
		this.timeOfTurn.setFont(new MyFont().getMyFont(Font.BOLD, 16));
		this.timeOfTurn.setForeground(Color.WHITE);
		this.add(timeOfTurn);
		JLabel playerConnectedIntro = new JLabel("Player");
		playerConnectedIntro.setBounds(20, 100, 185, 25);
		playerConnectedIntro.setFont(new MyFont().getMyFont(Font.BOLD, 16));
		playerConnectedIntro.setForeground(Color.WHITE);
		this.add(playerConnectedIntro);
		final JPanel containerDie = new JPanel();
		containerDie.setBounds(20, 275, 170, 100);
		containerDie.setOpaque(false);
		this.add(containerDie);
		this.die = new JButton();
		this.die.setBounds(20, 385, 170, 50);
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
		// disable the die if the partecipant has win or if is not his turn
		if (!coreGame.isTurnActive()
				|| coreGame.isVictory(coreGame.getMyPartecipant())) {
			this.die.setEnabled(false);
		}
		this.add(die);
		JButton fold = new JButton();
		fold.setIcon(new javax.swing.ImageIcon(ClassLoader
				.getSystemResource("sd/ui/images/exit.png")));
		fold.setBounds(20, 440, 170, 50);
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
	 * Enable the button for launch die
	 */
	public void enableTurn() {
		this.coreGame.setCurrentPartecipant(this.coreGame.getMyPartecipant()
				.getIp());
		this.setTimer();
	}

	/**
	 * @return multidimensional array of buffered images for the 6 final image
	 *         representing the die luaunch
	 */
	private void initExactDieFaces() {
		this.exactDieFaces = new BufferedImage[6][1];
		String myColor = this.coreGame.getMyPartecipant().getColor();
		for (int i=0; i<6; i++) {
			try {
				this.exactDieFaces[i][0] = ImageIO.read(ClassLoader
						.getSystemResource("sd/ui/images/bigDice/" + myColor
								+ "_" + (i + 1) + ".png"));
			} catch (IOException e) {
			}
		}
	}

	/**
	 * @return create array of buffered image with all possibile image for the
	 *         die while is routing
	 */
	private void initAnimationBuffer() {
		this.animationBuffer = new BufferedImage[36];
		int index = 0;
		for (int i=0; i<Constants.COLOR.length; i++) {
			for (int j=0; j<6; j++) {
				try {
					this.animationBuffer[index] = ImageIO.read(ClassLoader
							.getSystemResource("sd/ui/images/bigDice/"
									+ Constants.COLOR[i] + "_" + (j + 1)
									+ ".png"));
				} catch (IOException e) {
				}
				index++;
			}
		}

	}

	/**
	 * Make the animation of launch die in a specific container and set the
	 * possible destination for the result in the game panel
	 * @param Jpanel, the container for the die animation
	 */
	private void startAnimationDie(final JPanel panel) {
		/**
		 * I become the current partecipant only AFTER having launched the die,
		 * because of the call to initTurn()
		 */
		final int launchResult = coreGame.launchDie();
		coreGame.getMyPartecipant().setLastLaunch(launchResult);
		JLabel resultDie = new JLabel();
		resultDie.setBounds(60, 305, Constants.DIE_SIZE, Constants.DIE_SIZE);
		this.add(resultDie);
		final Runnable makeDieRoll = new Runnable() {
			@Override
			public void run() {
				Timer timer = new Timer(50, new ActionListener() {
					private int counter = 0;
					@Override
					public void actionPerformed(ActionEvent e) {
						if (counter <= Constants.ROTATIONS) {
							counter++;
							panel.removeAll();
							JLabel resultDie = new JLabel();
							resultDie.setBounds(60, 305, Constants.DIE_SIZE,
									Constants.DIE_SIZE);
							panel.add(resultDie);
							if (counter != Constants.ROTATIONS)
								resultDie.setIcon(new ImageIcon(
										animationBuffer[new Random()
												.nextInt(35)]));
							if (counter == Constants.ROTATIONS)
								resultDie.setIcon(new ImageIcon(
										exactDieFaces[launchResult - 1][0]));
						} else {
							((Timer) e.getSource()).stop();
							panel.removeAll();
							JLabel resultDie = new JLabel();
							resultDie.setBounds(60, 305, Constants.DIE_SIZE,
									Constants.DIE_SIZE);
							panel.add(resultDie);
							resultDie.setIcon(new ImageIcon(
									exactDieFaces[launchResult - 1][0]));
							userPlayer.getGamePanel().makePossibleMoveFlash();
						}

					}
				});
				timer.start();
			}
		};
		Thread roller = new Thread() {
			public void run() {
				try {
					SwingUtilities.invokeAndWait(makeDieRoll);
				} catch (Exception e) {
				}
			}
		};
		roller.start();
	}

	/**
	 * set the icon for all the player
	 */
	private void initRound() {
		this.currentPlayer = new ArrayList<CellButton>();
		for (int i=0; i<this.coreGame.getPartecipants().size(); i++) {
			/* a player might haven exited during the building of the GUI */
			CellButton button = new CellButton(0, 0, "images/turnMarkers/on/"
					+ Constants.COLOR[i] + ".png", new Cell(Constants.COLOR[i],
					0, 0));
			button.setBounds(20 + (i * 33), 128, 20, 20);
			this.currentPlayer.add(button);
			this.add(button);
			if (this.coreGame.getPartecipants().get(i).isStatusActive()) {
				JLabel lastDie = new JLabel();
				lastDie.setBounds(20 + (i * 33), 153, 20, 20);
				int lastLaunch = this.coreGame.getPartecipants().get(i)
						.getLastLaunch();
				if (lastLaunch > 0) {
					lastDie.setIcon(new javax.swing.ImageIcon(ClassLoader
							.getSystemResource("sd/ui/images/dice/"
									+ this.coreGame.getPartecipants().get(i)
											.getColor() + "_" + lastLaunch
									+ ".png")));
					this.add(lastDie);
				}
			}
		}
	}

	/**
	 * Set the icon for the current player as on, the other as off
	 */
	private void setPlayerConnected(boolean isDoubleTurn) {
		this.initRound();
		String color = this.coreGame.getNextActivePartecipant(
				this.coreGame.getCurrentPartecipant().getIp()).getColor();
		if (this.coreGame.getTurn() == 0) {
			color = this.coreGame.getPartecipants()
					.get(this.coreGame.getFirstActiveIndex()).getColor();
		} else if (isDoubleTurn) {
			color = this.coreGame.getCurrentPartecipant().getColor();
		}
		for (int i=0; i<this.coreGame.getPartecipants().size(); i++) {
			if (this.coreGame.getPartecipants().get(i).isStatusActive()) {
				if (!Constants.COLOR[i].equals(color)) {
					this.currentPlayer
							.get(i).setIcon(new javax.swing.ImageIcon(ClassLoader
									.getSystemResource("sd/ui/images/turnMarkers/off/"
									+Constants.COLOR[i]+".png")));
				} else {
					this.currentPlayer.get(i).changeState();
				}
			} else {
				this.currentPlayer.get(i).setVisible(false);
			}
		}
	}

	/**
	 * Set the timer for the turn of single player. When timer end the turn pass
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
					}
					countdown -= 1000;
					int seconds = (int) (countdown / 1000);
					timeOfTurn.setText(String.valueOf(seconds)+" sec");
				}
				die.setEnabled(false);
				timeOfTurn.setText("Waiting");
				if (coreGame.isTurnActive()) {
					coreGame.setTurnActive(false);
					coreGame.setDoubleTurn(false);
					userPlayer.getGamePanel().makePossibleMoveDisable();
					userPlayer.updateNext(coreGame.getPartecipants(), coreGame
							.getGameBoard(), coreGame.getCurrentPartecipant()
							.getIp(), coreGame.isDoubleTurn(), coreGame
							.getTurn(), true);
				}
			}
		}).start();
	}

}