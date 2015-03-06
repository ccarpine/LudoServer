package sd.util;

public class Constants {

	/**
	 * The name of the game
	 */
	public static final String NAME_GAME = "Do Not Be Angry";

	/**
	 * The ip address of a default server
	 */
	public static final String IP_SERVER = "130.136.4.77";

	/**
	 * Number of maximum players allowed for match
	 */
	public static final int MAX_PLAYER = 6;
	/**
	 * Milliseconds to wait during another player's turn
	 */
	public static final long MAX_WAIT_FOR_MATCH = 30000L;

	/**
	 * Number of pawns for a client
	 */
	public static final int BENCH_DIMENSION = 4;

	/*
	 * The following constants are used to manage crashes according to the phase
	 * in which the crash happens plus the latency
	 */

	/**
	 * Estimated milliseconds taken for each player to build his GUI
	 */
	public static final long MAX_TIME_TO_BUILD_GUI = 10000L;
	/**
	 * Milliseconds to wait during another player's turn
	 */
	public static final long MAX_TIME_FOR_TURN = 30000L;
	/**
	 * Estimated maximum latency
	 */
	public static final long LATENCY = 3000L;
	/**
	 * Estimated millisecons taken for a each player to update his status
	 */
	public static final long MAX_TIME_FOR_UPDATE = 9000L;

	/**
	 * SUCCESS, if a pawns can successfully be inserted into a cell
	 */
	public static final int SUCCESS = 0;
	/**
	 * WALL, if a pawn meets a wall of a different color
	 */
	public static final int WALL = 1;
	/**
	 * EATEN, if a pawn is eaten
	 */
	public static final int EATEN = 2;
	/**
	 * FAIL, if a pawn cannot be inserted into a cell
	 */
	public static final int FAIL = 3;
	/**
	 * Number of rows in the matrix representing the game board (it coincides
	 * with the maximum number of allowed players)
	 */
	public static final int ROWS = 6;
	/**
	 * Number of columns in the matrix representing the game board cells for
	 * each player (7 blank cells, 1 colored forstart, 4 for victory)
	 */
	public static final int COLUMNS = 12;
	/**
	 * The possible colors of the pawns. This order coincides with the one in
	 * which the players will alternate their turns
	 */
	public static final String[] COLOR = { "RED", "GREEN", "VIOLET", "YELLOW",
			"BLACK", "BLUE" };
	/**
	 * A common white cell is called BLANK
	 */
	public static final String BLANK = "WHITE";
	/**
	 * If the next invokation is needed to update the next player
	 */
	public static final int UPDATE_NEXT = 0;
	/**
	 * If the next invokation is needed to allow the next player to play
	 */
	public static final int PLAY_NEXT = 1;
	/**
	 * If the current partecipant is entitled to a second turn
	 */
	public static final int PLAY_AGAIN = 2;
	/**
	 * If the game is over
	 */
	public static final int END_GAME = 3;

	/* constants for game panel */
	/**
	 * X dimension of the grid for the game board GUI
	 */
	public static final int GUI_ROWS = 17;
	/**
	 * Y dimension of the grid for the game board GUI
	 */
	public static final int GUI_COLS = 19;
	/**
	 * Size of a cell
	 */
	public static final int CELL_SIZE = 30;
	/**
	 * Size of the die images
	 */
	public static final int DIE_SIZE = 62;

	/*
	 * The next 8 constants are "basic steps" to follow in order to build the
	 * grid for the GUI game board
	 */
	public static final String RIGHT = "RIGHT";
	public static final String LEFT = "LEFT";

	public static final String UP = "UP";
	public static final String UP_RIGHT = "UP_RIGHT";
	public static final String UP_LEFT = "UP_LEFT";

	public static final String DOWN = "DOWN";
	public static final String DOWN_RIGHT = "DOWN_RIGHT";
	public static final String DOWN_LEFT = "DOWN_LEFT";

	/*
	 * -------------------- PATHS TO FOLLOW DURING THE BUILDING OF THE GUI
	 * ---------------------
	 * 
	 * 
	 * /* Path to follow from the RED start cell
	 */
	private static final String[] PATH_RED = { DOWN, DOWN, DOWN_RIGHT,
			DOWN_RIGHT, UP_RIGHT, UP_RIGHT, DOWN_RIGHT };
	/* Position in the grid of the RED start cell */
	private static final int[] START_RED = { 1, 10 };

	/* Path to follow from the GREEN start cell */
	private static final String[] PATH_GREEN = { DOWN_LEFT, DOWN_LEFT, DOWN,
			DOWN, DOWN_RIGHT, DOWN_RIGHT, DOWN_LEFT };
	/* Position in the grid of the GREEN start cell */
	private static final int[] START_GREEN = { 5, 16 };

	/* Path to follow from the VIOLET start cell */
	private static final String[] PATH_VIOLET = { UP_LEFT, UP_LEFT, DOWN_LEFT,
			DOWN_LEFT, DOWN, DOWN, LEFT };
	/* Position in the grid of the VIOLET start cell */
	private static final int[] START_VIOLET = { 13, 14 };

