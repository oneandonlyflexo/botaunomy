package botaunomy.block.tile;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import com.google.common.collect.Multimap;
import com.mojang.authlib.GameProfile;

import botaunomy.Botaunomy;
import botaunomy.ItemStackType;
import botaunomy.ModInfo;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.botania.api.mana.IManaItem;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

@Mod.EventBusSubscriber
public class ElvenFakePlayerHandler  {

	 private WeakReference<ElvenFakePlayer> refMyFakePlayer =new WeakReference<> (null);
	 private ArrayList<ItemStackType.Types> typePlayerToolCache;
	 TileElvenAvatar avatarParent;

	 
	public ElvenFakePlayerHandler (World ws, UUID uname, BlockPos pos,TileElvenAvatar avatar ) {
		//GameProfile profile = new GameProfile(uname, uname.toString());
		init( ws,  uname,  pos, avatar );		
		avatarParent=avatar;
	}

	public WeakReference<FakePlayer>  getRefAndRetryInit (World ws, UUID uname, BlockPos pos,TileElvenAvatar avatar )  {
		if(ws.isRemote) return null;
		
		if (refMyFakePlayer.get()==null) init(ws, uname, pos, avatar);
		
		return new  WeakReference<FakePlayer> ((FakePlayer)(refMyFakePlayer.get())) ;
	}
	
	private class ElvenFakePlayer extends FakePlayer{

		public ElvenFakePlayer(WorldServer world, GameProfile name) {
			super(world, name);
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@Override
	    public float getCooledAttackStrength(float adjustTicks)
	    {
			//System.out.println("Ticks; "+this.ticksSinceLastSwing+" getCooldownPeriod: "+this.getCooldownPeriod()); 
			return super.getCooledAttackStrength(adjustTicks);
		
	        //return MathHelper.clamp(((float)this.ticksSinceLastSwing + adjustTicks) / this.getCooldownPeriod(), 0.0F, 1.0F);
	    }
			
		@Override
	    public void onUpdate()
	    {
	     	super.onUpdate();
	     	++this.ticksSinceLastSwing;
	    }
		
		@SubscribeEvent
		public void onCriticalHit(CriticalHitEvent evt) {
			
		  if (!evt.getEntityPlayer().world.isRemote) {
		    //if (evt.getEntityPlayer().equals(this.refMyFakePlayer.get())){
			  if (evt.getEntityPlayer().equals(this)){
		    	ItemStack itemStack=evt.getEntityPlayer().getHeldItemMainhand() ;
		    	//if (itemStack.getItem() instanceof ItemSword) {		
		    		Multimap<String, AttributeModifier> attr=itemStack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
		    		Collection<AttributeModifier> coldmg=attr.get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());
		    		float dmg=0;
		    		for(AttributeModifier i : coldmg) {dmg= (float) i.getAmount();}
		    		System.out.println("DMG: "+dmg); 
			    	evt.setResult( Event.Result.ALLOW );
			    	evt.setDamageModifier(dmg*1.2F);
		    	//}
		    }
		  }
		}		
	}
	
	private class FakeTable extends Item implements  IManaItem{

		@Override
		public int getMana(ItemStack stack) {
			if (avatarParent!=null)
				return avatarParent.getCurrentMana();
			return 0;
		}

		@Override
		public int getMaxMana(ItemStack stack) {
			return TileElvenAvatar.MAX_MANA;
		}

		@Override
		public void addMana(ItemStack stack, int mana) {
			if (avatarParent!=null)
				avatarParent.recieveMana(mana);			
		}

		@Override
		public boolean canReceiveManaFromPool(ItemStack stack, TileEntity pool) {
			return false;
		}

		@Override
		public boolean canReceiveManaFromItem(ItemStack stack, ItemStack otherStack) {
			return false;
		}

		@Override
		public boolean canExportManaToPool(ItemStack stack, TileEntity pool) {
			return false;
		}

		@Override
		public boolean canExportManaToItem(ItemStack stack, ItemStack otherStack) {
			return true;
		}

		@Override
		public boolean isNoExport(ItemStack stack) {
			return false;
		}
		
	}
	
