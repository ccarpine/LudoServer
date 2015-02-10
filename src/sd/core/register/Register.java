package sd.core.register;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import sd.core.player.UserPlayerInterface;
import sd.util.Constants;

public class Register extends UnicastRemoteObject implements RegisterInterface {

	private static final long serialVersionUID = 1L;
	private List<String> gamersIp;
	private long counter;
	private boolean readyToPlay;
	private boolean resetTimer;

	protected Register() throws RemoteException {
		this.initVariable();
	}

	public long register(String clientIp) throws RemoteException {
		return this.registerSynch(clientIp);
	}

	/**
	 * start the timer for the start of the match
	 */
	private void startTimer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!readyToPlay && counter < Constants.MAX_WAIT_FOR_MATCH
						&& !resetTimer) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					counter += 1000;
				}
				if (resetTimer)
					System.out.println("reset del timer!!");
				else
					System.out.println("readyToPlay " + readyToPlay
							+ " - counter " + counter);
				startGame();
				initVariable();
			}
		}).start();
	}

	/**
	 * init the variable used for the registration
	 */
	private void initVariable() {
		this.counter = 0;
		this.gamersIp = new ArrayList<String>();
		this.readyToPlay = false;
		this.resetTimer = false;
	}

	/**
	 * allows the registred player to start the match
	 */
	private void startGame() {
		List<UserPlayerInterface> UsersPlayer = new ArrayList<UserPlayerInterface>();
		// loockup with all gamers and send request to all
		for (int i = 0; i < this.gamersIp.size(); i++) {
			try {
				UsersPlayer.add((UserPlayerInterface) Naming.lookup("rmi://"
						+ this.gamersIp.get(i) + "/RMIGameClient"));
			} catch (MalformedURLException | RemoteException
					| NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = this.gamersIp.size() - 1; i >= 0; i--) {
			try {
				UsersPlayer.get(i).start(this.gamersIp);
			} catch (RemoteException e) {
				System.out.println("L'utente " + this.gamersIp.get(i)
						+ " non è raggiungibile!");
			}
		}
	}

	/**
	 * register a gamer for the match
	 * 
	 * @param clientIp
	 *            , the ip pf the gamer
	 * @return long, the remaining time for the star of the match
	 */
	private synchronized long registerSynch(String clientIp) {
		if (!this.getPresenceIp(clientIp)) {
			if (this.gamersIp.size() == 0) {
				this.startTimer();
			}
			/* add the partecipant ip to the list */
			this.gamersIp.add(clientIp);
			System.out.println("SERVER ---- Client Ip:" + clientIp
					+ " -- Client registrati per la partita:"
					+ this.gamersIp.size());
			System.out.println("------------------------");
			/* partecipant limit reached, start the game */
			if (this.gamersIp.size() == Constants.MAX_PLAYER) {
				this.readyToPlay = true;
			}
		}
		System.out.println("Time to start"
				+ (Constants.MAX_WAIT_FOR_MATCH - this.counter));
		return (Constants.MAX_WAIT_FOR_MATCH - this.counter);
	}

	/**
	 * check the present of a player to avoid the registration of the same one
	 * twice
	 * 
	 * @param ip
	 *            , the ip of the gamer
	 * @return boolean, the result of that control
	 */
	private boolean getPresenceIp(String ip) {
		for (int i = 0; i < this.gamersIp.size(); i++) {
			if (ip.equals(this.gamersIp.get(i))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void deletePartecipant(String ip) throws RemoteException {
		for (int i = 0; i < this.gamersIp.size(); i++) {
			if (this.gamersIp.get(i).equals(ip)) {
				this.gamersIp.remove(i);
				break;
			}
		}
		/*
		 * if the only client register for the match leave, the timer have to be
		 * restarted
		 */
		if (this.gamersIp.size() == 0) {
			this.resetTimer = true;
			this.startTimer();
		}

		System.out.println(ip + " exited");

	}

	/**
	 * the main that allow the server to reacheable for a client
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) {
		try {
			RegisterInterface server = (RegisterInterface) new Register();
			Registry registry = null;
			try {
				System.out.println("Provo la Create registry");
				registry = LocateRegistry.createRegistry(1099);
			} catch (ConnectException e) {
				System.out.println("Create registry fallita");
				registry = LocateRegistry.getRegistry(1099);
				System.out.println("Get registry riuscita");
			} finally {
				// registry.rebind("RMILudoServer", server);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}