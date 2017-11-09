package one.flexo.botaunomy.item.base;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import one.flexo.botaunomy.ModInfo;
import vazkii.botania.common.core.BotaniaCreativeTab;

public abstract class ItemBase extends Item {

	public ItemBase(String name) {
		setCreativeTab(BotaniaCreativeTab.INSTANCE);
		setRegistryName(new ResourceLocation(ModInfo.modid, name));
		setUnlocalizedName(name);
	}
}
