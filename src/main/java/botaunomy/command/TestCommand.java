package botaunomy.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
public class TestCommand extends CommandBase {
	public static boolean flag=false;

  @Override
  public void execute(MinecraftServer server, ICommandSender sender, String[] params) throws CommandException {

	  	flag=true;
        String message = "Test On";
        TextComponentString text = new TextComponentString(message);
        text.getStyle().setColor(TextFormatting.GREEN);
        sender.sendMessage(text);

  }

  @Override
  public String getName() {
    return "TestCommand";
  }

  @Override
  public String getUsage(ICommandSender sender) {
    return "Set flag to true for test";
  }
  
  @Override
  public boolean checkPermission(MinecraftServer server, ICommandSender sender)
  {
      return true;
  }
}