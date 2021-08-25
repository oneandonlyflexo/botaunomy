package botaunomy.network;

import botaunomy.block.ElvenAvatarBlock;
import botaunomy.block.tile.TileElvenAvatar;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSwingArm implements IMessage  {
	
	public MessageSwingArm() {
	
	}
	
	public static final int SWING_ARM=1;
	public static final int RISE_ARM=2;
	public static final int DOWN_ARM=3;  
	
	private BlockPos blockPos;
	private int nSecuencia;
	
    @Override
    public void toBytes(ByteBuf buf) {
        // Encoding the position as a long is more efficient
        buf.writeLong(blockPos.toLong());
        buf.writeInt(nSecuencia);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // Encoding the position as a long is more efficient
        blockPos = BlockPos.fromLong(buf.readLong());
        nSecuencia=buf.readInt();
    }
    
	public MessageSwingArm(BlockPos pblockpos, int pNSecuencia) {
		blockPos=pblockpos;
		nSecuencia=pNSecuencia;
		ModSimpleNetworkChannel.INSTANCE.sendToAll(this);
	}
	public static class MessageSwingArmHandler implements IMessageHandler<MessageSwingArm, IMessage> {
	    
		public MessageSwingArmHandler() {
			
		}
		
		@Override
	    public IMessage onMessage(MessageSwingArm message, MessageContext ctx) {

			World world = Minecraft.getMinecraft().world;
			IBlockState ibs = world.getBlockState(message.blockPos);
			Block block = ibs.getBlock();
			if (block instanceof ElvenAvatarBlock)
			 if (world.isBlockLoaded(message.blockPos)) {
				 TileElvenAvatar avatar = (TileElvenAvatar) world.getTileEntity(message.blockPos);
				 int nSecuencia=message.nSecuencia;
				 if (nSecuencia==SWING_ARM)
					 avatar.secuencesAvatar.ActivateSecuence("swingArm");	
				 if (nSecuencia==RISE_ARM)
					 avatar.secuencesAvatar.ActivateSecuence("RiseArm");
				 if (nSecuencia==DOWN_ARM)
					 avatar.secuencesAvatar.ActivateSecuence("DownArm");
 
			 }		
	        return null;
	    }
	}
	
}