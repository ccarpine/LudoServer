package sd.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sd.util.Constants;

public class GameBoard implements Serializable{

	private static final long serialVersionUID = 1L;
	private Cell[][] cells;

	/** create an empty game board
	 */
	public GameBoard() {
		int victory = Constants.COLUMNS - Constants.BENCH_DIMENSION;
		this.cells = new Cell[Constants.ROWS][Constants.COLUMNS];
		for (int r = 0; r < Constants.ROWS; r++) {
			for (int c = 0; c < Constants.COLUMNS; c++) {
				if (c == 0) {
					this.cells[r][c] = new Cell(Constants.COLOR[r], r, c);
				} else if (c < victory) {
					this.cells[r][c] = new Cell(Constants.BLANK, r, c);
				} else {
					this.cells[r][c] = new Cell(Constants.COLOR[(r + 1)
							% Constants.ROWS], r, c);
				}
			}
		}
	}

	public Cell getCell(int row, int col) {
		return this.cells[row][col];
	}

	/** suggest the possible moves for a given partecipant and a given die
	 * @param partecipant, the partecipant the require the possible moves
	 * @param die, the number of the die
	 * @return the list of possible moves
	 */
	public List<Move> suggestMoves(Partecipant partecipant, int die) {
		List<Move> moves = new ArrayList<Move>();
		List<Cell> cellsOccupiedByPartecipant = this.getPawnsPositionByColor(partecipant.getColor());
		// rules for the value 5 of the die: a new pawn can enter
		//TODO correggi
		if (die > 3 && partecipant.getPawnsInBench() > 0) {
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
		// rules for other values
		if (cellsOccupiedByPartecipant.size() > 0) {
			for (int i = 0; i < cellsOccupiedByPartecipant.size(); i++) {
				Cell startCell = cellsOccupiedByPartecipant.get(i);
				Move move = getMoveByDie(startCell, die, partecipant.getColor());
				if (move != null) {
					moves.add(move);
				}
			}
		}
		for (int j = 0; j<moves.size();j++){
			System.out.println
			("Mosa" + j+ " --a: {" +moves.get(j).getDestination().getRow() + "," + moves.get(j).getDestination().getColumn() +"}");
		}
		
		return moves;
	}

	/** get the cells of the pawns of that color
	 * @param color, the color of pawn
	 * @return List<Cell>, the list of the pawns of that color
	 */
	private List<Cell> getPawnsPositionByColor(String color) {
		List<Cell> cells = new ArrayList<Cell>();
		for (int r = 0; r < Constants.ROWS; r++) {
			for (int c = 0; c < Constants.COLUMNS; c++) {
				if (this.cells[r][c].getPawns().size() > 0) {
					if (this.cells[r][c].getPawns().get(0).equals(color)) {
						cells.add(this.cells[r][c]);
					}
				}
			}
		}
		return cells;
	}

	/** get the next cell
	 * @param currentRow, the row of the current cell
	 * @param currentColumn, the column of current cell
	 * @param currentColorPawn, the color the current
	 * @return Cell, the next cell in gameboard
	 */
	private Cell getNextCell(int currentRow, int currentColumn, String currentColorPawn) {
		// return null if no other moves are possible
		if (currentColumn == Constants.COLUMNS - 1) {
			return null;
		}
		// return the next cell according to position and color of the pawn
		else if (this.cells[currentRow][currentColumn + 1].getColor().equals(currentColorPawn)
				|| this.cells[currentRow][currentColumn + 1].getColor().equals(Constants.BLANK)) {
			return this.cells[currentRow][currentColumn + 1];
		} else {
			return this.cells[(currentRow + 1) % Constants.ROWS][0];
		}
	}

	/** get the move obtain by a launch of die in according to the current cell
	 * @param startCell, the starting cell
	 * @param die, the number of the die
	 * @param partecipantColor, the color of the partecipant
	 * @return Move, return the Move obtained by a launch of die in according to the current cell
	 */
	private Move getMoveByDie(Cell startCell, int die, String partecipantColor) {
		Cell newStartCell = startCell;
		for (int d = 1; d <= die; d++) {
			 Cell nextCell = this.getNextCell(newStartCell.getRow(),newStartCell.getColumn(), newStartCell.getColor());
			if (nextCell != null) {
				int result = nextCell.walkAhead(partecipantColor);
				if (d != die) {
					if (result == Constants.WALL) {
						break;
					}
				} else {
					if (result == Constants.SUCCESS || result == Constants.EATEN) {
						return new Move(startCell, nextCell);
					}
				}
			}
			newStartCell = nextCell;
		}
		return null;
	}

	/** applies a move by the partecipant
	 * @param move, the move that could be applied
	 * @param partecipant, the partecipant that would apply the move
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
			System.out.println("Eaten: "+eaten);
			this.cells[destinationCell.getRow()][destinationCell.getColumn()]
					.addPawn(partecipant.getColor());
			System.out.println("Part: "+partecipant.getColor());
			return eaten;
		} else {
			this.cells[destinationCell.getRow()][destinationCell.getColumn()]
					.addPawn(partecipant.getColor());
			System.out.println("numero di pawns   "+this.cells[destinationCell.getRow()][destinationCell.getColumn()].getPawns().size());
			return null;
		}
	}

	/** check the victory
	 * @param partecipant, the partecipant that checks the victory
	 * @return boolean, the result of the answer about victory
	 */
	public boolean isVictory(Partecipant partecipant) {
		for (int i = Constants.COLUMNS - Constants.BENCH_DIMENSION; i < Constants.COLUMNS; i++) {
			if (this.cells[(partecipant.getColorPosition() + 1)
					% Constants.ROWS][i].getPawns().size() == 0)
				return false;
		}
		return true;
	}

}