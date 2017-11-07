/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-nd/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package one.flexo.botaunomy.config;

import org.apache.logging.log4j.Level;

import net.minecraftforge.common.config.Configuration;
import one.flexo.botaunomy.Botaunomy;
import one.flexo.botaunomy.proxy.CommonProxy;

public class Config {

	static final String CATEGORY_GENERAL = "general";
	static final String CATEGORY_DIMENSION = "dimensions";

	public static void readConfig() {
		Configuration cfg = CommonProxy.config;
		try {

			//Add in configs here when we got 'em!

		} catch (Exception e1) {
			Botaunomy.logger.log(Level.ERROR, "Problem loading config file!", e1);
		} finally {
			if (cfg.hasChanged()) {
				cfg.save();
			}
		}
	}

	static void initGeneralConfig(Configuration cfg) {

	}

}
