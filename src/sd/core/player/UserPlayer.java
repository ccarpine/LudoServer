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

import javax.swing.SwingUtilities;

import sd.core.CoreGame;
import sd.core.GameBoard;
import sd.core.Partecipant;
import sd.ui.ControlBoardPanel;
import sd.ui.GamePanel;
import sd.ui.IntroPanel;
import sd.ui.MainFrame;
import sd.ui.VictoryFrame;
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
	/* variable for test */
	private int whenCrash;
	private int turnCrash;
	private int whoCrash;
	/**
	 * when launched, it creates a future game player giving him the possibility
	 * to register at the server
	 * 
	 * @param ServerIp
	 *            , the ip address of the register server
	 * 
	 */
	public UserPlayer() throws RemoteException {
		this.buildGUIDone = false;
		this.firstCycleDone = false;
		this.isPlaying = false;
		this.mainFrame = new MainFrame();
		this.mainFrame.addPanel(new IntroPanel(), BorderLayout.CENTER);
		this.whenCrash = 5;
		this.turnCrash = 0;
		this.whoCrash = 0;
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
				this.buildGUIAndForward();
			} else {
				this.waitFor(Constants.PHASE_BUILD_GUI,-1,false, this.coreGame.getTurn());
			}
		}

	}

	@Override
	/**
	 * If the player invoking this method is not the first that will play then he can build his GUI 
	 * and forward this permission to the next; On the contrary case, it means that all the other players
	 * have finished buildind their GUI. The first player can start the game
	 */
	public void buildGUI(List<Partecipant> partecipants) throws RemoteException {
		this.coreGame.setPartecipants(partecipants);
		if (!buildGUIDone) {
			buildGUIDone = true;
			this.buildGUIAndForward();
		}
	}

	/* it handles the lack of message buildGUI from the previous player ONLY */
	private void waitFor(final int phase, final int type, final boolean isDubleTurn, final int currentTurn) {
		if(phase == Constants.PHASE_BUILD_GUI){
			System.out.println("00==PHASE_BUILD_GUI. START");
		}
		if(phase ==  Constants.PHASE_FIRST_CYCLE){
			System.out.println("11==PHASE_FIRST_CYCLE. START");
		}
		if (phase == Constants.PHASE_CYCLE) {
			if (type==0) {
				System.out.println("22==PHASE CYCLE--UPDATE NEXT. START");
			} else {
				System.out.println("22==PHASE CYCLE--PLAY NEXT. START");
			}
		}
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
						System.out.println("wait:"+wait);
						if (coreGame.getNrActivePartecipantBefore(coreGame.getIDMyPartecipant()) == 0) {
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
					while (wait > 0 && isPlaying &&((phase == Constants.PHASE_BUILD_GUI && !buildGUIDone) 
							|| (phase == Constants.PHASE_FIRST_CYCLE && !firstCycleDone)
							|| (phase == Constants.PHASE_CYCLE && currentTurn == coreGame.getTurn()))) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						wait -= 1000;
					}
					if(phase == Constants.PHASE_BUILD_GUI){
						System.out.println("00==PHASE_BUILD_GUI. TURNO "+currentTurn+"- Sono uscita con wait a: " + wait/1000 + "sec");
					}
					if(phase ==  Constants.PHASE_FIRST_CYCLE){
						System.out.println("11==PHASE_FIRST_CYCLE. TURNO "+currentTurn+"- Sono uscita con wait a: " + wait/1000 + "sec");
					}
					if (phase == Constants.PHASE_CYCLE) {
						if (type==0) {
							System.out.println("22==PHASE CYCLE--UPDATE NEXT. TURNO "+currentTurn+"- Sono uscita con wait a: " + wait/1000 + "sec");
						} else {
							System.out.println("22==PHASE CYCLE--PLAY NEXT. TURNO "+currentTurn+"- Sono uscita con wait a: " + wait/1000 + "sec");
						}
					}
					
					if (wait <= 0) {
						boolean foundPreviousAlive = false;
						while (!foundPreviousAlive && isPlaying) {
							Partecipant previous = coreGame.getPreviousActive(coreGame.getMyPartecipant().getColor());
							try {
								if (previous.getIp().equals(coreGame.getMyPartecipant().getIp())){
									// if you are the only one alive, you have won 
									coreGame.incrementTurn();
									showVictory();
								}
								else {
									if (phase == Constants.PHASE_BUILD_GUI)
										System.out.println("mando ISALIVE a: "+ previous.getIp() + "nella fase BUILD GUI");
									else if (phase == Constants.PHASE_FIRST_CYCLE)
										System.out.println("mando ISALIVE a: "+ previous.getIp() + "nella fase FIRST CYCLE");
									else if (phase == Constants.PHASE_CYCLE){
										if (type==0) {
											System.out.println("mando ISALIVE a: "+ previous.getIp() + "nella fase PHASE CYCLE --UPDATE NEXT");
											System.out.println("current turn: " +currentTurn+ " Turn in coregame: "+coreGame.getTurn());
										} else {
											System.out.println("mando ISALIVE a: "+ previous.getIp() + "nella fase PHASE CYCLE --PLAY NEXT");
											System.out.println("current turn: " +currentTurn+ " Turn in coregame: "+coreGame.getTurn());
										}
									}
									UserPlayerInterface tryPrevious = (UserPlayerInterface) Naming.lookup("rmi://" + previous.getIp()	+ "/RMIGameClient");
									tryPrevious.isAlive(phase, coreGame.getMyPartecipant().getColor(), coreGame.getTurn());
									foundPreviousAlive = true;
									waitFor(phase, type, isDubleTurn, coreGame.getTurn());
								}
							}
							 // the previous player has crashed and it must be set as unactive
							catch (MalformedURLException | RemoteException | NotBoundException e) {
								System.out.println("WAIT FOR: set unactive" + previous.getIp()); 
								coreGame.setUnactivePartecipant(previous.getColor());
							}
						}
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
						UserPlayerInterface nextInTurn = (UserPlayerInterface) Naming.lookup("rmi://" + partecipant.getIp()+ "/RMIGameClient");
						if (coreGame.getCurrentPartecipant().getIp().equals(partecipant.getIp())){
							System.out.println("I send Init TURN to " +partecipant.getIp());
							nextInTurn.initTurn(coreGame.getPartecipants());
						}
						else{
							System.out.println("I send Init buil GUI to " +partecipant.getIp());
							nextInTurn.buildGUI(coreGame.getPartecipants());
						}
						foundNextAlive = true;
						waitFor(Constants.PHASE_FIRST_CYCLE, -1, false,coreGame.getTurn());
					} catch (MalformedURLException | NotBoundException | RemoteException e) {
						System.out.println("Non c'era lo metto come inattivo e passo al successivo " +partecipant.getIp());
						coreGame.setUnactivePartecipant(partecipant.getColor());
					}
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
		if (currentTurn == this.coreGame.getTurn() && this.isPlaying) {
			System.out.println("I receive UPDATE STATUS");
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
						System.out.println("4 UPDATE SEND (UPDATE_NEXT)");
						updateNext(partecipants, gameBoard, ipCurrentPartecipant, isDoubleTurn, currentTurn, true);
						System.out.println("UPDATE NEXT turn====" + coreGame.getTurn());
						if (coreGame.getIDMyPartecipant()==1 && coreGame.getTurn()==1){
							System.out.println("Esco per test !!!");
							System.exit(1);
							
						}
						break;
					/* giving the next player the permission to play */
					case Constants.PLAY_NEXT:
						System.out.println("5 INIT TURN SEND (PLAY_NEXT) - ip current partecipant " + ipCurrentPartecipant );
						playNext(true);
						break;
					// the client play again
					case Constants.PLAY_AGAIN:
						startTurn();
						break;
					case Constants.END_GAME:
						showVictory();
						updateNext(partecipants, gameBoard, ipCurrentPartecipant, isDoubleTurn, currentTurn, true);
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
	private void playNext(boolean doWait) {
		boolean foundNextAlive = false;
		while (!foundNextAlive) {
			Partecipant nextInTurnPartecipant = this.coreGame.getNextActivePartecipant(this.coreGame.getMyPartecipant().getIp());
			try {
				//System.out.println("4 INIT TURN to: " + nextInTurnPartecipant.getIp());
				UserPlayerInterface nextPlayer = (UserPlayerInterface) Naming.lookup("rmi://" + nextInTurnPartecipant.getIp() + "/RMIGameClient");
				nextPlayer.initTurn(this.coreGame.getPartecipants());
				foundNextAlive = true;
				/* wait for the next message it will be a Update status message 
				 * you have to wait for 1 turn and for all the update message*/
				if (doWait)
					this.waitFor(Constants.PHASE_CYCLE, Constants.PLAY_NEXT, false, this.coreGame.getTurn());
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
	public void updateNext(List<Partecipant> partecipants, GameBoard gameBoard, String ipCurrentPartecipant, boolean isDoubleTurn, int currentTurn, boolean doWait) {
		boolean foundNextAlive = false;
		while (!foundNextAlive) {
			Partecipant nextInTurnPartecipant = this.coreGame.getNextActivePartecipant(this.coreGame.getMyPartecipant().getIp());
			try {
				//System.out.println("5 UPDATE STATUS to: " + nextInTurnPartecipant.getIp());
				UserPlayerInterface nextInTurn = (UserPlayerInterface) Naming.lookup("rmi://" + nextInTurnPartecipant.getIp() + "/RMIGameClient");
				nextInTurn.updateStatus(partecipants, gameBoard,ipCurrentPartecipant, isDoubleTurn, currentTurn);
				foundNextAlive = true;
				//System.out.println("5 UPDATE STATUS il mio dowait e':" + doWait); 
				if (doWait) {
				/* wait for the next message it will be a Update status message */
				//	System.out.println("5 UPDATE STATUS faccio wait con dowait: " + doWait);
					this.waitFor(Constants.PHASE_CYCLE, Constants.UPDATE_NEXT, isDoubleTurn, this.coreGame.getTurn());
				}
			} catch (MalformedURLException | NotBoundException | RemoteException e1) {
				boolean currentCrash = false;
				if (nextInTurnPartecipant.getIp().equals(coreGame.getCurrentPartecipant().getIp())) {
					currentCrash = true;
				}
				this.coreGame.setUnactivePartecipant(nextInTurnPartecipant.getColor());
				if (currentCrash) {
					foundNextAlive = true;
					this.playNext(doWait);
				}
			}
		}
	}

	@Override
	/**
	 * It allows the user player, in which this method is invoked, to start his turn by enabling his die launch
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
			if (this.coreGame.isVictory(this.coreGame.getMyPartecipant())){
				showVictory();
			} else {
				System.out.println("I play");
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
	 * this method, if correctly invoked tells the invoker if the player is alive; moreover 
	 * the invoker tells the invoked that all the partecipants between them have crashed. 
	 */
	public void isAlive(int phase, String color, int currentTurn) throws RemoteException {
		boolean currentCrashed = false;
		boolean end = false;
		String pingerColor = color;
		while (!end) {
			Partecipant partecipant = this.coreGame.getPreviousActive(pingerColor);
			if (partecipant.getColor().equals(this.coreGame.getMyPartecipant().getColor()))
				end = true;
			else {
				if (partecipant.getIp().equals(this.coreGame.getCurrentPartecipant().getIp())) {
					currentCrashed = true;
				}
				this.coreGame.setUnactivePartecipant(partecipant.getColor());
				pingerColor = partecipant.getColor();
			}
		}
		if (buildGUIDone && phase == Constants.PHASE_BUILD_GUI) {
			boolean foundNextAlive = false;
			while (!foundNextAlive) {
				Partecipant nextInTurn = this.coreGame.getNextActivePartecipant(this.coreGame.getMyPartecipant().getIp());
				try {
					System.out.println("IS ALIVE: invio la BUILD GUI a " + color);
					UserPlayerInterface nextInTurnPlayer = (UserPlayerInterface) Naming.lookup("rmi://"+ nextInTurn.getIp() +"/RMIGameClient");
					nextInTurnPlayer.buildGUI(this.coreGame.getPartecipants());
					foundNextAlive = true;
				} catch (MalformedURLException | NotBoundException e) {
					this.coreGame.setUnactivePartecipant(nextInTurn.getColor());
				}
			}
		}
		/* *
		 * if you receive a isAlive message during the first cycle 
		 * if you have alredy received the message you forward intiTurn to the invoker
		 * otherwise you wait for the message 
		 * */
		else if ((phase == Constants.PHASE_FIRST_CYCLE && firstCycleDone) || 
				(phase == Constants.PHASE_CYCLE)) {
			if (currentCrashed) {
				if (currentTurn == this.coreGame.getTurn()){
					System.out.println("IS ALIVE: current crashed so PLAY NEXT to: "+ color);
					this.playNext(false);
				}
			} else {
				if (currentTurn < this.coreGame.getTurn()){
					System.out.println("IS ALIVE: so UPDATE NEXT to: "+ color);
					this.updateNext(this.coreGame.getPartecipants(), this.coreGame.getGameBoard(), this.coreGame.getCurrentPartecipant().getIp(), this.coreGame.isDoubleTurn(), this.coreGame.getTurn(), false);
				}
			}
		}
	}
	
	private void showVictory() {
		this.isPlaying = false;				
		new VictoryFrame(coreGame.getCurrentPartecipant().getColor());
		this.mainFrame.dispose();
	}
	
	public static void main(String[] args) {
		try {
			UserPlayerInterface client = (UserPlayerInterface) new UserPlayer();
			/* get the ip */
			String ipAddress = Inet4Address.getLocalHost().getHostAddress();
			Naming.rebind("//" + ipAddress + "/RMIGameClient", client);
		} catch (UnknownHostException | RemoteException | MalformedURLException exc) {
			exc.printStackTrace();
		}
	}

}