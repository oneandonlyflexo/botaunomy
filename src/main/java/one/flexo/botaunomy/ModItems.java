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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import one.flexo.botaunomy.client.render.IModelRegister;
import one.flexo.botaunomy.item.WillRodItem;


@Mod.EventBusSubscriber
@ObjectHolder("botaunomy")
public class ModItems {

	public static final WillRodItem rod_will = null;
	public static final WillRodItem rod_work = null;



	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().registerAll(
				new WillRodItem("rod_will", true),
				new WillRodItem("rod_work", false));
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		for(Object block : Block.REGISTRY) {
			if(block instanceof IModelRegister)
				((IModelRegister) block).registerModels();
		}

		for(Object item : Item.REGISTRY) {
			if(item instanceof IModelRegister)
				((IModelRegister) item).registerModels();
		}
	}
}
