package org.oxytocina.geomancy.spells;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.oxytocina.geomancy.util.ManaUtil;

public class SpellContext {
    public LivingEntity caster;
    public ItemStack casterItem;
    public ItemStack spellStorage;
    public float availableSoul;
    public Stage stage;
    public boolean debugging = false;
    public int depthLimit = 100;
    public boolean depthLimitReached = false;
    public boolean couldntAffordSomething = false;

    // reference calls
    public SpellContext parentCall;
    public SpellComponent referenceCallingFrom;
    public SpellBlockResult referenceResult;
    public SpellBlockArgs internalVars;

    public SpellContext(LivingEntity caster, ItemStack casterItem, ItemStack spellStorage, float availableSoul){
        this.caster = caster;
        this.casterItem=casterItem;
        this.spellStorage=spellStorage;
        this.availableSoul=availableSoul;
        this.stage = Stage.PreInit;
    }

    public boolean tryConsumeSoul(float amount){
        if(!canAfford(amount)) { couldntAffordSomething = true; return false; }

        availableSoul -= amount;
        if(caster instanceof PlayerEntity player){
            ManaUtil.setMana(player,availableSoul);
            return true;
        }
        // TODO: livingentity mana
        return true;
    }

    public boolean canAfford(float amount){
        if(caster instanceof PlayerEntity player){
            availableSoul = ManaUtil.getMana(player);
            return availableSoul>=amount;
        }

        // TODO: livingentity mana
        return true;
    }

    public void refreshAvailableSoul(){
        if(caster instanceof PlayerEntity player){
            availableSoul = ManaUtil.getMana(player);
            return;
        }

        // TODO: livingentity mana
        return;
    }

    public SpellContext createReferenced(SpellComponent comp){
        SpellContext res = new SpellContext(caster,casterItem,spellStorage,availableSoul);
        res.parentCall = this;
        res.referenceCallingFrom = comp;
        res.internalVars=new SpellBlockArgs();
        return res;
    }

    public boolean isChild(){
        return parentCall!=null;
    }

    public SpellSignal getParentVar(String varName){
        if(!isChild()) return null;
        return internalVars.get(varName) ;
    }

    public static enum Stage{
        PreInit,
        Run
    }
}
