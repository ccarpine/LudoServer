package sd.core;

import java.io.Serializable;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import sd.util.Constants;

public class Partecipant implements Serializable {

	private static final long serialVersionUID = 1L;
	private int pawnsInBench;
	private String ip;
	private String color;
	private int colorPosition;
	private int lastLaunch;
	private boolean statusActive;

	/** 
	 * It creates a user game player taking part into the game and setting his
	 * bench as full
	 * @param ip String with the ip address of that game player
	 * @param color String with the color of the pawns assigned to that game player
	 * @param colorPosition Integer representing the position of the color in the relative array
	 */
	public Partecipant(String ip, String color, int colorPosition) {
		this.ip = ip;
		this.color = color;
		this.pawnsInBench = Constants.BENCH_DIMENSION;
		this.colorPosition = colorPosition;
		this.lastLaunch = 0;
		this.statusActive = true;
	}

	/**
	 * 
	 * @return the number of remained pawns in bench for a player
	 */
	public int getPawnsInBench() {
		return pawnsInBench;
	}

	/**
	 * It increments of 1 the number of the partecipant's pawns in his bench
	 */
	public void addPawnsInBench() {
		if (this.pawnsInBench < Constants.BENCH_DIMENSION) {
			this.pawnsInBench++;
		}
	}

	/**
	 * It decrements of 1 the number of the partecipant's pawns in his bench
	 */
	public void removePawnsInBench() {
		if (this.pawnsInBench > 0) {
			this.pawnsInBench--;
		}
	}

	public String getIp() {
		return ip;
	}

	public String getColor() {
		return color;
	}

	public int getColorPosition() {
		return colorPosition;
	}
	
	/**
	 * 
	 * @return the value of the last die launch for the Partecipant
	 */
	public int getLastLaunch() {
		return lastLaunch;
	}

	/**
	 * 
	 * @param int, it sets the value of the last die launch for the partecipant
	 */
	public void setLastLaunch(int lastLaunch) {
		this.lastLaunch = lastLaunch;
	}

	/**
	 * 
	 * @return boolean, it checks if the Partecipant is still playing or has crashed
	 */
	public boolean isStatusActive() {
		return statusActive;
	}

	/**
	 * @param status boolean which sets the status of the player: true if he is still playing/active; 
	 * otherwise false if it has crashed
	 */
	public void setStatusActive(boolean status) {
		this.statusActive = status;
	}

	/**
	 * @return boolean, it verifies if the partecipant ip is equal to the one of the machine 
	 * this method is invokated in
	 */
	public boolean isMine() {
		try {
			return this.ip.equals(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			return false;
		}
	}

}