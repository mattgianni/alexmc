package org.gianni.alexmc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Driver {

	private static Logger logger = LoggerFactory.getLogger(Driver.class);

	public static void main(String[] args) {
		logger.debug("starting application");
		TrayItem.factory();
	}

}
