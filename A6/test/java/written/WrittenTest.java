package written;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import controller.ControllerImpl;
import model.World;

class WrittenTest {

    @Test
    void testSpiral() {

        World.clearWorld();
        ControllerImpl controller = new ControllerImpl();
        int i = 0;
        try {
            controller.loadWorld("src/test/resources/files/spiralWorld.txt");
            controller.printWorld(System.out);
            for (; i < 150; i++) {
                controller.advanceTime(1);
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                controller.printWorld(System.out);
                Thread.sleep(750);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have thrown an exception.");
        }
    }

    @Test
    void testEatAndBud() {

        World.clearWorld();
        ControllerImpl controller = new ControllerImpl();
        try {
            controller.newWorld(24, 24);
            controller.loadCritters("src/test/resources/files/eat-and-bud.txt", 1);
            controller.printWorld(System.out);
            for (int i = 0; i < 150; i++) {
                System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
                controller.advanceTime(1);
                controller.printWorld(System.out);
                Thread.sleep(750);
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Should not have thrown an exception.");
        }

    }

}
