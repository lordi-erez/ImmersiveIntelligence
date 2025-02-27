package pl.pabilo8.immersiveintelligence.client.gui;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.GuiIEContainerBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import pl.pabilo8.immersiveintelligence.ImmersiveIntelligence;
import pl.pabilo8.immersiveintelligence.api.Utils;
import pl.pabilo8.immersiveintelligence.api.crafting.DustUtils;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.second.TileEntityFiller;
import pl.pabilo8.immersiveintelligence.common.gui.ContainerFiller;

import java.util.ArrayList;

/**
 * @author Pabilo8
 * @since 10-07-2019
 */
public class GuiFiller extends GuiIEContainerBase
{
	public static final String TEXTURE = ImmersiveIntelligence.MODID+":textures/gui/filler.png";
	TileEntityFiller tile;

	public GuiFiller(InventoryPlayer inventoryPlayer, TileEntityFiller tile)
	{
		super(new ContainerFiller(inventoryPlayer, tile));
		this.ySize = 168;
		this.tile = tile;
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientUtils.bindTexture(TEXTURE);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		if(!tile.dustStorage.isEmpty())
		{
			int stored = (int)(60*(tile.dustStorage.amount/(float)tile.dustCapacity));
			float[] rgb = Utils.rgbIntToRGB(DustUtils.getColor(tile.dustStorage));

			GlStateManager.color(rgb[0],rgb[1],rgb[2]);
			this.drawTexturedModalRect(guiLeft+56, guiTop+2+(60-stored), 176, 60-stored, 64, stored);
			GlStateManager.color(1f,1f,1f);
		}

		Utils.drawPowerBar(guiLeft+161-4, guiTop+24, 7, 47, tile.getEnergyStored(null)/(float)tile.getMaxEnergyStored(null));
	}

	@Override
	public void drawScreen(int mx, int my, float partial)
	{
		super.drawScreen(mx, my, partial);
		this.renderHoveredToolTip(mx, my);

		ArrayList<String> tooltip = new ArrayList<>();

		if(mx > guiLeft+161-4&&mx < guiLeft+168-4&&my > guiTop+24&&my < guiTop+71)
			tooltip.add(Utils.getPowerLevelString(tile));

		int stored = (int)(60*(tile.dustStorage.amount/(float)tile.dustCapacity));
		if(mx > guiLeft+56&&mx < guiLeft+56+60&&my >guiTop+2+(60-stored)&&my < guiTop+2+(60-stored)+stored)
		{
			tooltip.add(DustUtils.getDustName(tile.dustStorage));
			tooltip.add(tile.dustStorage.amount+" mB");
		}

		if(!tooltip.isEmpty())
		{
			ClientUtils.drawHoveringText(tooltip, mx, my, fontRenderer, guiLeft+xSize, -1);
			RenderHelper.enableGUIStandardItemLighting();
		}
	}
}
