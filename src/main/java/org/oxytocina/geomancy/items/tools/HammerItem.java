package org.oxytocina.geomancy.items.tools;

import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.blocks.blockEntities.IHammerable;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlock;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.enchantments.ModEnchantments;

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

        // Register left-click event for attacking blocks in the constructor of your special item
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (/*!world.isClient() && */hand == Hand.MAIN_HAND) {
                ItemStack stack = player.getStackInHand(hand);

                // Check if the player is holding specific item
                if (stack.getItem() instanceof HammerItem hammer) {
                    hammer.useOnBlock(new ItemUsageContext(world,player,hand,stack, new BlockHitResult(pos.toCenterPos(),direction,pos,false)));
                    return ActionResult.SUCCESS;
                }
            }
            return ActionResult.PASS;
        });
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(getDescription().formatted(Formatting.GRAY));
    }

    public MutableText getDescription() {
        return Text.translatable("item.geomancy.hammer.desc");
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {

        boolean hittingSmithery = false;
        if(miner.getWorld().getBlockEntity(pos) instanceof SmitheryBlockEntity){
            hittingSmithery = true;
        }
        return !miner.isCreative() && !hittingSmithery;

    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        boolean hittingSmithery = false;
        if(state.getBlock() instanceof SmitheryBlock){
            hittingSmithery = true;
        }
        return hittingSmithery?0:super.getMiningSpeedMultiplier(stack,state);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

        PlayerEntity miner = context.getPlayer();
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();

        if(world.getBlockEntity(pos) instanceof IHammerable hammerable){

            if(hammerable.isHammerable()){
                ItemStack hammerStack = context.getStack();

                if(!miner.getItemCooldownManager().isCoolingDown(hammerStack.getItem())){

                    float skill = getSmithingSkill(hammerable,miner,hammerStack);
                    hammerable.onHitWithHammer(miner,hammerStack,skill);

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

        return super.useOnBlock(context);
    }



    public int getHitProgress(@Nullable PlayerEntity player){
        return progressPerHit;
    }

    public int getCooldown(@Nullable PlayerEntity player){
        return cooldown;
    }

    public float getSmithingSkill(IHammerable hammerable, @Nullable PlayerEntity player, ItemStack hammerStack){
        float res = 0;

        res+=skillBonus;

        int skillfulLevel = ModEnchantments.getLevel(hammerStack, Geomancy.locate("skillful"));

        res+=skillfulLevel*5;

        res*=skillMultiplier;

        return res;
    }

    public void damageAfterSmithingUse(ItemStack stack, PlayerEntity player){
        stack.damage(2,player,p -> {});
    }
}
