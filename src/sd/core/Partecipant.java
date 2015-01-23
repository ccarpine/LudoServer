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
	
	
	public Partecipant(String ip, String color, int colorPosition) {

		this.ip = ip;
		this.color = color;
		this.pawnsInBench = Constants.BENCH_DIMENSION;
		this.colorPosition = colorPosition;

	}

	public int getPawnsInBench() {
		return pawnsInBench;
	}

	public void addPawnsInBench() {
		if (this.pawnsInBench < Constants.BENCH_DIMENSION)
			this.pawnsInBench++;
	}

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
	
	public boolean isMine() {
		try {
			return this.ip.equals(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			return false;
		}
	}

}
