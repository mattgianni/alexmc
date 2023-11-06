package org.gianni.alexmc;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrayItem {

	private static Logger logger = LoggerFactory.getLogger(TrayItem.class);

	private GhostClicker ghost = null;
	private Thread worker = null;

	private TrayItem() {
	}

	private void init() {

		ghost = new GhostClicker();
		worker = new Thread(ghost);
		worker.start();

		TrayIcon trayIcon = null;

		if (SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();

			Image image = Toolkit.getDefaultToolkit()
					.getImage(getClass().getClassLoader().getResource("images/blue-finger-click.png"));

			ActionListener actionQuit = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logger.debug(e.getActionCommand() + " clicked.");
					ghost.kill();
					worker.interrupt();
					try {
						logger.debug("attemptign to reconnect with child thread ...");
						worker.join(5000);
						logger.debug("sucessfully reconnected with child thread!");
					} catch (Exception ex) {
						logger.debug("failed to join with worker thread ... exitting anyway.");
					}

					logger.debug("application exitting.");
					System.exit(0);
				}
			};

			ActionListener actionPickStrategy = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String cmd = e.getActionCommand();
					logger.debug(cmd + " clicked.");

					if (cmd == "MC4")
						ghost.setStrategy(new KillStrategy());
					else if (cmd == "MC5")
						ghost.setStrategy(new KillStrategy(5));
					else if (cmd == "Grabber")
						ghost.setStrategy(new TestImgStrategy());
					else if (cmd == "Off")
						ghost.setStrategy(new OffStrategy());
					else if (cmd == "AFK")
						ghost.setStrategy(new KeyIntervalStrategy());
					else if (cmd == "AFK Space")
						ghost.setStrategy(new KeyIntervalStrategy(KeyEvent.VK_SPACE, 20000));
				}
			};

			// create a popup menu
			PopupMenu popup = new PopupMenu();

			// add strategies
			MenuItem grabber = new MenuItem("Grabber");
			grabber.addActionListener(actionPickStrategy);
			popup.add(grabber);

			MenuItem mc4 = new MenuItem("MC4");
			mc4.addActionListener(actionPickStrategy);
			popup.add(mc4);

			MenuItem mc5 = new MenuItem("MC5");
			mc5.addActionListener(actionPickStrategy);
			popup.add(mc5);

			MenuItem defaultItem = new MenuItem("AFK");
			defaultItem.addActionListener(actionPickStrategy);
			popup.add(defaultItem);

			MenuItem afkSpace = new MenuItem("AFK Space");
			afkSpace.addActionListener(actionPickStrategy);
			popup.add(afkSpace);

			MenuItem off = new MenuItem("Off");
			off.addActionListener(actionPickStrategy);
			popup.add(off);

			// create menu item for the default action
			MenuItem quitItem = new MenuItem("Quit");
			quitItem.addActionListener(actionQuit);
			popup.add(quitItem);

			// construct a TrayIcon
			trayIcon = new TrayIcon(image, "Alex MC", popup);

			// set the TrayIcon properties
			trayIcon.addActionListener(actionQuit);

			// ...
			// add the tray image
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}
			// ...

		} else {
			// no system tray support
		}

	}

	public static TrayItem factory() {
		// create the TrayItem
		TrayItem ti = new TrayItem();
		ti.init();

		return ti;
	}
}
