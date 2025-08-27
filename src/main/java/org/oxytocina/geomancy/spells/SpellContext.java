package org.oxytocina.geomancy.spells;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.blocks.blockEntities.AutocasterBlock;
import org.oxytocina.geomancy.blocks.blockEntities.AutocasterBlockEntity;
import org.oxytocina.geomancy.entity.CasterDelegateEntity;
import org.oxytocina.geomancy.items.ISpellSelectorItem;
import org.oxytocina.geomancy.util.ManaUtil;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.concurrent.TimeUnit;

public class SpellContext {
    public LivingEntity caster;
    public AutocasterBlockEntity casterBlock;
    public CasterDelegateEntity delegate;
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
    public long startTime;
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
            CasterDelegateEntity delegate,
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
        this.delegate = delegate;
        this.casterItem=casterItem;
        this.spellStorage=spellStorage;
        this.availableSoul=availableSoul;
        this.stage = Stage.PreInit;
        this.soulCostMultiplier=soulCostMultiplier;
        this.soulConsumed=soulConsumed;
        this.soundBehavior=soundBehavior;
        this.startTime = System.nanoTime();

        sourceType = delegate!=null?SourceType.Delegate
                : caster!=null?SourceType.Caster
                : casterBlock!=null?SourceType.Block
        : SourceType.Caster;
    }

    public NbtCompound toNbt(){
        NbtCompound res = new NbtCompound();
        writeNbt(res);
        return res;
    }

    public void writeNbt(NbtCompound nbt){
        if(caster!=null) nbt.putUuid("caster",caster.getUuid());
        if(casterBlock!=null) nbt.put("casterBlock",NbtHelper.fromBlockPos(casterBlock.getPos()));
        if(delegate!=null) nbt.putUuid("delegate",delegate.getUuid());
        if(casterItem!=null){ NbtCompound temp = new NbtCompound(); casterItem.writeNbt(temp); nbt.put("casterItem", temp);}
        if(spellStorage!=null){ NbtCompound temp = new NbtCompound(); spellStorage.writeNbt(temp); nbt.put("spellStorage", temp);}
        nbt.putFloat("availableSoul",availableSoul);
        nbt.putFloat("soulConsumed",soulConsumed);
        nbt.putFloat("soulCostMultiplier",soulCostMultiplier);
        nbt.putString("stage", stage.name());
        nbt.putBoolean("debugging", debugging);
        nbt.putInt("depthLimit",depthLimit);
        nbt.putInt("baseDepth",baseDepth);
        nbt.putInt("highestRecordedDepth",highestRecordedDepth);
        nbt.putBoolean("depthLimitReached",depthLimitReached);
        nbt.putBoolean("couldntAffordSomething",couldntAffordSomething);
        if(grid!=null){ NbtCompound temp = new NbtCompound(); grid.writeNbt(temp); nbt.put("grid", temp);}
        nbt.putString("soundBehavior", soundBehavior.name());
        nbt.putString("sourceType", sourceType.name());

        if(isChild()){
            NbtCompound parentNbt = parentCall.toNbt();
            nbt.put("parent",parentNbt);
        }
        if(referenceCallingFrom!=null){ NbtCompound temp = new NbtCompound(); referenceCallingFrom.writeNbt(temp); nbt.put("referenceCallingFrom", temp);}
        if(referenceResult!=null){ NbtCompound temp = new NbtCompound(); referenceResult.writeNbt(temp); nbt.put("referenceResult", temp);}
        if(internalVars!=null){ NbtCompound temp = new NbtCompound(); internalVars.writeNbt(temp); nbt.put("internalVars", temp);}

    }

    public static SpellContext fromNbt(ServerWorld world, NbtCompound nbt){
        var casterItem = nbt.contains("casterItem") ? ItemStack.fromNbt(nbt.getCompound("casterItem")) : null;
        var spellStorage = nbt.contains("spellStorage") ? ItemStack.fromNbt(nbt.getCompound("spellStorage")) : null;
        var caster = nbt.contains("caster") ? world.getEntity(nbt.getUuid("caster")) : null;
        var casterBlock = nbt.contains("casterBlock") ? world.getBlockEntity(NbtHelper.toBlockPos(nbt.getCompound("casterBlock"))) : null;
        var delegate = nbt.contains("delegate") ? world.getEntity(nbt.getUuid("delegate")) : null;
        var res =new SpellContext(
                nbt.contains("grid")?new SpellGrid(casterItem,nbt.getCompound("grid")):null,
                (LivingEntity) caster,(AutocasterBlockEntity) casterBlock,(CasterDelegateEntity) delegate,casterItem,spellStorage,
                nbt.getFloat("availableSoul"),nbt.getFloat("soulCostMultiplier"),nbt.getFloat("soulConsumed"),Enum.valueOf(SoundBehavior.class,nbt.getString("soundBehavior"))
        );
        res.debugging=nbt.getBoolean("debugging");
        res.stage = Enum.valueOf(Stage.class,nbt.getString("stage"));
        // TODO im tired of this serialization bs
        return res;
    }

    /// to be used SOLELY for stringifying spell signals!!
    public static SpellContext ofWorld(@Nullable World world, @Nullable PlayerEntity caster) {
        var res = new SpellContext(null,caster,null,null,null,null,0,0,0,null);
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
                return ManaUtil.tryConsumeMana(caster,amount,this);
            }

            case Block:{
                availableSoul -= amount;
                return ManaUtil.tryConsumeMana(casterBlock,amount,this);
            }

            case Delegate:
            default:{
                if(caster!=null){
                    if(caster instanceof PlayerEntity player && player.isCreative())
                        return true;
                    availableSoul -= amount;
                    return ManaUtil.tryConsumeMana(caster,amount,this);
                }
                if(casterBlock!=null){
                    availableSoul -= amount;
                    return ManaUtil.tryConsumeMana(casterBlock,amount,this);
                }
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
                if(caster!=null){
                    if(caster instanceof PlayerEntity player){
                        availableSoul = ManaUtil.getMana(player);
                        if(player.isCreative()) return true;
                        return availableSoul>=amount;
                    }

                    // TODO: livingentity mana
                    return true;
                }
                if(casterBlock!=null){
                    availableSoul = ManaUtil.getMana(casterBlock.getWorld(),casterBlock);
                    return availableSoul>=amount;
                }
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
                if(caster instanceof PlayerEntity player){
                    availableSoul = ManaUtil.getMana(player);
                    return;
                }
                availableSoul = ManaUtil.getMana(casterBlock.getWorld(),casterBlock);
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
        SpellContext res = new SpellContext(this.grid,caster,casterBlock,delegate,casterItem,spellStorage,availableSoul,soulCostMultiplier,soulConsumed,soundBehavior);
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
            case Delegate -> casterBlock!=null?casterBlock.getWorld():caster!=null?caster.getWorld():worldOverride;
            default -> worldOverride;
        };
    }

    public Vec3d getOriginPos() {
        return switch(sourceType)
        {
            case Caster -> caster.getPos();
            case Block -> casterBlock.getPos().toCenterPos();
            case Delegate -> delegate.getPos();
            default-> null;
        };
    }

    public BlockPos getOriginBlockPos() {
        return Toolbox.posToBlockPos(getOriginPos());
    }

    public ISpellSelectorItem getSpellSelector() {
        return (ISpellSelectorItem)casterItem.getItem();
    }

    public Inventory getInventory() {
        return switch(sourceType){
            case Caster -> (caster instanceof PlayerEntity pe)?pe.getInventory():null;
            case Block -> casterBlock;
            case Delegate -> (caster instanceof PlayerEntity pe)?pe.getInventory():casterBlock;
            default->null;
        };
    }

    public SoundCategory getSoundCategory() {
        if(caster!=null) return SoundCategory.PLAYERS;
        return SoundCategory.BLOCKS;
    }

    public int getExecutionTimeMS(){
        return (int)TimeUnit.NANOSECONDS.toMillis(System.nanoTime()-startTime);
    }

    public final int TIMEOUT_MS = 1000;
    public boolean timedOut(){
        return getExecutionTimeMS() > TIMEOUT_MS;
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
