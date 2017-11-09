/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-nd/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package one.flexo.botaunomy;

import net.minecraft.block.Block;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import one.flexo.botaunomy.block.ElvenAvatarBlock;

@ObjectHolder("botaunomy")
public class ModBlocks {

	public static final ElvenAvatarBlock elven_avatar = null;


	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
				new ElvenAvatarBlock());
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {

	}

	@SideOnly(Side.CLIENT)
	public static void initItemModels() {

	}
}
