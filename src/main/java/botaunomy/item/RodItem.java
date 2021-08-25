package botaunomy.item;



import botaunomy.ModResources;
import botaunomy.nibbler.ItemBase;
import net.minecraft.util.*;


public class RodItem extends ItemBase {

	private static final ResourceLocation avatarOverlay = new ResourceLocation(ModResources.MODEL_AVATAR_WILL_WORK);

	
	public RodItem(String name, String ptooltip) {
		super(name,ptooltip);
	}

	public ResourceLocation getOverlayResource() {
		return avatarOverlay;
	}
		
}
