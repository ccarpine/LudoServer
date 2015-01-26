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

	/**
	 * Creates a user game player taking part into the game and setting his
	 * bench as full
	 * 
	 * @param ip, the ip address of that game player
	 * @param color, the color of the pawns assigned to that game player
	 * @param colorPosition, the position of the color in the relative array
	 */
	public Partecipant(String ip, String color, int colorPosition) {
		this.ip = ip;
		this.color = color;
		this.pawnsInBench = Constants.BENCH_DIMENSION;
		this.colorPosition = colorPosition;
	}

	public int getPawnsInBench() {
		return pawnsInBench;
	}

	/**
	 * increments of 1 the number of the partecipant's pawns in his bench
	 */
	public void addPawnsInBench() {
		if (this.pawnsInBench < Constants.BENCH_DIMENSION)
			this.pawnsInBench++;
	}

	/**
	 * decrements of 1 the number of the partecipant's pawns in his bench
	 */
	public void removePawnsInBench() {
		if (this.pawnsInBench > 0)
			this.pawnsInBench--;
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
	 * verifies if the partecipant ip is equal to the one of the machine this method is invokated in
	 */
	public boolean isMine() {
		try {
			return this.ip.equals(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			return false;
		}
	}

}