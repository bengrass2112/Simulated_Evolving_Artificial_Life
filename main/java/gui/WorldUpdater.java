package gui;

public class WorldUpdater implements Runnable {

	@Override
	public void run() {
		Client.get("world", CenterPane.get().canvas.getSubWorld());

	}

}
