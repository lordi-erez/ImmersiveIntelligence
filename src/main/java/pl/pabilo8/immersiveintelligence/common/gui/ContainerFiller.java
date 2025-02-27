package pl.pabilo8.immersiveintelligence.common.gui;

import blusunrize.immersiveengineering.common.gui.ContainerIEBase;
import blusunrize.immersiveengineering.common.gui.IESlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.first.TileEntityChemicalBath;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.second.TileEntityFiller;

/**
 * @author Pabilo8
 * @since 10-07-2019
 */
public class ContainerFiller extends ContainerIEBase<TileEntityFiller>
{
	public ContainerFiller(InventoryPlayer inventoryPlayer, TileEntityFiller tile)
	{
		super(inventoryPlayer, tile);
		//Dust input slot

		this.addSlotToContainer(new Slot(this.inv, 0, 120, 5));

		this.slotCount = tile.getInventory().size();
		this.tile = tile;

		for(int i = 0; i < 3; i++)
			for(int j = 0; j < 9; j++)
				addSlotToContainer(new Slot(inventoryPlayer, j+i*9+9, 8+j*18, 86+i*18));
		for(int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(inventoryPlayer, i, 8+i*18, 144));
	}
}
