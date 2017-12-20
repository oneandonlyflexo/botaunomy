package one.flexo.botaunomy.nibbler;

import one.flexo.botaunomy.ModInfo;
import one.flexo.botaunomy.ModRegistry;
import one.flexo.nibbler.item.NibblerItem;
import one.flexo.nibbler.registry.NibblerRegisteredObject;
import one.flexo.nibbler.registry.NibblerRegistry;

public abstract class ItemBase extends NibblerItem implements NibblerRegisteredObject {

	public ItemBase(String name) {
		super(ModInfo.modid, name, ModRegistry.tab);
	}

	@Override
	public NibblerRegistry getRegistry() {
		return ModRegistry.instance;
	}

}
