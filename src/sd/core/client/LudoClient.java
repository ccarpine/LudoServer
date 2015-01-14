package sd.core.client;

import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import sd.core.server.ServerInterface;

public class LudoClient extends UnicastRemoteObject implements ClientInterface {
	
	private static final long serialVersionUID = 1L;
	private List<String> gamersIp;
	private boolean isPlaying;
	
	public LudoClient() throws RemoteException {
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
			ClientInterface client = (ClientInterface) new LudoClient();
			Naming.rebind("//localhost/RMIGameClient", client);
			//ServerInterface server = (ServerInterface) Naming.lookup("rmi://" + serverIp + "/RMILudoServer");
			ServerInterface server = (ServerInterface) Naming.lookup("rmi://localhost/RMILudoServer");
			/* get the ip */
			String ipAddress = Inet4Address.getLocalHost().getHostAddress();
			long timeToStart = server.register(ipAddress);
			System.out.println("CLIENT ---- time to start:" + timeToStart);
			System.out.println("CLIENT ---- Ip address:" + ipAddress);
		} catch (NotBoundException | UnknownHostException |RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
		
	}
}
