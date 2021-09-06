package botaunomy.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModSimpleNetworkChannel {

	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("botaunomy");
	
	private static int packetId = 0;
    public static int nextID() {
        return packetId++;
    }
	 
	public ModSimpleNetworkChannel() {
		// TODO Auto-generated constructor stub
	}
	
    public static void registerMessages() {
        INSTANCE.registerMessage(MessageMana.MessageManaHandler.class, MessageMana.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(MessageInventoryEmpty.MessageInventoryEmptyHandler.class, MessageInventoryEmpty.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(MessageMoveArm.MessageMoveArmHandler.class, MessageMoveArm.class, nextID(), Side.CLIENT);
        INSTANCE.registerMessage(MessageEnabled.MessageEnabledHandler.class, MessageEnabled.class, nextID(), Side.CLIENT);
    }
}
