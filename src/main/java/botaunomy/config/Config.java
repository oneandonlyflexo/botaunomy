/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package botaunomy.config;

import org.apache.logging.log4j.Level;

import botaunomy.Botaunomy;
import botaunomy.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

public class Config {

	static final String CATEGORY_GENERAL = "general";
	static final String CATEGORY_DIMENSION = "dimensions";

    // This values below you can access elsewhere in your mod:
    private static String toolsCanBeUsedtoBreak = "axe;pickaxe;hoe;shovel;hatchet";
    public static String[] toolsList;
    

    // Call this from CommonProxy.preInit(). It will create our config if it doesn't
    // exist yet and read the values if it does exist.
	public static void readConfig() {
		Configuration cfg = CommonProxy.config;
		try {

            cfg.load();
            initGeneralConfig(cfg);
            initDimensionConfig(cfg);

		} catch (Exception e1) {
			Botaunomy.logger.log(Level.ERROR, "Problem loading config file!", e1);
		} finally {
			if (cfg.hasChanged()) {
				cfg.save();
			}
		}
	}

    private static void initGeneralConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
        // cfg.getBoolean() will get the value in the config if it is already specified there. If not it will create the value.        
        //boolean = cfg.getBoolean("nameBoolean", CATEGORY_GENERAL, , "Set to false if you don't like this tutorial");
        toolsCanBeUsedtoBreak = cfg.getString("toolsCanBeUsedtoBreak", CATEGORY_GENERAL, toolsCanBeUsedtoBreak, "Set name of tools , or part of name, than can be used, separated by ;");
        
        toolsList = toolsCanBeUsedtoBreak.split(";");
    }

    private static void initDimensionConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_DIMENSION, "Dimension configuration");
    }

}
