/**
 * 
 */
package org.gianni.alexmc;

/**
 * A strategy that does nothing - basically a no-op
 */

import java.awt.Robot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Do nothing
 */
public class OffStrategy implements Strategy {
	private static Logger logger = LoggerFactory.getLogger(OffStrategy.class);
	private long beatCount = 0;

	public OffStrategy() {
		logger.debug("off strategy initialized.");
	}

	public void resume() {
		logger.debug("off strategy resumed.");
	}

	public int beat(Robot robot) {
		beatCount++;
		if (beatCount % 1000 == 0)
			logger.debug("heart beat felt " + beatCount + " times.");

		return Strategy.OK;
	}
}
