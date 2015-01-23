package sd.ui;

import java.util.List;

import sd.core.CoreGame;
import sd.core.Move;
import sd.util.Constants;
import layout.TableLayout;

public class GamePanel extends BGPanel {

	private static final long serialVersionUID = 1L;

	private double[][] size;
	private CoreGame coreGame;
	private CellButton[][] cellsButton;

	public GamePanel(CoreGame coreGame) {
		super("images/table.png");
		this.coreGame = coreGame;
		this.cellsButton = new CellButton[Constants.ROWS][Constants.COLUMNS];
		this.size = new double[2][];
		this.size[0] = new double[Constants.GUI_COLS];
		this.size[1] = new double[Constants.GUI_ROWS];

		for (int i = 0; i < Constants.GUI_COLS; i++) {
			this.size[0][i] = Constants.CELL_SIZE;
		}

		for (int i = 0; i < Constants.GUI_ROWS; i++) {
			this.size[1][i] = Constants.CELL_SIZE;
		}

		this.setLayout(new TableLayout(size));

		CellButton buttonPosition = null;
		int[] currentPosition = new int[2];

		for (int i = 0; i < Constants.COLOR.length; i++) {

			/* creating first cell for that color */
			buttonPosition = new CellButton(Constants.STARTS_COLORS[i][0], Constants.STARTS_COLORS[i][1], 
					"images/starts/off/"+Constants.COLOR[i]+".png", "images/starts/on/"+Constants.COLOR[i]+".png", 
					this.coreGame.getGameBoard().getCell(i, 0));
			 this.cellsButton[i][0] = buttonPosition;
			currentPosition[0] = Constants.STARTS_COLORS[i][0];
			currentPosition[1] = Constants.STARTS_COLORS[i][1];
			this.add(buttonPosition, this.positionToString(currentPosition));

			/* creating remaining cells before next color */
			for (int j = 0; j < Constants.PATHS_COLORS[i].length; j++) {
				int[] nextPosition = this.getPositionButton(currentPosition,
						Constants.PATHS_COLORS[i][j]);
				buttonPosition = new CellButton(nextPosition[0], nextPosition[1], 
						"images/box/off/"+Constants.BLANK+".png", "images/box/on/"+Constants.BLANK+".png", 
						this.coreGame.getGameBoard().getCell(i, j+1));
				this.cellsButton[i][j+1] = buttonPosition;
				currentPosition[0] = nextPosition[0];
				currentPosition[1] = nextPosition[1];
				this.add(buttonPosition, this.positionToString(nextPosition));

			}

			/* creating starting cell for the win path for that color */
			buttonPosition = new CellButton(Constants.STARTS_WIN_COLORS[i][0], Constants.STARTS_WIN_COLORS[i][1], 
					"images/victory/off/"+Constants.COLOR[i]+".png", "images/victory/on/"+Constants.COLOR[i]+".png", 
					this.coreGame.getGameBoard().getCell(i, Constants.COLUMNS - Constants.BENCH_DIMENSION));
			this.cellsButton[i][Constants.COLUMNS - Constants.BENCH_DIMENSION] = buttonPosition;
			currentPosition[0] = Constants.STARTS_WIN_COLORS[i][0];
			currentPosition[1] = Constants.STARTS_WIN_COLORS[i][1];
			this.add(buttonPosition, this.positionToString(currentPosition));

			for (int j = 0; j < Constants.PATHS_WIN_COLORS[i].length; j++) {
				int[] nextPosition = this.getPositionButton(currentPosition,
						Constants.PATHS_WIN_COLORS[i][j]);
				buttonPosition = new CellButton(nextPosition[0], nextPosition[1], 
						"images/victory/off/"+Constants.COLOR[i]+".png", "images/victory/on/"+Constants.COLOR[i]+".png", 
						this.coreGame.getGameBoard().getCell(i, Constants.COLUMNS - Constants.BENCH_DIMENSION + j + 1));
				this.cellsButton[i][Constants.COLUMNS - Constants.BENCH_DIMENSION + j + 1]  = buttonPosition;
				currentPosition[0] = nextPosition[0];
				currentPosition[1] = nextPosition[1];
				this.add(buttonPosition, this.positionToString(nextPosition));

			}
			
			// Adding pawns in bench
			if (i < this.coreGame.getPartecipants().size()) {
				buttonPosition = new CellButton(
						Constants.STARTS_BENCH_COLORS[i][0], Constants.STARTS_BENCH_COLORS[i][1],
						"images/pawns/off/"+Constants.COLOR[i]+".png", "images/pawns/on/"+Constants.COLOR[i]+".png", null);
				currentPosition[0] = Constants.STARTS_BENCH_COLORS[i][0];
				currentPosition[1] = Constants.STARTS_BENCH_COLORS[i][1];
				this.add(buttonPosition, this.positionToString(currentPosition));

				for (int j = 0; j < Constants.PATH_BENCH.length; j++) {
					int[] nextPosition = this.getPositionButton(currentPosition,
							Constants.PATH_BENCH[j]);
					buttonPosition = new CellButton(nextPosition[0], nextPosition[1], 
							"images/pawns/off/"+Constants.COLOR[i]+".png", "images/pawns/on/"+Constants.COLOR[i]+".png", null);
					currentPosition[0] = nextPosition[0];
					currentPosition[1] = nextPosition[1];
					this.add(buttonPosition, this.positionToString(nextPosition));
				}
			}

			/* creating benches */
			buttonPosition = new CellButton(
					Constants.STARTS_BENCH_COLORS[i][0], Constants.STARTS_BENCH_COLORS[i][1],
					"images/box/off/"+Constants.COLOR[i]+".png", "images/box/on/"+Constants.COLOR[i]+".png", null);
			currentPosition[0] = Constants.STARTS_BENCH_COLORS[i][0];
			currentPosition[1] = Constants.STARTS_BENCH_COLORS[i][1];
			this.add(buttonPosition, this.positionToString(currentPosition));

			for (int j = 0; j < Constants.PATH_BENCH.length; j++) {
				int[] nextPosition = this.getPositionButton(currentPosition,
						Constants.PATH_BENCH[j]);
				buttonPosition = new CellButton(nextPosition[0], nextPosition[1], 
						"images/box/off/"+Constants.COLOR[i]+".png", "images/box/on/"+Constants.COLOR[i]+".png", null);
				currentPosition[0] = nextPosition[0];
				currentPosition[1] = nextPosition[1];
				this.add(buttonPosition, this.positionToString(nextPosition));
			}
			
			

		}

	}

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

	private String positionToString(int[] position) {

		return String.valueOf(position[1]) + "," + String.valueOf(position[0]);

	}
	
	/* change the panel so the player can choose a move*/
	public void setPossibleMovesStartingFrom(List<Move> possibleMoves){
		for (int i=0; i<possibleMoves.size(); i++) {
			this.cellsButton[possibleMoves.get(i).getDestination().getRow()][possibleMoves.get(i).getDestination().getColumn()].changeState();
		}
	
		
	}

}
