package org.oxytocina.geomancy.spells;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.blocks.blockEntities.AutocasterBlock;
import org.oxytocina.geomancy.blocks.blockEntities.AutocasterBlockEntity;
import org.oxytocina.geomancy.util.ManaUtil;
import org.oxytocina.geomancy.util.Toolbox;

public class SpellContext {
    public LivingEntity caster;
    public AutocasterBlockEntity casterBlock;
    public ItemStack casterItem;
    public ItemStack spellStorage;
    protected float availableSoul;
    public float soulConsumed = 0;
    public float soulCostMultiplier = 1;
    public Stage stage;
    public boolean debugging = false;
    public int depthLimit = 100;
    public int baseDepth = 0;
    public int highestRecordedDepth = 0;
    public boolean depthLimitReached = false;
    public boolean couldntAffordSomething = false;
    public SpellGrid grid;
    public SoundBehavior soundBehavior;

    public SourceType sourceType;

    // reference calls
    public SpellContext parentCall;
    public SpellComponent referenceCallingFrom;
    public SpellBlockResult referenceResult = SpellBlockResult.empty();
    public SpellBlockArgs internalVars;

    private World worldOverride = null;

    public SpellContext(
            SpellGrid grid,
            LivingEntity caster,
            AutocasterBlockEntity casterBlock,
            ItemStack casterItem,
            ItemStack spellStorage,
            float availableSoul,
            float soulCostMultiplier,
            float soulConsumed,
            SoundBehavior soundBehavior
    ){
        this.grid=grid;
        this.caster = caster;
        this.casterBlock = casterBlock;
        this.casterItem=casterItem;
        this.spellStorage=spellStorage;
        this.availableSoul=availableSoul;
        this.stage = Stage.PreInit;
        this.soulCostMultiplier=soulCostMultiplier;
        this.soulConsumed=soulConsumed;
        this.soundBehavior=soundBehavior;

        sourceType = caster!=null?SourceType.Caster
        : casterBlock!=null?SourceType.Block
        : SourceType.Delegate;
    }

    /// to be used SOLELY for stringifying spell signals!!
    public static SpellContext ofWorld(@Nullable World world) {
        var res = new SpellContext(null,null,null,null,null,0,0,0,null);
        res.worldOverride = world;
        return res;
    }

    public boolean tryConsumeSoul(float amount){
        if(isChild()) return parentCall.tryConsumeSoul(amount);

        amount *= soulCostMultiplier;
        if(!canAfford(amount)) { couldntAffordSomething = true; return false; }
        soulConsumed += amount;

        switch (sourceType){
            case Caster :{
                if(caster instanceof PlayerEntity player && player.isCreative())
                    return true;

                availableSoul -= amount;
                return ManaUtil.tryConsumeMana(caster,amount);
            }

            case Block:{
                availableSoul -= amount;
                return ManaUtil.tryConsumeMana(casterBlock,amount);
            }

            case Delegate:
            default:{
                break;
            }
        }

        return true;
    }

    public boolean canAfford(float amount){
        if(isChild()) return parentCall.canAfford(amount);

        switch (sourceType){
            case Caster :{
                if(caster instanceof PlayerEntity player){
                    availableSoul = ManaUtil.getMana(player);
                    if(player.isCreative()) return true;
                    return availableSoul>=amount;
                }

                // TODO: livingentity mana
                return true;
            }

            case Block:{
                availableSoul = ManaUtil.getMana(casterBlock.getWorld(),casterBlock);
                return availableSoul>=amount;
            }

            case Delegate:
            default:{
                break;
            }
        }

        return true;
    }

    public void refreshAvailableSoul(){
        if(isChild()) {parentCall.getCasterMaxSoul(); return;}

        switch (sourceType){
            case Caster :{
                if(caster instanceof PlayerEntity player){
                    availableSoul = ManaUtil.getMana(player);
                    return;
                }
                // TODO: livingentity mana
                return;
            }

            case Block:{
                availableSoul = ManaUtil.getMana(casterBlock.getWorld(),casterBlock);
            }

            case Delegate:
            default:{
                break;
            }
        }


    }

    public float getCasterMaxSoul(){
        if(isChild()) return parentCall.getCasterMaxSoul();

        switch (sourceType){
            case Caster :{
                if(caster instanceof PlayerEntity pe){
                    return ManaUtil.getMaxMana(pe);
                }
                // TODO: livingentity mana
                return 100;
            }

            case Block:{
                return ManaUtil.getMaxMana(casterBlock.getWorld(),casterBlock);
            }

            case Delegate:
            default:{
                break;
            }
        }

        return 100;
    }

    public SpellContext createReferenced(SpellComponent comp){
        SpellContext res = new SpellContext(this.grid,caster,casterBlock,casterItem,spellStorage,availableSoul,soulCostMultiplier,soulConsumed,soundBehavior);
        res.parentCall = this;
        res.referenceCallingFrom = comp;
        res.internalVars=new SpellBlockArgs();
        res.baseDepth = highestRecordedDepth;
        return res;
    }

    public boolean isChild(){
        return parentCall!=null;
    }

    public SpellSignal getParentVar(String varName){
        if(internalVars==null||!internalVars.has(varName)) return null;
        return internalVars.get(varName) ;
    }

    public World getWorld() {
        return switch(sourceType)
        {
            case Caster -> caster!=null?caster.getWorld():worldOverride;
            case Block -> casterBlock!=null?casterBlock.getWorld():worldOverride;
            default -> worldOverride;
        };
    }

    public Vec3d getOriginPos() {
        return switch(sourceType)
        {
            case Caster -> caster.getPos();
            case Block -> casterBlock.getPos().toCenterPos();
            default-> null;
        };
    }

    public BlockPos getOriginBlockPos() {
        return Toolbox.posToBlockPos(getOriginPos());
    }

    public enum SourceType{
        Caster,
        Block,
        Delegate
    }

    public enum Stage{
        PreInit,
        Run
    }

    public enum SoundBehavior{
        Full,
        Reduced,
        Silent
    }
}
