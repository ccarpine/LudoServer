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
	private boolean firstCycleDone;
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
		this.firstCycleDone = false;
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
				this.buildGUIAndForward(this.coreGame.getPartecipants());
			} else {
				this.waitFor(Constants.PHASE_BUILD_GUI, buildGUIDone);
			}
		}

	}

	/* it handles the lack of message buildGUI from the previous player ONLY */
	/*private void waitBuildGUI() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				long wait = coreGame.getTimeForBuildGUI();
				//System.out.println("Attendo per " + wait + " millisecondi");
				/* All the players before me have crashed */
	/*			if (wait == 0) {
				//	System.out.println("Costruisco la GUI e faccio forward");
					buildGUIAndForward(coreGame.getPartecipants());
				}
				else {
					while (wait > 0 && !buildGUIDone) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						wait -= 1000;
					}
					if (!buildGUIDone) {
						boolean foundPreviousAlive = false;
						while (!foundPreviousAlive) {
							Partecipant previous = coreGame.getPreviousActive(coreGame.getMyPartecipant().getColor());
						//	System.out.println("Cerco di pingare "+ previous.getIp());
							try {
								UserPlayerInterface tryPrevious = (UserPlayerInterface) Naming.lookup("rmi://" + previous.getIp()	+ "/RMIGameClient");
								tryPrevious.isAlive(coreGame.getMyPartecipant().getColor());
								foundPreviousAlive = true;
								System.out.println(previous.getIp() + " è vivo");
								waitBuildGUI();
							}
							/*
							 * the previous player has crashed and it must be
							 * set as unactive
							 */
		/*					catch (MalformedURLException | RemoteException | NotBoundException e) {
								// e.printStackTrace();
							//	System.out.println(previous.getIp()	+ " has crashed");
								coreGame.setUnactivePartecipant(previous.getColor());
							}
						}
					}
				}
			}
		}).start();

	}*/

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
				this.initTurn();
			} else {
				System.out.println("Non sono il primo e creo l'interfaccia");
				this.buildGUIAndForward(partecipants);
			}
		}
	}

	/* it handles the lack of message buildGUI from the previous player ONLY */
	private void waitFor(final int phaseNumber, final boolean phase) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				long wait = 0;
				
				//long wait = coreGame.getTimeForTheFirstCycle();
				
				if (phaseNumber == Constants.PHASE_BUILD_GUI) {
					wait = coreGame.getTimeForBuildGUI();
				}
				
				else if (phaseNumber == Constants.PHASE_FIRST_CYCLE) {
					wait = coreGame.getTimeForTheFirstCycle();
				}
				
				while (wait > 0 && !phase) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					wait -= 1000;
				}
				
					boolean foundPreviousAlive = false;
					while (!foundPreviousAlive) {
						Partecipant previous = coreGame.getPreviousActive(coreGame.getMyPartecipant().getColor());
						try {
							
							UserPlayerInterface tryPrevious = (UserPlayerInterface) Naming.lookup("rmi://" + previous.getIp()	+ "/RMIGameClient");
							tryPrevious.isAlive(coreGame.getMyPartecipant().getColor());
							foundPreviousAlive = true;
							
							waitFor(phaseNumber, phase);
						}
						/*
						 * the previous player has crashed and it must be set as unactive
						 */
						catch (MalformedURLException | RemoteException | NotBoundException e) {
							coreGame.setUnactivePartecipant(previous.getColor());
						}
					}
				}
			
		}).start();

	}

	/**
	 * it builds the GUI for the player that invokes this method and sends this
	 * permission to the one next to him in the list of the partecipants for
	 * that match.
	 */
	private void buildGUIAndForward(final List<Partecipant> partecipants) {
		this.coreGame.setPartecipants(partecipants);
		System.out.println("Current partecipant is "+ this.coreGame.getCurrentPartecipant().getIp());
		for (int j = 0; j < this.coreGame.getPartecipants().size(); j++) {
			System.out.println("Partecipant " + this.coreGame.getPartecipants().get(j).getIp()
					+ " is active = " + this.coreGame.getPartecipants().get(j).isStatusActive());
		}
		System.out.println("Current is " + this.coreGame.getCurrentPartecipant().getIp());

		new Thread() {
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							System.out.println("Chiamo initInterface()");
							initInterface();
						}
					});
				} catch (Exception ex) {
				}
				Partecipant partecipant = coreGame.getNextActivePartecipant(coreGame.getMyPartecipant().getIp());
				System.out.println("chiamo buildGUI su: " + partecipant.getIp());
				try {
					UserPlayerInterface nextInTurn = (UserPlayerInterface) Naming.lookup("rmi://" + partecipant.getIp()+ "/RMIGameClient");
					if (coreGame.getMyPartecipant().getIp().equals("192.168.1.81")){
						System.exit(0);
					}
					nextInTurn.buildGUI(coreGame.getPartecipants());
					/* all partecipant wait for update for the first turn exept the first player that wait for his first turn*/
					waitFor(Constants.PHASE_FIRST_CYCLE, firstCycleDone); 
					/*
					 * my following player has crashed and so the crash of NEXT
					 * partecipant is handled
					 */
				} catch (MalformedURLException | RemoteException | NotBoundException e) {
					System.out.println(partecipant.getIp() + " has crashed!");
					coreGame.setUnactivePartecipant(partecipant.getColor());
					/*
					 * ATTENZIONE: adesso devo passare la lista aggiornata con i
					 * giocatori che sono crashati
					 */
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
		this.gamePanel.drawGUI();
		this.controlBoardPanel.drawControlBoardGUI(false);
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
		if (currentTurn == this.coreGame.getTurn()) {
			this.firstCycleDone = true;
			new Thread() {
				public void run() {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								/*
								 * the internal memory status and the gui of the game is updated
								 */
								result = coreGame.updateStatus(partecipants,gameBoard, ipCurrentPartecipant);
								coreGame.incrementTurn();
								controlBoardPanel.drawControlBoardGUI(isDoubleTurn);
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
						updateNext(partecipants, gameBoard, ipCurrentPartecipant, isDoubleTurn, currentTurn);
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
						JOptionPane.showMessageDialog(null, "il vincitore e': " + coreGame.getWinner());
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
		boolean foundNextAlive = false;
		while (!foundNextAlive) {
			Partecipant nextInTurnPartecipant = this.coreGame.getNextActivePartecipant(this.coreGame.getMyPartecipant().getIp());
			try {
				System.out.println("Mando playnext a: " + nextInTurnPartecipant);
				UserPlayerInterface nextPlayer = (UserPlayerInterface) Naming.lookup("rmi://" + nextInTurnPartecipant.getIp() + "/RMIGameClient");
				nextPlayer.initTurn();
				foundNextAlive = true;
			} catch (RemoteException | MalformedURLException | NotBoundException e) {
				this.coreGame.setUnactivePartecipant(nextInTurnPartecipant.getColor());
			}
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
	public void updateNext(List<Partecipant> partecipants, GameBoard gameBoard, String ipCurrentPartecipant, boolean isDoubleTurn, int currentTurn) {
		boolean foundNextAlive = false;
		while (!foundNextAlive) {
			Partecipant nextInTurnPartecipant = this.coreGame.getNextActivePartecipant(this.coreGame.getMyPartecipant().getIp());
			try {
				System.out.println("Mando updateNext a: " + nextInTurnPartecipant);
				UserPlayerInterface nextInTurn = (UserPlayerInterface) Naming.lookup("rmi://" + nextInTurnPartecipant.getIp() + "/RMIGameClient");
				nextInTurn.updateStatus(partecipants, gameBoard,ipCurrentPartecipant, isDoubleTurn, currentTurn);
				foundNextAlive = true;
			} catch (MalformedURLException | NotBoundException | RemoteException e1) {
				this.coreGame.setUnactivePartecipant(nextInTurnPartecipant.getColor());
			}
		}

	}

	@Override
	/**
	 * It allows the user player, in which this method is invoked, to start his turn by enabling his die launch
	 */
	public void initTurn() throws RemoteException {
		if (!this.coreGame.iWin()) {
			this.firstCycleDone = true;
			System.out.println("Sono il primo e gioco");
			this.gamePanel.drawGUI();
			this.controlBoardPanel.drawControlBoardGUI(false);
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
			UserPlayerInterface client = (UserPlayerInterface) new UserPlayer(args[0]);
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
		String pingerColor = color;
		while (!end) {
			Partecipant partecipant = this.coreGame.getPreviousActive(pingerColor);
			if (partecipant.getColor().equals(this.coreGame.getMyPartecipant().getColor()))
				end = true;
			else {
				this.coreGame.setUnactivePartecipant(partecipant.getColor());
				pingerColor = partecipant.getColor();
			}
		}
		/* *
		 * if you receive a isAlive message during the first cycle 
		 * if you have alredy received the message you forward intiTurn to the invoker
		 * otherwise you wait for the message 
		 * */
		if (this.buildGUIDone) {
			System.out.println("abbiamo costruito l'interfaccia e siamo stati pingati da "+ color);
			System.out.println("il current partecipant e' " + this.coreGame.getCurrentPartecipant().getIp());
			
			boolean foundNextAlive = false;
			while (!foundNextAlive) {
				Partecipant nextInTurn = this.coreGame.getNextActivePartecipant(this.coreGame.getMyPartecipant().getIp());
				UserPlayerInterface nextInTurnPlayer = null;
				if (this.coreGame.getCurrentPartecipant().getColor().equals(color)) {
					try {
						System.out.println("IS ALIVE: faccio l'init turn a " + color);
						nextInTurnPlayer = (UserPlayerInterface) Naming.lookup("rmi://"+ nextInTurn.getIp() +"/RMIGameClient");
						nextInTurnPlayer.initTurn();
						foundNextAlive = true;
					} catch (MalformedURLException | NotBoundException e) {
						this.coreGame.setUnactivePartecipant(nextInTurn.getColor());
					}
				}
				else {
					try {
						System.out.println("IS ALIVE: faccio l'update a " + color);
						nextInTurnPlayer = (UserPlayerInterface) Naming.lookup("rmi://"+ nextInTurn.getIp() +"/RMIGameClient");
						nextInTurnPlayer.updateStatus(this.coreGame.getPartecipants(), 
												this.coreGame.getGameBoard(), 
												this.coreGame.getCurrentPartecipant().getIp(), 
												this.coreGame.isDoubleTurn(), 
												this.coreGame.getTurn());
						foundNextAlive = true;
					} catch (MalformedURLException | NotBoundException e) {
						this.coreGame.setUnactivePartecipant(nextInTurn.getColor());
					}
				}
			}
		}
	}

}