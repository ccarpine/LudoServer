package sd.core.register;

import java.io.IOException;
import java.net.Inet4Address;
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

	/** 
	 * It allows a player to register in order to take part into a match
	 * @param String, the ip address of the invoking player
	 * @return long, the remaining time for the beginning of the match
	 * @throws RemoteException
	 */
	public long register(String clientIp) throws RemoteException {
		return this.registerSynch(clientIp);
	}

	/**
	 * It starts the timer to wait to accept further registrations before the match begins
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
					}
					counter += 1000;
				}
				startGame();
				initVariable();
			}
		}).start();
	}

	/**
	 * It initialized the variables used for the registration
	 */
	private void initVariable() {
		this.counter = 0;
		this.gamersIp = new ArrayList<String>();
		this.readyToPlay = false;
		this.resetTimer = false;
	}

	/**
	 * Allows the registered player to start the match
	 * @throws RemoteException 
	 */
	private void startGame() {
		List<UserPlayerInterface> UsersPlayer = new ArrayList<UserPlayerInterface>();
		// loockup with all gamers and send request to all
		for (int i=0; i<this.gamersIp.size(); i++) {
			try {
				Registry registry = LocateRegistry.getRegistry(this.gamersIp.get(i), 6000);
				UsersPlayer.add((UserPlayerInterface) registry.lookup("rmi://"
						+ this.gamersIp.get(i) + "/RMIGameClient"));
			} catch (RemoteException | NotBoundException e) {
				System.out.println("The user "+this.gamersIp.get(i)+" is not reacheable");
				this.gamersIp.remove(i);
				i--;
			}
		}
		for (int i=this.gamersIp.size()-1; i >= 0; i--) {
			try {
				UsersPlayer.get(i).start(this.gamersIp);
			} catch (RemoteException e) {
				System.out.println("The user "+this.gamersIp.get(i)+" is not reacheable");
			}
		}
	}

	/**
	 * Register a gamer for the match. This method needs to be synchronized to prevnts race conditions
	 * in accessing the list of the player waiting for the match to start
	 * @param clientIp, the ip address of the gamer
	 * @return long, the remaining time for the star of the match
	 */
	private synchronized long registerSynch(String clientIp) {
		if (!this.getPresenceIp(clientIp)) {
			if (this.gamersIp.size() == 0) {
				this.startTimer();
			}
			/* add the partecipant ip to the list */
			this.gamersIp.add(clientIp);
			System.out.println("SERVER ---- Client Ip: "+clientIp
					+" -- Client registred for the match: "
					+this.gamersIp.size());
			System.out.println("------------------------");
			/* partecipant limit reached, start the game */
			if (this.gamersIp.size() == Constants.MAX_PLAYER) {
				this.readyToPlay = true;
			}
		}
		System.out.println("Time to start: "
				+(Constants.MAX_WAIT_FOR_MATCH - this.counter));
		return (Constants.MAX_WAIT_FOR_MATCH - this.counter);
	}

	/**
	 * It checks the presence of a player to avoid the registration of the same one
	 * twice
	 * 
	 * @param String, the ip address of the gamer
	 * @return boolean, the result of that control
	 */
	private boolean getPresenceIp(String ip) {
		for (int i=0; i<this.gamersIp.size(); i++) {
			if (ip.equals(this.gamersIp.get(i))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void deletePartecipant(String ip) throws RemoteException {
		for (int i=0; i<this.gamersIp.size(); i++) {
			if (this.gamersIp.get(i).equals(ip)) {
				this.gamersIp.remove(i);
				break;
			}
		}
		/**
		 * If the one and only client registered for the match leaves then the timer has to be
		 * restarted
		 */
		if (this.gamersIp.size() == 0) {
			this.resetTimer = true;
			this.startTimer();
		}
		System.out.println("The user "+ip+" exited");

	}

	/**
	 * The main function that allows the server to be attainable from clients
	 * @throws IOException
	 */
	public static void main(String[] args) {
		try {
			RegisterInterface server = (RegisterInterface) new Register();
			Registry registry = LocateRegistry.createRegistry(6000);
			registry.rebind("rmi://"+Inet4Address.getLocalHost().getHostAddress()+"/RMILudoServer", server);
		} catch (IOException e) {
			System.out.println("RMI Registry not called");
		}
	}

}