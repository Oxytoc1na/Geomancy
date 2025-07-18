package org.oxytocina.geomancy.client.screen.slots;

import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.screen.slot.*;
import net.minecraft.registry.tag.*;

public class TagFilterSlot extends Slot {
	
	private final TagKey<Item> acceptedTag;
	
	public TagFilterSlot(Inventory inventory, int index, int x, int y, TagKey<Item> acceptedTag) {
		super(inventory, index, x, y);
		this.acceptedTag = acceptedTag;
	}
	
	@Override
	public boolean canInsert(ItemStack stack) {
		return stack.isIn(acceptedTag);
	}
	
}
