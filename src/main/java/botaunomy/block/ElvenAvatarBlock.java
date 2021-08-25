/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/




package botaunomy.block;

import java.util.UUID;

import javax.annotation.Nonnull;
import botaunomy.config.Config;
import botaunomy.item.RodItem;
import botaunomy.ModInfo;
import botaunomy.block.tile.TileElvenAvatar;
import botaunomy.nibbler.BlockBase;
import botaunomy.nibbler.TileEntityRegisteredBlocked;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.common.block.tile.TileSimpleInventory;
import vazkii.botania.common.core.helper.InventoryHelper;
import vazkii.botania.common.lexicon.LexiconData;


public class ElvenAvatarBlock extends BlockBase implements ILexiconable,TileEntityRegisteredBlocked {
	
	private UUID placerUUID;

	public static final String NAME = "elven_avatar";

	private static final AxisAlignedBB X_AABB = new AxisAlignedBB(.3125, 0, .21875, 1-.3125, 17/16.0, 1-.21875);
	private static final AxisAlignedBB Z_AABB = new AxisAlignedBB(.21875, 0, .3125, 1-.21875, 17/16.0, 1-.3125);

	public ElvenAvatarBlock() {
		super(Material.IRON, NAME);
		setHardness(2.0F);
		setSoundType(SoundType.METAL);
		setDefaultState(blockState.getBaseState().withProperty(BotaniaStateProps.CARDINALS, EnumFacing.NORTH));
		
		this.setUnlocalizedName(ModInfo.modid + "." + NAME);
	}
	
	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
		if(state.getValue(BotaniaStateProps.CARDINALS).getAxis() == EnumFacing.Axis.X)
			return X_AABB;
		else return Z_AABB;
	}

	@Nonnull
	@Override
	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, BotaniaStateProps.CARDINALS);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BotaniaStateProps.CARDINALS).getIndex();
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta < 2 || meta > 5) {
			meta = 2;
		}
		return getDefaultState().withProperty(BotaniaStateProps.CARDINALS, EnumFacing.getFront(meta));
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing s, float xs, float ys, float zs) {
		
	
			TileElvenAvatar avatar = (TileElvenAvatar) world.getTileEntity(pos);
			ItemStack stackOnAvatar = avatar.getItemHandler().getStackInSlot(0);
			ItemStack stackOnAvatar2 =null; 
			if (avatar.getItemHandler().getSlots()>=2 )
				stackOnAvatar2= avatar.getItemHandler().getStackInSlot(1);
			
			ItemStack stackOnPlayer = player.getHeldItem(hand);
			
			String  nameStackOnPlayer=stackOnPlayer.getUnlocalizedName();
			boolean rodWorkOnPlayer= (stackOnPlayer.getItem() instanceof RodItem) && nameStackOnPlayer.contains("rod_work");
			boolean rodWillOnAvatar= (stackOnAvatar.getItem() instanceof RodItem) && stackOnAvatar.getUnlocalizedName().contains("rod_will");
			
			//from title to player	
			
			if (hand == EnumHand.OFF_HAND ) return false;
			
			if(stackOnAvatar2!= null && !stackOnAvatar2.isEmpty()) {	
				avatar.getItemHandler().setStackInSlot(1, ItemStack.EMPTY);
				ItemHandlerHelper.giveItemToPlayer(player, stackOnAvatar2);
				avatar.markDirty();	
			}else
				if(!stackOnAvatar.isEmpty()&&!rodWorkOnPlayer) {
					avatar.secuencesAvatar.ActivateSecuence("DownArm");
					avatar.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
					ItemHandlerHelper.giveItemToPlayer(player, stackOnAvatar);
					avatar.resetBreak();
					avatar.markDirty();
					return true;
				} else 					
					if(!stackOnPlayer.isEmpty()) // && (stackOnPlayer.getItem() instanceof IAvatarWieldable || stackOnPlayer.getItem() instanceof IElvenAvatarWieldable))				
					{  
						
										
						boolean dontGive;
						dontGive=nameStackOnPlayer.equals("item.twigWand");
						dontGive|=(Block.getBlockFromItem(player.getHeldItemMainhand().getItem()) != Blocks.AIR); 
						
						if (!dontGive&&checkStackName(nameStackOnPlayer))
							//dont let give botania twigwand or block
						{							
							//player.sendStatusMessage(new TextComponentString(TextFormatting.GREEN + "->" +nameStackOnPlayer), false);						
							if (rodWorkOnPlayer) {
								if (!rodWillOnAvatar && !stackOnAvatar.isEmpty()) avatar.getItemHandler().setStackInSlot(1, stackOnPlayer.splitStack(1));
								//rod_work to left hand
							}
							else {
								avatar.secuencesAvatar.ActivateSecuence("RiseArm");		
								avatar.getItemHandler().setStackInSlot(0, stackOnPlayer.splitStack(1));		
								avatar.inventoryToFakePlayer();
							}
							avatar.markDirty();
							return true;
						}
					}
		
		return false;
	}
	
	private boolean checkStackName(String s) {

		if (s.toLowerCase().contains("rod_will")) return true;
		if (s.toLowerCase().contains("rod_work")) return true;
		for(int a = 0;a<Config.toolsList.length; a++) {
			if (s.toLowerCase().contains(Config.toolsList[a])) return true;
		}
		return false;
	}

	@Override
	public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
		TileSimpleInventory inv = (TileSimpleInventory) world.getTileEntity(pos);
		InventoryHelper.dropInventory(inv, world, state, pos);
		super.breakBlock(world, pos, state);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {	
		if (placer instanceof EntityPlayer){
			placerUUID=EntityPlayer.getUUID(((EntityPlayer)placer).getGameProfile());
		}else {
			placerUUID = UUID.randomUUID();
		}	
		world.setBlockState(pos, state.withProperty(BotaniaStateProps.CARDINALS, placer.getHorizontalFacing().getOpposite()));
		//TileElvenAvatar avatar = (TileElvenAvatar) world.getTileEntity(pos);		
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Nonnull
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileElvenAvatar(placerUUID);
	}
	
	
	@Override
	public Class<? extends TileEntity> getTileEntityClass(){
		return TileElvenAvatar.class;
	}

	@Nonnull
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
		return LexiconData.avatar;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		customRegisterModels();
	}

}
