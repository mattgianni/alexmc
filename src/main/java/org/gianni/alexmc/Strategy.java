package org.gianni.alexmc;

import java.awt.Robot;

public interface Strategy {
	static final int OK = 1;

	public int beat(Robot robot);
}
