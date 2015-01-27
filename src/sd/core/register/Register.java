package sd.core.register;

import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import sd.core.player.UserPlayerInterface;
import sd.util.Constants;

public class Register extends UnicastRemoteObject implements RegisterInterface {

	private static final long serialVersionUID = 1L;
	private List<String> gamersIp;
	private Thread ludoChronometer;
	private long counter;

	
	protected Register() throws RemoteException {
		this.initVariable();
	}

	public long register(String clientIp) throws RemoteException {
		return this.registerSynch(clientIp);
	}

	/** start the timer for the start of the match
	 */
	private void startTimer() {
		this.ludoChronometer = new Thread(new Runnable() {
			@Override
			public void run() {
				counter = 0;
				long startedAt = System.currentTimeMillis();
				while (counter < Constants.MAX_WAIT_FOR_MATCH) {
					counter = System.currentTimeMillis() - startedAt;
				}
				startGame();
				initVariable();
			}
		});
		this.ludoChronometer.start();
	}

	/** end the timer for the start of the match
	 */
	private void endTimer() {
		this.ludoChronometer.interrupt();
	}

	/** init the variable used for the registration
	 */
	private void initVariable() {
		this.gamersIp = new ArrayList<String>();
	}

	/** allows the registred player to start the match
	 */
	private void startGame() {
		List<UserPlayerInterface> UsersPlayer = new ArrayList<UserPlayerInterface>();
		// loockup with all gamers
		for (int i=0; i<this.gamersIp.size(); i++) {
			try {
				UsersPlayer.add((UserPlayerInterface) Naming.lookup("rmi://" + this.gamersIp.get(i) + "/RMIGameClient"));
			} catch (MalformedURLException | RemoteException | NotBoundException e) {
				e.printStackTrace();
			}
		}
		// send the request of start to all gamers
		for (int i=this.gamersIp.size()-1; i>=0; i--) {
			try {
				System.out.println("call for the start for the IP"+ UsersPlayer.get(i));
				UsersPlayer.get(i).start(this.gamersIp);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/** register a gamer for the match
	 * @param clientIp, the ip pf the gamer
	 * @return long, the remaining time for the star of the match
	 */
	private synchronized long registerSynch(String clientIp) {
		if (!this.getPresenceIp(clientIp)) {
			if (this.gamersIp.size() == 0) {
				this.startTimer();
			}
			/* add the partecipant ip to the list */
			this.gamersIp.add(clientIp);
			System.out.println("SERVER ---- Client Ip:" + clientIp);
			System.out.println("------------------------");
			System.out.println("SERVER ---- Client registrati per la partita:" + this.gamersIp.size());
			System.out.println("------------------------");
			/* partecipant limit reached, start the game */
			if (this.gamersIp.size() == Constants.MAX_PLAYER) {
				this.endTimer();
				this.startGame();
				this.initVariable();
				return 0;
			}
		}
		return (Constants.MAX_WAIT_FOR_MATCH - this.counter);
	}
	
	/** check the present of a player to avoid the registration of the same one twice
	 * @param ip, the ip of the gamer
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
	
	/** the main that allow the server to reacheable for a client
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			RegisterInterface server = (RegisterInterface) new Register();
			String ipAddress = Inet4Address.getLocalHost().getHostAddress();
			Naming.rebind("//" + ipAddress + "/RMILudoServer", server);
		} catch (RemoteException | MalformedURLException | UnknownHostException e) {
			e.printStackTrace();
		}
	}

}