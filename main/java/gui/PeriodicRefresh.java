package gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class PeriodicRefresh {
	private Timeline t;

	public PeriodicRefresh(double rate) {
		setInterval(rate);
	}

	public void play() {
		t.play();
	}

	public void pause() {
		t.pause();
	}

	public void setInterval(double rate) {
		double interval = Math.max(1 / rate, 0.03333);
		boolean playing = false;
		if (t != null) {
			playing = t.getStatus() == Animation.Status.RUNNING;
			t.stop();
		}
		t = new Timeline(new KeyFrame(Duration.seconds(interval), e -> {
			UpdateTask update = new UpdateTask();
			new Thread(update).start();
		}));
		t.setCycleCount(Timeline.INDEFINITE);
		if (playing) {
			t.play();
		}
	}
}
