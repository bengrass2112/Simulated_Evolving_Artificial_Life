package system;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import controller.ControllerImpl;
import model.Critter;
import model.World;

public class SystemTest {

	@Test
	void test() {
		ControllerImpl controller = new ControllerImpl();

		controller.loadWorld("src/test/resources/files/testWorld.txt");

		controller.printWorld(System.out);

		Critter critter = World.tileAt(3, 2).critter();
		int[] mem = critter.getMemory();

		for (int i = 0; i < 32; i++) {
			controller.advanceTime(1);
			System.out.println("\n" + i);
			controller.printWorld(System.out);
			System.out.println(printArr(mem));
			if (i == 0) {
				assertEquals(11, mem[0]);
				assertEquals(1, mem[1]);
				assertEquals(3, mem[2]);
				assertEquals(1, mem[3]);
				assertEquals(500, mem[4]);
				assertEquals(42, mem[6]);
				assertEquals(1, mem[8]);
				assertEquals(2, mem[9]);
				assertEquals(3, mem[10]);
			}
			else if (i == 1) {
				assertEquals(11, mem[0]);
				assertEquals(1, mem[1]);
				assertEquals(3, mem[2]);
				assertEquals(1, mem[3]);
				assertEquals(500, mem[4]);
				assertEquals(42, mem[6]);
				assertEquals(100, mem[8]);
				assertEquals(103, mem[9]);
				assertEquals(104, mem[10]);
			} else if (i == 2) {
				assertEquals(11, mem[0]);
				assertEquals(1, mem[1]);
				assertEquals(3, mem[2]);
				assertEquals(1, mem[3]);
				assertEquals(500, mem[4]);
				assertEquals(42, mem[6]);
				assertEquals(-1, mem[8]);
				assertEquals(-2, mem[9]);
				assertEquals(104, mem[10]);
			} else if (i == 3) {
				assertEquals(11, mem[0]);
				assertEquals(1, mem[1]);
				assertEquals(3, mem[2]);
				assertEquals(1, mem[3]);
				assertEquals(499, mem[4]);
				assertEquals(42, mem[6]);
				assertEquals(-21, mem[8]);
				assertEquals(-11, mem[9]);
				assertEquals(-31, mem[10]);
			}
			else if (i == 4) {
				assertEquals(11, mem[0]);
				assertEquals(1, mem[1]);
				assertEquals(3, mem[2]);
				assertEquals(1, mem[3]);
				assertEquals(498, mem[4]);
				assertEquals(42, mem[6]);
				assertEquals(-11, mem[9]);
				assertEquals(-1, mem[10]);
			}
			else if (i == 5) {
				assertEquals(11, mem[0]);
				assertEquals(1, mem[1]);
				assertEquals(3, mem[2]);
				assertEquals(1, mem[3]);
				assertEquals(496, mem[4]);
				assertEquals(42, mem[6]);
				assertEquals(-1, mem[10]);
				// Check serve if time
			} else if (i == 6) {
				assertEquals(495, mem[4]);
			} else if (i == 7) {
				assertEquals(492, mem[4]);
			}
		}

	}

	private String printArr(int[] arr) {
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			str.append(arr[i] + " ");
		}
		return str.toString();
	}

}
