package org.oxytocina.geomancy.items.artifacts;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Util.Toolbox;
import org.oxytocina.geomancy.damageTypes.ModDamageTypes;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.progression.advancement.ModCriteria;

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

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        // check for duplicates, fling inventory if there are any

        if(TrinketsApi.getTrinketComponent(entity).isPresent())
        {
            TrinketComponent comp = TrinketsApi.getTrinketComponent(entity).get();
            var duplicates = comp.getEquipped(stack.getItem());
            if(duplicates.size() > 1)
            {
                if(entity instanceof PlayerEntity player)
                {
                    //player.sendMessage(Text.literal("DUPES!!: " + duplicates.size()), false);

                    // trigger advancement
                    if (player instanceof ServerPlayerEntity serverPlayer) {

                        ModCriteria.SIMPLE.trigger(serverPlayer, "duplicate_trinkets");
                    }
                }

                var world = entity.getWorld();
                var pos = entity.getPos();
                var eyepos = entity.getEyePos();

                world.playSound(pos.getX(),pos.getY(),pos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10.0F, 0.8F + Toolbox.random.nextFloat() * 0.2F, false);
                world.playSound(pos.getX(),pos.getY(),pos.getZ(), SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + Toolbox.random.nextFloat() * 0.2F, false);

                entity.damage(ModDamageTypes.of(world, ModDamageTypes.DUPLICATE_TRINKETS), 5.0F);


                // yeet
                for(var pair : duplicates){
                    pair.getLeft().inventory().removeStack(pair.getLeft().index());
                    var itemEntity = Toolbox.spawnItemStackAsEntity(
                            world, eyepos, pair.getRight(),
                            Toolbox.RandomItemDropVelocity(0.25f), false, entity);

                }



            }
        }

    }
}
