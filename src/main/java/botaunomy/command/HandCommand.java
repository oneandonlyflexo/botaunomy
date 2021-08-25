package botaunomy.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class HandCommand extends CommandBase {

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {

	// HandCommand.logger.info("execute called");
	  String unlocalicedName = null;
	  String displaydName = null;
	  String registryName=null;
	  
	if (sender instanceof EntityPlayer) {
		ItemStack i=((EntityPlayer)sender).getHeldItemMainhand();
		unlocalicedName=i.getUnlocalizedName();
		displaydName=i.getDisplayName();		
		registryName=i.getItem().getRegistryName().toString();
	}
		
    //if (params != null && params.length > 0) {
      //for (String param : params) {
        String message = "MainHand : " + unlocalicedName +" | " + displaydName +" | " + registryName;
        TextComponentString text = new TextComponentString(message);
        text.getStyle().setColor(TextFormatting.GREEN);
        sender.sendMessage(text);
      //}
    //}
  }

  @Override
  public String getName() {
    return "MainHand";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "command.MainHand.usage";
  }
  
  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender)
  {
      return true;
  }
}