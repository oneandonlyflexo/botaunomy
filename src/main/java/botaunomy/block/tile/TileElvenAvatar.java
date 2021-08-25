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

//TODO

//parar swin cuando se termina el mana
//icono botnaia wand of forest
//Poder cargar un json directamente como modelo (generar el código en el aire).


package botaunomy.block.tile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import botaunomy.ItemStackType;
import botaunomy.client.render.SecuencesAvatar;
import botaunomy.model.ModelAvatar3;
import botaunomy.network.MessageMoveArm;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import vazkii.botania.api.item.IAvatarTile;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.common.block.tile.TileSimpleInventory;

/**
 * The key bit to this class is the increase in max mana and the ability to use IElvenAvatarWieldable items.  It also
 * Offers some methods to the wieldables as the elven weildables are more powerful and can do more
 *
 * @author "oneandonlyflexo"
 */
public class TileElvenAvatar extends TileSimpleInventory implements IAvatarTile , ITickable ,IElvenAvatarItemHadlerChangedListener {
	
	public  SecuencesAvatar secuencesAvatar=new SecuencesAvatar();
	
	public static final int POINTS_SEQUENCE_DURATION = 125;
	private float[][] anglePoints= new float[ModelAvatar3.NARC][ModelAvatar3.NPOINTS];
	
