package botaunomy.network;

import botaunomy.block.ElvenAvatarBlock;
import botaunomy.block.tile.TileElvenAvatar;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageInventoryEmpty implements IMessage  {
	
	public MessageInventoryEmpty() {
	
	}
	
	private BlockPos blockPos;
	
    @Override
    public void toBytes(ByteBuf buf) {
        // Encoding the position as a long is more efficient
        buf.writeLong(blockPos.toLong());
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // Encoding the position as a long is more efficient
        blockPos = BlockPos.fromLong(buf.readLong());
    }
    
	public MessageInventoryEmpty(BlockPos pblockpos) {
		blockPos=pblockpos;
		ModSimpleNetworkChannel.INSTANCE.sendToAll(this);
	}
	public static class MessageInventoryEmptyHandler implements IMessageHandler<MessageInventoryEmpty, IMessage> {
	    
		public MessageInventoryEmptyHandler() {
			
		}
		
		@Override
	    public IMessage onMessage(MessageInventoryEmpty message, MessageContext ctx) {

			World world = Minecraft.getMinecraft().world;
			IBlockState ibs = world.getBlockState(message.blockPos);
			Block block = ibs.getBlock();
			if (block instanceof ElvenAvatarBlock)
			 if (world.isBlockLoaded(message.blockPos)) {
				 TileElvenAvatar avatar = (TileElvenAvatar) world.getTileEntity(message.blockPos);
				 avatar.getItemHandler().setStackInSlot(0, ItemStack.EMPTY);
			 }		
	        return null;
	    }
	}
	
}
