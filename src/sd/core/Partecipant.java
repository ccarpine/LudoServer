package sd.core;

import sd.util.Constants;

public class Partecipant {

	private int pawnsInBench;
	private String myIp;
	private String myColor;
	

	public Partecipant(String ip, String color) {

		this.myIp = ip;
		this.myColor = color;
		this.pawnsInBench = Constants.BENCH_DIMENSION;

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

	public String getMyIp() {
		return myIp;
	}

	public String getMyColor() {
		return myColor;
	}


}
