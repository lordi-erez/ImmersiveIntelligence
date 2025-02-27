package pl.pabilo8.immersiveintelligence.client.render.hans;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.entity.EntityHans;

/**
 * @author Pabilo8
 * @since 27.08.2021
 */
@SideOnly(Side.CLIENT)
public class LayerHansHeldItem implements LayerRenderer<EntityHans>
{
	protected final HansRenderer livingEntityRenderer;

	public LayerHansHeldItem(HansRenderer livingEntityRendererIn)
	{
		this.livingEntityRenderer = livingEntityRendererIn;
	}

	public void doRenderLayer(EntityHans entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		boolean flag = entitylivingbaseIn.getPrimaryHand()==EnumHandSide.RIGHT;
		ItemStack itemstack = flag?entitylivingbaseIn.getHeldItemOffhand(): entitylivingbaseIn.getHeldItemMainhand();
		ItemStack itemstack1 = flag?entitylivingbaseIn.getHeldItemMainhand(): entitylivingbaseIn.getHeldItemOffhand();

		if(!itemstack.isEmpty()||!itemstack1.isEmpty())
		{
			GlStateManager.pushMatrix();

			if(this.livingEntityRenderer.getMainModel().isChild)
			{
				float f = 0.5F;
				GlStateManager.translate(0.0F, 0.75F, 0.0F);
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
			}

			this.renderHeldItem(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
			this.renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
			GlStateManager.popMatrix();
		}
	}

	private void renderHeldItem(EntityLivingBase living, ItemStack stack, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide)
	{
		if(!stack.isEmpty())
		{
			if(living.isSneaking()&&stack.getItem()==IIContent.itemBinoculars)
				return;

			GlStateManager.pushMatrix();

			if(living.isSneaking())
			{
				GlStateManager.translate(0.0F, 0.2F, 0.0F);
			}
			// Forge: moved this call down, fixes incorrect offset while sneaking.
			this.translateToHand(handSide);
			GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
			boolean flag = handSide==EnumHandSide.LEFT;
			GlStateManager.translate((float)(flag?-1: 1)/16.0F, 0.125F, -0.625F);
			Minecraft.getMinecraft().getItemRenderer().renderItemSide(living, stack, transformType, flag);
			GlStateManager.popMatrix();
		}
	}

	protected void translateToHand(EnumHandSide handSide)
	{
		((ModelBiped)this.livingEntityRenderer.getMainModel()).postRenderArm(0.0625F, handSide);
	}

	public boolean shouldCombineTextures()
	{
		return false;
	}
}
