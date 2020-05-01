import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import java.awt.Color;
import java.awt.Point;

public class TetrisUnitTest {
	@Test
	public void testSingleRowCheckRow() {
		// single row check
		Grid grid = new Grid();
		setupRowCheck(grid, 1);
		grid.checkRows();
		checkRowCheck(grid);
	}

	@Test
	public void testDoubleRowCheckRows() {
		// double row check
		Grid grid = new Grid();
		setupRowCheck(grid, 2);
		grid.checkRows();
		checkRowCheck(grid);
	}

	private static void checkRowCheck(Grid grid) {

		// all squares should empty except: (row: 19, col: 0)
		//									(row: 19, col: 5)
		// 									(row: 19, col 8)

		for (int row = 0; row < Grid.HEIGHT; row++) {
			for (int col = 0; col < Grid.WIDTH; col++) {
				if (row != 19) {
					// all squares not in row 19 should be empty
					assertFalse("Fail state, wanted False, got True" + row + ", " + col, grid.isSet(row, col));
				} else if ((col == 0) || (col == 5) || (col == 8)) {
					// squares in row 19 and in either col: 0,5 or 8
					// must be not empty
					assertTrue("Fail state, wanted True, got False" + row + ", " + col, grid.isSet(row, col));
				} else {
					// squares in row 19 and NOT in either col: 0,5 or 8
					// must be empty
					assertFalse("Fail state, wanted False, got True" + row + ", " + col, grid.isSet(row, col));
				}
			}
		}
	}

	private static void setupRowCheck(Grid grid, int numOfRows) {
		// add numOfRows full rows
		for (int row = Grid.HEIGHT - numOfRows; row < Grid.HEIGHT; row++) {
			for (int col = 0; col < Grid.WIDTH; col++) {
				grid.set(row, col, Color.RED);
			}
		}
		int foo = numOfRows + 1;
		// add some colored squares above
		grid.set(Grid.HEIGHT - foo, 0, Color.RED);
		grid.set(Grid.HEIGHT - foo, 5, Color.RED);
		grid.set(Grid.HEIGHT - foo, 8, Color.RED);
	}

	@Test
	public void testNearFullGridRowCheck() {
		// caution: This Test uses similar looking code to the other tests,
		// but in an inverse way.
		
		/**
		 * Fill the entire grid except row: 0, col: 5
		 * 							   row: 0, col: 6
		 * 							   row: 10, col: 5
		 * 							   row: 10, col: 6
		 * 
		 * run checkRows()
		 * 
		 * everything should be empty except: row: 18, col 5
		 * 									  row: 18, col 6
		 * 									  row: 19, col 5
		 * 									  row: 19, col 6
		 */
		Grid grid = new Grid();

		// make every Square Red
		for (int row = 0; row < Grid.HEIGHT; row++) {
			for (int col = 0; col < Grid.WIDTH; col++) {
				grid.set(row, col, Color.RED);
			}
		}

		setSquaresEmpty(grid, 0);
		setSquaresEmpty(grid, 10);

		grid.checkRows();

		// check if checkRows() worked correctly.
		for (int row = 0; row < Grid.HEIGHT; row++) {
			for (int col = 0; col < Grid.WIDTH; col++) {
				if (row != 19 && row != 18) {
					// all squares not in row 19 should be empty
					assertFalse("Fail state, wanted False, got True" + row + ", " + col, grid.isSet(row, col));
				} else if ((col == 5) || (col == 6)) {
					// must be not empty
//					if (grid.isSet(row, col))
					assertTrue("Fail state, wanted True, got False" + row + ", " + col, grid.isSet(row, col));

				} else {
					// must be empty
					assertFalse("Fail state, wanted False, got True" + row + ", " + col, grid.isSet(row, col));
				}
			}
		}
	}

	private static void setSquaresEmpty(Grid grid, int row) {
		for (int i = 0; i < 10; i++) {
			if (i != 5 && i != 6) {
				grid.set(row, i, Color.WHITE);
			}
		}
	}

	// to be used to inspect Failures.
	private void printFailState(Grid grid) {
		for (int row = 0; row < grid.HEIGHT; row++) {
			for (int col = 0; col < grid.WIDTH; col++) {
				System.out.println("row: " + row + " col: " + col + " isSet?: " + grid.isSet(row, col));
			}
		}
	}

	@Test
	public void testMovement() {
		/**
		 * starting points = row: 0, column: 4
		 *  				 row: 1, column: 4
		 *  				 row: 2, column: 4
		 *    				 row:2, column: 5]
		 * 
		 * Strangely, the X and Y values of the Point Objects seem to be flipped. 
		 * x is vertical and y in horizontal....
		 */

		// look for if hitting a wall stops movement
		// The assertFalse stopped one Square before the wall and never get stopped.
		// The assertTrue hit the wall and got stopped.
		assertFalse(wallTest(Direction.LEFT, 3));
		assertTrue(wallTest(Direction.LEFT, 4));

		assertFalse(wallTest(Direction.RIGHT, 3));
		assertTrue(wallTest(Direction.RIGHT, 4));

		assertFalse(wallTest(Direction.DOWN, 16));
		assertTrue(wallTest(Direction.DOWN, 17));

		// A set Square in the way test.
		// assertTrue for can move, assertFalse for can't move.
		assertFalse(setTest(Direction.LEFT, 0, 3));
		assertTrue(setTest(Direction.LEFT, 0, 2));

		assertFalse(setTest(Direction.RIGHT, 0, 5));
		assertTrue(setTest(Direction.RIGHT, 0, 6));

		assertFalse(setTest(Direction.DOWN, 3, 5));
		assertTrue(setTest(Direction.DOWN, 4, 5));

	}

	private boolean wallTest(Direction dir, int safeMoves) {
		Grid grid = new Grid();
		LShape piece = new LShape(1, Grid.WIDTH / 2 - 1, grid);

		for (int i = 0; i < safeMoves; i++) {
			if (piece.canMove(dir)) {
				piece.move(dir);
			}
		}
		Point[] before = piece.getLocations();

		if (piece.canMove(dir)) {
			piece.move(dir);
		}

		Point[] after = piece.getLocations();
		return checkSame(before, after);
	}

	private boolean checkSame(Point[] before, Point[] after) {
		for (int i = 0; i < before.length; i++) {
			if (before[i].getX() == after[i].getX() && before[i].getY() == after[i].getY()) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	private boolean setTest(Direction dir, int row, int col) {
		Grid grid = new Grid();
		LShape piece = new LShape(1, Grid.WIDTH / 2 - 1, grid);

		// set square right next to the piece
		grid.set(row, col, Color.BLACK);
		return piece.canMove(dir);
	}

}
