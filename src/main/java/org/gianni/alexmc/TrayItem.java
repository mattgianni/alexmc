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
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class TrayItem implements NativeKeyListener {

	private static Logger logger = LoggerFactory.getLogger(TrayItem.class);

	private GhostClicker ghost = null;
	private Thread worker = null;

	private Map<String, Strategy> strategies;

	private TrayItem() {
		strategies = new LinkedHashMap<String, Strategy>();
		strategies.put("Slime", new KillStrategy(5, 6000L, 1500L));
		strategies.put("Grabber", new TestImgStrategy());
		strategies.put("AFK", new KeyIntervalStrategy());
		strategies.put("AFK Space", new KeyIntervalStrategy(KeyEvent.VK_SPACE, 20000));
		strategies.put("Off", new OffStrategy());
	}

	public void nativeKeyPressed(NativeKeyEvent e) {
		logger.trace(
				"nativeKeyPressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()) + " [" + e.getKeyCode() + "]"
						+ " END = " + NativeKeyEvent.VC_END);

		if (e.getKeyCode() == NativeKeyEvent.VC_END) {
			logger.debug("END key pressed");
			ghost.toggle();
		}
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
		// logger.debug(
		// "nativeKeyReleased: " + NativeKeyEvent.getKeyText(e.getKeyCode()) + "[" +
		// e.getKeyCode() + "]");
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
		// logger.debug("Key Typed: " +
		// NativeKeyEvent.getKeyText(e.getKeyCode()));
	}

	private void init() {

		ghost = new GhostClicker();
		worker = new Thread(ghost);
		worker.start();

		TrayIcon trayIcon = null;

		if (SystemTray.isSupported()) {

			SystemTray tray = SystemTray.getSystemTray();

			// get the image for the tray icon
			Image image = Toolkit.getDefaultToolkit()
					.getImage(getClass().getClassLoader().getResource("images/alex_face_16.png"));

			ActionListener actionQuit = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logger.debug(e.getActionCommand() + " clicked.");

					logger.debug("unregistering native hook ...");
					try {
						GlobalScreen.unregisterNativeHook();
					} catch (Exception ex) {
						logger.error("failed to unregister native hook ...");
					}

					logger.debug("killing ghost");
					ghost.kill();

					logger.debug("stopping the worker thread");
					worker.interrupt();
					try {
						logger.debug("attempting to reconnect with child thread ...");
						worker.join(5000);
						logger.debug("sucessfully reconnected with child thread!");
					} catch (Exception ex) {
						logger.debug("failed to join with worker thread ... exitting anyway.");
					}

					logger.debug("application exitting.");
					System.exit(0);
				}
			};
			// create a popup menu
			PopupMenu popup = new PopupMenu();

			// add the stragegies as menu items
			for (var entry : strategies.entrySet()) {
				MenuItem item = new MenuItem(entry.getKey());
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						logger.debug(e.getActionCommand() + " clicked.");
						ghost.setStrategy(entry.getValue());
					}
				});
				popup.add(item);
			}

			// add separator
			popup.addSeparator();

			// create menu item for the default action
			MenuItem quitItem = new MenuItem("Quit");
			quitItem.addActionListener(actionQuit);
			popup.add(quitItem);

			// construct a TrayIcon
			trayIcon = new TrayIcon(image, "Alex MC", popup);

			// what happens when you double-click -- just quit for now
			trayIcon.addActionListener(actionQuit);

			// ...
			// add the tray image
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}

			// setup the global keyboard hook
			try {
				GlobalScreen.registerNativeHook();
				GlobalScreen.addNativeKeyListener(this);
			} catch (NativeHookException e) {
				logger.error("failed to register native hook ...");
			}

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
