/**
 * 
 */
package org.gianni.alexmc;

import java.awt.Robot;
import java.awt.event.InputEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple beat strategy where a key hit repeatedly every interval of time.
 */
public class KillStrategy implements Strategy {
	private static Logger logger = LoggerFactory.getLogger(KillStrategy.class);

	private int swings;
	private long swingGap;
	private long interval;
	private long last = System.currentTimeMillis();
	private int currentSwing = 0;
	private boolean feed = true;
	private long feedCounter = 0l;

	public KillStrategy() {
		this(4, 60000l, 1000l);
	}

	public KillStrategy(int swings) {
		this(swings, 60000l, 2000l);
	}

	public KillStrategy(int swings, long interval, long swingGap) {
		logger.debug("creating KillStrategy (" + swings + ", " + interval + ")");
		this.swings = swings;
		this.interval = interval;
		this.swingGap = swingGap;
		this.last = System.currentTimeMillis();
	}

	public void resume() {
		last = System.currentTimeMillis();
		feedCounter = 0l;
		currentSwing = 0;
	}

	private void feed(Robot robot) {
		this.feedCounter++;
		if (this.feed && this.feedCounter % 20 == 0) {
			logger.debug("feed counter exceeded, time to eat");
			robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
			logger.debug("finished eating");
		}
	}

	public int beat(Robot robot) {
		long now = System.currentTimeMillis();
		long delta = now - this.last;

		if (delta > (interval + currentSwing * swingGap)) {
			currentSwing++;
			logger.debug("taking swing #" + currentSwing);
			robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
			if (currentSwing == swings) {
				logger.debug("done with this cycle of swings");
				this.currentSwing = 0;
				this.feed(robot);
				this.last = System.currentTimeMillis();
			}
		}

		return Strategy.OK;
	}
}
