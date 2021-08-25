/*******************************************************************************
 * Copyright (C) 2017 Jeremy Grozavescu <oneandonlyflexo>
 * https://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * This file is part of Botaunomy, which is open source:
 * https://github.com/oneandonlyflexo/botaunomy
 ******************************************************************************/
package botaunomy.client.render;

import javax.annotation.Nullable;


import org.lwjgl.opengl.GL11;

import botaunomy.ModBlocks;
import botaunomy.ModResources;
import botaunomy.block.tile.TileElvenAvatar;
import botaunomy.model.ModelAvatar3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.state.BotaniaStateProps;
import vazkii.botania.client.core.handler.ClientTickHandler;
import botaunomy.item.RodItem;
//import vazkii.botania.client.model.ModelAvatar;

/**
 * This is a straight of copy of Botania's RenderTileAvatar class because I needed to change the texture.  I should
 * probably try to figure out how to access other mod's private fields and modify them because then I could just use
 * the existing class and modify it's texture field to be the correct one.
 *
 * @author "oneandonlyflexo"
 */
public class RenderTileElvenAvatar extends TileEntitySpecialRenderer<TileElvenAvatar> {

	

	private static final float[] ROTATIONS = new float[] {
			180F, 0F, 90F, 270F
	};

	private static final ResourceLocation avatarTexture = new ResourceLocation(ModResources.MODEL_ELVEN_AVATAR3);
	private static final ResourceLocation overlayTexture = new ResourceLocation(ModResources.MODEL_ELVEN_AVATAR_OVERLAY);
	private static final ModelAvatar3 model = new ModelAvatar3();
	private static final double DESPL =0.22;

	
	@Override
	public void render(@Nullable TileElvenAvatar avatar, double d0, double d1, double d2, float pticks, int digProgress, float unused) {
		
		if (avatar!=null)
			avatar.secuencesAvatar.update(pticks);
		
		if (avatar != null)
			if (!avatar.getWorld().isBlockLoaded(avatar.getPos(), false)
					|| avatar.getWorld().getBlockState(avatar.getPos()).getBlock() != ModBlocks.elven_avatar)
				return;

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1F, 1F, 1F, 1F);

		double t0=0;
		double t2=0;
		if (avatar!=null) {
			EnumFacing enumfacing=avatar.getAvatarFacing();
			if (enumfacing==EnumFacing.EAST) t0=-DESPL;
			if (enumfacing==EnumFacing.WEST) t0=DESPL;
			
			if (enumfacing==EnumFacing.NORTH) t2=DESPL;
			if (enumfacing==EnumFacing.SOUTH) t2=-DESPL;
		}		
		GlStateManager.translate(d0+t0, d1, d2+t2);

		Minecraft.getMinecraft().renderEngine.bindTexture(avatarTexture);
		EnumFacing facing = avatar != null && avatar.getWorld() != null
				? avatar.getWorld().getBlockState(avatar.getPos()).getValue(BotaniaStateProps.CARDINALS)
						: EnumFacing.SOUTH;
		
				GlStateManager.translate(0.5F, 1.6F, 0.5F);
				GlStateManager.scale(1F, -1F, -1F);
				GlStateManager.rotate(ROTATIONS[Math.max(Math.min(ROTATIONS.length - 1, facing.getIndex() - 2), 0)], 0F, 1F, 0F);
				
				ItemStack stack =null;
				ItemStack stack2 =null;
				
				if (avatar != null) {
					stack= avatar.getItemHandler().getStackInSlot(0);
					if (avatar.getItemHandler().getSlots()>=2 )
						stack2= avatar.getItemHandler().getStackInSlot(1);
				}
		
				boolean risearm= (stack!=null)&&(!stack.isEmpty())&&(!avatar.secuencesAvatar.isActive());				
				model.render(avatar,pticks,risearm);

				if (avatar == null) {
					GlStateManager.color(1F, 1F, 1F);
					GlStateManager.scale(1F, -1F, -1F);
					GlStateManager.enableRescaleNormal();
					GlStateManager.popMatrix();
					return;
				}
				
				if((stack!=null)&&!stack.isEmpty()) {

					renderTool(avatar,stack,0.6F,true) ;

					if (avatar.haveMana()) {																		
						if (stack.getItem() instanceof RodItem) {
							RodItem willrod = (RodItem) stack.getItem();								
								Minecraft.getMinecraft().renderEngine.bindTexture(willrod.getOverlayResource());
							}
							else
								Minecraft.getMinecraft().renderEngine.bindTexture(overlayTexture);														 
							 renderOverlay(avatar,pticks,risearm,1.01F);
					}
				}
				
				if((stack2!=null)&&!stack2.isEmpty()) {
					renderTool(avatar,stack2,0.6F,false) ;
				}

				
				GlStateManager.color(1F, 1F, 1F);
				GlStateManager.scale(1F, -1F, -1F);
				GlStateManager.enableRescaleNormal();
				GlStateManager.popMatrix();
	}

	private void renderTool(TileElvenAvatar avatar,ItemStack stack, float s,boolean righthand) {
		
		GlStateManager.pushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);		
		GlStateManager.scale(s, s, s);
				
		if (righthand) {
			if (!avatar.secuencesAvatar.isActive()) {					
				float corr=(float)Math.sin(avatar.secuencesAvatar.getEndValue("RiseArm","toolCorrection"));					
				GlStateManager.translate(-0.5F,avatar.secuencesAvatar.getEndValue("RiseArm","toolOffset"), -0.4f -(corr*0.3));
				GlStateManager.rotate(avatar.secuencesAvatar.getEndValue("RiseArm","tool"), 1, 0, 0);										
			}else {
							
				float corr=(float)Math.sin(avatar.secuencesAvatar.getValue("toolCorrection"));					
				GlStateManager.translate(-0.5F,avatar.secuencesAvatar.getValue("toolOffset"), -0.4f -(corr*0.3));
				GlStateManager.rotate(avatar.secuencesAvatar.getValue("tool"), 1, 0, 0);
			}
		}else		
		{
			
			float corr=(float)Math.sin(avatar.secuencesAvatar.getInitValue("RiseArm","toolCorrection"));					
			GlStateManager.translate(+0.5F,1.9, -0.4f -(corr*0.3));
			GlStateManager.rotate(0.2F, 1, 0, 0);
					
		}
		
		Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND);
		GlStateManager.popMatrix();		
	}
	
	private void renderOverlay(TileElvenAvatar avatar,float pticks,boolean risearm,float s) {
		
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.scale(s, s, s);
		GlStateManager.translate(0F, -0.01F, 0F);
		int light = 15728880;
		int lightmapX = light % 65536;
		int lightmapY = light / 65536;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX, lightmapY);
		float alpha = (float) Math.sin(ClientTickHandler.ticksInGame / 20D) / 2F + 0.5F;
		GlStateManager.color(1F, 1F, 1F, alpha + 0.183F);												
		model.render(avatar,pticks,risearm);														
		GlStateManager.popMatrix();
		
	}
}
