/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-nd/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package one.flexo.botaunomy;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import one.flexo.botaunomy.item.WillRodItem;
import one.flexo.botaunomy.item.base.ItemBlockBase;

@ObjectHolder("botaunomy")
public class ModItems {

	public static final WillRodItem rod_will = null;
	public static final WillRodItem rod_work = null;

	public static void registerItems(Register<Item> event) {
		event.getRegistry().registerAll(
				new WillRodItem("rod_will", true),
				new WillRodItem("rod_work", false));
	}

	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				new ItemBlockBase(ModBlocks.elven_avatar));
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {

	}
}
