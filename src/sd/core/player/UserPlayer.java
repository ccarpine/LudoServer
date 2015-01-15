package sd.core.player;

import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import sd.core.MainGame;
import sd.core.register.RegisterInterface;

/* si occupa di registrarsi ed in seguito avviare la partita e visualizzare interfaccia --> elabora il gioco che 
 * che avviene tutto nella classe MainGame */
public class UserPlayer extends UnicastRemoteObject implements UserPlayerInterface {
	
	private static final long serialVersionUID = 1L;
	private MainGame mainGame;
	private boolean isPlaying;
	
	public UserPlayer() throws RemoteException {
		this.isPlaying =  false;
	}
	
	public void start(List<String> gamersIp){
		
		if (!this.isPlaying){
			this.isPlaying = true;
			
//			for (int i=0; i < gamersIp.size(); i++) {
//				System.out.println("la partita ha inizio");
//			}
			
			mainGame = new MainGame(gamersIp);
			
		}		
		
	}
	
	public static void main(String[] args) {
		try {
			UserPlayerInterface client = (UserPlayerInterface) new UserPlayer();
			/* get the ip */
			String ipAddress = Inet4Address.getLocalHost().getHostAddress();
			Naming.rebind("//"+ipAddress+"/RMIGameClient", client);
			RegisterInterface server = (RegisterInterface) Naming.lookup("rmi://" + args[0] + "/RMILudoServer");
			
			
			long timeToStart = server.register(ipAddress);
			System.out.println("CLIENT ---- time to start:" + timeToStart);
			System.out.println("CLIENT ---- Ip address:" + ipAddress);
			System.out.println("CLIENT ---- Ip address:" + ipAddress);
		} catch (NotBoundException | UnknownHostException |RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
}
