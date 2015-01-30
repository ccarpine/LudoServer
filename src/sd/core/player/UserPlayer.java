package sd.core.player;

import java.awt.BorderLayout;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import sd.core.CoreGame;
import sd.core.GameBoard;
import sd.core.Partecipant;
import sd.ui.ControlBoardPanel;
import sd.ui.GamePanel;
import sd.ui.IntroPanel;
import sd.ui.MainFrame;
import sd.util.Constants;

/* si occupa di registrarsi ed in seguito avviare la partita e visualizzare interfaccia --> elabora il gioco che 
 * che avviene tutto nella classe MainGame */
public class UserPlayer extends UnicastRemoteObject implements
		UserPlayerInterface {

	private static final long serialVersionUID = 1L;
	private MainFrame mainFrame;
	private GamePanel gamePanel;
	private ControlBoardPanel controlBoardPanel;
	private CoreGame coreGame;
	private boolean isPlaying;
	private int result;
	private boolean buildGUIDone;

	/**
	 * when launched, it creates a future game player giving him the possibility
	 * to register at the server
	 * 
	 * @param ServerIp
	 *            , the ip address of the register server
	 * 
	 */
	public UserPlayer(String ServerIp) throws RemoteException {
		this.buildGUIDone = false;
		this.isPlaying = false;
		this.mainFrame = new MainFrame();
		this.mainFrame.addPanel(new IntroPanel(ServerIp), BorderLayout.CENTER);
	}

	/**
	 * this method is invokated by the server on the client when a match can
	 * start, either if a maximum number of 6 players is has been reached or the
	 * waiting time out has expired. A user game player will build his GUI as
	 * soon as received the permission from the one before him in the list. The
	 * first of the list (which is the first to play) will be the last to build
	 * the GUI.
	 * 
	 * @param gamersIp
	 *            , the list of all gamers that have been registered by the
	 *            server for a match
	 */
	public void start(List<String> gamersIp) {

		if (!this.isPlaying) {
			this.isPlaying = true;
			// init core game
			this.coreGame = new CoreGame(gamersIp);
			this.gamePanel = new GamePanel(this.coreGame, this);
			this.controlBoardPanel = new ControlBoardPanel(this.coreGame, this);
			// init GUI here
			if (this.coreGame.amItheCurrentPartecipant()) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				this.buildGUIAndForward(this.coreGame.getPartecipants());
			}
			else {

				this.waitBuildGUI();
			}
		}

	}

	/* it handles the lack of message buildGUI from the previous player ONLY */
	private void waitBuildGUI() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				long wait = coreGame.getTimeForBuildGUI();

				/* All the players before me have crashed */
				if (wait == 0) {
					buildGUIAndForward(coreGame.getPartecipants());

				}

				else {

					while (wait > 0 && !buildGUIDone) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						wait -= 1000;
					}

					if (!buildGUIDone) {
						boolean foundPreviousAlive = false;

						while (!foundPreviousAlive) {
							Partecipant previous = coreGame
									.getPreviousActive(coreGame
											.getMyPartecipant().getColor());
							try {
								UserPlayerInterface tryPrevious = (UserPlayerInterface) Naming
										.lookup("rmi://" + previous.getIp()
												+ "/RMIGameClient");
								tryPrevious.isAlive(coreGame.getMyPartecipant().getColor());
								foundPreviousAlive = true;
								waitBuildGUI();
							}

							/* the previous player has crashed and it must be set as unactive*/
							catch (MalformedURLException | RemoteException
									| NotBoundException e) {
								e.printStackTrace();
								coreGame.setUnactivePartecipant(previous.getColor());
							}

						}
					}

					else {
						if (wait > 0) {
							System.out
									.println("Tempo che era rimasto: " + wait);
						}
					}

				}
			}
		}).start();

	}

	@Override
	/**
	 * If the player invoking this method is not the first that will play then he can build his GUI 
	 * and forward this permission to the next; On the contrary case, it means that all the other players
	 * have finished buildind their GUI. The first player can start the game
	 */
	public void buildGUI(List<Partecipant> partecipants) throws RemoteException {
		if (!buildGUIDone) {
			buildGUIDone = true;
			if (this.coreGame.amItheCurrentPartecipant()) {
				System.out.println("Sono il primo e gioco");
				this.initTurn();
			} else {
				System.out.println("Non sono il primo e creo l'interfaccia");
				this.buildGUIAndForward(partecipants);
			}
		}
	}

	/**
	 * it build the gui for the player that invokes this method and sends this
	 * permission to the one next to him in the list of the partecipants for
	 * that match.
	 */
	private void buildGUIAndForward(final List<Partecipant> partecipants) {				
		this.coreGame.setPartecipants(partecipants);
		
		for(int j=0; j<this.coreGame.getPartecipants().size(); j++) {
			System.out.println("Partecipant " + this.coreGame.getPartecipants().get(j).getIp() + 
					" is active = " + this.coreGame.getPartecipants().get(j).isStatusActive());
		}
		
		new Thread() {
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							initInterface();
						}
					});
				} catch (Exception ex) {
				}
				Partecipant partecipant = coreGame.getNextActivePartecipant(coreGame.getMyPartecipant().getIp());
				
				try {
					UserPlayerInterface nextInTurn = (UserPlayerInterface) Naming
							.lookup("rmi://" + partecipant.getIp() + "/RMIGameClient");
					nextInTurn.buildGUI(coreGame.getPartecipants());
				
			    /* my following player has crashed and so the crash of NEXT partecipant is handled */
				} catch (MalformedURLException | RemoteException| NotBoundException e) {
					//e.printStackTrace();
					System.out.println(partecipant.getIp() + " has crashed!");
					coreGame.setUnactivePartecipant(partecipant.getColor());
					/* ATTENZIONE: adesso devo passare la lista aggiornata con i giocatori che sono crashati */
					buildGUIAndForward(coreGame.getPartecipants());
				}
			}
		}.start();
	}

	/**
	 * init the main interface
	 */
	private void initInterface() {
		this.mainFrame.resetFrame();
		this.mainFrame.setSize(775, 532);
		this.mainFrame.addPanel(this.gamePanel, BorderLayout.WEST);
		this.mainFrame.addPanel(this.controlBoardPanel, BorderLayout.CENTER);
	}

	@Override
	/** this method is invoked by a client when he has finished his turn so that all the other partecipants
	 * can update the status of the match and its game board.
	 * 
	 * @param partecipants, the list of all the partecipants still taking part into the match (in case of crash) that need to have their game board updated
	 * @param gameBoard, the game board and the status of the partecipant that has just played
	 * @param ipCurrentPartecipant, the ip address of the player that has just played
	 */
	public void updateStatus(final List<Partecipant> partecipants,
			final GameBoard gameBoard, final String ipCurrentPartecipant,
			final boolean isDoubleTurn, final int currentTurn)
			throws RemoteException {

		System.out.println("turno ricevuto: " + currentTurn
				+ " e turno del core: " + this.coreGame.getTurn());

		if (currentTurn == this.coreGame.getTurn()) {

			new Thread() {
				public void run() {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								/*
								 * the internal memory status and the gui of the
								 * game is updated
								 */
								result = coreGame.updateStatus(partecipants,
										gameBoard, ipCurrentPartecipant);
								coreGame.incrementTurn();
								controlBoardPanel
										.drawControlBoardGUI(isDoubleTurn);
								gamePanel.drawGUI();
							}
						});
					} catch (Exception ex) {
					}
					/*
					 * according to the previous update, there are several
					 * possible consequences
					 */
					switch (result) {
					/* sending the update to the next player */
					case Constants.UPDATE_NEXT:
						System.out.println("4 UPDATE SEND (" + result + ")");
						updateNext(partecipants, gameBoard,
								ipCurrentPartecipant, isDoubleTurn, currentTurn);
						break;
					/* giving the next player the permission to play */
					case Constants.PLAY_NEXT:
						System.out.println("5 INIT TURN SEND (" + result + ")");
						playNext();
						break;
					// the client play again
					case Constants.PLAY_AGAIN:
						try {
							initTurn();
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						break;
					case Constants.END_GAME:
						JOptionPane.showMessageDialog(null, "il vincitore e': "
								+ coreGame.getWinner());
						break;
					default:
						break;
					}
				}
			}.start();
		}
	}

	/**
	 * this method is invoked by a user player when he can allow the next to
	 * play
	 */
	private void playNext() {
		try {
			String nextPartecipantId = this.coreGame.getNextActivePartecipant(
					this.coreGame.getMyPartecipant().getIp()).getIp();
			UserPlayerInterface nextPlayer = (UserPlayerInterface) Naming
					.lookup("rmi://" + nextPartecipantId + "/RMIGameClient");
			nextPlayer.initTurn();
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param partecipants
	 *            , partecipants still taking part into a match (in case of
	 *            crash)
	 * @param gameBoard
	 *            , the current state of the game board in the current match
	 * @param ipCurrentPartecipant
	 */
	public void updateNext(List<Partecipant> partecipants, GameBoard gameBoard,
			String ipCurrentPartecipant, boolean isDoubleTurn, int currentTurn) {
		try {
			String nextInTurnId = this.coreGame.getNextActivePartecipant(
					this.coreGame.getMyPartecipant().getIp()).getIp();
			UserPlayerInterface nextInTurn = (UserPlayerInterface) Naming
					.lookup("rmi://" + nextInTurnId + "/RMIGameClient");
			nextInTurn.updateStatus(partecipants, gameBoard,
					ipCurrentPartecipant, isDoubleTurn, currentTurn);
		} catch (MalformedURLException | NotBoundException | RemoteException e1) {
			e1.printStackTrace();
		}

	}

	@Override
	/**
	 * It allows the user player, in which this method is invoked, to start his turn by enabling his die launch
	 */
	public void initTurn() throws RemoteException {
		if (!this.coreGame.iWin()) {
			this.coreGame.setTurnActive(true);
			this.controlBoardPanel.enableTurn();
		} else {
			JOptionPane.showMessageDialog(null, "You Win!!!!");
		}
	}

	/**
	 * @return the game panel
	 */
	public GamePanel getGamePanel() {
		return this.gamePanel;
	}

	/**
	 * @return the control board panel
	 */
	public ControlBoardPanel getControlBoardPanel() {
		return this.controlBoardPanel;
	}

	public static void main(String[] args) {
		try {
			UserPlayerInterface client = (UserPlayerInterface) new UserPlayer(
					args[0]);
			/* get the ip */
			String ipAddress = Inet4Address.getLocalHost().getHostAddress();
			Naming.rebind("//" + ipAddress + "/RMIGameClient", client);
		} catch (UnknownHostException | RemoteException | MalformedURLException exc) {
			exc.printStackTrace();
		}
	}

	@Override
	/**
	 * this method, if correctly invoked tells the invoker if the player is alive; moreover 
	 * the invoker tells the invoked that all the partecipants between them have crashed. 
	 */
	public void isAlive(String color) throws RemoteException {

		boolean end = false;

		while (!end) {
			Partecipant partecipant = this.coreGame.getPreviousActive(color);
			if (partecipant.getColor().equals(
					this.coreGame.getMyPartecipant().getColor()))
				end = true;
			else {
				this.coreGame.setUnactivePartecipant(partecipant.getColor());
				color = partecipant.getColor();
			}

		}

	}

}