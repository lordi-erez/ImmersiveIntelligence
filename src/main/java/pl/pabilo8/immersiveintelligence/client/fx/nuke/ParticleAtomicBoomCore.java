package pl.pabilo8.immersiveintelligence.client.fx.nuke;

import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import pl.pabilo8.immersiveintelligence.client.fx.IIParticle;
import pl.pabilo8.immersiveintelligence.client.fx.ParticleRenderer.DrawingStages;
import pl.pabilo8.immersiveintelligence.common.entity.EntityAtomicBoom;

import javax.annotation.Nonnull;

/**
 * @author Pabilo8
 * @since 17.07.2020
 */
public class ParticleAtomicBoomCore extends IIParticle
{
	private float actualParticleScale;

	public ParticleAtomicBoomCore(World world, double x, double y, double z, double mx, double my, double mz, float size)
	{
		super(world, x, y, z, 0, 0, 0);
		this.motionX = mx*1.55;
		this.motionY = my*0.65;
		this.motionZ = mz*1.55;
		this.particleScale = ((float)(size*0.85+(size*0.15*Utils.RAND.nextGaussian()))*2f)*0.45f;
		this.actualParticleScale = this.particleScale;
		this.particleMaxAge = (int)(5+(10*Utils.RAND.nextGaussian()))+1;
		this.particleGravity = 0.25f;
		this.setParticleTextureIndex(0);
	}

	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if(this.particleAge++ >= this.particleMaxAge)
		{
			this.setExpired();
		}

		this.move(this.motionX, this.motionY, this.motionZ);
		this.motionX *= 0.98D;
		this.motionY *= 0.98D;
		this.motionZ *= 0.98D;
		if(world==null)
			return;

		EntityPlayer entityplayer = this.world.getClosestPlayer(this.posX, this.posY, this.posZ, 2.0D, false);

		if(entityplayer!=null)
		{
			AxisAlignedBB axisalignedbb = entityplayer.getEntityBoundingBox();

			if(this.posY > axisalignedbb.minY)
			{
				this.posY += (axisalignedbb.minY-this.posY)*0.2D;
				this.motionY += (entityplayer.motionY-this.motionY)*0.2D;
				this.setPosition(this.posX, this.posY, this.posZ);
			}
		}

		if(this.onGround)
		{
			this.motionX *= 0.699999988079071D;
			this.motionZ *= 0.699999988079071D;
		}
	}

	@Override
	public int getFXLayer()
	{
		return 0;
	}

	@Override
	public int getBrightnessForRender(float p_70070_1_)
	{
		return 240<<16|240;
	}

	/**
	 * Renders the particle
	 */
	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		float f = ((float)this.particleAge+partialTicks)/(float)this.particleMaxAge;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		setRBGColorF(0.8f+(0.2f*f), 0.3f+(0.7f*f), (0.4f*f));

		this.setParticleTextureIndex((int)(2+3-(3*f)));

		setAlphaF(0.25f-((1f-f)*0.1f));

		this.particleScale = this.actualParticleScale*2f;
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);

		setAlphaF(1f);

		this.particleScale = this.actualParticleScale*2f;
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);

	}

	@Nonnull
	@Override
	public DrawingStages getDrawStage()
	{
		return DrawingStages.NORMAL;
	}
}