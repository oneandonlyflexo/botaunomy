package botaunomy.block.tile;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import botaunomy.Botaunomy;
import botaunomy.ModInfo;
import botaunomy.network.MessageInventoryEmpty;
import botaunomy.network.MessageMana;
import botaunomy.network.MessageSwingArm;
import botaunomy.nibbler.NibblerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TitleElvenAvatar_FakePlayerHelper {

	TileElvenAvatar avatar;
	protected WeakReference<FakePlayer> avatarPlayer;
	public final UUID uuid;
	
	
	private static final int ROD_MANA_COST = 200;
	private static final int BREAK_MANA_COST=200;
	
	private BlockPos posBlockToBreak;	
	private boolean isBreaking=false;
	private boolean isRodRighClick=false;
	private boolean blockRighClick=false;
	private float curBlockDamageMP;

	
	
	public boolean isBusy() {
		boolean isBusy;
		isBusy=isBreaking||isRodRighClick||blockRighClick;
		return (isBusy);
	}
	
	public TitleElvenAvatar_FakePlayerHelper(TileElvenAvatar pavatar,UUID puuid) {
		avatar=pavatar;
		uuid=puuid;
	}
	
	public void resetBreak() {       
        isBreaking = false;
        curBlockDamageMP = 0.0f;
        stopBreaking();
    }
	
	private World getWorld() {
		return avatar.getWorld(); 
	}
	
	private BlockPos getPos() {
		return avatar.Position();
	}
	
	
	public void updateHelper() {
	
	WeakReference<FakePlayer> player=getAvatarPlayer();
		
		if (player!=null && posBlockToBreak!=null ) {
	        boolean detect =  !getWorld().isAirBlock(posBlockToBreak) || detectEntity().size()>0;
	        if (!detect && isBreaking)
	            stopBreaking();
	        if (detect && isBreaking && !getWorld().isRemote) {
	            Objects.requireNonNull(player.get()).interactionManager.updateBlockRemoving();
	            continueBreaking();
	        }
		}
	}
	
	
    private void onPlayerDestroyBlock( FakePlayer player) {
    	
    	BlockPos targetPos=posBlockToBreak;
        IBlockState stateBlockToBreak = getWorld().getBlockState(targetPos);
        Block blockToBreak = stateBlockToBreak.getBlock();
        
        ItemStack stackMainHand = player.getHeldItemMainhand();
        if (!stackMainHand.isEmpty() && stackMainHand.getItem().onBlockStartBreak(stackMainHand, targetPos, player)) {
            return;
        }

        if ((blockToBreak instanceof BlockCommandBlock) && !player.canUseCommandBlock()) {
            return;
        } else if (stateBlockToBreak.getMaterial() == Material.AIR) {
            return;
        } else {
        	
        	//blockBreakParticles
        	getWorld().playEvent(2001, targetPos, Block.getStateId(stateBlockToBreak));
            
            if (!stackMainHand.isEmpty()) {
            	
            	if (blockToBreak.canHarvestBlock(getWorld(), targetPos, player)) {
            		blockToBreak.harvestBlock(getWorld(), player, targetPos, stateBlockToBreak, null, stackMainHand); //itemblock drop
            		int fortune=EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stackMainHand) ;
            		blockToBreak.dropXpOnBlockBreak(getWorld(), targetPos,blockToBreak.getExpDrop(stateBlockToBreak, getWorld(), targetPos, fortune));
            	}
            	stackMainHand.onBlockDestroyed(getWorld(), stateBlockToBreak, targetPos, player);//set use
                boolean flag = blockToBreak.removedByPlayer(stateBlockToBreak, getWorld(), targetPos, player, false);
                if (flag) {                	
                    blockToBreak.onBlockDestroyedByPlayer(getWorld(), targetPos, stateBlockToBreak);                            
                }
                
               // if (player.experience>0)                
                  //player.sendStatusMessage(new TextComponentString(TextFormatting.GREEN + "XP->" +player.experience), false);	
            	            	
                if (stackMainHand.isEmpty()) {
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, stackMainHand, EnumHand.MAIN_HAND);                    
                    avatar.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
                    inventoryToFakePlayer(player);
    				if(getWorld() instanceof WorldServer) {
    					new MessageInventoryEmpty(getPos());
    				}
                    
                }else {
                	avatar.getItemHandler().setStackInSlot(0,stackMainHand.copy());		
                }
            }	
        }
    }
   
    private void stopBreaking() {
        this.isBreaking = false;
        WeakReference<FakePlayer> player=getAvatarPlayer();
        if (!getWorld().isRemote && player!=null && posBlockToBreak!=null) {        	
            IBlockState iblockstate = getWorld().getBlockState(posBlockToBreak);
            if (iblockstate.getMaterial() != Material.AIR) {
            	getWorld().sendBlockBreakProgress(Objects.requireNonNull(player.get()).getEntityId(), this.posBlockToBreak, -1);
            }              
			if(this.getWorld() instanceof WorldServer) {
				
				ItemStack stackMainHand = avatar.getItemHandler().getStackInSlot(0);	
				this.inventoryToFakePlayer();
				if (!stackMainHand.isEmpty())
					new MessageSwingArm (getPos(),MessageSwingArm.RISE_ARM);
				else
					new MessageSwingArm (getPos(),MessageSwingArm.DOWN_ARM);
			}
        }
    }
    
    private void continueBreaking() {
        BlockPos targetPos = posBlockToBreak;
        WeakReference<FakePlayer> player=getAvatarPlayer();
    
            if (this.isBreaking &&  !player.get().getHeldItemMainhand().isEmpty()) {
                IBlockState iblockstate = getWorld().getBlockState(targetPos);

                if (iblockstate.getMaterial() == Material.AIR) {
                    this.isBreaking = false;
                }
                this.curBlockDamageMP += iblockstate.getPlayerRelativeBlockHardness(player.get(), getWorld(), targetPos);
                if (this.curBlockDamageMP >= 1.0F) {
                    this.isBreaking = false;
                    player.get().interactionManager.blockRemoving(this.posBlockToBreak);
                    this.onPlayerDestroyBlock((FakePlayer) player.get());
                    stopBreaking();
                    this.curBlockDamageMP = 0.0F;
                }

                getWorld().sendBlockBreakProgress(player.get().getEntityId(), this.posBlockToBreak, (int) (this.curBlockDamageMP * 10.0F) - 1);
            }
            
            if(player.get().getHeldItemMainhand().isEmpty())
            		resetBreak();
    }
    
	private BlockPos getTargetPos() {
		try {
			BlockPos targetPos = getPos().offset(avatar.getAvatarFacing());			
			return targetPos;
		}
		catch (Exception e) {
			//TODO: Print error
			return null;
		}
		
	}
	
	
	
	public void rightClickBlockWhithItem(ItemStack stack) {
		if (!avatar.isEnabled()) {
			return;
		}

		if (isBusy()) return;
		
		//getElapsedFunctionalTicks
		if(avatar.getCurrentMana() >= ROD_MANA_COST && avatar.isAvatarTick()) {
	
			boolean interactedWithBlock = false;
			WeakReference<FakePlayer> avatarPlayer = getAvatarPlayer();
			if (avatarPlayer == null) {
				return;
			}			
			
			BlockPos targetPos=getTargetPos();
			if (targetPos!=null) {
				blockRighClick=true;
				
				
				interactedWithBlock = interactBlockWithItem(avatar, avatarPlayer, targetPos);
			
				if(interactedWithBlock) {
					avatar.recieveMana(-ROD_MANA_COST);
					if(avatar.getWorld() instanceof WorldServer) {
						new MessageSwingArm (getPos(),MessageSwingArm.RISE_ARM);
						new MessageMana(avatar.Position(),avatar.getCurrentMana());
					}
				}			
				blockRighClick=false;
			}
		}	
	}
	
	
	
	
	public void beginBreak( ItemStack stack) {
		if (!avatar.isEnabled()) {
			return;
		}
		
		if (isBusy()) return;
		

		if(avatar.getCurrentMana() >= BREAK_MANA_COST && avatar.isAvatarTick()) {
			boolean interactedWithBlock = false;
			boolean interactedWithEntities = false;

			//TileEntity tile = this.asTileEntity();
			WeakReference<FakePlayer> avatarPlayer = this.getAvatarPlayer();
			if (avatarPlayer == null) {
				return;
			}
			//boolean rightClick = useRightClick();
			try {
				BlockPos targetPos =  getTargetPos();
				interactedWithBlock = breakBlock( avatarPlayer, targetPos);
			}
			catch (Exception e) {
				//TODO: Print error
			}
			if(interactedWithBlock || interactedWithEntities) {
				avatar.recieveMana(-BREAK_MANA_COST);				
				if(this.getWorld() instanceof WorldServer) {
					new MessageMana(getPos(),avatar.getCurrentMana());
					new MessageSwingArm (getPos(),MessageSwingArm.SWING_ARM);
				}
			}
		}
	}
	

	private boolean breakBlock(WeakReference<FakePlayer> player, BlockPos targetPos) {
		
		 if(Block.getBlockFromItem(player.get().getHeldItemMainhand().getItem()) == Blocks.AIR) {
		 //This ain't no block placer! , if holding a block dont use.
		
	        if (!this.isBreaking) {
	
	                IBlockState iblockstate = getWorld().getBlockState(targetPos);
	                boolean blockIsAir = iblockstate.getMaterial() == Material.AIR;
	                
	                //if (flag && iblockstate.getPlayerRelativeBlockHardness(player.get(), world, targetPos) >= 1.0F) {
	                //    this.onPlayerDestroyBlock(targetPos, (FakePlayer) player.get());
	                //} else {
	                
	                if (!blockIsAir) {
	                    this.isBreaking = true;
	                    this.posBlockToBreak = targetPos;
	                    //this.currentItemHittingBlock = player.get().getHeldItemMainhand();
	                    this.curBlockDamageMP = 0.0F;
	                    getWorld().sendBlockBreakProgress(player.get().getEntityId(), this.posBlockToBreak, (int) (this.curBlockDamageMP * 10.0F) - 1);
	                    return true;
	                }
	        }
		}
        return false;
    }  

	
    private List<Entity> detectEntity() {
    	//TODO
        BlockPos newPos = this.getPos().offset(avatar.getAvatarFacing());
        AxisAlignedBB bounding= new AxisAlignedBB(newPos);
        List<Entity> detectedEntities = new ArrayList<>();
        detectedEntities = getWorld().getEntitiesWithinAABB(Entity.class,bounding);
        return detectedEntities;
    }
	
	/*
	 //Detect entitys
   Vec3d base;
   Vec3d target;
   Vec3d look;
   base = new Vec3d(Objects.requireNonNull(player.get()).posX, Objects.requireNonNull(player.get()).posY, Objects.requireNonNull(player.get()).posZ);
	 RayTraceResult toUse;
	 RayTraceResult traceEntity;
	 RayTraceResult trace;
	 look = Objects.requireNonNull(player.get()).getLookVec();
	 target = base.add(new Vec3d(look.x * 5, look.y * 5, look.z * 5));
	 traceEntity = FakePlayerUtils.traceEntities((FakePlayer) player.get(), base, target, world);
	 trace = world.rayTraceBlocks(base, target, false, false, true);
    toUse = trace == null ? traceEntity : trace;

	
   //ItemStack itm = FakePlayerUtils.leftClickInDirection((FakePlayer) player.get(), this.world, this.pos, EnumFacing.UP, world.getBlockState(pos), toUse);
   if(toUse.typeOfHit== RayTraceResult.Type.BLOCK) 
   */	
   
	public WeakReference<FakePlayer> getAvatarPlayer() {	
		getWorld();
		if(!(getWorld() instanceof WorldServer)) return null;		
		//if  world is no instanceof worldserver  avatarPlayer is  null
		if (avatarPlayer == null) {
			avatarPlayer = initFakePlayer((WorldServer) getWorld(), uuid);
			if (avatarPlayer == null) {
				//TODO: Log error
				//TODO: create flag to stop trying to make fake player				
			}else {
				resetBreak();
			}
			return null;
		}	
		return avatarPlayer;
	}
	
	
	public void inventoryToFakePlayer() {		
		 WeakReference<FakePlayer> player =getAvatarPlayer();
		 if (player!=null) inventoryToFakePlayer(player.get());
	}
	
	private void inventoryToFakePlayer(FakePlayer player ) {
		 if (player!=null) {
			 
				ItemStack inventoryItem = avatar.getItemHandler().getStackInSlot(0);
				if (inventoryItem!=null && !(inventoryItem.isEmpty())) {
					player.inventory.mainInventory.set(player.inventory.currentItem, inventoryItem);
					player.getAttributeMap().applyAttributeModifiers(inventoryItem.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
				}else
				{
					player.inventory.mainInventory.clear();
					player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);					
				}
		 }
	}
	
	private  WeakReference<FakePlayer> initFakePlayer(WorldServer ws, UUID uname) {
		//GameProfile profile = new GameProfile(uname, uname.toString());
		WeakReference<FakePlayer> fakePlayer;
		try {
			
			fakePlayer = new WeakReference<>(new FakePlayer(ws,  new GameProfile(uname, ModInfo.modid)));
			//fakePlayer = new WeakReference<FakePlayer>(FakePlayerFactory.get(ws, profile));
			//fakePlayer = new WeakReference<>(FakePlayerUtils.get(ws, new GameProfile(uname, ModInfo.modid)));
			
			if (fakePlayer == null || fakePlayer.get() == null) {
				return null;
			}
			
			fakePlayer.get().onGround = true;
			fakePlayer.get().connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(), new NetworkManager(EnumPacketDirection.SERVERBOUND), fakePlayer.get()) {
				@SuppressWarnings("rawtypes")
				@Override
				public void sendPacket(Packet packetIn) {}
			};			
			fakePlayer.get().setSilent(true);
			
		
			
			fakePlayer.get().posX=getPos().getX()+0.5F;
			fakePlayer.get().posY=getPos().getY()-0.5F;
			fakePlayer.get().posZ=getPos().getZ()+0.5F;		
			
			//avatar see the other side
			fakePlayer.get().rotationYaw = NibblerUtil.Entity.getYaw(avatar.getAvatarFacing().getOpposite());						
			fakePlayer.get().rotationPitch=0;
			fakePlayer.get().setSneaking(false);
			
			inventoryToFakePlayer(fakePlayer.get());
			
			return fakePlayer;
		}
		catch (Exception e) {
			Botaunomy.logger.error("Exception thrown trying to create fake player : " + e.getMessage());
			return null;
		}
	}
	
	public void readPacketNBT(NBTTagCompound par1nbtTagCompound) {
		isBreaking = par1nbtTagCompound.getBoolean("isRemoving");
 	}

	public void writePacketNBT(NBTTagCompound par1nbtTagCompound) {
		par1nbtTagCompound.setBoolean("isRemoving", isBreaking);
	}	

	
	public void rodRightClick(TileElvenAvatar avatar) {
		if (!avatar.isEnabled()) {
			return;
		}

		if (isBusy()) return;
		
		//getElapsedFunctionalTicks
		if(avatar.getCurrentMana() >= ROD_MANA_COST && avatar.isAvatarTick()) {
	
			boolean interactedWithBlock = false;
			boolean interactedWithEntities = false;
			WeakReference<FakePlayer> avatarPlayer = getAvatarPlayer();
			if (avatarPlayer == null) {
				return;
			}			
			BlockPos targetPos=getTargetPos();
			if (targetPos!=null) {
				isRodRighClick=true;
				interactedWithBlock = interactBlock(avatar, avatarPlayer, targetPos);
				//interactedWithEntities = interactEntities(avatar, avatarPlayer, targetPos, useRightClick);
	
				if(interactedWithBlock || interactedWithEntities) {
					avatar.recieveMana(-ROD_MANA_COST);
					if(avatar.getWorld() instanceof WorldServer) {
						new MessageSwingArm (getPos(),MessageSwingArm.RISE_ARM);
						new MessageMana(avatar.Position(),avatar.getCurrentMana());
					}
				}
				isRodRighClick=false;
			}
		}

	}


	private boolean interactBlock( //Always rightclick
			TileElvenAvatar tileElvenAvatar,
			WeakReference<FakePlayer> fakePlayer,
			BlockPos targetPos)
	{
		if(interactFluid(tileElvenAvatar, fakePlayer, targetPos, true)) {
			return true;
		}
		if(Block.getBlockFromItem(fakePlayer.get().getHeldItemMainhand().getItem()) != Blocks.AIR) {
			//This ain't no block placer!
			return false;
		}
		
		
		IBlockState iblockstate = getWorld().getBlockState(targetPos);
		boolean blockIsAir = iblockstate.getMaterial() == Material.AIR;
		if(!blockIsAir) {
						
			World world = tileElvenAvatar.getWorld();
			EnumActionResult r;
			
			r = fakePlayer.get().interactionManager.processRightClickBlock(fakePlayer.get(), world, fakePlayer.get().getHeldItemMainhand(), EnumHand.MAIN_HAND, targetPos, EnumFacing.UP, .5F, .5F, .5F);
			if (r == EnumActionResult.SUCCESS) {
				if(avatar.getWorld() instanceof WorldServer) {
					new MessageSwingArm (getPos(),MessageSwingArm.SWING_ARM);
				}
				return true; //Yay!
			}

		}
		return false;
	}
	
	private boolean interactBlockWithItem( 
			TileElvenAvatar tileElvenAvatar,
			WeakReference<FakePlayer> fakePlayer,
			BlockPos targetPos)
	{
			
		if(Block.getBlockFromItem(fakePlayer.get().getHeldItemMainhand().getItem()) != Blocks.AIR) {
			//This ain't no block placer!
			return false;
		}
		IBlockState iblockstate = getWorld().getBlockState(targetPos);
		boolean blockIsAir = iblockstate.getMaterial() == Material.AIR;
		if(!blockIsAir) {
						
			World world = tileElvenAvatar.getWorld();
			EnumActionResult r;
			r = fakePlayer.get().interactionManager.processRightClickBlock(fakePlayer.get(), world, fakePlayer.get().getHeldItemMainhand(), EnumHand.MAIN_HAND, targetPos, EnumFacing.UP, .5F, .5F, .5F);
			if (r == EnumActionResult.SUCCESS) {
				if(avatar.getWorld() instanceof WorldServer) {
					new MessageSwingArm (getPos(),MessageSwingArm.SWING_ARM);
				}
				return true; //Yay!
			}
		}
		return false;
	}
	

	private boolean interactFluid(
			TileElvenAvatar tileElvenAvatar,
			WeakReference<FakePlayer> avatarPlayer,
			BlockPos targetPos,
			boolean rightClick)
	{
		//TODO: deal with dat fluid
		return false;
	}
	
	
	/* right click item without target
	 

		r = fakePlayer.get().interactionManager.processRightClick(fakePlayer.get(), world, fakePlayer.get().getHeldItemMainhand(), EnumHand.MAIN_HAND);
		if (r == EnumActionResult.SUCCESS) {
				return true; //Yay!
			}		
		ActionResult<ItemStack> res = fakePlayer.get().getHeldItemMainhand().getItem().onItemRightClick(world, fakePlayer.get(), EnumHand.MAIN_HAND);
		if (res != null && res.getType() == EnumActionResult.SUCCESS) {
				return true; //Yay!
		}

	 */
	

	/*
	private boolean interactEntities(
			TileElvenAvatar tileElvenAvatar,
			WeakReference<FakePlayer> avatarPlayer,
			BlockPos targetPos,
			boolean rightClick)
	{
		boolean interacted = true;
		AxisAlignedBB entityRange = new AxisAlignedBB(targetPos).offset(0.5, 0, 0.5);

		World world = tileElvenAvatar.getWorld();
		List<EntityLivingBase> living = world.getEntitiesWithinAABB(EntityLivingBase.class, entityRange);
		List<EntityMinecart> carts = world.getEntitiesWithinAABB(EntityMinecart.class, entityRange);
		List<Entity> all = new ArrayList<Entity>(living);
		all.addAll(carts);//works since  they share a base class but no overlap

		if (rightClick) {
			world.markChunkDirty(targetPos, avatar);
			if(world.isRemote == false) {
				for(Entity entity : all) {//both living and minecarts
					if (entity != null && entity.isDead == false) {
						if (EnumActionResult.FAIL != avatarPlayer.get().interactOn(entity, EnumHand.MAIN_HAND)) {
							dropInventory(world, avatarPlayer, targetPos);
							interacted = true;
							break;
						}
					}
				}
			}
		}
		else {
			ItemStack mainHand = avatarPlayer.get().getHeldItemMainhand();
			avatarPlayer.get().onGround = true;
			for (EntityLivingBase entity : living) {
				interacted = true;
				avatarPlayer.get().attackTargetEntityWithCurrentItem(entity);
				float damage = NibblerUtil.Entity.getAttackDamage(mainHand, entity);
				entity.attackEntityFrom(DamageSource.causePlayerDamage(avatarPlayer.get()), damage);
			}
		}
		return interacted;
	}

	private void dropInventory(World world, WeakReference<FakePlayer> avatarPlayer, BlockPos pos) {
		ItemStack mainHand = avatarPlayer.get().getHeldItemMainhand();
		for (ItemStack s : avatarPlayer.get().inventory.mainInventory) {
			if (!s.isEmpty() && !s.equals(mainHand)) {
				EntityItem entityItem = new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, s.copy());
				if (world.isRemote == false) {
					world.spawnEntity(entityItem);
				}
				s.setCount(0);
			}
		}
	}
*/
	
}
