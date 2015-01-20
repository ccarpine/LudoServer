package sd.core;

import java.util.ArrayList;
import java.util.List;

import sd.util.Constants;

public class GameBoard {

	private Cell[][] cells;

	public GameBoard() {
		this.cells = new Cell[Constants.ROWS][Constants.COLUMNS];
		for (int r = 0; r < Constants.ROWS; r++) {
			for (int c = 0; c < Constants.COLUMNS; c++) {
				if (c < (Constants.COLUMNS - Constants.BENCH_DIMENSION)) {
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

	// returns the list of possible moves according to the value of the die for
	// a specific partecipant
	public List<Move> suggestMoves(Partecipant partecipant, int die) {
		List<Move> moves = new ArrayList<Move>();
		List<Cell> cellsOccupiedByPartecipant = this
				.getPawnsPositionByColor(partecipant.getColor());

		// rules for the value 5 of the die: a new pawn can enter
		if (die == 5 && partecipant.getPawnsInBench() > 0) {
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

		// rules for any other value of the die: if 6 the partecipant can move
		// on of 12 or 6 cells
		if (cellsOccupiedByPartecipant.size() != 0) {

			for (int i = 0; i < cellsOccupiedByPartecipant.size(); i++) {
				Cell startCell = cellsOccupiedByPartecipant.get(i);
				Move move = getMoveByDie(startCell, die, partecipant.getColor());
				if (move != null) {
					moves.add(move);
				}
				if (die == 6) {
					Move secondMove = getMoveByDie(startCell, die,
							partecipant.getColor());
					if (secondMove != null) {
						moves.add(secondMove);
					}
				}
			}

		}

		return moves;

	}

	// return all cells of the pawns of that color
	private List<Cell> getPawnsPositionByColor(String color) {
		List<Cell> cells = new ArrayList<Cell>();
		for (int r = 0; r < Constants.ROWS; r++) {
			for (int c = 0; c < Constants.COLUMNS; c++) {
				if (this.cells[r][c].getColor().equals(color)) {
					cells.add(this.cells[r][c]);
				}
			}
		}
		return cells;
	}

	// return the next possible cell
	private Cell getNextCell(int currentRow, int currentColumn,
			String currentColorPawn) {

		// return null if no other moves are possible
		if (currentColumn == Constants.COLUMNS - 1) {
			return null;
		}

		// return the next cell according to position and color of the pawn
		else if (this.cells[currentRow][currentColumn + 1].getColor().equals(
				currentColorPawn)
				|| this.cells[currentRow][currentColumn + 1].getColor().equals(
						Constants.BLANK)) {
			return this.cells[currentRow][currentColumn + 1];
		} else {
			return this.cells[(currentRow + 1) % Constants.ROWS][0];
		}

	}

	// returns the move obtained by the value of the die for that partecipant
	// starting from a specific cell
	private Move getMoveByDie(Cell startCell, int die, String partecipantColor) {
		for (int d = 1; d <= die; d++) {
			Cell nextCell = this.getNextCell(startCell.getRow(),
					startCell.getColumn(), startCell.getColor());
			if (nextCell != null) {
				int result = nextCell.walkAhead(partecipantColor);
				if (d != die) {
					if (result == Constants.WALL) {
						break;
					}
				} else {
					if (result == Constants.SUCCESS
							|| result == Constants.EATEN) {
						return new Move(startCell, nextCell);
					}
				}
			}
		}
		return null;
	}

	// applies the choosen move by the partecipant and return the color of eaten
	// pawn if present
	public String makeMove(Move move, Partecipant partecipant) {
		if (move == null) {
			return null;
		}
		Cell startingCell = move.getStart();
		Cell destinationCell = move.getDestination();

		int result = this.cells[destinationCell.getRow()][destinationCell
				.getColumn()].tryAddPawn(partecipant.getColor());
		this.cells[startingCell.getRow()][startingCell.getColumn()].getPawns()
				.remove(0);

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

	// check if the partecipant wins
	public boolean isVictory(Partecipant partecipant) {

		for (int i = Constants.COLUMNS - Constants.BENCH_DIMENSION; i < Constants.COLUMNS; i++) {
			if (this.cells[(partecipant.getColorPosition() + 1)
					% Constants.ROWS][i].getPawns().size() == 0)
				return false;
		}

		return true;

	}

}