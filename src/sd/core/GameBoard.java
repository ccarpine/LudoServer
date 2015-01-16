package sd.core;

import java.util.ArrayList;
import java.util.List;

import sd.util.Constants;


public class GameBoard {
	
	private Cell[][] cells;
	
	public GameBoard() {
		this.cells = new Cell[Constants.ROWS][Constants.COLUMNS];
		for (int r=0; r<Constants.ROWS; r++) {
			for (int c=0; c<Constants.COLUMNS; c++) {
				if (c < (Constants.COLUMNS - Constants.BENCH_DIMENSION)) {
					this.cells[r][c] = new Cell(Constants.BLANK, r, c);
				} else {
					this.cells[r][c] = new Cell(Constants.COLOR[(r+1) % Constants.ROWS], r, c);
				}
			}
		}
	}
	
	public Cell getCell(int row, int col) {
		return this.cells[row][col];
	}
	
	public List<Move> suggestMoves(Partecipant partecipant, int die) {
		List<Move> moves = new ArrayList<Move>();
		List<Cell> cellsOccupiedByPartecipant = this.getPawnsPositionByColor(partecipant.getColor());
		
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
			
		if (cellsOccupiedByPartecipant.size() != 0) {
			
			for (int i=0; i<cellsOccupiedByPartecipant.size(); i++) {
				Cell startCell = cellsOccupiedByPartecipant.get(i);
				Move move = getMoveByDie(startCell, die, partecipant.getColor());
				if (move != null) {
					moves.add(move);
				}
				if (die == 6) {
					Move secondMove = getMoveByDie(startCell, die, partecipant.getColor());
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
		for (int r=0; r<Constants.ROWS; r++) {
			for (int c=0; c<Constants.COLUMNS; c++) {
				if (this.cells[r][c].getColor().equals(color)) {
					cells.add(this.cells[r][c]);
				}
			}
		}
		return cells;
	}
	
	// return the next possible cell
	private Cell getNextCell(int currentRow, int currentColumn, String currentColorPawn) {
		
		// return null if no other moves are possible
		if (currentColumn == Constants.COLUMNS - 1) {
			return null;
		} 
		
		// return the next cell according to position and color of the pawn
		else if (this.cells[currentRow][currentColumn+1].getColor().equals(currentColorPawn) || 
				this.cells[currentRow][currentColumn+1].getColor().equals(Constants.BLANK)) {
			return this.cells[currentRow][currentColumn+1];
		} else {
			return this.cells[(currentRow+1) % Constants.ROWS][0];
		}
		
	}
	
	private Move getMoveByDie(Cell startCell, int die, String partecipantColor) {
		for (int d=1; d<=die; d++) {
			Cell nextCell = this.getNextCell(startCell.getRow(), startCell.getColumn(), startCell.getColor());
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
		}
		return null;
	}

}