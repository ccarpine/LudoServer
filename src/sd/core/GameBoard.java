package sd.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sd.util.Constants;

public class GameBoard implements Serializable{

	private static final long serialVersionUID = 1L;
	private Cell[][] cells;

	/** 
	 * Create an empty game board consisting in the benches cells, the vistory cells and the blank cells
	 */
	public GameBoard() {
		int victory = Constants.COLUMNS - Constants.BENCH_DIMENSION;
		this.cells = new Cell[Constants.ROWS][Constants.COLUMNS];
		for (int r=0; r<Constants.ROWS; r++) {
			for (int c=0; c<Constants.COLUMNS; c++) {
				if (c == 0) {
					this.cells[r][c] = new Cell(Constants.COLOR[r], r, c);
				} else if (c < victory) {
					this.cells[r][c] = new Cell(Constants.BLANK, r, c);
				} else {
					this.cells[r][c] = new Cell(Constants.COLOR[(r + 1) % Constants.ROWS], r, c);
				}
			}
		}
	}

	public Cell getCell(int row, int col) {
		return this.cells[row][col];
	}

	/** 
	 * It suggests the possible moves for a given partecipant and a given die launch
	 * @param partecipant Partecipant that requires the possible moves
	 * @return List<Move>, the list of possible moves
	 */
	public List<Move> suggestMoves(Partecipant partecipant) {
		List<Move> moves = new ArrayList<Move>();
		List<Cell> cellsOccupiedByPartecipant = this.getPawnsPositionByColor(partecipant.getColor());
		// rules for the value 5 or 6 of the die: a new pawn can enter
		if (partecipant.getLastLaunch() >= 5 && partecipant.getPawnsInBench() > 0) {
			Cell startingCell = this.cells[partecipant.getColorPosition()][0];
			int result = startingCell.tryAddPawn(partecipant.getColor());
			switch (result) {
				case Constants.SUCCESS:
					moves.add(new Move(null, startingCell));
					break;
				case Constants.EATEN:
					moves.add(new Move(null, startingCell));
					break;
				default:
					break;
			}
		}
		// rules for other die values
		if (cellsOccupiedByPartecipant.size() > 0) {
			for (int i=0; i<cellsOccupiedByPartecipant.size(); i++) {
				Cell startCell = cellsOccupiedByPartecipant.get(i);
				Move move = getMoveByDie(startCell, partecipant);
				if (move != null) {
					moves.add(move);
				}
			}
		}
		return moves;
	}

	/** 
	 * It gets the cells of the pawns of that color
	 * @param color String with the color of a pawn
	 * @return List<Cell>, the list of the pawns of that color
	 */
	private List<Cell> getPawnsPositionByColor(String color) {
		List<Cell> cells = new ArrayList<Cell>();
		for (int r=0; r<Constants.ROWS; r++) {
			for (int c=0; c<Constants.COLUMNS; c++) {
				if (this.cells[r][c].getPawns().size() > 0) {
					if (this.cells[r][c].getPawns().get(0).equals(color)) {
						cells.add(this.cells[r][c]);
					}
				}
			}
		}
		return cells;
	}

	/** 
	 * It gets the next cell
	 * @param currentRow Integer of the row of the current cell
	 * @param currentColumn Integer with the column of current cell
	 * @param currentColorPawn String with the color of the current player
	 * @return Cell, the next cell in gameboard
	 */
	private Cell getNextCell(int currentRow, int currentColumn, String currentColorPawn) {
		// return null if no other moves are possible
		if (currentColumn == Constants.COLUMNS - 1) {
			return null;
		// return the next cell according to position and color of the pawn
		} else if (this.cells[currentRow][currentColumn + 1].getColor().equals(currentColorPawn)
				|| this.cells[currentRow][currentColumn + 1].getColor().equals(Constants.BLANK)) {
			return this.cells[currentRow][currentColumn + 1];
		} else {
			return this.cells[(currentRow + 1) % Constants.ROWS][0];
		}
	}

	/** 
	 * It gets the move obtained by a launch of die in according to the current cell
	 * @param startCell Cell the starting cell
	 * @èaram partecipant Partecipant which is the interested partecipant
	 * @return Move, return the Move obtained by a launch of die in according to the current cell
	 */
	private Move getMoveByDie(Cell startCell, Partecipant partecipant) {
		Cell newStartCell = startCell;
		int die = partecipant.getLastLaunch();
		String partecipantColor = partecipant.getColor();
		for (int d=1; d<=die; d++) {
			 Cell nextCell = this.getNextCell(newStartCell.getRow(),newStartCell.getColumn(), partecipantColor);
			if (nextCell != null) {
				if (d != die) {
					if (nextCell.walkAhead(partecipantColor) == Constants.WALL) {
						break;
					}
				} else {
					int result = nextCell.tryAddPawn(partecipantColor);
					if (result == Constants.SUCCESS || result == Constants.EATEN) {
						return new Move(startCell, nextCell);
					}
				}
			} else {
				return null;
			}
			newStartCell = nextCell;
		}
		return null;
	}

	/** 
	 * It applies a move chosen by the partecipant
	 * @param move Move that is going to be applied
	 * @param partecipant Partecipant that will apply the move
	 * @return String, the color of eatean pawn if present
	 */
	public String makeMove(Move move, Partecipant partecipant) {
		if (move == null) {
			return null;
		}
		Cell startingCell = move.getStart();
		Cell destinationCell = move.getDestination();
		int result = this.cells[destinationCell.getRow()][destinationCell
				.getColumn()].tryAddPawn(partecipant.getColor());
		if (startingCell != null) {
			this.cells[startingCell.getRow()][startingCell.getColumn()].getPawns()
					.remove(0);
		}
		if (result == Constants.EATEN) {
			String eaten = this.cells[destinationCell.getRow()][destinationCell
					.getColumn()].getPawns().get(0);
			this.cells[destinationCell.getRow()][destinationCell.getColumn()]
					.addPawn(partecipant.getColor());
			return eaten;
		} else {
			this.cells[destinationCell.getRow()][destinationCell.getColumn()]
					.addPawn(partecipant.getColor());
			return null;
		}
	}

	/** 
	 * It checks the victory for a given partecipant
	 * @param partecipant Partecipant whose to check the victory
	 * @return boolean, the result of the answer about victory
	 */
	public boolean isVictory(Partecipant partecipant) {
		for (int i=Constants.COLUMNS-Constants.BENCH_DIMENSION; i<Constants.COLUMNS; i++) {
			if (this.cells[(partecipant.getColorPosition() - 1 + Constants.ROWS) % Constants.ROWS]
					[i].getPawns().size() == 0) {
				return false;
			}
		}
		return true;
	}
	
	public void clearPawnByColor(String color) {
		for (int r=0; r<Constants.ROWS; r++) {
			for (int c=0; c<Constants.COLUMNS; c++) {
				int pawns = cells[r][c].getPawns().size();
				for (int p=0; p<pawns; p++) {
					if (cells[r][c].getPawns().get(0).equals(color)) {
						cells[r][c].getPawns().remove(0);
					}
				}
			}
		}
	}

}