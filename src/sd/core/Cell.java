package sd.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sd.util.Constants;

public class Cell implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<String> pawns;
	private String color;
	private int row;
	private int column;

	/**  
	 * Create a new empty cell
	 * @param color, the color of the cell 
	 * @param row, the row of the cell
	 * @param column, the column of the cell
	 */
	public Cell(String color, int row, int column) {
		this.pawns = new ArrayList<String>();
		this.color = color;
		this.row = row;
		this.column = column;
	}

	public List<String> getPawns() {
		return this.pawns;
	}

	public String getColor() {
		return color;
	}

	public int getRow() {
		return row;
	}

	public int getColumn() {
		return column;
	}

	/** 
	 * Insert a pawn into a cell if is possible
	 * @param colorPawn, the color of the pawn over the cell
	 * @return int, the consequence of inserting a pawn in this cell
	 */
	public int addPawn(String colorPawn) {
		int result = this.tryAddPawn(colorPawn);
		switch (result) {
			case Constants.SUCCESS:
				this.pawns.add(colorPawn);
				break;
			case Constants.EATEN:
				this.pawns.set(0, colorPawn);
				break;
			default:
				break;
		}
		return result;
	}

	/** 
	 * Rules to check if it is possible to add pawn
	 * @param colorPawn, the color of the pawn that could be inserted
	 * @return int, the simulated consequence of inserting a pawn in this cell
	 */
	public int tryAddPawn(String colorPawn) {
		// rules if it is a goal of another
		if (!this.color.equals(colorPawn) && !this.color.equals(Constants.BLANK) && this.column != 0) {
				return Constants.FAIL;
		// rules if it is your goal
		} else if (this.color.equals(colorPawn)) {
			if (this.pawns.size() == 0) {
				return Constants.SUCCESS;
			} else if (this.pawns.size() == 1 && this.column == 0) {
				if (this.pawns.get(0).equals(colorPawn)){
					return Constants.SUCCESS;
				} else {
					return Constants.EATEN;
				}
			} else {
				return Constants.FAIL;
			}
		// rules if it is a blank cell
		} else if (this.pawns.size() == 0) {
			return Constants.SUCCESS;
		// rules if it is a busy cell occupied by myself
		} else if (this.pawns.size() == 1 && this.pawns.get(0).equals(colorPawn)) {
			return Constants.SUCCESS;
		// rules if there is a wall
		} else if (this.pawns.size() == 2) {
			return Constants.WALL;
		// rules if it is a busy cell occupied by another
		} else
			return Constants.EATEN;
	}

	/** 
	 * Rules if is possible to walk ahead
	 * @param colorPawn, the color of the pawn that could pass this cell
	 * @return int, the simulated consequence of passing this cell
	 */
	public int walkAhead(String colorPawn) {
		// rules if it is possible to go ahead
		if (this.pawns.size() <= 1) {
			return Constants.SUCCESS;
		} else if (this.pawns.get(0).equals(colorPawn)) {
			return Constants.SUCCESS;
		// rules if a wall is present not belonging to myself
		} else {
			return Constants.WALL;
		}
	}

}