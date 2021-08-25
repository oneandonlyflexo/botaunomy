package botaunomy.nibbler;

import net.minecraft.util.ResourceLocation;

public class NibbleResourceLocation extends ResourceLocation{


	public NibbleResourceLocation(String resourceDomainIn, String resourcePathIn) {
		super(resourceDomainIn, resourcePathIn);
	}

	public NibbleResourceLocation(String s) {
		super(s);
	}
	public  String getUnlocalizedName() {
		return this.resourceDomain + "." + this.resourcePath;
	}
	
	public String getItemBlockName() {
		return this.getResourceDomain() + ":" + this.getResourcePath();
	}	
}
