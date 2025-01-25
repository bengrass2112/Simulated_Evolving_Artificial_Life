package model;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import controller.ControllerImpl;

/**
 * Test suite for the controller.
 */
class ControllerTest {

    @Test
    void testNewWorld() {

        World.clearWorld();
        ControllerImpl controller = new ControllerImpl();
        try {
            controller.newWorld(10, 10);

            Tile[][] a = World.tiles();
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < a[i].length; j++) {
                    assertTrue(a[i][j].empty());
                }
            }

        } catch (Exception e) {
            fail("Should not have thrown an exception.");
        }

    }

    @Test
    void testLoadWorld() {

        World.clearWorld();
        ControllerImpl controller = new ControllerImpl();
        try {
            controller.loadWorld("src/test/resources/files/world.txt");
        } catch (Exception e) {
            fail("Should not have thrown an exception.");
        }

    }

    @Test
    void testPrintWorld() {

        World.clearWorld();
        ControllerImpl controller = new ControllerImpl();
        try {
            controller.loadWorld("src/test/resources/files/world.txt");
            controller.printWorld(System.out);
        } catch (Exception e) {
            fail("Should not have thrown an exception.");
        }
    }

    @Test
    void testRandomWorld() {

        World.clearWorld();
        ControllerImpl controller = new ControllerImpl();
        try {
            controller.newWorld();
        } catch (Exception e) {
            fail("Should not have thrown an exception.");
        }
    }

    void testloadCritters() {

        World.clearWorld();
        ControllerImpl controller = new ControllerImpl();
        try {
            controller.newWorld();
            controller.loadCritters("src/test/resources/files/example-critter.txt", 5);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have thrown an exception.");
        }
    }

    @Test
    void testTimeStep() {

        World.clearWorld();
        ControllerImpl controller = new ControllerImpl();
        try {
            controller.newWorld();
            controller.loadCritters("src/test/resources/files/example-critter.txt", 50);
            for (int i = 0; i < 10000; i++) {
                controller.advanceTime(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have thrown an exception.");
        }
    }

}
