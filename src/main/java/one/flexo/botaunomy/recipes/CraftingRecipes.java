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
import one.flexo.botaunomy.ModItems;

public class CraftingRecipes {

	public static ResourceLocation recipeElvenAvatar;

	public static ResourceLocation recipeWillRod;
	public static ResourceLocation recipeWorkRod;

	static {
		recipeElvenAvatar = ModBlocks.elven_avatar.getRegistryName();

		recipeWillRod = ModItems.rod_will.getRegistryName();
		recipeWorkRod= ModItems.rod_work.getRegistryName();
	}

}