	private static final int MAX_MANA = 125000;//quarter Mana Tablet
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
		return getInventory().haveItem();
	}
	
	public boolean haveMana(){
		return (mana>=200);
	}
	
	public  void inventoryToFakePlayer() {
		fakePlayerHelper.inventoryToFakePlayer();
	}
	
	public boolean resetBreak() {
		return fakePlayerHelper.resetBreak();
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
		
		if (getWorld().isRemote) return;

		boolean isAlreadyEnabled=enabled;
		enabled = true;
		for(EnumFacing dir : EnumFacing.VALUES) {
			int redstoneSide = world.getRedstonePower(pos.offset(dir), dir);
			if(redstoneSide == 15) {
				enabled = false;
				if (secuencesAvatar.isActive()&& isAlreadyEnabled)
					if (getInventory().haveItem()) {						
						new MessageMoveArm (getPos(),MessageMoveArm.RISE_ARM);
					}else {
						new MessageMoveArm (getPos(),MessageMoveArm.DOWN_ARM);			
						resetBreak();
					}	
				break;
			}
		}
		
		ItemStackType.Types type0=getInventory().getType0();
		ItemStackType.Types type1=getInventory().getType1();
		
		if(getInventory().haveItem()) {
			if (type0==ItemStackType.Types.ROD_WILL) {						  
					this.fakePlayerHelper.rodRightClick(this);
			}else //is a tool
				
				if(type1==ItemStackType.Types.ROD_WORK) { //left click a block					
					if (type0==ItemStackType.Types.BREAK) fakePlayerHelper.rightClickBlockWhithItem();;										
				}else 
				{														
					if (type0==ItemStackType.Types.BREAK) fakePlayerHelper.beginBreak();
					if (type0==ItemStackType.Types.USE||type0==ItemStackType.Types.SHEAR||type0==ItemStackType.Types.KILL) fakePlayerHelper.beginUse();
				}
		}

		if(enabled) {
			ticksElapsed++;			
		};
		
		fakePlayerHelper.updateHelper(ticksElapsed);
	}
	

	
	@Override
	public ElvenAvatarItemHadler getInventory() {
		return (ElvenAvatarItemHadler)getItemHandler();		
	}
	
	@Override
	public int getSizeInventory() {
		return 2; //two hands
	}
    
    @Override
    protected SimpleItemStackHandler createItemHandler() {
    	//TileSimpleInventory        	
    	return new ElvenAvatarItemHadler(this, true);        
    }
	
	    	
	public class ElvenAvatarItemHadler extends SimpleItemStackHandler{
		
		private ItemStackType.Types cacheType0;
		private ItemStackType.Types cacheType1;
		private ItemStack [] lastTryToInsert=new ItemStack [2] ;
		private List <IElvenAvatarItemHadlerChangedListener> listIInventoryChangedListener= new ArrayList<IElvenAvatarItemHadlerChangedListener> ();  
    	
		
		public <T extends TileSimpleInventory & IElvenAvatarItemHadlerChangedListener > ElvenAvatarItemHadler(T inv, boolean allowWrite) {
			super(inv, allowWrite);
			if (super.getStackInSlot(0)==null) super.setStackInSlot(0, ItemStack.EMPTY);
			if (super.getStackInSlot(1)==null) super.setStackInSlot(1, ItemStack.EMPTY);		

			reloadCache();
			addIInventoryChangedListener(inv);
		}
		
		private void reloadCache() {
			cacheType0= ItemStackType.getTypeTool(super.getStackInSlot(0));
			cacheType1= ItemStackType.getTypeTool(super.getStackInSlot(1));
		}
		
		 @Override
		 public void deserializeNBT(NBTTagCompound nbt) {
		 	super.deserializeNBT(nbt);
		 	reloadCache() ;			
		}
		
		public void addIInventoryChangedListener(IElvenAvatarItemHadlerChangedListener listener) {
			if (listener!=null)
				listIInventoryChangedListener.add(listener);
		}
		
		@Override
		protected int getStackLimit(int slot, ItemStack stack) {
			return 1;
		}

		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {		
			if (isItemValid(slot,stack))
				return super.insertItem(slot, stack, simulate);
			else return stack;
		}

		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			return super.extractItem(slot, amount, simulate);
		}

		@Override
		public void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			markDirty();
			if (slot==1) cacheType1= ItemStackType.getTypeTool(super.getStackInSlot(1));
			if (slot==0) cacheType0= ItemStackType.getTypeTool(super.getStackInSlot(0));

			for(int i=0;i<listIInventoryChangedListener.size();i++) {				
				listIInventoryChangedListener.get(i).onItemStackHandlerChanged(this,slot);
			}
						
		}
		
		@Override
		public ItemStack getStackInSlot(int slot) {
			return super.getStackInSlot(slot);
		}
				
		@Override
		public void setStackInSlot(int slot, ItemStack stack) {
			super.setStackInSlot(slot, stack);
		}
		
		public boolean isItemValid0() {
			return isItemValid(0, getStackInSlot(0));
		}
		
		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			
			if (lastTryToInsert[slot]!=null && stack.isItemEqual(lastTryToInsert[slot])) return false;
			boolean valid_super=super.isItemValid(slot, stack);
			boolean valid=false;
			if (valid_super) {
				ItemStackType.Types type=ItemStackType.getTypeTool(stack);
				if (slot==0 && type==ItemStackType.Types.NONE) valid=false;
				else
					if (slot==0 && type==ItemStackType.Types.BLOCK) valid=false;
					else
						if (slot==0 && type==ItemStackType.Types.ROD_WORK) valid=false;
						else
							if (slot==0) valid=true;
							else
								if (slot==1) valid=(type==ItemStackType.Types.ROD_WORK && cacheType0==ItemStackType.Types.BREAK);					
			}
			if (!valid) {
				lastTryToInsert[slot]=stack.copy();
			}else lastTryToInsert[slot]=null;
			return valid;
		}
		
		
		public void set0(ItemStack stack) {
			super.setStackInSlot(0, stack);
		}
		
		public void set1(ItemStack stack) {
			super.setStackInSlot(1, stack);
		}
		
		public void empty0() {
			setStackInSlot(0, ItemStack.EMPTY);
		}
		
		public void empty1() {
			setStackInSlot(1, ItemStack.EMPTY);
		}
		
		public ItemStack get0() {
			return getStackInSlot(0);
		}
		
		public ItemStack get1() {
			return getStackInSlot(1);
		}
		
		
		public ItemStack take0() {
			ItemStack t=getStackInSlot(0);
			setStackInSlot(0, ItemStack.EMPTY);
			return t;			
		}
		
		public ItemStack take1() {
			ItemStack t=getStackInSlot(1);
			setStackInSlot(1, ItemStack.EMPTY);
			return t;			
		}
		
		public ItemStackType.Types getType0() {
			return cacheType0;
		}
		
		public ItemStackType.Types getType1() {
			return cacheType1;
		}
		
		public boolean haveItem() {
			ItemStack stack=getStackInSlot(0);
			return(stack!=null && stack!=ItemStack.EMPTY);
		}
	

    }
			
	@Override
	public boolean isFull() {
		return mana >= MAX_MANA;
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		return getInventory().haveItem();
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}



	@Override
	public EnumFacing getAvatarFacing() {
		return world.getBlockState(getPos()).getValue(BotaniaStateProps.CARDINALS);
	}
	
	
	@Override
	public BlockPos getPos() {
		return super.getPos();
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
		
		super.readPacketNBT(par1nbtTagCompound.getCompoundTag("inventory"));//read inventory
		enabled = par1nbtTagCompound.getBoolean(TAG_ENABLED);
		ticksElapsed = par1nbtTagCompound.getInteger(TAG_TICKS_ELAPSED);
		mana = par1nbtTagCompound.getInteger(TAG_MANA);		
		this.fakePlayerHelper.readPacketNBT(par1nbtTagCompound);
	}
	
	@Override
	public void writePacketNBT(NBTTagCompound par1nbtTagCompound) {
		par1nbtTagCompound.setTag("inventory", this.getInventory().serializeNBT());
//		super.writePacketNBT(par1nbtTagCompound);//saves inventory
		par1nbtTagCompound.setBoolean(TAG_ENABLED, enabled);
		par1nbtTagCompound.setInteger(TAG_TICKS_ELAPSED, ticksElapsed);
		par1nbtTagCompound.setInteger(TAG_MANA, mana);
		this.fakePlayerHelper.writePacketNBT(par1nbtTagCompound);
	}
	
	public void setmana(int pmana) {
		//to set from server message
		mana=pmana;
	}

	@Override
	public void onItemStackHandlerChanged(ElvenAvatarItemHadler inventory,int slot) {
				
		if (getWorld().isRemote) return;		
		
		if (slot==0) {
			inventoryToFakePlayer(); //from player to avatar
			if (inventory.haveItem()) {
				//secuencesAvatar.ActivateSecuence("RiseArm");
				if(!this.secuencesAvatar.isActive())
					new MessageMoveArm (getPos(),MessageMoveArm.RISE_ARM);
				
				boolean isValid= inventory.isItemValid0();
				if (!isValid) this.fakePlayerHelper.dropItem(inventory.take0());
				
			}else {
								
				if (!resetBreak()) {
					new MessageMoveArm (getPos(),MessageMoveArm.DOWN_ARM);
				}
			}	
		}
		
		if (!getWorld().isRemote) {
			markDirty();
			IBlockState state=getWorld().getBlockState(pos);
			getWorld().notifyBlockUpdate(pos, state, state, 2);
		}
	}


	
}
