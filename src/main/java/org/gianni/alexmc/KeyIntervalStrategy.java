/**
 * 
 */
package org.gianni.alexmc;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * A simple beat strategy where a key hit repeatedly every interval of time.
 */
public class KeyIntervalStrategy implements Strategy {
	private static Logger logger = LoggerFactory.getLogger(GhostClicker.class);

	private int key;
	private long interval;
	private long last = System.currentTimeMillis();

	public KeyIntervalStrategy() {
		this(KeyEvent.VK_ALT, 5000);
	}

	public KeyIntervalStrategy(int key, long interval) {
		logger.debug("creating KeyIntervalStrategy (" + key + ", " + interval + ")");
		this.key = key;
		this.interval = interval;
		this.last = System.currentTimeMillis();
	}

	public void resume() {
		this.last = System.currentTimeMillis();
	}

	public int beat(Robot robot) {
		long now = System.currentTimeMillis();
		long delta = now - this.last;

		if (delta > this.interval) {
			logger.debug("interval triggered - pushing key.");
			robot.keyPress(key);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			robot.keyRelease(key);

			// subtract some amount to make it random (basically adds time to the interval)
			Random rand = new Random();
			int addTime = rand.nextInt((int) interval / 3) + rand.nextInt((int) interval / 3)
					+ rand.nextInt((int) interval / 3);
			this.last = System.currentTimeMillis() + addTime;
		}

		return Strategy.OK;
	}
}
