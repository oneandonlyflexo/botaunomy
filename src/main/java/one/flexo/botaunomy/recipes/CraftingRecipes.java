/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-nd/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package one.flexo.botaunomy.recipes;

import net.minecraft.util.ResourceLocation;
import one.flexo.botaunomy.ModBlocks;

public class CraftingRecipes {

	public static ResourceLocation recipeElvenAvatar;

	static {
		recipeElvenAvatar = ModBlocks.elven_avatar.getRegistryName();
	}

}
