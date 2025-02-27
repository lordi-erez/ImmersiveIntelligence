package pl.pabilo8.immersiveintelligence.client.gui;

import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.client.gui.GuiIEContainerBase;
import blusunrize.immersiveengineering.client.gui.elements.GuiButtonState;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import org.lwjgl.opengl.GL11;
import pl.pabilo8.immersiveintelligence.Config.IIConfig.Machines.EffectCrates;
import pl.pabilo8.immersiveintelligence.ImmersiveIntelligence;
import pl.pabilo8.immersiveintelligence.api.Utils;
import pl.pabilo8.immersiveintelligence.common.CommonProxy;
import pl.pabilo8.immersiveintelligence.common.IIContent;
import pl.pabilo8.immersiveintelligence.common.blocks.metal.TileEntityMedicalCrate;
import pl.pabilo8.immersiveintelligence.common.gui.ContainerMedicalCrate;
import pl.pabilo8.immersiveintelligence.common.network.IIPacketHandler;
import pl.pabilo8.immersiveintelligence.common.network.MessageBooleanAnimatedPartsSync;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Pabilo8
 * @since 2019-05-17
 */
public class GuiMedicalCrate extends GuiIEContainerBase
{
	private final TileEntityMedicalCrate tile;
	private static final String TEXTURE = ImmersiveIntelligence.MODID+":textures/gui/medical_crate.png";
	GuiButtonState buttonHealing = null, buttonBoost = null;

	public GuiMedicalCrate(InventoryPlayer inventoryPlayer, TileEntityMedicalCrate tile)
	{
		super(new ContainerMedicalCrate(inventoryPlayer, tile));
		this.tile = tile;
		this.ySize = 168;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		addButton(buttonHealing = new GuiButtonState(0, guiLeft+111, guiTop+2, 28, 24, "", tile.shouldHeal, TEXTURE, 176, 75, 0));
		addButton(buttonBoost = new GuiButtonState(1, guiLeft+111, guiTop+24, 28, 24, "", tile.shouldBoost, TEXTURE, 176, 51, 0));

		boolean upgraded = tile.hasUpgrade(IIContent.UPGRADE_INSERTER);
		buttonHealing.visible=upgraded;
		buttonBoost.visible=upgraded;
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if(button==buttonHealing)
		{
			tile.shouldHeal = !tile.shouldHeal;
			buttonHealing.state = tile.shouldHeal;
			IIPacketHandler.INSTANCE.sendToServer(new MessageBooleanAnimatedPartsSync(tile.shouldHeal, 1, tile.getPos()));
		}
		else if(button==buttonBoost)
		{
			tile.shouldBoost = !tile.shouldBoost;
			buttonBoost.state = tile.shouldBoost;
			IIPacketHandler.INSTANCE.sendToServer(new MessageBooleanAnimatedPartsSync(tile.shouldBoost, 2, tile.getPos()));
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		boolean upgraded = tile.hasUpgrade(IIContent.UPGRADE_INSERTER);
		int ww = upgraded?9: 36;

		ArrayList<String> tooltip = new ArrayList<String>();

		if(upgraded)
		{
			if(Utils.isPointInRectangle(buttonHealing.x,buttonHealing.y,buttonHealing.x+buttonHealing.width,buttonHealing.y+buttonHealing.height,mouseX,mouseY))
				tooltip.add(I18n.format(CommonProxy.DESCRIPTION_KEY+"medical_crate.heal"));
			else if(Utils.isPointInRectangle(buttonBoost.x,buttonBoost.y,buttonBoost.x+buttonBoost.width,buttonBoost.y+buttonBoost.height,mouseX,mouseY))
				tooltip.add(I18n.format(CommonProxy.DESCRIPTION_KEY+"medical_crate.boost"));
		}

		ClientUtils.handleGuiTank(tile.tanks[0], guiLeft+ww+10, guiTop+21, 16, 47, 177, 0, 20, 51, mouseX, mouseY, TEXTURE, tooltip);
		ClientUtils.handleGuiTank(tile.tanks[1], guiLeft+ww+54, guiTop+21, 16, 47, 177, 0, 20, 51, mouseX, mouseY, TEXTURE, tooltip);

		if(upgraded&&Utils.isPointInRectangle(guiLeft+153, guiTop+24, guiLeft+153+7, guiTop+24+47, mouseX, mouseY))
			tooltip.add(tile.energyStorage+"/"+EffectCrates.maxEnergyStored+" IF");

		if(!tooltip.isEmpty())
		{
			ClientUtils.drawHoveringText(tooltip, mouseX, mouseY, fontRenderer, guiLeft+xSize, -1);
			RenderHelper.enableGUIStandardItemLighting();
		}
	}

	/**
	 * Draws the background layer of this container (behind the items).
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int mx, int my)
	{
		boolean upgraded = tile.hasUpgrade(IIContent.UPGRADE_INSERTER);
		int ww = upgraded?9: 36;

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		ClientUtils.bindTexture(TEXTURE);
		this.drawTexturedModalRect(guiLeft, guiTop+79, 0, 79, xSize, ySize-79);
		this.drawTexturedModalRect(guiLeft+ww, guiTop, 9, 0, upgraded?167: 102, 79);

		Utils.drawPowerBar(guiLeft+153, guiTop+24,7,47,tile.energyStorage/(float)EffectCrates.maxEnergyStored);

		ClientUtils.handleGuiTank(tile.tanks[0], guiLeft+ww+10, guiTop+21, 16, 47, 177, 0, 20, 51, 0, 0, TEXTURE, null);
		ClientUtils.handleGuiTank(tile.tanks[1], guiLeft+ww+54, guiTop+21, 16, 47, 177, 0, 20, 51, 0, 0, TEXTURE, null);
	}
}
