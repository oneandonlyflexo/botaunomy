/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-nd/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package one.flexo.botaunomy.block.base;

import java.util.function.IntFunction;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import one.flexo.botaunomy.ModInfo;
import vazkii.botania.client.core.handler.ModelHandler;
import vazkii.botania.client.render.IModelRegister;
import vazkii.botania.common.core.BotaniaCreativeTab;

/**
 * @author "oneandonlyflexo"
 *
 * This was mostly copied from Botania, but I imagine it will get heavily modified and customized once I find my
 * voice in this modding business.
 */
public abstract class BaseBlock extends Block implements IModelRegister {

	public BaseBlock(Material par2Material, String name) {
		super(par2Material);
		setUnlocalizedName(name);
		setRegistryName(new ResourceLocation(ModInfo.modid, name));
		if(registerInCreative())
			setCreativeTab(BotaniaCreativeTab.INSTANCE); //Being a botania addon, still adding things to this tab
	}

	protected boolean registerInCreative() {
		return true;
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


	public static void registerCustomItemblock(Block b, String path) {
		registerCustomItemblock(b, 1, i -> path);
	}

	public static void registerCustomItemblock(Block b, int maxExclusive, IntFunction<String> metaToPath) {
		Item item = Item.getItemFromBlock(b);
		for (int i = 0; i < maxExclusive; i++) {
			ModelLoader.setCustomModelResourceLocation(
					item, i,
					new ModelResourceLocation(ModInfo.modid + ":itemblock/" + metaToPath.apply(i), "inventory")
					);
		}
	}
}
