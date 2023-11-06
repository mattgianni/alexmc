package org.gianni.alexmc;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mattg
 */
public class GhostClicker implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(GhostClicker.class);
	private static long beatInterval = 50;

	private Robot robot = null;
	private boolean alive = false;
	private Strategy strategy = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		alive = true;
		try {
			robot = new Robot();
			// this.setStrategy(new KeyIntervalStrategy());
			this.setStrategy(new OffStrategy());

			// Random rand = new Random();

			try {
				while (isAlive()) {
					strategy.beat(robot);
					Thread.sleep(beatInterval);
				}
			} catch (InterruptedException ex) {
				logger.debug("sleep interrupted.");
			}

			logger.debug("thread " + this.toString() + ": exitting.");
		} catch (AWTException ex) {
			ex.printStackTrace();
		}
	}

	public synchronized void setStrategy(Strategy strategy) {
		logger.debug("setting strategy to " + strategy.getClass().getName());
		this.strategy = strategy;
	}

	public synchronized boolean isAlive() {
		return alive;
	}

	public synchronized void kill() {
		logger.debug("kill called");
		alive = false;
	}

	public void click(int x, int y) {
		// robot.mouseMove(x, y);
		logger.debug("click called");
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		/*
		 * robot.keyPress(KeyEvent.VK_ALT);
		 * robot.keyRelease(KeyEvent.VK_ALT);
		 */
	}
}
