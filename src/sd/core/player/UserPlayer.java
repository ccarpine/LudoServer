package sd.core.player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import javax.swing.SwingUtilities;

import sd.core.CoreGame;
import sd.core.GameBoard;
import sd.core.Partecipant;
import sd.core.register.RegisterInterface;
import sd.ui.ControlBoardPanel;
import sd.ui.GamePanel;
import sd.ui.IntroPanel;
import sd.ui.MainFrame;
import sd.ui.VictoryFrame;
import sd.util.Constants;

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
	private boolean firstCycleDone;

	/**
	 * When launched, it creates a future game player giving him the possibility
	 * to register at the server
	 * @param ServerIp, the ip address of the register server
	 */
	public UserPlayer() throws RemoteException {
		this.buildGUIDone = false;
		this.firstCycleDone = false;
		this.isPlaying = false;
		this.mainFrame = new MainFrame();
		this.mainFrame.addPanel(new IntroPanel(this), BorderLayout.CENTER);
	}

	/** 
	 * It creates the CoreGame and prepares the game
	 * @param String, the ip address of the user player that wants to start the game
	 * @throws RemoteException
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
				this.buildGUIAndForward();
			} else {
				this.waitFor(Constants.PHASE_BUILD_GUI, -1, false,
						this.coreGame.getTurn());
			}
		}
	}

	@Override
	/** 
	 * Allows a player to initialize his GUI
	 * If the player invoking this method is not the first that will play then he can build his GUI 
	 * and forward this permission to the next; On the contrary case, it means that all the other players
	 * have finished buildind their GUI. The first player can start the game.
	 * @param List<Partecipant>, all the registered partecipants
	 * @throws RemoteException
	 */
	public void buildGUI(List<Partecipant> partecipants) throws RemoteException {
		this.coreGame.setPartecipants(partecipants);
		if (!buildGUIDone) {
			buildGUIDone = true;
			this.buildGUIAndForward();
		}
	}

	/***
	 * sets and starts an appropriate timer for the partecipant who calls the method considering a specific moment. 
	 * The timer represents the maximum time that the partecipant waits before a new method is invoked upon him.
	 * @param phase, int it can be PHASE_BUILD_GUI, PHASE_FIRST_CYCLE or PHASE_CYCLE. Represent a specific phase in the game, its value helps to estimate the time for the timer.
	 * @param type, int it can be UPDATE_NEXT, PLAY_NEXT or -1. The first two values are the only significant, the last one is used only if the phase is different from PHASE_CYCLE. 
	 * Its value tells if the participant is waiting after sending an updateStatus() an initTurn ()
	 * @param isDubleTurn, its value helps to estimate the time for the timer
	 * @param currentTurn, its value helps to stop the timer in case we are waiting for an old message
	 */
	private void waitFor(final int phase, final int type, final boolean isDubleTurn, final int currentTurn) {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean startWaiting = true;
				long wait = 0;
				switch (phase) {
					case Constants.PHASE_BUILD_GUI:
						wait = coreGame.getTimeForBuildGUI();
						if (wait == 0) {
							startWaiting = false;
							buildGUIAndForward();
						}
						break;
					case Constants.PHASE_FIRST_CYCLE:
						wait = coreGame.getTimeForTheFirstCycle();
						if (coreGame.getNrActivePartecipantBefore(coreGame
								.getIDPartecipantByColor(coreGame.getMyPartecipant().getColor())) == 0) {
							startWaiting = false;
							startTurn();
						}
						break;
					case Constants.PHASE_CYCLE:
						wait = coreGame.getTimeForCycle(type, isDubleTurn);
						break;
					default:
						break;
				}
				if (startWaiting) {
					while (wait > 0 && isPlaying && ((phase == Constants.PHASE_BUILD_GUI && !buildGUIDone)
									|| (phase == Constants.PHASE_FIRST_CYCLE && !firstCycleDone) 
									|| (phase == Constants.PHASE_CYCLE && currentTurn == coreGame.getTurn()))) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
						wait -= 1000;
					}
					
					if (wait <= 0) {
						boolean foundPreviousAlive = false;
						while (!foundPreviousAlive && isPlaying) {
							Partecipant previous = coreGame.getPreviousActive(coreGame
									.getMyPartecipant().getColor());
							try {
								if (previous.getIp().equals(coreGame.getMyPartecipant().getIp())) {
									// if you are the unique alive, you have won
									coreGame.incrementTurn();
									showVictory();
								} else {
									if (phase == Constants.PHASE_BUILD_GUI) {
										System.out.println("Try isAlive on " +previous.getIp()+" (Build GUI phase)");
									} else if (phase == Constants.PHASE_FIRST_CYCLE) {
										System.out.println("Try isAlive on " +previous.getIp()+" (First cycle phase)");
									} else if (phase == Constants.PHASE_CYCLE) {
										if (type == 0) {
											System.out.println("Try isAlive on " +previous.getIp() +" (Cycle phase, Update Next)");
										} else {
											System.out.println("Try isAlive on "+previous.getIp()+" (Cycle phase, Play Next)");
										}
									}
									Registry registry = LocateRegistry.getRegistry(previous.getIp(), 6000);
									UserPlayerInterface tryPrevious = (UserPlayerInterface) registry
										.lookup("rmi://"+previous.getIp()+"/RMIGameClient");
									tryPrevious.isAlive(phase, coreGame.getMyPartecipant().getColor(),
											coreGame.getTurn());
									foundPreviousAlive = true;
									waitFor(phase, type, isDubleTurn,coreGame.getTurn());
								}
							}
							/* the previous player has crashed and it must be set as unactive */
							catch (RemoteException | NotBoundException e) {
								System.out.println("The user "+previous.getIp()+" is not reacheable and I set him as inactive");
								coreGame.setUnactivePartecipant(previous.getColor());
							}
						}
					}
				}
			}
		}).start();

	}

	/**
	 * It builds the GUI for the player that invokes this method and sends this
	 * permission to the one next to him in the list of the partecipants for
	 * that match.
	 */
	private void buildGUIAndForward() {
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
				boolean foundNextAlive = false;
				while (!foundNextAlive) {
					Partecipant partecipant = coreGame.getNextActivePartecipant(coreGame.getMyPartecipant().getIp());
					try {
						Registry registry = LocateRegistry.getRegistry(partecipant.getIp(), 6000);
						UserPlayerInterface nextInTurn = (UserPlayerInterface) registry
								.lookup("rmi://"+partecipant.getIp()+"/RMIGameClient");
						if (coreGame.getCurrentPartecipant().getIp().equals(partecipant.getIp())) {
							System.out.println("I send Init turn to "+partecipant.getIp());
							nextInTurn.initTurn(coreGame.getPartecipants());
						} else {
							System.out.println("I send build GUI to "+partecipant.getIp());
							nextInTurn.buildGUI(coreGame.getPartecipants());
						}
						foundNextAlive = true;
						waitFor(Constants.PHASE_FIRST_CYCLE, -1, false, coreGame.getTurn());
					} catch (NotBoundException | RemoteException e) {
						coreGame.setUnactivePartecipant(partecipant.getIp());
						System.out.println("The user "+partecipant.getIp()+" is not reacheable and I set him as inactive");
					}
				}
			}
		}.start();
	}

	/**
	 * Init the main interface
	 */
	private void initInterface() {
		this.mainFrame.resetFrame();
		this.mainFrame.setSize(new Dimension(775, 510));
		this.mainFrame.addPanel(this.gamePanel, BorderLayout.WEST);
		this.mainFrame.addPanel(this.controlBoardPanel, BorderLayout.CENTER);
		this.gamePanel.drawGUI();
		this.controlBoardPanel.drawControlBoardGUI(false);
	}

	@Override
	/** 
	 * It updates the game status of the player in which this method is invokated
	 * @param List<Partecipant>, the users game players still taking part into the match. This list can change 
	 * 		  in case of crash of a user player
	 * @param GameBoard, the game board visibile by the current player
	 * @param String, the ip address of the current partecipants
	 * @param boolean, it establishes if the current partecipant can parform a double turn
	 * @param int, the value of the current turn
	 * @throws RemoteException
	 */
	public void updateStatus(final List<Partecipant> partecipants,
			final GameBoard gameBoard, final String ipCurrentPartecipant,
			final boolean isDoubleTurn, final int currentTurn)
			throws RemoteException {
		if (currentTurn == this.coreGame.getTurn() && this.isPlaying) {
			this.firstCycleDone = true;
			new Thread() {
				public void run() {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								/* the internal memory status and the gui is updated */
								result = coreGame.updateStatus(partecipants,gameBoard, ipCurrentPartecipant);
								coreGame.incrementTurn();
								controlBoardPanel.drawControlBoardGUI(isDoubleTurn);
								gamePanel.drawGUI();
							}
						});
					} catch (Exception ex) {
					}
					/* according to the previous update, there are several possible consequences */
					switch (result) {
						/* sending the update to the next player */
						case Constants.UPDATE_NEXT:
							updateNext(partecipants, gameBoard, ipCurrentPartecipant, isDoubleTurn,currentTurn, true);
							break;
						/* giving the next player the permission to play */
						case Constants.PLAY_NEXT:
							playNext(true);
							break;
						// the client play again
						case Constants.PLAY_AGAIN:
							startTurn();
							break;
						case Constants.END_GAME:
							showVictory();
							updateNext(partecipants, gameBoard, ipCurrentPartecipant, isDoubleTurn,currentTurn, true);
							break;
						default:
							break;
					}
				}
			}.start();

		}
	}

	/**
	 * This method is invoked by a user player when he can allow the next to play
	 */
	private void playNext(boolean doWait) {
		boolean foundNextAlive = false;
		while (!foundNextAlive) {
			Partecipant nextInTurnPartecipant = this.coreGame
					.getNextActivePartecipant(this.coreGame.getMyPartecipant()
							.getIp());
			try {
				Registry registry = LocateRegistry.getRegistry(nextInTurnPartecipant.getIp(), 6000);
				UserPlayerInterface nextPlayer = (UserPlayerInterface) registry
						.lookup("rmi://"+nextInTurnPartecipant.getIp()+"/RMIGameClient");
				nextPlayer.initTurn(this.coreGame.getPartecipants());
				foundNextAlive = true;
				/* wait for the next message */
				if (doWait) {
					this.waitFor(Constants.PHASE_CYCLE, Constants.PLAY_NEXT,false, this.coreGame.getTurn());
				}
			} catch (RemoteException | NotBoundException e) {
				this.coreGame.setUnactivePartecipant(nextInTurnPartecipant.getColor());
				System.out.println("The user " + nextInTurnPartecipant.getIp() + " is not reacheable. And I set him as inactive.");
			}
		}
	}

	/** 
	 * It update the first next active client after me
	 * @param partecipants, partecipants still taking part into a
	 * 		  match (in case of crash)
	 * @param gameBoard, the current state of the game board in
	 * 		  the current match
	 * @param ipCurrentPartecipant
	 */
	public void updateNext(List<Partecipant> partecipants, GameBoard gameBoard,
			String ipCurrentPartecipant, boolean isDoubleTurn, int currentTurn,
			boolean doWait) {
		boolean foundNextAlive = false;
		while (!foundNextAlive) {
			Partecipant nextInTurnPartecipant = this.coreGame.getNextActivePartecipant(this.coreGame.getMyPartecipant().getIp());
			try {
				Registry registry = LocateRegistry.getRegistry(nextInTurnPartecipant.getIp(), 6000);
				UserPlayerInterface nextInTurn = (UserPlayerInterface) registry
						.lookup("rmi://"+nextInTurnPartecipant.getIp()+"/RMIGameClient");
				nextInTurn.updateStatus(partecipants, gameBoard,ipCurrentPartecipant, isDoubleTurn, currentTurn);
				foundNextAlive = true;
				if (doWait) {
					this.waitFor(Constants.PHASE_CYCLE, Constants.UPDATE_NEXT,isDoubleTurn, this.coreGame.getTurn());
				}
			} catch (NotBoundException | RemoteException e1) {
				boolean currentCrash = false;
				if (nextInTurnPartecipant.getIp().equals(coreGame.getCurrentPartecipant().getIp())) {
					currentCrash = true;
				}
				this.coreGame.setUnactivePartecipant(nextInTurnPartecipant.getColor());
				System.out.println("The user " + nextInTurnPartecipant.getIp() + " is not reacheable. And I set him as inactive.");
				if (currentCrash) {
					foundNextAlive = true;
					this.playNext(doWait);
				}
			}
		}
	}

	@Override
	/** 
	 * It allows partcipant to start a turn after having updated the list of the status of all partecipants
	 * @param List<Partecipant>, the users game players still taking part into the match. This list can change 
	 * 		  in case of crash of a user player
	 * @throws RemoteException
	 */
	public void initTurn(List<Partecipant> partecipants) throws RemoteException {
		this.coreGame.setPartecipants(partecipants);
		this.startTurn();
	}

	private void startTurn() {
		if (!this.coreGame.isTurnActive() && this.isPlaying) {
			this.firstCycleDone = true;
			this.coreGame.setTurnActive(true);
			gamePanel.drawGUI();
			controlBoardPanel.drawControlBoardGUI(coreGame.isDoubleTurn());
			if (this.coreGame.isVictory(this.coreGame.getMyPartecipant())) {
				showVictory();
			} else {
				this.controlBoardPanel.enableTurn();
			}
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

	@Override
	/**
	 * This method, if correctly invoked tells the invoker if the player is alive; moreover 
	 * the invoker tells the invoked that all the partecipants between them have crashed. 
	 * @param int, the phase into which the game is 
	 * @param String, the color of the pinger
	 * @param int, the number of the current turn
	 */
	public void isAlive(int phase, String color, int currentTurn) throws RemoteException {
		boolean end = false;
		String pingerColor = color;
		while (!end) {
			Partecipant partecipant = this.coreGame.getPreviousActive(pingerColor);
			if (partecipant.getColor().equals(this.coreGame.getMyPartecipant().getColor())) {
				end = true;
			} else {
				this.coreGame.setUnactivePartecipant(partecipant.getColor());
				pingerColor = partecipant.getColor();
			}
		}
		if (buildGUIDone && phase == Constants.PHASE_BUILD_GUI) {
			boolean foundNextAlive = false;
			while (!foundNextAlive) {
				Partecipant nextInTurn = this.coreGame
						.getNextActivePartecipant(this.coreGame
								.getMyPartecipant().getIp());
				try {
					System.out.println("Reply to isAlive. I send build GUI to "+color);
					Registry registry = LocateRegistry.getRegistry(nextInTurn.getIp(), 6000);
					UserPlayerInterface nextInTurnPlayer = (UserPlayerInterface) registry
							.lookup("rmi://"+nextInTurn.getIp()+"/RMIGameClient");
					nextInTurnPlayer.buildGUI(this.coreGame.getPartecipants());
					foundNextAlive = true;
				} catch (NotBoundException e) {
					this.coreGame.setUnactivePartecipant(nextInTurn.getColor());
				}
			}
		/*
		 * If you receive a isAlive message during the first cycle if you have
		 * alredy received the message you forward intiTurn to the invoker
		 * otherwise you wait for the message
		 */
		} else if ((phase == Constants.PHASE_FIRST_CYCLE && firstCycleDone)
				|| (phase == Constants.PHASE_CYCLE)) {
			System.out.println("isAlive: phase_cycle");
			if (this.coreGame.getCurrentPartecipant().getIp().equals(this.coreGame.getMyPartecipant().getIp())) {
				if (currentTurn == this.coreGame.getTurn()) {
					System.out.println("Reply to isAlive. The current crashed, i send Play Next to: "+color);
					this.playNext(false);
				}
			} else {
				if (currentTurn < this.coreGame.getTurn()) {
					System.out.println("Reply to isAlive. I send Update Next to "+color);
					this.updateNext(this.coreGame.getPartecipants(),
						this.coreGame.getGameBoard(), this.coreGame.getCurrentPartecipant().getIp(),
							this.coreGame.isDoubleTurn(), this.coreGame.getTurn(), false);
				}
			}
		}
	}

	/**
	 * It disposes the main frame of the game and creates and shows a window showing the winner player
	 */
	private void showVictory() {
		this.isPlaying = false;
		new VictoryFrame(this, coreGame);
		this.mainFrame.dispose();
	}
	
	/**
	 * It causes the user player to register for a match by connecting to a dedicated server
	 * @param String, the ip address of the register server
	 * @return long, the time to wait before the game starts
	 */
	public long startConnection(String serverIP) {
		try {
			Registry registry = LocateRegistry.getRegistry(serverIP, 6000);
			RegisterInterface server = (RegisterInterface) registry
					.lookup("rmi://" + serverIP + "/RMILudoServer");
			return server.register(Inet4Address.getLocalHost().getHostAddress());
		} catch (RemoteException | NotBoundException | UnknownHostException e) {
			return -1L;
		}
	}
	
	/**
	 * This method is called when a waiting player does not want to take part into a match anymore
	 * @param String, the ip address of the register server
	 */
	public void exit(String serverIP) {
		try {
			Registry registry = LocateRegistry.getRegistry(serverIP, 6000);
			RegisterInterface server = (RegisterInterface) registry
					.lookup("rmi://"+serverIP+"/RMILudoServer");
			server.deletePartecipant(Inet4Address.getLocalHost()
					.getHostAddress());
		} catch (RemoteException | UnknownHostException | NotBoundException e) {
		} finally {
			System.exit(0);
		}
	}
	
	public static void main(String[] args) {
		try {
			UserPlayerInterface client = (UserPlayerInterface) new UserPlayer();
			Registry registry = LocateRegistry.createRegistry(6000);
			registry.rebind("rmi://"+Inet4Address.getLocalHost().getHostAddress()+"/RMIGameClient", client);
		} catch (IOException e) {
			System.out.println("Registry not called");
		}
	}
}