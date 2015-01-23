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
import sd.core.Move;
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
	
	/** when launched, it creates a future game player giving him the possibility to register at the server
	 * 
	 * @param ServerIp, the ip address of the register server
	 * 
	 */
	public UserPlayer(String ServerIp) throws RemoteException {
		this.isPlaying = false;
		System.out.println("costruisco lo user player");
		this.mainFrame = new MainFrame();
		System.out.println("main frame creato");
		this.mainFrame.addPanel(new IntroPanel(ServerIp), BorderLayout.CENTER);
	}

	/** this method is invokated by the server on the client when a match can start, either if a maximum number 
	 * of 6 players is has been reached or the waiting time out has expired. A user game player will build his GUI
	 * as soon as received the permission from the one before him in the list. The first of the list (which is the first
	 * to play) will be the last to build the GUI.
	 * 
	 * @param gamersIp, the list of all gamers that have been registered by the server for a match
	 */
	public void start(List<String> gamersIp) {

		System.out.println("UserPlayer starts " + isPlaying);

		if (!this.isPlaying) {
			this.isPlaying = true;
			
			// init core game
			this.coreGame = new CoreGame(gamersIp);
			/* init GUI here */
			if (coreGame.amItheCurrentPartecipant()) {
				//System.out.println("Sono il primo e creo l'interfaccia");
				this.buildGUIAndForward();
			}
			
			try {
				System.out.println("1 -->" +Inet4Address.getLocalHost().getHostAddress());
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
		}

	}

	@Override
	/**
	 * If the player invoking this method is not the first that will play then he can build his GUI 
	 * and forward this permission to the next; On the contrary case, it means that all the other players
	 * have finished buildind their GUI. The first player can start the game
	 */
	public void buildGUI() throws RemoteException {
		if (!coreGame.amItheCurrentPartecipant()) {
			//System.out.println("Non sono il primo e creo l'interfaccia");
			this.buildGUIAndForward();
		} else {
			this.initTurn();
		}
	}
	
	/**
	 * it build the gui for the player that invokes this method and sends this permission to the one next to him
	 * in the list of the partecipants for that match.
	 */
	private void buildGUIAndForward() {
		Thread t = new Thread() {
			public void run() {
				try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							mainFrame.resetFrame();
							mainFrame.setSize(775, 532);
							gamePanel = new GamePanel(coreGame);
							gamePanel.setPreferredSize(new java.awt.Dimension(570, 532));
							mainFrame.addPanel(gamePanel, BorderLayout.WEST);
							controlBoardPanel = new ControlBoardPanel(gamePanel, coreGame);
							mainFrame.addPanel(controlBoardPanel, BorderLayout.CENTER);
						}
					});
				} catch (Exception ex) {
				}
				try {
					String nextInTurnId = coreGame.getNextPartecipant(coreGame.getMyPartecipant().getIp()).getIp();
					UserPlayerInterface nextInTurn = (UserPlayerInterface) Naming.lookup("rmi://"+ nextInTurnId + "/RMIGameClient");
					nextInTurn.buildGUI();
				} catch (MalformedURLException | RemoteException | NotBoundException e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	@Override
	/** this method is invoked by a client when he has finished his turn so that all the other partecipants
	 * can update the status of the match and its game board.
	 * 
	 * @param partecipants, the list of all the partecipants still taking part into the match (in case of crash) that need to have their game board updated
	 * @param gameBoard, the game board and the status of the partecipant that has just played
	 * @param ipCurrentPartecipant, the ip address of the player that has just played
	 */
	public void updateStatus(List<Partecipant> partecipants, GameBoard gameBoard, String ipCurrentPartecipant) throws RemoteException {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		/* the internal memory status of the game is updated */
		int result = this.coreGame.updateStatus(partecipants, gameBoard, ipCurrentPartecipant);
		/* update GUI here */
		System.out.println("3 UPDATE RECEIVED -->");
		/* END update GUI here */
		
		/* according to the previous update, there are several possible consequences*/
		switch (result) {
		/* sending the update to the next player */
		case Constants.UPDATE_NEXT:
			System.out.println("4 UPDATE SEND ("+ result +")-->" +this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp() );
			this.updateNext(partecipants, gameBoard, ipCurrentPartecipant);
			break;

		/* giving the next player the permission to play*/
		case Constants.PLAY_NEXT:
			System.out.println("5 INIT TURN SEND ("+ result +")-->" +this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp() + "/RMIGameClient" );
			this.playNext();
			break;

		/* */
		case Constants.PLAY_AGAIN:
			this.initTurn();
			break;
			
		default:
			// result could be END_GAME
			break;

		}

	}

	/**
	 * this method is invoked by a user player when he can allow the next to play
	 */
	private void playNext() {
			try {
				String nextPartecipantId = this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp();
				UserPlayerInterface nextPlayer = (UserPlayerInterface) Naming.lookup("rmi://"+ nextPartecipantId + "/RMIGameClient");
				nextPlayer.initTurn();
			} catch (RemoteException |MalformedURLException |NotBoundException e) {
				e.printStackTrace();
			} 
	}
	
	/**
	 * 
	 * @param partecipants, partecipants still taking part into a match (in case of crash)
	 * @param gameBoard, the current state of the game board in the current match
	 * @param ipCurrentPartecipant
	 */
	private void updateNext(List<Partecipant> partecipants, GameBoard gameBoard, String ipCurrentPartecipant){
			try {
				String nextInTurnId = this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp();
				UserPlayerInterface nextInTurn = (UserPlayerInterface) Naming.lookup("rmi://"+ nextInTurnId + "/RMIGameClient");
				nextInTurn.updateStatus(partecipants, gameBoard, ipCurrentPartecipant);
			} catch (MalformedURLException | NotBoundException |RemoteException e1) {
				e1.printStackTrace();
			}
			
	}
	@Override
	public void initTurn() throws RemoteException {
		
		System.out.println("2 INIT TURN RECEIVED -->" );
		controlBoardPanel.enableTurn();
	}

	public void applyMove(Move chosenMove) {
		this.coreGame.handleTurn(chosenMove);
		/* update GUI here */
		System.out.println("MAKE MOVE");
		System.out.println("4 UPDATE SEND -->" +this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp() );
		this.updateNext(this.coreGame.getPartecipants(), this.coreGame.getGameBoard(), this.coreGame.getMyPartecipant().getIp());
	}
	
	public static void main(String[] args) {
		try {
			UserPlayerInterface client = (UserPlayerInterface) new UserPlayer(args[0]);
			/* get the ip */
			String ipAddress = Inet4Address.getLocalHost().getHostAddress();
			Naming.rebind("//" + ipAddress + "/RMIGameClient", client);
			
			System.out.println("CLIENT ---- Ip address:" + ipAddress);
			System.out.println("CLIENT ---- Ip address:" + ipAddress);
		} catch (UnknownHostException | RemoteException | MalformedURLException exc) {
				exc.printStackTrace();
		}
	}

}