package botaunomy;

import botaunomy.config.Config;
import botaunomy.item.RodItem;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ItemStackType {

    
	public static enum Types {
		NONE,
    	BREAK,
    	SHEAR,
    	USE,
    	KILL,  
    	ROD_WILL,
    	ROD_WORK,
    	BLOCK
      }
    
	public ItemStackType() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static Types getTypeTool(ItemStack i) {

		String s=i.getUnlocalizedName().toLowerCase();
		
		if(i.isEmpty()) return Types.NONE;
		
		if (i.getItem() instanceof RodItem && s.contains("rod_will")) return Types.ROD_WILL;
		if (i.getItem() instanceof RodItem && s.contains("rod_work")) return Types.ROD_WORK;
		 
		for(int a = 0;a<Config.breakingToolsList.length; a++) {
			if (s.toLowerCase().contains(Config.breakingToolsList[a])) return Types.BREAK;
		}
		for(int a = 0;a<Config.entitiesShearsList.length; a++) {
			if (s.toLowerCase().contains(Config.entitiesShearsList[a])) return Types.SHEAR;
		}	
		for(int a = 0;a<Config.entitiesToolsList.length; a++) {
			if (s.toLowerCase().contains(Config.entitiesToolsList[a])) return Types.USE;
		}
		for(int a = 0;a<Config.entitiesAtacksList.length; a++) {
			if (s.toLowerCase().contains(Config.entitiesAtacksList[a])) return Types.KILL;
		}	
		if (Block.getBlockFromItem(i.getItem()) != Blocks.AIR) return Types.BLOCK;
		
		return Types.NONE;
	}

}
