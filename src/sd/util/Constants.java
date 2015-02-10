package sd.util;

public class Constants {

	public static final String IP_SERVER = "130.136.4.207";

	public static final int MAX_PLAYER = 6;
	public static final long MAX_WAIT_FOR_MATCH = 30000L;
	public static final int BENCH_DIMENSION = 4;

	/*
	 * the following constants are used to manage crashes according to the phase
	 * in which the crash happens plus the latency
	 */
	public static final long MAX_TIME_TO_BUILD_GUI = 10000L;
	public static final long MAX_TIME_FOR_TURN = 30000L;
	public static final long LATENCY = 3000L;
	public static final long MAX_TIME_FOR_UPDATE = 9000L;

	public static final int SUCCESS = 0;
	public static final int WALL = 1;
	public static final int EATEN = 2;
	public static final int FAIL = 3;
	public static final int ROWS = 6;
	public static final int COLUMNS = 12;
	public static final String[] COLOR = { "RED", "GREEN", "VIOLET", "YELLOW",
			"BLACK", "BLUE" };
	public static final String BLANK = "WHITE";
	public static final int UPDATE_NEXT = 0;
	public static final int PLAY_NEXT = 1;
	public static final int PLAY_AGAIN = 2;
	public static final int END_GAME = 3;

	/* constants for game panel */
	public static final int GUI_ROWS = 17;
	public static final int GUI_COLS = 19;
	public static final int CELL_SIZE = 30;
	public static final int DIE_SIZE = 62;

	public static final String RIGHT = "RIGHT";
	public static final String LEFT = "LEFT";

	public static final String UP = "UP";
	public static final String UP_RIGHT = "UP_RIGHT";
	public static final String UP_LEFT = "UP_LEFT";

	public static final String DOWN = "DOWN";
	public static final String DOWN_RIGHT = "DOWN_RIGHT";
	public static final String DOWN_LEFT = "DOWN_LEFT";

	private static final String[] PATH_RED = { DOWN, DOWN, DOWN_RIGHT,
			DOWN_RIGHT, UP_RIGHT, UP_RIGHT, DOWN_RIGHT };
	private static final int[] START_RED = { 1, 10 };

	private static final String[] PATH_GREEN = { DOWN_LEFT, DOWN_LEFT, DOWN,
			DOWN, DOWN_RIGHT, DOWN_RIGHT, DOWN_LEFT };
	private static final int[] START_GREEN = { 5, 16 };

	private static final String[] PATH_VIOLET = { UP_LEFT, UP_LEFT, DOWN_LEFT,
			DOWN_LEFT, DOWN, DOWN, LEFT };
	private static final int[] START_VIOLET = { 13, 14 };

	private static final String[] PATH_YELLOW = { UP, UP, UP_LEFT, UP_LEFT,
			DOWN_LEFT, DOWN_LEFT, UP_LEFT };
	private static final int[] START_YELLOW = { 15, 8 };

	private static final String[] PATH_BLACK = { UP_RIGHT, UP_RIGHT, UP, UP,
			UP_LEFT, UP_LEFT, UP_RIGHT };
	private static final int[] START_BLACK = { 11, 2 };

	private static final String[] PATH_BLUE = { DOWN_RIGHT, DOWN_RIGHT,
			UP_RIGHT, UP_RIGHT, UP, UP, RIGHT };
	private static final int[] START_BLUE = { 3, 4 };

	private static final String[] PATH_WIN_RED = { DOWN, DOWN, DOWN };
	private static final int[] START_PATH_WIN_RED = { 2, 9 };

	private static final String[] PATH_WIN_GREEN = { DOWN_LEFT, DOWN_LEFT, LEFT };
	private static final int[] START_PATH_WIN_GREEN = { 5, 14 };

	private static final String[] PATH_WIN_VIOLET = { UP_LEFT, UP_LEFT, LEFT };
	private static final int[] START_PATH_WIN_VIOLET = { 11, 14 };

	private static final String[] PATH_WIN_YELLOW = { UP, UP, UP };
	private static final int[] START_PATH_WIN_YELLOW = { 14, 9 };

	private static final String[] PATH_WIN_BLACK = { UP_RIGHT, UP_RIGHT, RIGHT };
	private static final int[] START_PATH_WIN_BLACK = { 11, 4 };

	private static final String[] PATH_WIN_BLUE = { DOWN_RIGHT, DOWN_RIGHT,
			RIGHT };
	private static final int[] START_PATH_WIN_BLUE = { 5, 4 };

	/* constants for start of benches */
	private static final int[] START_PATH_BENCH_RED = { 0, 17 };
	private static final int[] START_PATH_BENCH_GREEN = { 8, 17 };
	private static final int[] START_PATH_BENCH_VIOLET = { 15, 17 };
	private static final int[] START_PATH_BENCH_YELLOW = { 15, 0 };
	private static final int[] START_PATH_BENCH_BLACK = { 8, 0 };
	private static final int[] START_PATH_BENCH_BLUE = { 0, 0 };

	/* paths for colors */
	public static final String[][] PATHS_COLORS = { PATH_RED, PATH_GREEN,
			PATH_VIOLET, PATH_YELLOW, PATH_BLACK, PATH_BLUE };
	public static final int[][] STARTS_COLORS = { START_RED, START_GREEN,
			START_VIOLET, START_YELLOW, START_BLACK, START_BLUE };

	/* paths for win colors */
	public static final String[][] PATHS_WIN_COLORS = { PATH_WIN_RED,
			PATH_WIN_GREEN, PATH_WIN_VIOLET, PATH_WIN_YELLOW, PATH_WIN_BLACK,
			PATH_WIN_BLUE };
	public static final int[][] STARTS_WIN_COLORS = { START_PATH_WIN_RED,
			START_PATH_WIN_GREEN, START_PATH_WIN_VIOLET, START_PATH_WIN_YELLOW,
			START_PATH_WIN_BLACK, START_PATH_WIN_BLUE };

	/* constants path for benches */
	public static final String[] PATH_BENCH = { RIGHT, DOWN, LEFT };

	/* start for color benches */
	public static final int[][] STARTS_BENCH_COLORS = { START_PATH_BENCH_RED,
			START_PATH_BENCH_GREEN, START_PATH_BENCH_VIOLET,
			START_PATH_BENCH_YELLOW, START_PATH_BENCH_BLACK,
			START_PATH_BENCH_BLUE };

	/* number of times the die rotates when launched */
	public static final int ROTATIONS = 30;

	public static final int PHASE_BUILD_GUI = 0;
	public static final int PHASE_FIRST_CYCLE = 1;
	public static final int PHASE_CYCLE = 2;

}