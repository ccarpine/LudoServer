package sd.core.player;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import sd.core.MainGame;
import sd.ui.Hall;

/* si occupa di registrarsi ed in seguito avviare la partita e visualizzare interfaccia --> elabora il gioco che 
 * che avviene tutto nella classe MainGame */
public class UserPlayer extends UnicastRemoteObject implements
		UserPlayerInterface {

	private static final long serialVersionUID = 1L;
	private static Hall hall;
	private MainGame mainGame;
	private boolean isPlaying;

	public UserPlayer(Hall hall) throws RemoteException {
		this.isPlaying = false;
		//this.hall = hall;
	}

	public void start(List<String> gamersIp) {
		
		System.out.println("UserPlayer starts " + isPlaying);
	
		if (!this.isPlaying) {
			this.isPlaying = true;

			hall.dispose();
			// for (int i=0; i < gamersIp.size(); i++) {
			// System.out.println("la partita ha inizio");
			// }

			mainGame = new MainGame(gamersIp);

		}

	}

	public static void main(String[] args) {
		hall = new Hall(args[0]);

	}
}
