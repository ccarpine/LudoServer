package sd.util;

public class Constants {

	public static final int BENCH_DIMENSION = 4;
	public static final int SUCCESS = 0;
	public static final int WALL = 1;
	public static final int EATEN = 2;
	public static final int FAIL = 3;
	public static final int ROWS = 6;
	public static final int COLUMNS = 12;
	public static final String[] COLOR = { "RED", "GREEN", "VIOLET", "YELLOW",
			"BLACK", "BLUE" };
	/*public static final Color[] COLOR_VALUES = { Color.RED, Color.GREEN, Color.VIOLET, Color.YELLOW,
		Color.BLACK, Color.BLUE };*/
	public static final String BLANK = "WHITE";
	public static final int UPDATE_NEXT = 0;
	public static final int PLAY_NEXT = 1;
	public static final int PLAY_AGAIN = 2;
	public static final int END_GAME = 3;

	/* constants for game panel */
	public static final int GUI_ROWS = 17;
	public static final int GUI_COLS = 19;
	
	public static final String RIGHT = "RIGHT";
	public static final String LEFT = "LEFT";
	
	public static final String UP = "UP";
	public static final String UP_RIGHT = "UP_RIGHT";
	public static final String UP_LEFT = "UP_LEFT";
	
	public static final String DOWN = "DOWN"; 
	public static final String DOWN_RIGHT = "DOWN_RIGHT" ; 
	public static final String DOWN_LEFT = "DOWN_LEFT";
	
	public static final String[] PATH_RED = {DOWN, DOWN, DOWN_RIGHT, DOWN_RIGHT, UP_RIGHT, UP_RIGHT, DOWN_RIGHT};
	public static final int[] START_RED = {1,10};
	
	public static final String[] PATH_GREEN = {DOWN_LEFT, DOWN_LEFT, DOWN, DOWN, DOWN_RIGHT, DOWN_RIGHT, DOWN_LEFT};
	public static final int[] START_GREEN = {5,16};
	
	public static final String[] PATH_VIOLET = {UP_LEFT, UP_LEFT, DOWN_LEFT, DOWN_LEFT, DOWN, DOWN,LEFT};
	public static final int[] START_VIOLET = {13,14};
	
	public static final String[] PATH_YELLOW = {UP, UP, UP_LEFT, UP_LEFT, DOWN_LEFT, DOWN_LEFT, UP_LEFT};
	public static final int[] START_YELLOW = {15,8};
	
	public static final String[] PATH_BLACK = {UP_RIGHT, UP_RIGHT, UP, UP, UP_LEFT, UP_LEFT, UP_RIGHT};
	public static final int[] START_BLACK = {11,2};
	
	public static final String[] PATH_BLUE = {DOWN_RIGHT, DOWN_RIGHT, UP_RIGHT, UP_RIGHT, UP, UP, RIGHT};
	public static final int[] START_BLUE = {3,4};
	
	public static final String[] PATH_WIN_RED = {DOWN, DOWN, DOWN};
	public static final int[] START_PATH_WIN_RED = {2,9};
	
	
	public static final String[] PATH_WIN_GREEN = {DOWN_LEFT, DOWN_LEFT, LEFT};
	public static final int[] START_PATH_WIN_GREEN = {5,14};
	
	
	public static final String[] PATH_WIN_VIOLET = {UP_LEFT, UP_LEFT, LEFT};
	public static final int[] START_PATH_WIN_VIOLET = {11,14};
	
	
	public static final String[] PATH_WIN_YELLOW = {UP, UP, UP};
	public static final int[] START_PATH_WIN_YELLOW = {14,9};
	
	
	public static final String[] PATH_WIN_BLACK = {UP_RIGHT, UP_RIGHT, RIGHT};
	public static final int[] START_PATH_WIN_BLAVK = {11,4};
	
	
	public static final String[] PATH_WIN_BLUE = {DOWN_RIGHT, DOWN_RIGHT, RIGHT};
	public static final int[] START_PATH_WIN_BLUE = {5,4};
	
	public static final int CELL_SIZE = 40;
	
	
	


}
