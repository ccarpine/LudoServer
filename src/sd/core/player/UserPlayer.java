package sd.core.player;

import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;


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
			
			try {
				System.out.println("1 -->" +Inet4Address.getLocalHost().getHostAddress() );
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
	public void updateStatus(List<Partecipant> partecipants, GameBoard gameBoard, String ipCurrentPartecipant) throws RemoteException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		int result = this.coreGame.updateStatus(partecipants, gameBoard, ipCurrentPartecipant);
		/* update GUI here */
		System.out.println("3 UPDATE RECEIVED -->");
		/* END update GUI here */
		
		switch (result) {
		case Constants.UPDATE_NEXT:
			System.out.println("4 UPDATE SEND ("+ result +")-->" +this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp() );
			this.updateNext(partecipants, gameBoard, ipCurrentPartecipant);
			break;

		case Constants.PLAY_NEXT:
			System.out.println("5 INIT TURN SEND ("+ result +")-->" +this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp() + "/RMIGameClient" );
			this.playNext();
			break;

		default:
			// result could be END_GAME
			break;

		}

	}

	private void playNext() {
			try {
				UserPlayerInterface nextPlayer = (UserPlayerInterface) Naming.lookup("rmi://"+ this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp()+ "/RMIGameClient");
				nextPlayer.initTurn();
			} catch (RemoteException |MalformedURLException |NotBoundException e) {
				e.printStackTrace();
			} 
	}
	
	private void updateNext(List<Partecipant> partecipants, GameBoard gameBoard, String ipCurrentPartecipant){
			try {
				UserPlayerInterface nextPlayer = (UserPlayerInterface) Naming.lookup("rmi://"+ this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp()+ "/RMIGameClient");
				nextPlayer.updateStatus(partecipants, gameBoard, ipCurrentPartecipant);
			} catch (MalformedURLException | NotBoundException |RemoteException e1) {
				e1.printStackTrace();
			}
			
	}
	@Override
	public void initTurn() throws RemoteException {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		System.out.println("2 INIT TURN RECEIVED -->" );
		List<Move> possibleMoves = this.coreGame.initTurn();
		/* update GUI here showing possible moves passing the list above */
		/* init GUI here */
		/* END update GUI here */
		/* TODO togliere questa apply move, va chiamata da interfaccia */
		this.applyMove(null);

	}

	public void applyMove(Move chosenMove) {
		this.coreGame.handleTurn(chosenMove);
		/* update GUI here */
		System.out.println("MAKE MOVE");
		System.out.println("4 UPDATE SEND -->" +this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp() );
		this.updateNext(this.coreGame.getPartecipants(), this.coreGame.getGameBoard(), this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp());
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