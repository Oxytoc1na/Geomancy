package org.oxytocina.geomancy.spells;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.util.ManaUtil;

public class SpellContext {
    public LivingEntity caster;
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

    // reference calls
    public SpellContext parentCall;
    public SpellComponent referenceCallingFrom;
    public SpellBlockResult referenceResult = SpellBlockResult.empty();
    public SpellBlockArgs internalVars;

    private World worldOverride = null;

    public SpellContext(
            SpellGrid grid,
            LivingEntity caster,
            ItemStack casterItem,
            ItemStack spellStorage,
            float availableSoul,
            float soulCostMultiplier,
            float soulConsumed,
            SoundBehavior soundBehavior
    ){
        this.grid=grid;
        this.caster = caster;
        this.casterItem=casterItem;
        this.spellStorage=spellStorage;
        this.availableSoul=availableSoul;
        this.stage = Stage.PreInit;
        this.soulCostMultiplier=soulCostMultiplier;
        this.soulConsumed=soulConsumed;
        this.soundBehavior=soundBehavior;
    }

    /// to be used SOLELY for stringifying spell signals!!
    public static SpellContext ofWorld(@Nullable World world) {
        var res = new SpellContext(null,null,null,null,0,0,0,null);
        res.worldOverride = world;
        return res;
    }

    public boolean tryConsumeSoul(float amount){
        if(isChild()) return parentCall.tryConsumeSoul(amount);

        amount*=soulCostMultiplier;
        if(!canAfford(amount)) { couldntAffordSomething = true; return false; }

        soulConsumed += amount;

        if(caster instanceof PlayerEntity player){
            if(player.isCreative())
            {
                return true;
            }
        }

        availableSoul -= amount;
        return ManaUtil.tryConsumeMana(caster,amount);
    }

    public boolean canAfford(float amount){
        if(isChild()) return parentCall.canAfford(amount);

        if(caster instanceof PlayerEntity player){
            availableSoul = ManaUtil.getMana(player);
            if(player.isCreative()) return true;
            return availableSoul>=amount;
        }

        // TODO: livingentity mana
        return true;
    }

    public void refreshAvailableSoul(){
        if(isChild()) {parentCall.getCasterMaxSoul(); return;}
        if(caster instanceof PlayerEntity player){
            availableSoul = ManaUtil.getMana(player);
            return;
        }

        // TODO: livingentity mana
        return;
    }

    public float getCasterMaxSoul(){
        if(isChild()) return parentCall.getCasterMaxSoul();
        if(caster instanceof PlayerEntity pe){
            return ManaUtil.getMaxMana(pe);
        }

        // TODO: livingentity mana
        return 100;
    }

    public SpellContext createReferenced(SpellComponent comp){
        SpellContext res = new SpellContext(this.grid,caster,casterItem,spellStorage,availableSoul,soulCostMultiplier,soulConsumed,soundBehavior);
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
        return caster!=null?caster.getWorld():worldOverride;
    }

    public static enum Stage{
        PreInit,
        Run
    }

    public enum SoundBehavior{
        Full,
        Reduced,
        Silent
    }
}
