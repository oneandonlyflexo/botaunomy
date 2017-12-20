/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package one.flexo.botaunomy.nibbler;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import one.flexo.botaunomy.ModInfo;
import one.flexo.botaunomy.ModRegistry;
import one.flexo.nibbler.block.NibblerBlock;
import one.flexo.nibbler.registry.NibblerRegisteredObject;
import one.flexo.nibbler.registry.NibblerRegistry;
import vazkii.botania.client.core.handler.ModelHandler;

/**
 * This was mostly copied from Botania, but I imagine it will get heavily modified and customized once I find my
 * voice in this modding business.
 *
 * @author "oneandonlyflexo"
 */
public abstract class BlockBase extends NibblerBlock implements NibblerRegisteredObject {

	public BlockBase(Material par2Material, String name) {
		super(ModInfo.modid, name, par2Material, ModRegistry.tab);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		if(Item.getItemFromBlock(this) != Items.AIR)
			ModelHandler.registerBlockToState(this, 0, getDefaultState());
	}

	@Override
	public boolean eventReceived(IBlockState state, World world, BlockPos pos, int id, int param) {
		super.eventReceived(state, world, pos, id, param);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(id, param) : false;
	}

	@Override
	public NibblerRegistry getRegistry() {
		return ModRegistry.instance;
	}
}
