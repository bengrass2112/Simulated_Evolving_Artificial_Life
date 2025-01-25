package gui;

import java.util.TimerTask;

class BackgroundTask extends TimerTask {
	@Override
	public void run() {
		synchronized (GUIController.mutex) {
		GUIController.controller.advanceTime(1);
		}
	}
}
