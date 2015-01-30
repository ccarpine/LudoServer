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
	private long counter;
	private boolean readyToPlay;

	
	protected Register() throws RemoteException {
		this.initVariable();
	}

	public long register(String clientIp) throws RemoteException {
		return this.registerSynch(clientIp);
	}

	/** start the timer for the start of the match
	 */
	private void startTimer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!readyToPlay && counter < Constants.MAX_WAIT_FOR_MATCH) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					counter += 1000;
				}
				startGame();
				initVariable();
			}
		}).start();
	}

	/** init the variable used for the registration
	 */
	private void initVariable() {
		this.counter = 0;
		this.gamersIp = new ArrayList<String>();
		this.readyToPlay = false;
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
				UsersPlayer.get(i).start(this.gamersIp);
				System.out.println("call for the start for the IP"+ this.gamersIp.get(i));
				
			} catch (RemoteException e) {
				System.out.println("L'utente " + this.gamersIp.get(i) + " non è raggiungibile!");
				//e.printStackTrace();
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
				this.readyToPlay = true;
			}
		}
		System.out.println("Time to start"+ (Constants.MAX_WAIT_FOR_MATCH - this.counter));
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

	@Override
	public void deletePartecipant(String ip) throws RemoteException {
		for(int i=0; i<this.gamersIp.size(); i++) {
			if(this.gamersIp.get(i).equals(ip)) {
				this.gamersIp.remove(i);
				break;
			}
		}
		
		System.out.println(ip + " exited");
		
	}

}