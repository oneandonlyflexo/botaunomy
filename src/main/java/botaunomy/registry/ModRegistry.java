/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * This file is part of Bark Books, which is open source:
 * https://github.com/oneandonlyflexo/barkbooks
 ******************************************************************************/
package botaunomy.registry;

import net.minecraft.creativetab.CreativeTabs;
//import one.flexo.nibbler.registry.NibblerRegistry;
import vazkii.botania.common.core.BotaniaCreativeTab;

public class ModRegistry {

	public static final Registry instance = new Registry();

	public static CreativeTabs tab = BotaniaCreativeTab.INSTANCE;
}
