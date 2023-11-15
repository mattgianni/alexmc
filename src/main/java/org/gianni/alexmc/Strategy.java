package org.gianni.alexmc;

import java.awt.Robot;

public interface Strategy {
	static final int OK = 1;

	// gets called periodically by the GhostClicker
	public int beat(Robot robot);

	// gets called when the strategy gets resumed
	public void resume();
}
