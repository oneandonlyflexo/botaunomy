package botaunomy.registry;

import net.minecraft.util.ResourceLocation;

public class UtilResourceLocation {

	public static  String getUnlocalizedName(ResourceLocation r) {
		return r.getResourceDomain() + "." + r.getResourcePath();
	}
	
	public static  String getItemBlockName(ResourceLocation r) {
		return r.getResourceDomain() + ":" + r.getResourcePath();
	}	
}
