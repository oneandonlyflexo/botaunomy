// 
// Decompiled by Procyon v0.5.36
// 

package botaunomy.model;

import net.minecraft.entity.Entity;
//import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelBase;

@SideOnly(Side.CLIENT)
public class ModelAvatar2 extends ModelBase
{
    public ModelRenderer body;
    public ModelRenderer rightarm;
    public ModelRenderer leftarm;
    public ModelRenderer rightleg;
    public ModelRenderer leftleg;
    public ModelRenderer head;
    
    public ModelAvatar2() {
		this.textureHeight=32;
        this.textureWidth=32;
        
        this.leftleg = new ModelRenderer(this, 0, 20);
        this.leftleg.setRotationPoint(1.5F, 18.0F, -0.5F);
        this.leftleg.addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3, 0.0F);
        this.leftleg.mirror=true;
    
		
        this.rightarm = new ModelRenderer(this, 0, 20);
		this.rightarm.setRotationPoint(-3.0F, 15.0F, -1.0F);
        this.rightarm.addBox(-2.0F, -1.0F, -1.0F, 2, 6, 3, 0.0F);
        this.setRotateAngle(this.rightarm, 0.0F, -0.0F, 0.08726646F);

		
        this.leftarm = new ModelRenderer(this, 0, 20);
        this.leftarm.setRotationPoint(3.0F, 15.0F, -1.0F);
        this.leftarm.addBox(0.0F, -1.0F, -1.0F, 2, 6, 3, 0.0F);       
        this.setRotateAngle(this.leftarm, 0.0F, -0.0F, -0.08726646F);

		
        this.head = new ModelRenderer(this, 0, 0);
		this.head.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.head.addBox(-3.0F, -6.0F, -3.0F, 6, 6, 6, 0.0F);
        
		this.rightleg = new ModelRenderer(this, 0, 20);
		this.rightleg.setRotationPoint(-1.5F, 18.0F, -0.5F);		
        this.rightleg.addBox(-1.5F, 0.0F, -1.5F, 3, 6, 3, 0.0F);
		
        this.body = new ModelRenderer(this, 0, 12);
		this.body.setRotationPoint(0.0F, 14.0F, 0.0F);
        this.body.addBox(-3.0F, 0.0F, -2.0F, 6, 4, 4, 0.0F);
    }
    
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
     }
    
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        this.leftleg.render(scale);
        this.rightarm.render(scale);
        this.leftarm.render(scale);
        this.head.render(scale);
        this.rightleg.render(scale);
        this.body.render(scale);
    }
    
	public void render() {
		
       float scale = 0.06666667F;

        this.leftleg.render(scale);
        this.rightarm.render(scale);
        this.leftarm.render(scale);
        this.head.render(scale);
        this.rightleg.render(scale);
        this.body.render(scale);
	}

}