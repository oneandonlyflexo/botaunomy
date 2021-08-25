package botaunomy.client.render;

import java.util.HashMap;

public class SecuencesAvatar {

	//only can be one secuence active
	
	private static  HashMap<Integer,Secuence> secuences=new HashMap<Integer,Secuence>();
	private String SecuenceActiveSecuenceName="";
	
	
	public static void init() {
		Secuence riseArm=addSecuence("RiseArm", 50);
		
		riseArm.addRange("Arm", 0, -3.1416F*(3F/4F));
		riseArm.addRange("tool", -70F+90F, -160F+90F);
		//riseArm.addRange("toolCorrection", 0F , (float)Math.PI+0.15F);
		//riseArm.addRange("toolOffset", 2.1F , 0.9F);
		riseArm.addRange("toolOffset", 2.1F , 0.9F-.35F);
		riseArm.addRange("toolCorrection", 0F , (float)Math.PI+0.5F);
		
		Secuence downArm=addSecuence("DownArm", 50);
		downArm.addRange("Arm",  -3.1416F*(3F/4F),0);
		
		SwinSecuence swingArm=new SwinSecuence ("swingArm", 25,riseArm );
		addSecuence(swingArm);
			
	}

	private static  Secuence addSecuence(String s,float pduration) {
		Secuence secuence=new Secuence (s, pduration);
		return addSecuence(secuence);
	}
	
	private static  Secuence addSecuence(Secuence secuence) {
		secuences.put(secuence.name.hashCode(), secuence);
		return secuence;
	}
	
	
	
	public boolean isElemenActiveSecuence(String elementName) {		
		if (!SecuenceActiveSecuenceName.equals("")) {
			Secuence secuence=secuences.get(SecuenceActiveSecuenceName.hashCode());	
			return(secuence.isElementInSequence(elementName));
		}else return false;
	}
	
	public void ActivateSecuence(String secuenceName) {		
		if (SecuenceActiveSecuenceName.equals(secuenceName) )return; //only reset if distinct
		SecuenceActiveSecuenceName=secuenceName;
		Secuence secuence=secuences.get(SecuenceActiveSecuenceName.hashCode());
		secuence.reset(); 		
	}
	
	public void update(float pticks) {
		if (!SecuenceActiveSecuenceName.equals("")) {;
			Secuence secuence=secuences.get(SecuenceActiveSecuenceName.hashCode());
			secuence.update(pticks);
		}
	}
	
	public float getValue(String elementName) {		
		if (!SecuenceActiveSecuenceName.equals("")) {
			Secuence secuence=secuences.get(SecuenceActiveSecuenceName.hashCode());
			return(secuence.getValue(elementName));
		}else return 0F;
	}
	
	public boolean elementExists(String elementName) {
		if (!SecuenceActiveSecuenceName.equals("")) {
			Secuence secuence=secuences.get(SecuenceActiveSecuenceName.hashCode());
			return(secuence.rangeExists(elementName));
		}else return false;
	}
	
	public float getEndValue(String SecuenceName,String elementName) {				
		Secuence secuence=secuences.get(SecuenceName.hashCode());
		return(secuence.getEndValue(elementName));

	}
	
	public float getInitValue(String SecuenceName,String elementName) {				
		Secuence secuence=secuences.get(SecuenceName.hashCode());
		return(secuence.getInitValue(elementName));

	}
	
	
	public boolean isActive() {
		if (SecuenceActiveSecuenceName.equals(""))return false;		
		Secuence secuence=secuences.get(SecuenceActiveSecuenceName.hashCode());	
		return !secuence.isFinished;
	}
}
