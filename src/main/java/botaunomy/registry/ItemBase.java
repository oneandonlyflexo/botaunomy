package botaunomy.registry;

import java.util.List;

import javax.annotation.Nullable;

import botaunomy.ModInfo;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemBase extends Item  {

	public ResourceLocation resourceLocation;
	private  String tooltip=null;
	
	public ItemBase(String name,String ptooltip) {
		super();
		if(ModRegistry.tab != null) {
			setCreativeTab(ModRegistry.tab);
		}
		resourceLocation= new ResourceLocation(ModInfo.modid,name);
		ModRegistry.instance.addItemForRegistry(this);		
		
		if (ptooltip!=null && !ptooltip.isEmpty())
			tooltip=ptooltip;
		
	}

	public Registry getRegistry() {
		return ModRegistry.instance;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(resourceLocation, "inventory"));
	}
	
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> ptooltip, ITooltipFlag flagIn)
    {
		if (tooltip!=null)
			ptooltip.add(tooltip);
    }


}
