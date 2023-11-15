/**
 * WIP - testing image recognition to support AFK fishing
 * Not yet working
 */

package org.gianni.alexmc;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestImgStrategy implements Strategy {
	private static Logger logger = LoggerFactory.getLogger(TestImgStrategy.class);

	private long interval;
	private long last = System.currentTimeMillis();
	private int count = 0;

	public TestImgStrategy() {
		this(3000l);
	}

	public TestImgStrategy(long interval) {
		this.interval = interval;
		this.last = System.currentTimeMillis();
	}

	public void resume() {
		this.last = System.currentTimeMillis();
	}

	public int beat(Robot robot) {
		long now = System.currentTimeMillis();
		long delta = now - this.last;

		if (delta > interval) {
			logger.debug("taking a screen grab - " + count);
			takeScreenGrab(robot);
			this.last = System.currentTimeMillis();
		}

		return Strategy.OK;
	}

	public void takeScreenGrab(Robot robot) {
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
		int dat[] = screenFullImage.getRGB(0, 0, screenRect.width, screenRect.height, null, 0, screenRect.width);
		logger.debug("screen grab buffer size: " + dat.length);
		String fn = String.format("grabs/sg_%05d.png", count++);
		try {
			ImageIO.write(screenFullImage, "png", new File(fn));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
