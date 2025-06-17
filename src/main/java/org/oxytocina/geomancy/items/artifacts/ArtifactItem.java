package org.oxytocina.geomancy.items.artifacts;

import dev.emi.trinkets.api.TrinketItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.ModItems;

import java.util.List;

public class ArtifactItem extends TrinketItem {

    public ArtifactItem(Settings settings, ArtifactSettings artifactSettings) {
        super(settings);
        ModItems.ArtifactItems.add(this);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(getDescription().formatted(Formatting.GRAY));
    }

    public MutableText getDescription() {
        return Text.translatable(Registries.ITEM.getId(this).toTranslationKey("item","desc"));
    }

    //public Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid) {
    //    var modifiers = super.getModifiers(stack, slot, entity, uuid);
    //    // +10% movement speed
    //    modifiers.put(EntityAttributes.GENERIC_MOVEMENT_SPEED, new EntityAttributeModifier(uuid, "geomancy:movement_speed", 0.1, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
    //    // If the player has access to ring slots, this will give them an extra one
    //    SlotAttributes.addSlotModifier(modifiers, "hand/ring", uuid, 1, EntityAttributeModifier.Operation.ADDITION);
    //    return modifiers;
    //}
}
