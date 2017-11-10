package one.flexo.botaunomy.item.base;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import one.flexo.botaunomy.ModInfo;
import one.flexo.botaunomy.client.render.IModelRegister;
import one.flexo.botaunomy.util.ModIdType;
import vazkii.botania.common.core.BotaniaCreativeTab;

public abstract class ItemBase extends Item implements IModelRegister {

	public ItemBase(String name) {
		setCreativeTab(BotaniaCreativeTab.INSTANCE);
		setRegistryName(new ResourceLocation(ModInfo.modid, name));
		setUnlocalizedName(ModIdType.DEFAULT.getId(name));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerModels() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
