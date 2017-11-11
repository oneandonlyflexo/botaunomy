/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-nd/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package one.flexo.botaunomy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import one.flexo.botaunomy.block.ElvenAvatarBlock;
import one.flexo.botaunomy.block.tile.TileElvenAvatar;
import one.flexo.botaunomy.item.base.ItemBlockBase;


@Mod.EventBusSubscriber
@ObjectHolder("botaunomy")
public class ModBlocks {

	public static final ElvenAvatarBlock elven_avatar = null;


	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().registerAll(
				new ElvenAvatarBlock());

	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				new ItemBlockBase(ModBlocks.elven_avatar));

		registerTileEntities();
	}

	private static void registerTileEntities() {

		registerTile(TileElvenAvatar.class, elven_avatar);

	}

	private static void registerTile(Class<? extends TileEntity> clazz, Block block) {
		GameRegistry.registerTileEntity(clazz, block.getRegistryName().toString());
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {

	}

	@SideOnly(Side.CLIENT)
	public static void initItemModels() {

	}
}
