package sd.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;

import sd.util.CellButton;
import sd.util.Constants;
import layout.TableLayout;

public class GamePanel extends BGPanel {

	private static final long serialVersionUID = 1L;
	private double[][] size;
	

	public GamePanel() {
		super("");
		this.size = new double[Constants.GUI_ROWS][Constants.GUI_COLS];

		for (int i = 0; i < Constants.GUI_ROWS; i++) {
			for (int j = 0; j < Constants.GUI_COLS; j++)
				size[i][j] = 30;
		}

		this.setLayout(new TableLayout(size));

		/* creating red path */
		CellButton startRed = new CellButton(Constants.START_RED[0],Constants.START_RED[1],Color.RED);
		int[] currentPosition = {Constants.START_RED[0],Constants.START_RED[1]};
		this.add(startRed, "0,1");
		
		for (int i = 0; i < Constants.PATH_RED.length; i++) {
			int[] nextPosition = this.getPositionButton(currentPosition, Constants.PATH_RED[i]);
			CellButton button = new CellButton(nextPosition[0], nextPosition[1], Color.WHITE);
			currentPosition[0] = nextPosition[0];
			currentPosition[1] = nextPosition[1];
			this.add(button, this.positionToString(nextPosition));
			
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
		
		return String.valueOf(position[0])+","+String.valueOf(position[1]);
		
	}

	public static void main(String argv[]) {
		
		JFrame prova = new JFrame();
		prova.setSize(1000, 1000);
		prova.setLayout(new BorderLayout());
		prova.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		GamePanel gp = new GamePanel();
		prova.add(gp);
		
		prova.setVisible(true);

	}
	
	

}
