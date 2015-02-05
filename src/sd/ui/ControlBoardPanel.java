package sd.ui;

import java.awt.Color;
import java.awt.Font;
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
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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

	/**
	 * 
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
	}

	public void drawControlBoardGUI(boolean isDoubleTurn) {
		this.removeAll();
		this.updateUI();
		JLabel logo = new JLabel();
		logo.setBounds(20, 10, 170, 80);
		logo.setIcon(new javax.swing.ImageIcon(ClassLoader
				.getSystemResource("sd/ui/images/logo.jpg")));
		this.add(logo);
		JLabel colorIntro = new JLabel("You");
		colorIntro.setBounds(20, 100, 185, 25);
		colorIntro.setFont(new MyFont().getMyFont(Font.BOLD, 16));
		colorIntro.setForeground(Color.WHITE);
		this.add(colorIntro);
		JButton color = new JButton();
		color.setBounds(20, 128, 30, 30);
		color.setIcon(new javax.swing.ImageIcon(ClassLoader
				.getSystemResource("sd/ui/images/turnMarkers/on/SMALL_"
						+ this.coreGame.getMyPartecipant().getColor() + ".png")));
		color.setBorder(null);
		color.setFocusPainted(false);
		color.setBorderPainted(false);
		color.setContentAreaFilled(false);
		this.add(color);
		this.timeOfTurn = new JLabel("Waiting");
		this.timeOfTurn.setBounds(75, 133, 185, 25);
		this.timeOfTurn.setFont(new MyFont().getMyFont(Font.BOLD, 16));
		this.timeOfTurn.setForeground(Color.WHITE);
		this.add(timeOfTurn);
		JLabel playerConnectedIntro = new JLabel("Player");
		playerConnectedIntro.setBounds(20, 180, 185, 25);
		playerConnectedIntro.setFont(new MyFont().getMyFont(Font.BOLD, 16));
		playerConnectedIntro.setForeground(Color.WHITE);
		this.add(playerConnectedIntro);
		final JPanel containerDie = new JPanel();
		containerDie.setBorder(BorderFactory.createTitledBorder(null,
				"Container die", 0, 0, null, new java.awt.Color(0, 0, 0)));
		containerDie.setBounds(20, 275, 170, 100);
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
		if (!coreGame.isTurnActive() || coreGame.isVictory(coreGame.getMyPartecipant()))
			this.die.setEnabled(false);
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
	 * enable the button for launch die
	 */
	public void enableTurn() {
			this.setTimer();
	}

	/**
	 * 
	 * @return multidimensional array of buffered images for the 6 final image
	 *         representing the die luaunch
	 */
	private BufferedImage[][] initExactDieFaces() {
		BufferedImage[][] result = new BufferedImage[6][1];
		int row = -1;
		String myColor = this.coreGame.getMyPartecipant().getColor();
		if (myColor.equals(Constants.COLOR[0])) {
			row = 0;
		} else if (myColor.equals(Constants.COLOR[1])) {
			row = 2;
		} else if (myColor.equals(Constants.COLOR[2])) {
			row = 4;
		} else if (myColor.equals(Constants.COLOR[3])) {
			row = 1;
		} else if (myColor.equals(Constants.COLOR[4])) {
			row = 5;
		} else if (myColor.equals(Constants.COLOR[5])) {
			row = 3;
		}
		int offset = 0;
		for (int i=0; i<6; i++) {
			result[i][0] = DieSprite.getSprite(i, row, offset);
			offset += 2;
		}
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
			result[i] = DieSprite.getSprite(colSprite, rowSprite, 0);
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
	private void startAnimationDie(final JPanel panel) {
		/*
		 * I become the current partecipant only AFTER having launched the die,
		 * because of the call to initTurn()
		 */
		new Thread() {
			public void run() {
				try {
					SwingUtilities.invokeLater(new Runnable() { 
						public void run() {
							BufferedImage[] animationBuffer = initAnimationBuffer();
							BufferedImage[][] exactDieFaces = initExactDieFaces();
							int launchResult = coreGame.launchDie();
							coreGame.getMyPartecipant().setLastLaunch(launchResult);
							AnimationSprite animation = new AnimationSprite(animationBuffer, Constants.DIE_ANIMATION_SPEED);
							animation.start();
							JLabel resultDie = new JLabel();
							resultDie.setBounds(60, 265, Constants.DIE_SIZE, Constants.DIE_SIZE);
							for (int counter = 0; counter < Constants.DIE_ANIMATION_SPEED * 100; counter++) {
								animation.update();
								panel.removeAll();
								panel.updateUI();
								resultDie.setIcon(new ImageIcon(animationBuffer[counter % Constants.ROTATIONS]));
								panel.add(resultDie);
								panel.updateUI();
								updateUI();
							}	
							panel.removeAll();
							panel.updateUI();
							AnimationSprite resultAnimation = new AnimationSprite(exactDieFaces[launchResult - 1], 6);
							resultAnimation.start();
							resultAnimation.update();
							resultDie.setIcon(new ImageIcon(exactDieFaces[launchResult - 1][0]));
							resultDie.setBounds(60, 265, Constants.DIE_SIZE, Constants.DIE_SIZE);
							panel.add(resultDie);
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							userPlayer.getGamePanel().makePossibleMoveFlash();
						}
					});
				} catch (Exception ex) {
				}
			}
		}.start();
	}

	/**
	 * set the icon for all the player
	 */
	private void initRound() {
		this.currentPlayer = new ArrayList<CellButton>();
		for (int i = 0; i < this.coreGame.getPartecipants().size(); i++) {
			/* a player might haven exited during the building of the GUI */
			CellButton button = new CellButton(0, 0, "images/turnMarkers/on/"+ Constants.COLOR[i] + ".png", new Cell(Constants.COLOR[i],0, 0));
			button.setBounds(20 + (i * 33), 208, 20, 20);
			this.currentPlayer.add(button);
			this.add(button);
			if (this.coreGame.getPartecipants().get(i).isStatusActive()) {
				JLabel lastDie = new JLabel();
				lastDie.setBounds(20 + (i * 33), 233, 20, 20);
				int lastLaunch = this.coreGame.getPartecipants().get(i).getLastLaunch();
				if (lastLaunch > 0) {
					lastDie.setIcon(new javax.swing.ImageIcon(ClassLoader.getSystemResource("sd/ui/images/dice/"+ this.coreGame.getPartecipants().get(i).getColor() + "_"+ lastLaunch + ".png")));
					this.add(lastDie);
				}
			}
		}
	}

	/**
	 * set the icon for the current player as on, the other as off
	 */
	private void setPlayerConnected(boolean isDoubleTurn) {
		this.initRound();
		String color = this.coreGame.getNextActivePartecipant(this.coreGame.getCurrentPartecipant().getIp()).getColor();
		if (this.coreGame.getTurn() == 0) {
			color = this.coreGame.getPartecipants().get(this.coreGame.getFirstActiveIndex()).getColor();
		} else if (isDoubleTurn) {
			color = this.coreGame.getCurrentPartecipant().getColor();
		}
		for (int i = 0; i < this.coreGame.getPartecipants().size(); i++) {
			if (this.coreGame.getPartecipants().get(i).isStatusActive()) {
				if (!Constants.COLOR[i].equals(color)) {
					this.currentPlayer.get(i)
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
					countdown-=1000;
					int seconds = (int) (countdown/1000);
					timeOfTurn.setText(String.valueOf(seconds)+" sec");
				}
				die.setEnabled(false);
				timeOfTurn.setText("Waiting");
				if (coreGame.isTurnActive()) {
					coreGame.setTurnActive(false);
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