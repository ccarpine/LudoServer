package sd.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import sd.util.CellButton;
import sd.util.Constants;
import layout.TableLayout;

public class GamePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private double[][] size;
	

	public GamePanel() {
		super();
		
		this.size = new double[2][];
		this.size[0] = new double[Constants.GUI_COLS];
		this.size[1] = new double[Constants.GUI_ROWS];
		
		for (int i=0; i<Constants.GUI_COLS; i++) {
			this.size[0][i] = Constants.CELL_SIZE;
		}
		

		for (int i = 0; i < Constants.GUI_ROWS; i++) {
				this.size[1][i] = Constants.CELL_SIZE;
		}
		
		this.setLayout(new TableLayout(size));
		
		CellButton buttonPosition = null;
		int[] currentPosition = new int[2];

		/* creating red path */
		buttonPosition = new CellButton(Constants.START_RED[0],Constants.START_RED[1],Color.RED);
		currentPosition[0] = Constants.START_RED[0];
		currentPosition[1] = Constants.START_RED[1];
		this.add(buttonPosition,this.positionToString(currentPosition));
		
		for (int i = 0; i < Constants.PATH_RED.length; i++) {
			int[] nextPosition = this.getPositionButton(currentPosition, Constants.PATH_RED[i]);
			buttonPosition = new CellButton(nextPosition[0], nextPosition[1], Color.WHITE);
			currentPosition[0] = nextPosition[0];
			currentPosition[1] = nextPosition[1];
			this.add(buttonPosition, this.positionToString(nextPosition));
			
		}
		
		/* creating green path*/
		buttonPosition = new CellButton(Constants.START_GREEN[0],Constants.START_GREEN[1],Color.GREEN);
		currentPosition[0] = Constants.START_GREEN[0];
		currentPosition[1] = Constants.START_GREEN[1];
		this.add(buttonPosition,this.positionToString(currentPosition));
		
		for (int i = 0; i < Constants.PATH_GREEN.length; i++) {
			int[] nextPosition = this.getPositionButton(currentPosition, Constants.PATH_GREEN[i]);
			buttonPosition = new CellButton(nextPosition[0], nextPosition[1], Color.WHITE);
			currentPosition[0] = nextPosition[0];
			currentPosition[1] = nextPosition[1];
			this.add(buttonPosition, this.positionToString(nextPosition));
			
		}
		
		/* creating violet path*/
		buttonPosition = new CellButton(Constants.START_VIOLET[0],Constants.START_VIOLET[1], new Color(153,0,153));
		currentPosition[0] = Constants.START_VIOLET[0];
		currentPosition[1] = Constants.START_VIOLET[1];
		this.add(buttonPosition,this.positionToString(currentPosition));
		
		for (int i = 0; i < Constants.PATH_VIOLET.length; i++) {
			int[] nextPosition = this.getPositionButton(currentPosition, Constants.PATH_VIOLET[i]);
			buttonPosition = new CellButton(nextPosition[0], nextPosition[1], Color.WHITE);
			currentPosition[0] = nextPosition[0];
			currentPosition[1] = nextPosition[1];
			this.add(buttonPosition, this.positionToString(nextPosition));
			
		}
		
		/* creating yellow position*/
		buttonPosition = new CellButton(Constants.START_YELLOW[0],Constants.START_YELLOW[1], Color.YELLOW);
		currentPosition[0] = Constants.START_YELLOW[0];
		currentPosition[1] = Constants.START_YELLOW[1];
		this.add(buttonPosition,this.positionToString(currentPosition));
		
		for (int i = 0; i < Constants.PATH_YELLOW.length; i++) {
			int[] nextPosition = this.getPositionButton(currentPosition, Constants.PATH_YELLOW[i]);
			buttonPosition = new CellButton(nextPosition[0], nextPosition[1], Color.WHITE);
			currentPosition[0] = nextPosition[0];
			currentPosition[1] = nextPosition[1];
			this.add(buttonPosition, this.positionToString(nextPosition));
			
		}
		
		/* creating black position*/
		buttonPosition = new CellButton(Constants.START_BLACK[0],Constants.START_BLACK[1], Color.BLACK);
		currentPosition[0] = Constants.START_BLACK[0];
		currentPosition[1] = Constants.START_BLACK[1];
		this.add(buttonPosition,this.positionToString(currentPosition));
		
		for (int i = 0; i < Constants.PATH_BLACK.length; i++) {
			int[] nextPosition = this.getPositionButton(currentPosition, Constants.PATH_BLACK[i]);
			buttonPosition = new CellButton(nextPosition[0], nextPosition[1], Color.WHITE);
			currentPosition[0] = nextPosition[0];
			currentPosition[1] = nextPosition[1];
			this.add(buttonPosition, this.positionToString(nextPosition));
			
		}
		
		/* creating blue position*/
		buttonPosition = new CellButton(Constants.START_BLUE[0],Constants.START_BLUE[1], Color.BLUE);
		currentPosition[0] = Constants.START_BLUE[0];
		currentPosition[1] = Constants.START_BLUE[1];
		this.add(buttonPosition,this.positionToString(currentPosition));
		
		for (int i = 0; i < Constants.PATH_BLUE.length; i++) {
			int[] nextPosition = this.getPositionButton(currentPosition, Constants.PATH_BLUE[i]);
			buttonPosition = new CellButton(nextPosition[0], nextPosition[1], Color.WHITE);
			currentPosition[0] = nextPosition[0];
			currentPosition[1] = nextPosition[1];
			this.add(buttonPosition, this.positionToString(nextPosition));
			
		}
		

	}

	private int[] getPositionButton(int[] currentPosition, String moveDirection) {
		
		int[] nextPositionButton = new int[2];
		
		switch (moveDirection) {
		
		case Constants.UP: 
			nextPositionButton[0] = currentPosition[0]-1;
			nextPositionButton[1] = currentPosition[1];
			break;
			
		case Constants.DOWN:
			nextPositionButton[0] = currentPosition[0]+1;
			nextPositionButton[1] = currentPosition[1];
			break;
			
		case Constants.LEFT:
			nextPositionButton[0] = currentPosition[0];
			nextPositionButton[1] = currentPosition[1]-1;
			break;
		
		case Constants.RIGHT:
			nextPositionButton[0] = currentPosition[0];
			nextPositionButton[1] = currentPosition[1]+1;
			break;
			
		case Constants.UP_LEFT:
			nextPositionButton[0] = currentPosition[0]-1;
			nextPositionButton[1] = currentPosition[1]-1;
			break;
		
		case Constants.UP_RIGHT:
			nextPositionButton[0] = currentPosition[0]-1;
			nextPositionButton[1] = currentPosition[1]+1;
			break;
			
		case Constants.DOWN_LEFT:
			nextPositionButton[0] = currentPosition[0]+1;
			nextPositionButton[1] = currentPosition[1]-1;
			break;
			
		case Constants.DOWN_RIGHT:
			nextPositionButton[0] = currentPosition[0]+1;
			nextPositionButton[1] = currentPosition[1]+1;
			break;
			
		default:
			break;
		
		}
		
		return nextPositionButton;
		
	}
	
	private String positionToString(int[] position) {
		
		return String.valueOf(position[1])+","+String.valueOf(position[0]);
		
	}

	public static void main(String argv[]) {
		
		JFrame prova = new JFrame();
		prova.setSize(1000, 1000);
		//prova.setLayout(new BorderLayout());
		prova.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GamePanel gp = new GamePanel();
		prova.getContentPane().add(gp);
		prova.setVisible(true);

	}
	
	

}
