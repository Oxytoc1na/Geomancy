package org.oxytocina.geomancy.items;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.Toolbox;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.damageTypes.ModDamageTypes;
import org.oxytocina.geomancy.items.artifacts.ArtifactSettings;
import org.oxytocina.geomancy.progression.advancement.ModCriteria;
import org.oxytocina.geomancy.sound.ModSoundEvents;

import java.util.List;

public class HammerItem extends MiningToolItem {

    public float skillBonus = 10;
    public float skillMultiplier = 1;
    public int progressPerHit = 10;
    public int cooldown = 20;

    public HammerItem(float attackDamage, float attackSpeed, ToolMaterial material, TagKey<Block> effectiveBlocks, Settings settings, float skillBonus, float skillMultiplier,int progressPerHit, int cooldown) {
        super(attackDamage, attackSpeed, material, effectiveBlocks, settings);
        this.skillBonus=skillBonus;
        this.skillMultiplier=skillMultiplier;
        this.progressPerHit=progressPerHit;
        this.cooldown=cooldown;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(getDescription().formatted(Formatting.GRAY));
    }

    public MutableText getDescription() {
        return Text.translatable(Registries.ITEM.getId(this).toTranslationKey("item","desc"));
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {

        boolean hittingSmithery = false;

        if(miner.getWorld().getBlockEntity(pos) instanceof SmitheryBlockEntity smithery){
            hittingSmithery = true;

            if(smithery.currentRecipe!=null){
                ItemStack hammerStack = miner.getInventory().getMainHandStack();

                if(!miner.getItemCooldownManager().isCoolingDown(hammerStack.getItem())){

                    float skill = getPlayerSmithingSkill(smithery,miner);
                    smithery.onHitWithHammer(miner,hammerStack,skill);

                    if(!world.isClient)
                        damageAfterSmithingUse(hammerStack,miner);

                    // cooldown
                    miner.getItemCooldownManager().set(hammerStack.getItem(),getCooldown(miner));
                    miner.resetLastAttackedTicks();
                    // mining fatigue to slow down hit animation
                    miner.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE,getCooldown(miner),1,false,false,false));
                }
            }




        }

        return !miner.isCreative() && !hittingSmithery;
    }

    public int getHitProgress(PlayerEntity player){
        return progressPerHit;
    }

    public int getCooldown(PlayerEntity player){
        return cooldown;
    }

    public float getPlayerSmithingSkill(SmitheryBlockEntity smithery, PlayerEntity player){
        float res = 0;

        res+=skillBonus;

        res*=skillMultiplier;

        return res;
    }

    public void damageAfterSmithingUse(ItemStack stack, PlayerEntity player){
        stack.damage(2,player,p -> {});
    }
}
