/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package one.flexo.botaunomy;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import one.flexo.botaunomy.block.ElvenAvatarBlock;
import one.flexo.botaunomy.block.tile.TileElvenAvatar;


public class ModBlocks {

	public static final ElvenAvatarBlock elven_avatar = new ElvenAvatarBlock();

	public static void init() {

	}

	public static void registerTileEntities() {

		registerTile(TileElvenAvatar.class, elven_avatar);

	}

	private static void registerTile(Class<? extends TileEntity> clazz, Block block) {
		GameRegistry.registerTileEntity(clazz, block.getRegistryName().toString());
	}
}
