/**
 * WIP - testing image recognition to support AFK fishing
 * Not yet working
 */

package org.gianni.alexmc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestImageParse {

	private static int[] sig = { 0x706b63, 0x87755a, 0x8e8c89, 0x493a1e, 0x1c0f00, 0x917e66, 0x6b6562, 0x050100,
			0x2c210f, 0x4f3f27, 0x30271b, 0x000000, 0x3c2e17, 0x73552d, 0x73562e, 0x696764, 0xffffff, 0xffffff,
			0xffffff, 0xffffff, 0xffffff, 0xffffb6, 0x66003a, 0x90dbff, 0xffffdb, 0x903a3a, 0x90b690, 0x3a3a90,
			0xdbffff, 0xffffb6, 0x66003a, 0x90dbff, 0xffffb6, 0x66003a, 0x90dbff, 0xffffb6, 0x66003a, 0x90dbff,
			0xffffff, 0xffffb6, 0x66003a, 0x90dbff, 0xffdb90, 0x3a0066, 0xb6ffff, 0xffffff, 0xffffff, 0xffffff,
			0xffdb90, 0x3a0066, 0xb6ffff, 0xffffff, 0xffffff, 0xffffff, 0xffffb6, 0x66003a, 0x90dbff, 0xffffff,
			0xffdb90, 0x3a0066, 0xb6ffff, 0xffffdb, 0x903a3a, 0x90dbff, 0xffffb6, 0x66003a, 0x90dbff, 0xffffff,
			0xffffb6, 0x66003a, 0x90dbff };

	private static Logger logger = LoggerFactory.getLogger(TestImageParse.class);
	private int width;

	private int ad_n = 0;
	private int ad_sum = 0;

	public static void main(String[] args) throws IOException {
		logger.debug("main() called.");
		TestImageParse obj = new TestImageParse();
		obj.run();
	}

	public int[] findsig(int[] sig, int[] field) {

		long st = System.nanoTime();
		logger.debug("findsig called - field contains " + field.length + " elements.");

		int sl = sig.length;
		int fl = field.length;

		List<Integer> matches = new ArrayList<Integer>();
		for (int i = 0; i <= fl - sl; i++) {
			int e = 0;
			while (e < sl && sig[e] == (field[e + i] & 0xffffff))
				e++;
			if (e == sl)
				matches.add(i);
			ad_n++;
			ad_sum += e;
		}

		int[] retval = new int[matches.size()];
		for (int i = 0; i < retval.length; i++)
			retval[i] = matches.get(i);

		long et = System.nanoTime();
		long delta = et - st;

		logger.debug(String.format("findsig took %.2f ms to finish.", (double) delta / 1000000.0));
		return retval;
	}

	private String rgb2hex(int color) {
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color >> 0) & 0xFF;

		return String.format("0x%02x%02x%02x", r, g, b);
	}

	public void run() throws IOException {
		String fn = "grabs/sg_00009.png";
		BufferedImage img = ImageIO.read(new File(fn));

		int h = img.getHeight();
		int w = img.getWidth();
		this.width = w;

		int[] rgb = img.getRGB(0, 0, w, h, null, 0, w);

		int[] matches = findsig(TestImageParse.sig, rgb);
		for (int m : matches) {
			int x = m % this.width;
			int y = m / this.width;
			System.out.println(String.format("[%d] - (%d, %d)", m, x, y));
		}

		logger.debug("findsig iterations: " + this.ad_n);
		logger.debug("findsig total count: " + this.ad_sum);
		logger.debug("findsig avg depth: " + (double) this.ad_sum / (double) ad_n);
	}

	public void printsig() throws IOException {
		String fn = "grabs/sg_00009.png";
		BufferedImage img = ImageIO.read(new File(fn));

		int h = img.getHeight();
		int w = img.getWidth();
		this.width = w;

		int[] rgb = img.getRGB(0, 0, w, h, null, 0, w);
		for (int i = 141; i < 212; i++) {
			System.out.println(String.format("%s,", rgb2hex(rgb[xy(i, 119)])));
		}
	}

	private int xy(int x, int y) {
		return y * this.width + x;
	}

}
