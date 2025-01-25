package gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ConstrainedDoubleProperty extends SimpleDoubleProperty {
	public final DoubleProperty max;
	public final DoubleProperty min;

	public ConstrainedDoubleProperty(double init, double maxValue, double minValue) {
		super(init);
		this.max = new SimpleDoubleProperty(maxValue);
		this.min = new SimpleDoubleProperty(minValue);
		max.addListener((observable, oldValue, newValue) -> {
			if (newValue.doubleValue() < min.get()) {
				max.set(min.get());
			}
			if (newValue.doubleValue() < this.get()) {
					this.set(newValue.doubleValue());
				}

			});
		min.addListener((observable, oldValue, newValue) -> {
			if (newValue.doubleValue() > max.get()) {
				min.set(max.get());
			}
			if (newValue.doubleValue() > this.get()) {
				this.set(newValue.doubleValue());
			}

		});
	}

	@Override
	public void set(double d) {
		if (d >= max.get()) {
			super.set(max.get());
		} else if (d <= min.get()) {
			super.set(min.get());
		} else {
			super.set(d);
		}
	}

}
