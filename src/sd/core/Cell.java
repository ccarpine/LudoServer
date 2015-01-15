package sd.core;

import java.util.ArrayList;
import java.util.List;

import sd.util.Constants;

public class Cell {

	private List<String> pawns;

	public Cell() {
		this.pawns = new ArrayList<String>();
	}

	public List<String> getPawns() {
		return this.pawns;
	}

	public int addPawn(String colorPawn) {

		if (this.pawns.size() == 0) {
			this.pawns.add(colorPawn);
			return Constants.SUCCESS;
		}

		else {

			if (this.pawns.size() == 1 && this.pawns.get(0).equals(colorPawn)) {
				this.pawns.add(colorPawn);
				return Constants.SUCCESS;
			}

			else if (this.pawns.size() == 2)
				return Constants.WALL;

			else {
				/*
				 * bisogna salvare in una var temporanea chi c'era nella cella
				 * in modo da rispedirlo in panchina
				 */
				this.pawns.set(0, colorPawn);
				return Constants.EATEN;
			}

		}
	}

	public int walkAhead(String colorPawn) {

		if (this.pawns.size() <= 1)
			return Constants.SUCCESS;

		else if (this.pawns.get(0).equals(colorPawn))
			return Constants.SUCCESS;
		else
			return Constants.WALL;

	}

}