	private void init(World ws, UUID uname, BlockPos pos,TileElvenAvatar avatar )  {
		try {
			WorldServer wss;
			if (ws instanceof WorldServer) {
				wss=(WorldServer) ws;
			}else return;
			
			ElvenFakePlayer player=new ElvenFakePlayer(wss,  new GameProfile(uname, ModInfo.modid));
			refMyFakePlayer = new WeakReference<>(player);
			
			//fakePlayer = new WeakReference<FakePlayer>(FakePlayerFactory.get(ws, profile));
			//fakePlayer = new WeakReference<>(FakePlayerUtils.get(ws, new GameProfile(uname, ModInfo.modid)));
			
			if (refMyFakePlayer.get() == null)	{
				refMyFakePlayer= null;
				return;
			}
			FakePlayer myFakePlayer=refMyFakePlayer.get();
			
			myFakePlayer.onGround = true;
			myFakePlayer.connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(), new NetworkManager(EnumPacketDirection.SERVERBOUND), refMyFakePlayer.get()) {
				@SuppressWarnings("rawtypes")
				@Override
				public void sendPacket(Packet packetIn) {}
			};			
			myFakePlayer.setSilent(true);
		
			myFakePlayer.posX=pos.getX()+0.5F;
			myFakePlayer.posY=pos.getY()-0.5F;
			myFakePlayer.posZ=pos.getZ()+0.5F;		
			
	
			myFakePlayer.rotationYaw = getYaw(avatar.getAvatarFacing());						
			myFakePlayer.rotationPitch=0;
			myFakePlayer.setSneaking(false);
			
			this.inventoryToFakePlayer(avatar);
			
			ItemStack fakeTablet=new ItemStack(new FakeTable());
			player.inventory.mainInventory.set(35, fakeTablet);

		}
		catch (Exception e) {
			Botaunomy.logger.error("Exception thrown trying to create fake player : " + e.getMessage());
			refMyFakePlayer= null;
		}
		
	}
	
	public static float getYaw(EnumFacing currentFacing) {
		switch (currentFacing) {
		case DOWN:
		case UP:
		case SOUTH:
		default:
			return 0;
		case WEST:
			return 90f;
		case NORTH:
			return 180f;
		case EAST:
			return 270f;
		}
	}
	
	public   ArrayList<ItemStackType.Types>  stackMainHandType(){
		return typePlayerToolCache;
	} 
	
	public ItemStack stackMainHand() {
		if(refMyFakePlayer.get()!=null)
			return refMyFakePlayer.get().getHeldItemMainhand();
		else 
			return ItemStack.EMPTY;
	}
	
	public void fakePlayerToInventory(TileElvenAvatar avatar ) {
		 FakePlayer player=refMyFakePlayer.get();
		 if (player==null) return;
		 ItemStack stack=player.getHeldItem(EnumHand.MAIN_HAND);
		 if (stack==null || stack== ItemStack.EMPTY) return;
		 
		 avatar.getInventory().set0(stack);
		 typePlayerToolCache=avatar.getInventory().getType0();	
		 
	}	
	
	
	public void inventoryToFakePlayer(TileElvenAvatar avatar ) {
		 FakePlayer player=refMyFakePlayer.get();
		 if (player!=null) {
			 
				ItemStack inventoryItem = avatar.getInventory().get0();
				
				if (inventoryItem!=null && !(inventoryItem.isEmpty())) {
					
					//player.inventory.mainInventory.set(player.inventory.currentItem, inventoryItem);
					//player.getAttributeMap().applyAttributeModifiers(inventoryItem.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
					player.setHeldItem(EnumHand.MAIN_HAND, inventoryItem);
					typePlayerToolCache=avatar.getInventory().getType0();

				}else
				{
					//player.inventory.mainInventory.clear();
					//player.inventory.mainInventory				
					player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
					typePlayerToolCache=new ArrayList<ItemStackType.Types>();
					typePlayerToolCache.add(ItemStackType.Types.NONE) ;
														
				}
		 }
	}	
	
	
}
