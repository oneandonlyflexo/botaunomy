/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 * 
 * 
 * FakePlayer code from Thaumic-Tinkerer
 * https://github.com/Thaumic-Tinkerer/ThaumicTinkerer
 * Katrina Knight
 * KatrinaAS
 ******************************************************************************/
package botaunomy.block.tile;

//TODO

//El uso de mana en el config

//Tools witn entities (all enties in a range)
//variable for atack tools, for use tools ....





import java.util.UUID;
import javax.annotation.Nonnull;

import botaunomy.client.render.SecuencesAvatar;
import botaunomy.model.ModelAvatar3;
import botaunomy.nibbler.ItemBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import vazkii.botania.api.item.IAvatarTile;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.common.block.tile.TileSimpleInventory;
import botaunomy.item.RodItem;


/**
 * The key bit to this class is the increase in max mana and the ability to use IElvenAvatarWieldable items.  It also
 * Offers some methods to the wieldables as the elven weildables are more powerful and can do more
 *
 * @author "oneandonlyflexo"
 */
public class TileElvenAvatar extends TileSimpleInventory implements IAvatarTile , ITickable  {
	
	public  SecuencesAvatar secuencesAvatar=new SecuencesAvatar();
	
	public static final int POINTS_SEQUENCE_DURATION = 125;
	private float[][] anglePoints= new float[ModelAvatar3.NARC][ModelAvatar3.NPOINTS];
	
	private static final int MAX_MANA = 25600;
	private static final int AVATAR_TICK=20;
	protected static final String TAG_ENABLED = "enabled";
	protected static final String TAG_TICKS_ELAPSED = "ticksElapsed";
	protected static final String TAG_MANA = "mana";

	protected boolean enabled=true;
	protected int ticksElapsed;
	protected int mana;

	private TitleElvenAvatar_FakePlayerHelper fakePlayerHelper;


	public boolean isAvatarTick() {
		return((ticksElapsed%AVATAR_TICK==0));
	}
	
	public boolean haveItem() {
		return(this.getItemHandler().getStackInSlot(0)!=null);
	}
	
	public boolean haveMana(){
		return (mana>=200);
	}
	
	public  void inventoryToFakePlayer() {
		fakePlayerHelper.inventoryToFakePlayer();
	}
	
	public void resetBreak() {
		fakePlayerHelper.resetBreak();
	}
	
	public void updateRotatePoints(ModelRenderer[][] points,float[] RNDs,float elapsed) {
		
		for (int b=0;b<ModelAvatar3.NARC;b++)
		for (int a=0; a<ModelAvatar3.NPOINTS;a++) {			
			int points_duration = 1000- ((1000-POINTS_SEQUENCE_DURATION)*(mana/MAX_MANA));						
			anglePoints[b][a]+=((-3.1416F*2F/points_duration)*elapsed)*RNDs[a];
			if(anglePoints[b][a]>(-3.1416F*2F)) anglePoints[b][a]-=(-3.1416F*2F);			
			points[b][a].rotateAngleY=anglePoints[b][a]+(((-3.1416F*2.F)/ModelAvatar3.NARC)*b);
			points[b][a].rotateAngleZ=points[b][a].rotateAngleY;
			points[b][a].rotateAngleX=points[b][a].rotateAngleY;			
		}		
	}

	public TileElvenAvatar() {
		this(UUID.randomUUID());			
	}
	
	public TileElvenAvatar(UUID puuid) {	
		super();
		fakePlayerHelper= new TitleElvenAvatar_FakePlayerHelper(this,puuid);
	}
	
	@Override
	public  void  update() {
		enabled = true;
		for(EnumFacing dir : EnumFacing.VALUES) {
			int redstoneSide = world.getRedstonePower(pos.offset(dir), dir);
			if(redstoneSide > 0) {
				enabled = false;
				break;
			}
		}

		ItemStack stack = itemHandler.getStackInSlot(0);
		ItemStack stack2=null;
		if (itemHandler.getSlots()>=2 )
		    stack2 = itemHandler.getStackInSlot(1);
		
		if(!stack.isEmpty()) {
			if(stack.getItem() instanceof RodItem) {				
			    ItemBase itembase=(ItemBase) stack.getItem();
				if(itembase.nibbleResourceLocation.toString().equals("botaunomy:rod_will"))
						this.fakePlayerHelper.rodRightClick(this);
			}else
				
				if(stack2!=null && stack2.getItem() instanceof RodItem) {
					fakePlayerHelper.rightClickBlockWhithItem(stack);
				}else
				{					
					fakePlayerHelper.beginBreak(stack);
				}
		}

		if(enabled) {
			ticksElapsed++;			
		};
		
		fakePlayerHelper.updateHelper();
	}
	

	
	@Override
	public IItemHandler getInventory() {
		return getItemHandler();
	}
	
	@Override
	public int getSizeInventory() {
		return 2; //two hands
	}

	
    @Override
    protected SimpleItemStackHandler createItemHandler() {
    	//TileSimpleInventory    
    	return new SimpleItemStackHandler(this, true) {        	
            @Override
            protected int getStackLimit(int slot, ItemStack stack) {
                return 1;
            }            
        };
    }
			
	@Override
	public boolean isFull() {
		return mana >= MAX_MANA;
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		return !itemHandler.getStackInSlot(0).isEmpty();
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}



	@Override
	public EnumFacing getAvatarFacing() {
		return world.getBlockState(getPos()).getValue(BotaniaStateProps.CARDINALS);
	}
	
	public BlockPos Position() {
		return this.getPos();
	}

	@Override
	public int getElapsedFunctionalTicks() {
		return ticksElapsed;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void recieveMana(int mana) {
		this.mana = Math.max(0, Math.min(MAX_MANA, this.mana + mana));
		markDirty();		
	}
	
	@Override
	public void readPacketNBT(NBTTagCompound par1nbtTagCompound) {
		super.readPacketNBT(par1nbtTagCompound);
		enabled = par1nbtTagCompound.getBoolean(TAG_ENABLED);
		ticksElapsed = par1nbtTagCompound.getInteger(TAG_TICKS_ELAPSED);
		mana = par1nbtTagCompound.getInteger(TAG_MANA);		
		this.fakePlayerHelper.readPacketNBT(par1nbtTagCompound);
	}
	
	@Override
	public void writePacketNBT(NBTTagCompound par1nbtTagCompound) {
		super.writePacketNBT(par1nbtTagCompound);
		par1nbtTagCompound.setBoolean(TAG_ENABLED, enabled);
		par1nbtTagCompound.setInteger(TAG_TICKS_ELAPSED, ticksElapsed);
		par1nbtTagCompound.setInteger(TAG_MANA, mana);
		this.fakePlayerHelper.writePacketNBT(par1nbtTagCompound);
	}
	
	public void setmana(int pmana) {
		//to set from server message
		mana=pmana;
	}
	
}