	/* Path to follow from the YELLOW start cell */
	private static final String[] PATH_YELLOW = { UP, UP, UP_LEFT, UP_LEFT,
			DOWN_LEFT, DOWN_LEFT, UP_LEFT };
	/* Position in the grid of the YELLOW start cell */
	private static final int[] START_YELLOW = { 15, 8 };

	/* Path to follow from the BLACK start cell */
	private static final String[] PATH_BLACK = { UP_RIGHT, UP_RIGHT, UP, UP,
			UP_LEFT, UP_LEFT, UP_RIGHT };
	/* Position in the grid of the BLACK start cell */
	private static final int[] START_BLACK = { 11, 2 };

	/* Path to follow from the BLUE start cell */
	private static final String[] PATH_BLUE = { DOWN_RIGHT, DOWN_RIGHT,
			UP_RIGHT, UP_RIGHT, UP, UP, RIGHT };
	/* Position in the grid of the BLUE start cell */
	private static final int[] START_BLUE = { 3, 4 };

	/* -------------------------- VICTORY PATHS ------------------------ */

	/* Path to follow from the first RED victory cell */
	private static final String[] PATH_WIN_RED = { DOWN, DOWN, DOWN };
	/* Position of the first RED victory cell */
	private static final int[] START_PATH_WIN_RED = { 2, 9 };

	/* Path to follow from the first GREEN victory cell */
	private static final String[] PATH_WIN_GREEN = { DOWN_LEFT, DOWN_LEFT, LEFT };
	/* Position of the first GREEN victory cell */
	private static final int[] START_PATH_WIN_GREEN = { 5, 14 };

	/* Path to follow from the first VIOLET victory cell */
	private static final String[] PATH_WIN_VIOLET = { UP_LEFT, UP_LEFT, LEFT };
	/* Position of the first VIOLET victory cell */
	private static final int[] START_PATH_WIN_VIOLET = { 11, 14 };

	/* Path to follow from the first YELLOW victory cell */
	private static final String[] PATH_WIN_YELLOW = { UP, UP, UP };
	/* Position of the first YELLOW victory cell */
	private static final int[] START_PATH_WIN_YELLOW = { 14, 9 };

	/* Path to follow from the first BLACK victory cell */
	private static final String[] PATH_WIN_BLACK = { UP_RIGHT, UP_RIGHT, RIGHT };
	/* Position of the first BLACK victory cell */
	private static final int[] START_PATH_WIN_BLACK = { 11, 4 };

	/* Path to follow from the first BLUE victory cell */
	private static final String[] PATH_WIN_BLUE = { DOWN_RIGHT, DOWN_RIGHT,
			RIGHT };
	/* Position of the first BLUE victory cell */
	private static final int[] START_PATH_WIN_BLUE = { 5, 4 };

	/* ---------------------- CONSTANTS FOR BENCHES ----------------- */
	private static final int[] START_PATH_BENCH_RED = { 0, 17 };
	private static final int[] START_PATH_BENCH_GREEN = { 8, 17 };
	private static final int[] START_PATH_BENCH_VIOLET = { 15, 17 };
	private static final int[] START_PATH_BENCH_YELLOW = { 15, 0 };
	private static final int[] START_PATH_BENCH_BLACK = { 8, 0 };
	private static final int[] START_PATH_BENCH_BLUE = { 0, 0 };

	/* --------------- ORDER OF THE PATHS IN THE GUI ----------------- */
	public static final String[][] PATHS_COLORS = { PATH_RED, PATH_GREEN,
			PATH_VIOLET, PATH_YELLOW, PATH_BLACK, PATH_BLUE };
	public static final int[][] STARTS_COLORS = { START_RED, START_GREEN,
			START_VIOLET, START_YELLOW, START_BLACK, START_BLUE };

	/* --------------- ORDER OF THE WIN PATHS IN THE GUI */
	public static final String[][] PATHS_WIN_COLORS = { PATH_WIN_RED,
			PATH_WIN_GREEN, PATH_WIN_VIOLET, PATH_WIN_YELLOW, PATH_WIN_BLACK,
			PATH_WIN_BLUE };
	public static final int[][] STARTS_WIN_COLORS = { START_PATH_WIN_RED,
			START_PATH_WIN_GREEN, START_PATH_WIN_VIOLET, START_PATH_WIN_YELLOW,
			START_PATH_WIN_BLACK, START_PATH_WIN_BLUE };

	/* CONSTANTS PATHS FOR BENCHES */
	public static final String[] PATH_BENCH = { RIGHT, DOWN, LEFT };

	/* start for color benches */
	public static final int[][] STARTS_BENCH_COLORS = { START_PATH_BENCH_RED,
			START_PATH_BENCH_GREEN, START_PATH_BENCH_VIOLET,
			START_PATH_BENCH_YELLOW, START_PATH_BENCH_BLACK,
			START_PATH_BENCH_BLUE };

	/* number of times the die rotates when launched */
	public static final int ROTATIONS = 30;

	/* phase of game */
	public static final int PHASE_BUILD_GUI = 0;
	public static final int PHASE_FIRST_CYCLE = 1;
	public static final int PHASE_CYCLE = 2;

}