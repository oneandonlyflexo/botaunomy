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
    private static String toolsCanBeUsedOnBlock = "axe;pickaxe;hoe;shovel;hatchet;shears;dyePowder.white";
    private static String toolsCanBeUsedWithEntities= "bucket;bowl";
    private static String toolsCanShearEntities= "shears";
    private static String toolsCanAtackEntities= "sword";
    private static String itemsContainMana= "manatablet;capacitor";
    
    public static String[] onBlockToolsList;
    public static String[] entitiesToolsList;
    public static String[] entitiesShearsList;
    public static String[] entitiesAtacksList;
    public static String[] itemsContainManaList;
    
    public static int useManaCost=160;
    public static int rodManaCost=160;
    public static int breakManaCost=160;
    

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
        toolsCanBeUsedOnBlock = cfg.getString("toolsCanBeUsedOnBlock", CATEGORY_GENERAL, toolsCanBeUsedOnBlock, "Set name of tools , or part of name, than can be used on block, separated by ;");
        toolsCanBeUsedWithEntities = cfg.getString("toolsCanBeUsedWithEntities", CATEGORY_GENERAL, toolsCanBeUsedWithEntities, "Set name of tools , or part of name, than can be used, separated by ;");
        toolsCanShearEntities = cfg.getString("toolsCanBeShearEntities", CATEGORY_GENERAL, toolsCanShearEntities, "Set name of tools , or part of name, than can be used to shear, separated by ;");
        toolsCanAtackEntities = cfg.getString("toolsCanAtackEntities", CATEGORY_GENERAL, toolsCanAtackEntities, "Set name of tools , or part of name, than can be used to attack, separated by ;");
        itemsContainMana = cfg.getString("itemContainMana", CATEGORY_GENERAL, itemsContainMana, "Set name of items , or part of name, than can contain mana, separated by ;");
        
        useManaCost=cfg.getInt("UseManaCost", CATEGORY_GENERAL, useManaCost, 50, 1000, "Mana cost each time avatar uses a tool on entity");
        rodManaCost=cfg.getInt("RodManaCost", CATEGORY_GENERAL, rodManaCost, 50, 1000, "Mana cost each time avatar uses rod on a block");
        breakManaCost=cfg.getInt("BreakManaCost", CATEGORY_GENERAL, breakManaCost, 50, 1000, "Mana cost each time avatar try to break a block");
        
        //is needed to check if had shear , by defaut returns always true.
        
        onBlockToolsList = toolsCanBeUsedOnBlock.split(";");
        entitiesToolsList = toolsCanBeUsedWithEntities.split(";");
        entitiesShearsList = toolsCanShearEntities.split(";");
        entitiesAtacksList = toolsCanAtackEntities.split(";");
        itemsContainManaList = itemsContainMana.split(";");
        
        
    }

    private static void initDimensionConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_DIMENSION, "Dimension configuration");
    }

}
