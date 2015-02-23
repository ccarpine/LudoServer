package sd.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import layout.TableLayout;
import sd.core.Cell;
import sd.core.CoreGame;
import sd.core.Move;
import sd.core.player.UserPlayer;
import sd.util.Constants;

/**
 * Game Panel, portion of GUI for the Board, manage
 */
public class GamePanel extends BGPanel {

	private static final long serialVersionUID = 1L;
	private CoreGame coreGame;
	private UserPlayer userPlayer;
	private CellButton[][] cellsButton;
	private List<Move> possibleMoves;

	/**
	 * Create a gameBoard, each cell is a cell button with a specific look and listener
	 * @param coreGame
	 */
	public GamePanel(CoreGame coreGame, UserPlayer userPlayer) {
		super("images/table1.jpg");
		this.setPreferredSize(new Dimension(570, 510));
		this.coreGame = coreGame;
		this.userPlayer = userPlayer;
		this.possibleMoves = new ArrayList<Move>();
		// one color per row and 16 columns where 12 are for victory plus blank path and 4 are for bench
		this.cellsButton = new CellButton[Constants.ROWS][Constants.COLUMNS+Constants.BENCH_DIMENSION];
		double[][] size = new double[2][];
		size[0] = new double[Constants.GUI_COLS];
		size[1] = new double[Constants.GUI_ROWS];
		/* set the panel dimension, and display */
		for (int i=0; i<Constants.GUI_COLS; i++)
			size[0][i] = Constants.CELL_SIZE;
		for (int i=0; i<Constants.GUI_ROWS; i++)
			size[1][i] = Constants.CELL_SIZE;
		this.setLayout(new TableLayout(size));
	}
	
