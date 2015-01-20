package sd.core.player;

import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import javax.swing.JOptionPane;

import sd.core.CoreGame;
import sd.core.GameBoard;
import sd.core.Move;
import sd.core.Partecipant;
import sd.ui.IntroPanel;
import sd.ui.MainFrame;
import sd.util.Constants;

/* si occupa di registrarsi ed in seguito avviare la partita e visualizzare interfaccia --> elabora il gioco che 
 * che avviene tutto nella classe MainGame */
public class UserPlayer extends UnicastRemoteObject implements
		UserPlayerInterface {

	private static final long serialVersionUID = 1L;
	private MainFrame mainFrame;
	private CoreGame coreGame;
	private boolean isPlaying;
	
	public UserPlayer(String ServerIp) throws RemoteException {
		this.isPlaying = false;
		System.out.println("costruisco lo user player");
		this.mainFrame = new MainFrame();
		System.out.println("main frame creato");
		this.mainFrame.addPanel(new IntroPanel(ServerIp));
	}

	public void start(List<String> gamersIp) {

		System.out.println("UserPlayer starts " + isPlaying);

		if (!this.isPlaying) {
			this.isPlaying = true;
			
			this.coreGame = new CoreGame(gamersIp);
			/* init GUI here */
			String message = null;
			try {
				message = Inet4Address.getLocalHost().getHostAddress() + ": START partita  " + System.currentTimeMillis();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			System.out.println(message);
			/* END update GUI here */
			
				/* check if I'm the first player */ 
				if (coreGame.amItheCurrentPartecipant()) {
					try {
						/* start my turn */
						this.initTurn();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			
		}

	}

	@Override
	public void updateStatus(List<Partecipant> partecipants, GameBoard gameBoard) throws RemoteException {
		
		if (gameBoard!=null){
			System.out.println("gameboard" + gameBoard);
		}
		if (partecipants!=null){
			System.out.println("Update size partecipant:" + partecipants.size());
		}
		if (this.coreGame!=null){
			System.out.println("il core e' vuoto");
		}
		System.out.flush();
		int result = this.coreGame.updateStatus(partecipants, gameBoard);
		/* update GUI here */
		String message = null;
		try {
			message = Inet4Address.getLocalHost().getHostAddress() + ": UPDATE status" + System.currentTimeMillis();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println(message);
		/* END update GUI here */
		
		
		
		
		System.out.println("Update status");
		switch (result) {
		case Constants.UPDATE_NEXT:
			try {
				UserPlayerInterface nextPlayer = (UserPlayerInterface) Naming
						.lookup("rmi://"
								+ this.coreGame.getNextPartecipant(
										this.coreGame.getMyPartecipant()
												.getIp()).getIp()
								+ "/RMIGameClient");
				nextPlayer.updateStatus(partecipants, gameBoard);
			} catch (MalformedURLException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		case Constants.PLAY_NEXT:
			try {
				UserPlayerInterface nextPlayer = (UserPlayerInterface) Naming
						.lookup("rmi://"
								+ this.coreGame.getNextPartecipant(
										this.coreGame.getMyPartecipant()
												.getIp()).getIp()
								+ "/RMIGameClient");
				nextPlayer.initTurn();
			} catch (MalformedURLException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			// result could be END_GAME
			break;

		}

	}

	@Override
	public void initTurn() throws RemoteException {
		List<Move> possibleMoves = this.coreGame.initTurn();
		/* update GUI here showing possible moves passing the list above */
		/* init GUI here */
		String message = null;
		try {
			message = Inet4Address.getLocalHost().getHostAddress() + ": INIT TURN " + System.currentTimeMillis();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		System.out.println(message);
		/* END update GUI here */
		/* TODO togliere questa apply move, va chiamata da interfaccia */
		this.applyMove(null);

	}

	public void applyMove(Move chosenMove) {

		this.coreGame.handleTurn(chosenMove);
		/* update GUI here */

		try {
			UserPlayerInterface nextPlayer = (UserPlayerInterface) Naming
					.lookup("rmi://"
							+ this.coreGame.getNextPartecipant(
									this.coreGame.getMyPartecipant().getIp())
									.getIp() + "/RMIGameClient");
			
			nextPlayer.updateStatus(this.coreGame.getPartecipants(), this.coreGame.getGameBoard());
		} catch (MalformedURLException | NotBoundException | RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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