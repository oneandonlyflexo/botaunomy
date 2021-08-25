package botaunomy.block.tile;

import java.lang.ref.WeakReference;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import botaunomy.Botaunomy;
import botaunomy.ItemStackType;
import botaunomy.ModInfo;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ElvenFakePlayer  {

	 private WeakReference<FakePlayer> refMyFakePlayer =new WeakReference<> (null);
	 private ItemStackType.Types typePlayerToolCache;
		
	public WeakReference<FakePlayer>  getRefAndRetryInit (World ws, UUID uname, BlockPos pos,TileElvenAvatar avatar )  {
		if(ws.isRemote) return null;
		
		if (refMyFakePlayer.get()==null) init(ws, uname, pos, avatar);
		
		return refMyFakePlayer;
	}
	

	public ElvenFakePlayer (World ws, UUID uname, BlockPos pos,TileElvenAvatar avatar ) {
		//GameProfile profile = new GameProfile(uname, uname.toString());
		init( ws,  uname,  pos, avatar );
	}
	
	
	private void init(World ws, UUID uname, BlockPos pos,TileElvenAvatar avatar )  {
		try {
			WorldServer wss;
			if (ws instanceof WorldServer) {
				wss=(WorldServer) ws;
			}else return;
			
			FakePlayer player=new FakePlayer(wss,  new GameProfile(uname, ModInfo.modid));
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
			
			//avatar facing opposite the other side
			myFakePlayer.rotationYaw = getYaw(avatar.getAvatarFacing().getOpposite());						
			myFakePlayer.rotationPitch=0;
			myFakePlayer.setSneaking(false);
			
			this.inventoryToFakePlayer(avatar);

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
	
	public   ItemStackType.Types stackMainHandType(){
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
					player.inventory.mainInventory.set(player.inventory.currentItem, inventoryItem);
					player.getAttributeMap().applyAttributeModifiers(inventoryItem.getAttributeModifiers(EntityEquipmentSlot.MAINHAND));
					//typePlayerToolCache=ItemStackType.getTypeTool(inventoryItem);
					typePlayerToolCache=avatar.getInventory().getType0();
				}else
				{
					player.inventory.mainInventory.clear();
					player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
					typePlayerToolCache=ItemStackType.Types.NONE;
				}
		 }
	}	
	
	
}