	/***
	 * fill the structure for game panel: 
	 * all bench, path and pawn are are positioned in the right place
	 */
	public void drawGUI() {
		this.removeAll();
		this.updateUI();
		/* create all cell and button in the panel */
		CellButton buttonPosition = null;
		int[] currentPosition = new int[2];
		for (int i=0; i<Constants.COLOR.length; i++) { /* loop in colors */
			/* creating starting cells for current color */
			buttonPosition = new CellButton(Constants.STARTS_COLORS[i][0], Constants.STARTS_COLORS[i][1], 
					getPathIcon(this.coreGame.getGameBoard().getCell(i, 0)), 
					this.coreGame.getGameBoard().getCell(i, 0));
			this.cellsButton[i][0] = buttonPosition;
			currentPosition[0] = Constants.STARTS_COLORS[i][0];
			currentPosition[1] = Constants.STARTS_COLORS[i][1];
			this.add(buttonPosition, this.positionToString(currentPosition));
			/* creating remaining cells for current color */
			for (int j=0; j<Constants.PATHS_COLORS[i].length; j++) {
				int[] nextPosition = this.getPositionButton(currentPosition,
						Constants.PATHS_COLORS[i][j]);
				buttonPosition = new CellButton(nextPosition[0], nextPosition[1], 
						getPathIcon(this.coreGame.getGameBoard().getCell(i, j+1)), 
						this.coreGame.getGameBoard().getCell(i, j+1));
				this.cellsButton[i][j+1] = buttonPosition;
				currentPosition[0] = nextPosition[0];
				currentPosition[1] = nextPosition[1];
				this.add(buttonPosition, this.positionToString(nextPosition));
			}
			/* creating starting cell for the win path for current color */
			buttonPosition = new CellButton(
					Constants.STARTS_WIN_COLORS[i][0], Constants.STARTS_WIN_COLORS[i][1], 
					getPathIcon(this.coreGame.getGameBoard().getCell((i + Constants.COLOR.length - 1)% Constants.COLOR.length, Constants.COLUMNS - Constants.BENCH_DIMENSION)), 
					this.coreGame.getGameBoard().getCell((i + Constants.ROWS - 1)% Constants.COLOR.length, Constants.COLUMNS - Constants.BENCH_DIMENSION));
			this.cellsButton[(i + Constants.ROWS - 1)% Constants.ROWS][Constants.COLUMNS - Constants.BENCH_DIMENSION] = buttonPosition;
			currentPosition[0] = Constants.STARTS_WIN_COLORS[i][0];
			currentPosition[1] = Constants.STARTS_WIN_COLORS[i][1];
			this.add(buttonPosition, this.positionToString(currentPosition));
			/* creating remaning cell for the win path for current color */
			for (int j=0; j<Constants.PATHS_WIN_COLORS[i].length; j++) {
				int[] nextPosition = this.getPositionButton(currentPosition,
						Constants.PATHS_WIN_COLORS[i][j]);
				buttonPosition = new CellButton(nextPosition[0], nextPosition[1], 
						getPathIcon(this.coreGame.getGameBoard().getCell((i + Constants.ROWS - 1)% Constants.COLOR.length, Constants.COLUMNS - Constants.BENCH_DIMENSION + j + 1)), 
						this.coreGame.getGameBoard().getCell((i + Constants.ROWS - 1)% Constants.COLOR.length, Constants.COLUMNS - Constants.BENCH_DIMENSION + j + 1));
				this.cellsButton[(i + Constants.ROWS - 1)% Constants.ROWS][Constants.COLUMNS - Constants.BENCH_DIMENSION + j + 1]  = buttonPosition;
				currentPosition[0] = nextPosition[0];
				currentPosition[1] = nextPosition[1];
				this.add(buttonPosition, this.positionToString(nextPosition));
			}
			/* creating starting cell for the bench path for current color */
			String colorPawn = Constants.COLOR[i];
			if (i < this.coreGame.getPartecipants().size() && this.coreGame.getPartecipants().get(i).isStatusActive()) {
				if (this.coreGame.getPartecipants().get(i).getPawnsInBench() > 0) {
					colorPawn += "_"+Constants.COLOR[i];
				}
			}
			buttonPosition = new CellButton(Constants.STARTS_BENCH_COLORS[i][0], Constants.STARTS_BENCH_COLORS[i][1],"images/box/on/"+colorPawn+".png", null);
			this.cellsButton[i][Constants.COLUMNS] = buttonPosition;
			currentPosition[0] = Constants.STARTS_BENCH_COLORS[i][0];
			currentPosition[1] = Constants.STARTS_BENCH_COLORS[i][1];
			this.add(buttonPosition, this.positionToString(currentPosition));
			/* creating remaning cell for the bench path for current color */
			for (int j=0; j<Constants.PATH_BENCH.length; j++) {
				int[] nextPosition = this.getPositionButton(currentPosition, Constants.PATH_BENCH[j]);
				if (i < this.coreGame.getPartecipants().size() && this.coreGame.getPartecipants().get(i).isStatusActive()) {
					if (this.coreGame.getPartecipants().get(i).getPawnsInBench() > j+1) {
						colorPawn = Constants.COLOR[i]+"_"+Constants.COLOR[i];
					} else {
						colorPawn = Constants.COLOR[i];
					}
				} else {
					colorPawn = Constants.COLOR[i];
				}
				buttonPosition = new CellButton(nextPosition[0], nextPosition[1], 
						"images/box/on/"+colorPawn+".png", null);
				this.cellsButton[i][j+Constants.COLUMNS+1] = buttonPosition;
				currentPosition[0] = nextPosition[0];
				currentPosition[1] = nextPosition[1];
				this.add(buttonPosition, this.positionToString(nextPosition));
			}
		}
		this.updateUI();
	}
	
	/***
	 * 
	 * @param cell, specificic cell of gamebord
	 * @return the path of the picture that represent the state of a specific cell 
	 */
	private String getPathIcon(Cell cell) {
		int sizeCell = cell.getPawns().size();
		switch (sizeCell) {
			case 0:
				if (cell.getColumn() == 0) {
					return "images/starts/on/"+cell.getColor()+".png";
				} else if (cell.getColumn() >= 8) {
					return "images/victory/on/"+cell.getColor()+".png";
				} else {
					return "images/box/on/"+cell.getColor()+".png";
				}
			case 1:
				return "images/box/on/"+cell.getColor()+"_"+cell.getPawns().get(0)+".png";
			case 2:
				return "images/box/on/"+cell.getColor()+"_WALL_"+cell.getPawns().get(0)+".png";
			default:
				break;
		}
		return null;
	}

