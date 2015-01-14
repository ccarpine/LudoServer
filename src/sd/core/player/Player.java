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

import sd.core.register.RegisterInterface;

public class Player extends UnicastRemoteObject implements PlayerInterface {
	
	private static final long serialVersionUID = 1L;
	private List<String> gamersIp;
	private boolean isPlaying;
	
	public Player() throws RemoteException {
		this.isPlaying =  false;
		this.gamersIp = new ArrayList<String>();
	}
	
	public void start(List<String> gamersIp){
		if (!this.isPlaying){
			this.isPlaying = true;
			this.gamersIp = gamersIp;
			for (int i=0; i < this.gamersIp.size(); i++) {
				System.out.println("la partita ha inizio");
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			PlayerInterface client = (PlayerInterface) new Player();
			/* get the ip */
			String ipAddress = Inet4Address.getLocalHost().getHostAddress();
			Naming.rebind("//"+ipAddress+"/RMIGameClient", client);
			RegisterInterface server = (RegisterInterface) Naming.lookup("rmi://" + args[0] + "/RMILudoServer");
			//RegisterInterface server = (RegisterInterface) Naming.lookup("rmi://localhost/RMILudoServer");
			
			long timeToStart = server.register(ipAddress);
			System.out.println("CLIENT ---- time to start:" + timeToStart);
			System.out.println("CLIENT ---- Ip address:" + ipAddress);
			System.out.println("CLIENT ---- Ip address:" + ipAddress);
		} catch (NotBoundException | UnknownHostException |RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
}
