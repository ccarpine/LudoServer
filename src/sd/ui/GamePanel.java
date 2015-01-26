package sd.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import sd.core.Cell;
import sd.core.CoreGame;
import sd.core.Move;
import sd.util.Constants;
import layout.TableLayout;

/**
 * 
 * Game Panel, portion of GUI for the Board, manage
 *
 */
public class GamePanel extends BGPanel {

	private static final long serialVersionUID = 1L;

	private CoreGame coreGame;
	private CellButton[][] cellsButton;
	private CellButton[][] pawnsOnBoard;
	private List<Move> possibleMoves;

	/**
	 * create a gameBoard, each cell is a cell button with a specific look and listener
	 * @param coreGame
	 */
	public GamePanel(CoreGame coreGame) {
		super("images/table.png");
		this.coreGame = coreGame;
		this.possibleMoves = new ArrayList<Move>();
		// cells: one color per row and 16 columns where 12 are for victory plus blank path and 4 are for bench
		this.cellsButton = new CellButton[Constants.ROWS][Constants.COLUMNS+Constants.BENCH_DIMENSION];
		// pawns: one color per row and 16 columns where 12 are for victory plus blank path and 4 are for bench
		this.pawnsOnBoard = new CellButton[Constants.ROWS][Constants.COLUMNS+Constants.BENCH_DIMENSION];
		double[][] size = new double[2][];
		size[0] = new double[Constants.GUI_COLS];
		size[1] = new double[Constants.GUI_ROWS];

		/* set the panel dimension, and display */
		for (int i = 0; i < Constants.GUI_COLS; i++)
			size[0][i] = Constants.CELL_SIZE;
		for (int i = 0; i < Constants.GUI_ROWS; i++)
			size[1][i] = Constants.CELL_SIZE;
		this.setLayout(new TableLayout(size));

		/* create all cell and button in the panel */
		CellButton buttonPosition = null;
		int[] currentPosition = new int[2];
		for (int i = 0; i < Constants.COLOR.length; i++) { /* loop in colors */
			final int colorIndex = i;
			/* creating starting cells for current color */
			buttonPosition = new CellButton(Constants.STARTS_COLORS[i][0], Constants.STARTS_COLORS[i][1], 
					"images/starts/off/"+Constants.COLOR[i]+".png", "images/starts/on/"+Constants.COLOR[i]+".png", 
					this.coreGame.getGameBoard().getCell(i, 0));
			this.cellsButton[i][0] = buttonPosition;
			this.cellsButton[i][0].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					applyMove(cellsButton[colorIndex][0].getRowOnGameBoard(), cellsButton[colorIndex][0].getColOnGameBoard());
				}
			});
			currentPosition[0] = Constants.STARTS_COLORS[i][0];
			currentPosition[1] = Constants.STARTS_COLORS[i][1];
			this.add(buttonPosition, this.positionToString(currentPosition));

			/* creating remaining cells for current color */
			for (int j = 0; j < Constants.PATHS_COLORS[i].length; j++) {
				final int pathIndex = j;
				int[] nextPosition = this.getPositionButton(currentPosition,
						Constants.PATHS_COLORS[i][j]);
				buttonPosition = new CellButton(nextPosition[0], nextPosition[1], 
						"images/box/off/"+Constants.BLANK+".png", "images/box/on/"+Constants.BLANK+".png", 
						this.coreGame.getGameBoard().getCell(i, j+1));
				this.cellsButton[i][j+1] = buttonPosition;
				this.cellsButton[i][j+1].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applyMove(cellsButton[colorIndex][pathIndex+1].getRowOnGameBoard(), cellsButton[colorIndex][pathIndex+1].getColOnGameBoard());
					}
				});
				currentPosition[0] = nextPosition[0];
				currentPosition[1] = nextPosition[1];
				this.add(buttonPosition, this.positionToString(nextPosition));
			}

			/* creating starting cell for the win path for current color */
			buttonPosition = new CellButton(Constants.STARTS_WIN_COLORS[i][0], Constants.STARTS_WIN_COLORS[i][1], 
					"images/victory/off/"+Constants.COLOR[i]+".png", "images/victory/on/"+Constants.COLOR[i]+".png", 
					this.coreGame.getGameBoard().getCell(i, Constants.COLUMNS - Constants.BENCH_DIMENSION));
			this.cellsButton[i][Constants.COLUMNS - Constants.BENCH_DIMENSION] = buttonPosition;
			this.cellsButton[i][Constants.COLUMNS - Constants.BENCH_DIMENSION].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					applyMove(cellsButton[colorIndex][Constants.COLUMNS - Constants.BENCH_DIMENSION].getRowOnGameBoard(), cellsButton[colorIndex][Constants.COLUMNS - Constants.BENCH_DIMENSION].getColOnGameBoard());
				}
			});
			currentPosition[0] = Constants.STARTS_WIN_COLORS[i][0];
			currentPosition[1] = Constants.STARTS_WIN_COLORS[i][1];
			this.add(buttonPosition, this.positionToString(currentPosition));
			
			/* creating remaning cell for the win path for current color */
			for (int j = 0; j < Constants.PATHS_WIN_COLORS[i].length; j++) {
				final int pathIndex = j;
				int[] nextPosition = this.getPositionButton(currentPosition,
						Constants.PATHS_WIN_COLORS[i][j]);
				buttonPosition = new CellButton(nextPosition[0], nextPosition[1], 
						"images/victory/off/"+Constants.COLOR[i]+".png", "images/victory/on/"+Constants.COLOR[i]+".png", 
						this.coreGame.getGameBoard().getCell(i, Constants.COLUMNS - Constants.BENCH_DIMENSION + j + 1));
				this.cellsButton[i][Constants.COLUMNS - Constants.BENCH_DIMENSION + j + 1]  = buttonPosition;
				this.cellsButton[i][Constants.COLUMNS - Constants.BENCH_DIMENSION + j + 1].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						applyMove(cellsButton[colorIndex][Constants.COLUMNS - Constants.BENCH_DIMENSION + pathIndex + 1].getRowOnGameBoard(), cellsButton[colorIndex][Constants.COLUMNS - Constants.BENCH_DIMENSION + pathIndex + 1].getColOnGameBoard());
					}
				});
				currentPosition[0] = nextPosition[0];
				currentPosition[1] = nextPosition[1];
				this.add(buttonPosition, this.positionToString(nextPosition));
			}
			
			/* adding pawns in bench if the partecipant is present */
			if (i < this.coreGame.getPartecipants().size()) {
				buttonPosition = new CellButton(
						Constants.STARTS_BENCH_COLORS[i][0], Constants.STARTS_BENCH_COLORS[i][1],
						"images/pawns/off/"+Constants.COLOR[i]+".png", "images/pawns/on/"+Constants.COLOR[i]+".png", null);
				this.pawnsOnBoard[0][0] = buttonPosition;
				currentPosition[0] = Constants.STARTS_BENCH_COLORS[i][0];
				currentPosition[1] = Constants.STARTS_BENCH_COLORS[i][1];
				this.add(buttonPosition, this.positionToString(currentPosition));

				for (int j = 0; j < Constants.PATH_BENCH.length; j++) {
					int[] nextPosition = this.getPositionButton(currentPosition,
							Constants.PATH_BENCH[j]);
					buttonPosition = new CellButton(nextPosition[0], nextPosition[1], 
							"images/pawns/off/"+Constants.COLOR[i]+".png", "images/pawns/on/"+Constants.COLOR[i]+".png", null);
					this.pawnsOnBoard[i][j+1] = buttonPosition;
					currentPosition[0] = nextPosition[0];
					currentPosition[1] = nextPosition[1];
					this.add(buttonPosition, this.positionToString(nextPosition));
				}
			}

			/* creating starting cell for the bench path for current color */
			buttonPosition = new CellButton(
					Constants.STARTS_BENCH_COLORS[i][0], Constants.STARTS_BENCH_COLORS[i][1],
					"images/box/off/"+Constants.COLOR[i]+".png", "images/box/on/"+Constants.COLOR[i]+".png", null);
			this.cellsButton[0][Constants.COLUMNS] = buttonPosition;
			currentPosition[0] = Constants.STARTS_BENCH_COLORS[i][0];
			currentPosition[1] = Constants.STARTS_BENCH_COLORS[i][1];
			this.add(buttonPosition, this.positionToString(currentPosition));

			/* creating remaning cell for the bench path for current color */
			for (int j = 0; j < Constants.PATH_BENCH.length; j++) {
				int[] nextPosition = this.getPositionButton(currentPosition,
						Constants.PATH_BENCH[j]);
				buttonPosition = new CellButton(nextPosition[0], nextPosition[1], 
						"images/box/off/"+Constants.COLOR[i]+".png", "images/box/on/"+Constants.COLOR[i]+".png", null);
				this.cellsButton[i][j+Constants.COLUMNS+1] = buttonPosition;
				currentPosition[0] = nextPosition[0];
				currentPosition[1] = nextPosition[1];
				this.add(buttonPosition, this.positionToString(nextPosition));
			}
		}
	}

	/**
	 *  
	 * @param currentPosition, current position (cell index) while we are building the GUI
	 * @param moveDirection, the direction of the next cell
	 * @return the nextPosition (cell index) of the next cell in according to moveDirecetion
	 */
	private int[] getPositionButton(int[] currentPosition, String moveDirection) {
		int[] nextPositionButton = new int[2];
		switch (moveDirection) {
		case Constants.UP:
			nextPositionButton[0] = currentPosition[0] - 1;
			nextPositionButton[1] = currentPosition[1];
			break;
		case Constants.DOWN:
			nextPositionButton[0] = currentPosition[0] + 1;
			nextPositionButton[1] = currentPosition[1];
			break;
		case Constants.LEFT:
			nextPositionButton[0] = currentPosition[0];
			nextPositionButton[1] = currentPosition[1] - 1;
			break;
		case Constants.RIGHT:
			nextPositionButton[0] = currentPosition[0];
			nextPositionButton[1] = currentPosition[1] + 1;
			break;
		case Constants.UP_LEFT:
			nextPositionButton[0] = currentPosition[0] - 1;
			nextPositionButton[1] = currentPosition[1] - 1;
			break;
		case Constants.UP_RIGHT:
			nextPositionButton[0] = currentPosition[0] - 1;
			nextPositionButton[1] = currentPosition[1] + 1;
			break;
		case Constants.DOWN_LEFT:
			nextPositionButton[0] = currentPosition[0] + 1;
			nextPositionButton[1] = currentPosition[1] - 1;
			break;
		case Constants.DOWN_RIGHT:
			nextPositionButton[0] = currentPosition[0] + 1;
			nextPositionButton[1] = currentPosition[1] + 1;
			break;
		default:
			break;
		}
		return nextPositionButton;
	}

	/**
	 * 
	 * @param position, position in size
	 * @return position converted to string
	 */
	private String positionToString(int[] position) {
		return String.valueOf(position[1]) + "," + String.valueOf(position[0]);

	}
	
	/**
	 * make move chosen by partecipant
	 * @param row, int the row in gameBoard of destination cell
	 * @param column, int the column in gameBoard of destination cell
	 */
	private void applyMove(int row, int column) {
		this.makePossibleMoveDisable();
		Cell cellStart = null;
		Cell cellDestination = new Cell(null, row, column);
		for (int i=0; i<this.possibleMoves.size(); i++) {
			if (this.possibleMoves.get(i).getDestination().getRow() == row && this.possibleMoves.get(i).getDestination().getColumn() == column) {
				cellStart = new Cell(null, this.possibleMoves.get(i).getStart().getRow(), this.possibleMoves.get(i).getStart().getColumn());
				break;
			}
		}
		Move chosenMove = new Move(cellStart, cellDestination);
		// result is the color of the eaten pawn
		String result = this.coreGame.handleTurn(chosenMove);
		int indexColorMover = this.getIndexColor(this.coreGame.getMyPartecipant().getColor());
		Cell cellStartGUI = this.getPawnPositionGUI(indexColorMover, cellStart);
		Cell cellDestinationGUI = this.getPawnPositionGUI(indexColorMover, cellDestination);
		if (result != null) {
			int indexColorEaten = this.getIndexColor(result);
			Cell eatenPawnPosition = getPawnPositionGUI(indexColorEaten, cellDestination);
			Cell freeBenchPosition = getFreePositionBenchGUI(indexColorEaten);
			this.movePawn(eatenPawnPosition, freeBenchPosition, indexColorEaten);
		}
		this.movePawn(cellStartGUI, cellDestinationGUI, indexColorMover);
		//TODO
		/* update GUI here */
		System.out.println("MAKE MOVE");
		System.out.println("4 UPDATE SEND -->" +this.coreGame.getNextPartecipant(this.coreGame.getMyPartecipant().getIp()).getIp() );
		//TODO
		//this.updateNext(this.coreGame.getPartecipants(), this.coreGame.getGameBoard(), this.coreGame.getMyPartecipant().getIp());
	}
	
	/**
	 * set all the destination cell of possibile move disabled, the partecipant have already choose
	 */
	private void makePossibleMoveDisable() {
		for (int i=0; i<possibleMoves.size(); i++) {
			this.cellsButton[possibleMoves.get(i).getDestination().getRow()][possibleMoves.get(i).getDestination().getColumn()].changeState();
		}
	}

	/**
	 * change the panel according to value of launch die
	 * so the player can choose a move
	 * @param resultDie, int the result of launch die
	 */
	public void makePossibleMoveFlash(int resultDie) {
		possibleMoves = coreGame.initTurn(resultDie);
		for (int i=0; i<possibleMoves.size(); i++) {
			this.cellsButton[possibleMoves.get(i).getDestination().getRow()][possibleMoves.get(i).getDestination().getColumn()].changeState();
		}
	}
	
	/**
	 * return the cell of the first free position in the bench of that color
	 * @param colorBenchIndex, the color of bench to increment
	 * @return Cell, the cell of the first free position in the bench of that color
	 */
	private Cell getFreePositionBenchGUI(int colorBenchIndex) {
		Cell freeCell = null;
		for (int g=0; g<Constants.BENCH_DIMENSION; g++) {
			for (int j=0; j<Constants.BENCH_DIMENSION; j++) {
				if (this.pawnsOnBoard[colorBenchIndex][j] != null) {
					if (this.cellsButton[colorBenchIndex][Constants.COLUMNS+g].getRow() == this.pawnsOnBoard[colorBenchIndex][j].getRow() && 
							this.cellsButton[colorBenchIndex][Constants.COLUMNS+g].getCol() == this.pawnsOnBoard[colorBenchIndex][j].getCol()) {
						break;
					} else if (j == Constants.BENCH_DIMENSION - 1) {
						freeCell = new Cell(Constants.COLOR[colorBenchIndex], colorBenchIndex, Constants.COLUMNS+g);
					}
				}
			}
		}
		return freeCell;
	}
	
	/**
	 * return the corrisponding ui cell for that pawn
	 * @param colorBenchIndex, the color of the pawn
	 * @param cellPosition, the cell in memory
	 * @return Cell, the corrisponding ui cell for that pawn
	 */
	private Cell getPawnPositionGUI(int colorBenchIndex, Cell cellPosition) {
		for (int j=0; j<Constants.COLUMNS+Constants.BENCH_DIMENSION; j++) {
			if (this.pawnsOnBoard[colorBenchIndex][j].getRowOnGameBoard() == cellPosition.getRow() &&
					this.pawnsOnBoard[colorBenchIndex][j].getColOnGameBoard() == cellPosition.getColumn()) {
				return new Cell(Constants.COLOR[colorBenchIndex], colorBenchIndex, j);
			}
		}
		return null;
	}
	
	/** return the index of the color inside Constants.COLOR
	 * @param color, the string color
	 * @return int, the index of the color inside Constants.COLOR
	 */
	private int getIndexColor(String color) {
		if (color.equals(Constants.COLOR[0])) {
			return 0;
		} else if (color.equals(Constants.COLOR[1])) {
			return 1;
		} else if (color.equals(Constants.COLOR[2])) {
			return 2;
		} else if (color.equals(Constants.COLOR[3])) {
			return 3;
		} else if (color.equals(Constants.COLOR[4])) {
			return 4;
		} else {
			return 5;
		}
	}
	
	/** move the pawn from a gui cell to another 
	 * @param startPosition, the gui cell from which the pawn moves
	 * @param destinationPosition, the gui cell to which the pawn moves
	 * @param indexColorPawn, the color of the moving pawn
	 */
	private void movePawn(Cell startPosition, Cell destinationPosition, int indexColorPawn) {
		this.pawnsOnBoard[startPosition.getRow()][startPosition.getColumn()].setIcon(null);
		this.pawnsOnBoard[destinationPosition.getRow()][destinationPosition.getColumn()].setIcon(
				new javax.swing.ImageIcon(ClassLoader.getSystemResource("sd/ui/images/pawns/on/"+Constants.COLOR[indexColorPawn]+".png")));
		this.updateUI();
		this.revalidate();
		this.repaint();
	}
	
}