	/**
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
	 * @param position, position in size
	 * @return position converted to string
	 */
	private String positionToString(int[] position) {
		return String.valueOf(position[1]) + "," + String.valueOf(position[0]);

	}
	
	/**
	 * Make move chosen by partecipant
	 * @param row, int the row in gameBoard of destination cell
	 * @param column, int the column in gameBoard of destination cell
	 */
	private void applyMove(int row, int column) {
		this.makePossibleMoveDisable();
		Cell cellStart = null;
		Cell cellDestination = new Cell(null, row, column);
		for (int i=0; i<this.possibleMoves.size(); i++) {
			if (this.possibleMoves.get(i).getDestination().getRow() == row && this.possibleMoves.get(i).getDestination().getColumn() == column) {
				// if start cell is equal to null it means that pawn is in the bench
				if (this.possibleMoves.get(i).getStart() == null) {
					cellStart = null;
				} else {
					cellStart = new Cell(null, this.possibleMoves.get(i).getStart().getRow(), this.possibleMoves.get(i).getStart().getColumn());
				}
				break;
			}
		}
		Move chosenMove = new Move(cellStart, cellDestination);
		// if start cell is equal to null the core game decrement the pawns in bench
		if (cellStart == null) {
			this.coreGame.getCurrentPartecipant().removePawnsInBench();
		}
		// result is the color of the eaten pawn
		/*String result = */this.coreGame.handleTurn(chosenMove);
		this.drawGUI();
		if (coreGame.isTurnActive()) {
			coreGame.setTurnActive(false);
			this.userPlayer.updateNext(this.coreGame.getPartecipants(), this.coreGame.getGameBoard(), this.coreGame.getCurrentPartecipant().getIp(), this.coreGame.isDoubleTurn(), this.coreGame.getTurn(), true);
		}
	}
	
	/**
	 * Set all the destination cell of possibile move disabled, the partecipant have already choose
	 */
	public void makePossibleMoveDisable() {
		for (int i=0; i<possibleMoves.size(); i++) {
			this.cellsButton[possibleMoves.get(i).getDestination().getRow()][possibleMoves.get(i).getDestination().getColumn()].changeState();
			for (ActionListener a: this.cellsButton[possibleMoves.get(i).getDestination().getRow()][possibleMoves.get(i).getDestination().getColumn()].getActionListeners()) {
				this.cellsButton[possibleMoves.get(i).getDestination().getRow()][possibleMoves.get(i).getDestination().getColumn()].removeActionListener(a);
			}
			this.cellsButton[possibleMoves.get(i).getDestination().getRow()][possibleMoves.get(i).getDestination().getColumn()].removeAll();
		}
	}

	/**
	 * Change the panel according to value of launch die
	 * so the player can choose a move
	 * @param resultDie, int the result of launch die
	 */
	public void makePossibleMoveFlash() {
		possibleMoves = coreGame.initTurn();
		if (possibleMoves.size() == 0) {
			this.coreGame.setTurnActive(false);
			this.userPlayer.updateNext(this.coreGame.getPartecipants(), this.coreGame.getGameBoard(), this.coreGame.getCurrentPartecipant().getIp(), this.coreGame.isDoubleTurn(), this.coreGame.getTurn(), true);
		} else {
			for (int i=0; i<possibleMoves.size(); i++) {
				final int moveIndex = i;
				this.cellsButton[possibleMoves.get(i).getDestination().getRow()][possibleMoves.get(i).getDestination().getColumn()].changeState();
				this.cellsButton[possibleMoves.get(i).getDestination().getRow()][possibleMoves.get(i).getDestination().getColumn()].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (coreGame.isTurnActive()) {
							applyMove(possibleMoves.get(moveIndex).getDestination().getRow(), possibleMoves.get(moveIndex).getDestination().getColumn());
						}
					}
				});
			}
		}
	}
	
}