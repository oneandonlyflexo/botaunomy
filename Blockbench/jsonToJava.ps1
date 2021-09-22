$file="ModelAvatarJson.json"
$name="AvatarTest"
$scriptPath = split-path -parent $MyInvocation.MyCommand.Definition
$json=Get-Content -Path $scriptPath"\"$file | ConvertFrom-Json

class point {
    [float]$X
    [float]$Y
    [float]$Z
}

$texture_sizeWith=$json.texture_size[0]
$texture_sizeHeight=$json.texture_size[0]
#private ModelRenderer ... ;

$elements =  [System.Collections.ArrayList]::new()
$definition_ModelRendered
$ModelRenderer_values


#de izquierda a derecha , desde -7 hasta 8 16 puntos, el 0 esta en el centro y hacia la izquierda disminuye
#de cerca a lejos, , desde -7 hasta 8, el 0 esta en el centro y hacia cerca disminuye
#de arriba abajo, desde 8 hasta 24, hacia arriba disminuye , el centro es el 24.

foreach ($elementoJson in $json.elements){
    $elements.Add($elementoJson.name)
    $definition_ModelRendered+="private ModelRenderer "+$elementoJson.name+";`r`n"

        #size
        $size = New-Object point
        $size.X=$elementoJson.to[0]-$elementoJson.from[0]
        $size.y=$elementoJson.to[1]-$elementoJson.from[1]
        $size.z=$elementoJson.to[2]-$elementoJson.from[2]

    	#rotation
        $x=-($elementoJson.rotation.origin[0]-8)
        $y=-($elementoJson.rotation.origin[1]-24)
        $z=($elementoJson.rotation.origin[2]-8)

  		#Position
        $px=($elementoJson.rotation.origin[0]-$elementoJson.from[0])-($Size.X)
        $py=($elementoJson.rotation.origin[1]-$elementoJson.from[1])-($Size.Y)
        $pz=($elementoJson.rotation.origin[2]-$elementoJson.from[2])-($Size.Z)

        #Texture
        $txi=($elementoJson.faces.east.uv[0])*2
        $tyi=($elementoJson.faces.down.uv[1])*2
        #$txf=($elementoJson.faces.south.uv[2])
        #$tyf=($elementoJson.faces.west.uv[3])
		

    $ModelRenderer_values+=$elementoJson.name+"=new ModelRenderer(this);`r`n"
    $ModelRenderer_values+=$elementoJson.name+".setRotationPoint("+$x+"F,"+$y+"F,"+$z+"F);`r`n"
    $ModelRenderer_values+=$elementoJson.name+".cubeList.add(new ModelBox("+$elementoJson.name+","+$txi+","+$tyi+","+$px+"F,"+$py+"F,"+ $pz+"F,"+  $size.X+","+ $size.y+","+ $size.z+", 0.0F, false));`r`n"
    $ModelRenderer_values+="`r`n"

}

$plantilla="
// Made with Powershell
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class @(name) extends ModelBase
{
	
	@(definition_ModelRendered)
	
	public  @(name) () {

		this.textureWidth = @(textureWidth);
		this.textureHeight = @(textureHeight);		
		@(ModelRenderer_values)
		
	}

	public void render(TileElvenAvatar avatar,float elapsed, boolean riseArm) {				
		
	        float scale = 0.06666667F	        	      
			this.body.render(scale);
			this.rightarm.render(scale);
			this.leftarm.render(scale);
			this.rightleg.render(scale);
			this.leftleg.render(scale);
			this.head.render(scale);
			this.botton.render(scale);			
	}
	
	public void setRotationAngle(ModelRenderer modelRenderer, float x, float y, float z) {
		modelRenderer.rotateAngleX = x;
		modelRenderer.rotateAngleY = y;
		modelRenderer.rotateAngleZ = z;
	}
}"