package pl.pabilo8.immersiveintelligence.client.gui.elements;

import blusunrize.immersiveengineering.api.Lib;
import blusunrize.immersiveengineering.client.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import pl.pabilo8.immersiveintelligence.ImmersiveIntelligence;
import pl.pabilo8.immersiveintelligence.api.Utils;
import pl.pabilo8.immersiveintelligence.common.CommonProxy;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.first.TileEntityPacker.PackerTask;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * @author Pabilo8
 * @since 29.09.2021
 */
public class GuiPackerTaskList extends GuiButton
{
	boolean unpacker;
	private ArrayList<PackerTask> entries;
	private final Consumer<Integer> taskChange;
	private static final ResourceLocation TEXTURE_PACKER = new ResourceLocation(ImmersiveIntelligence.MODID+":textures/gui/packer.png");
	private long prevWheelNano = 0;
	private int scroll;
	private int maxScroll;
	public int hoveredOption = -1;
	public int selectedOption = -1;

	public GuiPackerTaskList(int id, int x, int y, int w, int h, boolean unpacker, ArrayList<PackerTask> entries, Consumer<Integer> taskChange)
	{
		super(id, x, y, w, h, "");
		this.unpacker = unpacker;
		this.entries = entries;
		this.taskChange = taskChange;
		recalculateEntries();
	}

	/**
	 * Refreshes scroll value
	 */
	public void recalculateEntries()
	{
		maxScroll = (Math.max(entries.size()-4, 0))*20;
	}

	/**
	 * Sets and refreshes scroll value
	 */
	public void setScrollPercent(float percent)
	{
		recalculateEntries();
		this.scroll = Math.round(MathHelper.clamp(percent*maxScroll, 0, maxScroll));
	}

	/**
	 * @return scroll percent (current/max)
	 */
	public float getScrollPercent()
	{
		return scroll/(float)maxScroll;
	}

	/**
	 * Draws this list to the screen.
	 */
	@Override
	public void drawButton(@Nonnull Minecraft mc, int mx, int my, float partialTicks)
	{
		if(entries.size() > 0&&Utils.isPointInRectangle(x, y, x+width, y+height, mx, my))
		{

			//Handle DWheel
			int mouseWheel = Mouse.getEventDWheel();
			if(mouseWheel!=0&&maxScroll > 0&&Mouse.getEventNanoseconds()!=prevWheelNano)
			{
				prevWheelNano = Mouse.getEventNanoseconds();
				scroll -= Integer.signum(mouseWheel)*10;
			}
			if(Mouse.isButtonDown(0)&&Utils.isPointInRectangle(x+width-11, y, x+width, y+(height-11), mx, my))
			{
				float v = (my-y)/(float)height;
				setScrollPercent((my-y+(v > 0.5f?v/20f: -v/20f))/(height-11));
			}
			scroll = MathHelper.clamp(scroll, 0, maxScroll);

			//variable selection
			hoveredOption = (int)Math.floor(((my+scroll)-this.y)/20f);
			if(hoveredOption >= entries.size())
				hoveredOption = -1;
		}
		else
			hoveredOption = -1;


		//draw scrollbar
		GlStateManager.pushMatrix();
		Utils.bindTexture(TEXTURE_PACKER);
		drawTexturedModalRect(this.x+width-10, this.y+(int)(getScrollPercent()*(height-12)), 161, 211, 9, 14);

		scissor(x, y, width, height);

		int i = 0;

		//draw variables

		for(PackerTask entry : entries)
		{
			drawEntry(mc, this.x, this.y+(i*20)-scroll, i==hoveredOption, entry);
			i += 1;
		}

		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		GlStateManager.popMatrix();
	}

	void drawEntry(Minecraft mc, int x, int y, boolean hovered, PackerTask action)
	{
		//Base
		GL11.glPushMatrix();
		GlStateManager.disableLighting();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GlStateManager.enableAlpha();

		Utils.bindTexture(TEXTURE_PACKER);

		//Background
		this.drawTexturedModalRect(x, y, 96, 211, 65, 20);

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.fontRenderer.drawString(I18n.format(CommonProxy.DESCRIPTION_KEY+"metal_multiblock1.packer.task."+action.actionType.getActionName(unpacker)), x+2, y+7, Utils.COLOR_H1, false);

		if(Objects.equals(action.stack.oreName, "*"))
		{
			boolean u = mc.fontRenderer.getUnicodeFlag();
			mc.fontRenderer.setUnicodeFlag(true);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x+47+5, y+1, 0);
			GlStateManager.scale(2, 2, 1);
			mc.fontRenderer.drawString("*", 0, 0, Lib.COLOUR_I_ImmersiveOrange);

			GlStateManager.popMatrix();
			mc.fontRenderer.setUnicodeFlag(u);
		}
		else
		{
			RenderHelper.enableGUIStandardItemLighting();
			long rand = mc.player.ticksExisted;
			ItemStack stack = action.stack.stack;
			if(stack.isEmpty()&&action.stack.stackList!=null&&action.stack.stackList.size() > 0)
				stack = action.stack.stackList.get((int)(rand/20)%action.stack.stackList.size());
			if(stack.isEmpty()&&action.stack.oreName!=null)
			{
				List<ItemStack> ores = OreDictionary.getOres(action.stack.oreName);
				if(ores!=null)
				{
					Iterator<ItemStack> i = ores.iterator();
					while(i.hasNext())
					{
						ItemStack next = i.next();
						if(next.getHasSubtypes()&&next.getMetadata()==32767)
						{
							ores.remove(next);
							NonNullList<ItemStack> list = NonNullList.create();
							next.getItem().getSubItems(next.getItem().getCreativeTab(), list);
							ores.addAll(list);
						}
					}
					if(ores.size() > 0)
						stack = ores.get((int)(rand/20)%ores.size());
				}
			}
			mc.getRenderItem().renderItemIntoGUI(stack, x+47, y+2);
			RenderHelper.disableStandardItemLighting();
		}

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		GL11.glPopMatrix();
	}

	/**
	 * "Cuts out" canvas, so opengl can only render stuff within a certain border
	 */
	private void scissor(int x, int y, int xSize, int ySize)
	{
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		ScaledResolution res = new ScaledResolution(ClientUtils.mc());
		x = x*res.getScaleFactor();
		ySize = ySize*res.getScaleFactor();
		y = ClientUtils.mc().displayHeight-(y*res.getScaleFactor())-ySize;
		xSize = xSize*res.getScaleFactor();
		GL11.glScissor(x, y, xSize, ySize);
	}


	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent e).
	 */
	@Override
	public boolean mousePressed(Minecraft mc, int mx, int my)
	{
		int s = selectedOption;
		if(Utils.isPointInRectangle(x, y, x+width-11, y+height, mx, my))
		{
			selectedOption = (selectedOption!=hoveredOption)?hoveredOption: -1;
			taskChange.accept(s);
		}

		return selectedOption!=s;
	}
}