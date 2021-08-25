/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 * 
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package botaunomy.lexicon;

import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.common.crafting.ModCraftingRecipes;
import vazkii.botania.common.lexicon.BasicLexiconEntry;
import vazkii.botania.common.lexicon.page.PageCraftingRecipe;
import vazkii.botania.common.lexicon.page.PageText;

public abstract class LexiconData {

	private static LexiconEntry elvenAvatar;

	static {
		elvenAvatar = new BasicLexiconEntry("elven_avatar", BotaniaAPI.categoryDevices);
		elvenAvatar.setLexiconPages(new PageText("0"), new PageText("1"),
				new PageCraftingRecipe("2", ModCraftingRecipes.recipeAvatar));
	}
	
}
