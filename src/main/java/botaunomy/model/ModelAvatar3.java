// Made with Powershell

package botaunomy.model;

import botaunomy.block.tile.TileElvenAvatar;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelAvatar3 extends ModelBase
{
	
		private ModelRenderer body;
		private ModelRenderer rightarm;
		private ModelRenderer leftarm;
		private ModelRenderer rightleg;
		private ModelRenderer leftleg;
		private ModelRenderer head;
		private ModelRenderer botton;
		
		
		public static final int NPOINTS=33;
		public static final int NARC=3;
		
		private ModelRenderer[][] points= new ModelRenderer[NARC][NPOINTS];
		private float[] rndForPoints= new float[NPOINTS];
		

	
	public  ModelAvatar3() {
		
		
		for (int a=0; a<NPOINTS;a++) {
			rndForPoints[a]=(float)Math.random();
		}
		
		
		this.textureWidth = 32;
		this.textureHeight = 32;	
	
		body= new ModelRenderer(this);
		body.setRotationPoint(0F,16F,0F);
		body.cubeList.add(new ModelBox(body, 0, 12, -3.0F, -2.0F, -2.0F, 6, 4, 4, 0.0F, false));
		
		rightarm= new ModelRenderer(this);
		rightarm.setRotationPoint(-4F,14F,0.5F);
		rightarm.cubeList.add(new ModelBox(rightarm, 0, 20, -1F,0F,-1.5F,2,6,3, 0.0F, false));
		
		leftarm= new ModelRenderer(this);
		leftarm.setRotationPoint(4F,14F,0.5F);
		leftarm.cubeList.add(new ModelBox(leftarm, 0, 20, -1F,0F,-1.5F,2,6,3, 0.0F, false));
	
		rightleg= new ModelRenderer(this);
		rightleg.setRotationPoint(-1.5F,19F,0.5F);
		rightleg.cubeList.add(new ModelBox(rightleg, 12, 20, -1.5F,-1F,-1.5F,3,6,3, 0.0F, false));
	
		leftleg= new ModelRenderer(this);
		leftleg.setRotationPoint(1.5F,19F,0.5F);
		leftleg.cubeList.add(new ModelBox(leftleg, 12, 20, -1.5F,-1F,-1.5F,3,6,3, 0.0F, false));

		head= new ModelRenderer(this);
		head.setRotationPoint(0F,14F,0F);
		head.cubeList.add(new ModelBox(head, 0, 0, -3F,-6F,-3F,6,6,6, 0.0F, false));
		
		
		botton= new ModelRenderer(this);
		botton.setRotationPoint(0F,19.5F,2.5F);	
		botton.cubeList.add(new ModelBox(botton, 4, 12, -2F,-1.5F,-0.5F,4,3,1, 0.0F, false));
		
        
		//de izquierda a derecha , desde -7 hasta 8 16 puntos, el 0 esta en el centro y hacia la izquierda disminuye
		//de cerca a lejos, , desde -7 hasta 8, el 0 esta en el centro y hacia cerca disminuye
		//de arriba abajo, desde 8 hasta 24, hacia arriba disminuye , el centro es el 24.
		
		
		//rotation
        //x=-($elementoJson.rotation.origin[0]-8)
        //y=-($elementoJson.rotation.origin[1]-24)
        //z=($elementoJson.rotation.origin[2]-8)
		
		//Position
        //x=($elementoJson.rotation.origin[0]-$From.X)-($Size.X)
        //y=($elementoJson.rotation.origin[1]-$From.Y)-($Size.Y)
        //z=($elementoJson.rotation.origin[2]-$From.Z)-($Size.Z)
		
		

		float origenx=-14F;
		float origeny=-4; //-8 , but is scaled 2/3
		
		for (int b=0;b<NARC;b++)
		for (int a=0; a<NPOINTS;a++) {

			points[b][a]=new ModelRenderer(this);
			
			double angle=(3.1416F/2F)/16F*a;
			double cos=Math.cos(angle)*16;
			float desx=(float) -(origenx+cos);
			double sin=Math.sin(angle)*16;
			float desy=(float) (sin);

			double color=6-Math.floor((double)(rndForPoints[a]*7F));
			int textureOffsetX= (int)color*4;
			
			points[b][a].setRotationPoint(0F,origeny+24F,0F);						
			points[b][a].setTextureOffset(textureOffsetX, 29);			
			points[b][a].cubeList.add(new ModelBox(points[b][a], textureOffsetX, 29,origenx+desx-1F, -desy-1F ,-1F,1,1,1,0F, false));
		}	
	}

	public void render(TileElvenAvatar avatar,float elapsed, boolean riseArm) {				
		

	        float scale = 0.06666667F;
	        
			//rightarm.rotateAngleX=-3.1416F*(3F/4F);

	         rightarm.rotateAngleX=0;
	         if (avatar!=null) { 
	        	 if (riseArm) 
	        		 rightarm.rotateAngleX=avatar.secuencesAvatar.getEndValue("RiseArm","Arm");
	        	 else 
	        		if (avatar.secuencesAvatar.isElemenActiveSecuence("Arm"))
	        			rightarm.rotateAngleX=avatar.secuencesAvatar.getValue("Arm");
	        	
	         }
	        
	        
			this.body.render(scale);
			this.rightarm.render(scale);
			this.leftarm.render(scale);
			this.rightleg.render(scale);
			this.leftleg.render(scale);
			this.head.render(scale);
			this.botton.render(scale);
			    
			
			
			if (avatar!=null) {
				
				//float difscale=((float)(Math.random()*scale)/10F)-(scale/10F);
				avatar.updateRotatePoints(points,rndForPoints,elapsed);
				
				for (int b=0;b<NARC;b++)
				for (int a=0; a<NPOINTS;a++) {
					if (!avatar.haveMana()||!avatar.haveItem()||!avatar.isEnabled()) break;
					else if (Math.random()>.35)
							points[b][a].render(scale*2F/3F);
				}
			}			
	}
	
	

	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}