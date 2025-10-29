package org.oxytocina.geomancy.spells.effectors;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellSignal;
import org.oxytocina.geomancy.util.ParticleUtil;
import org.oxytocina.geomancy.util.RecipeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.oxytocina.geomancy.spells.SpellBlocks.*;

public class TransmuteItemSpell {
    private static final List<TransmuteData> transmuteData = new ArrayList<>();

    public static SpellBlock get(){
        return SpellBlock.Builder.create("transmute_item")
                .inputs(SpellSignal.createUUID().named("item"))
                .func((comp,vars) -> {
                    var ent = vars.get("item").getEntity(comp.world());
                    if(!(ent instanceof ItemEntity ient)) return SpellBlockResult.empty();

                    // find recipe

                    // try the special ones first
                    for(var dat : transmuteData){
                        if(!dat.test(ient)) continue;
                        // calculate cost
                        float manaCost = 5f
                                +normalCastOffsetSoulCost(comp,ent.getPos())
                                +dat.cost*ient.getStack().getCount();
                        if(canAfford(comp,manaCost)){
                            dat.run(ient);
                            trySpendSoul(comp,manaCost);
                            spawnCastParticles(comp, ParticleUtil.ParticleData.createGenericCastSuccess(comp,comp.context.getOriginPos()));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,comp.context.getOriginPos()));
                        }
                        return SpellBlockResult.empty();
                    }

                    // go after recipes
                    var recipe = RecipeUtil.getConversionRecipeFor(ModRecipeTypes.TRANSMUTE,comp.world(),ient.getStack());
                    if(recipe!=null){
                        // calculate cost
                        float manaCost = 5f
                                +normalCastOffsetSoulCost(comp,ent.getPos())
                                +recipe.getCost()*ient.getStack().getCount();
                        if(canAfford(comp,manaCost)){
                            var resStack = recipe.craft(ImplementedInventory.of(DefaultedList.ofSize(1,ient.getStack())),null);
                            resStack.setCount(ient.getStack().getCount());
                            ient.setStack(resStack);
                            trySpendSoul(comp,manaCost);
                            spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastSuccess(comp,comp.context.getOriginPos()));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,ParticleUtil.ParticleData.createGenericCastBroke(comp,comp.context.getOriginPos()));
                        }
                        return SpellBlockResult.empty();
                    }

                    // no fitting transmutation recipe found
                    return SpellBlockResult.empty();
                })
                .category(SpellBlock.Category.Effector).build();
    }

    public static class TransmuteData{
        public final float cost;
        public final Function<ItemEntity,Boolean> predicate;
        public Consumer<ItemEntity> func;

        public TransmuteData(float cost, Item item){
            this.cost=cost;
            this.predicate=e->e.getStack().getItem()==item;
            func = t->{};
        }

        public TransmuteData(float cost, Function<ItemEntity,Boolean> predicate){
            this.cost=cost;
            this.predicate=predicate;
            func = t->{};
        }

        public TransmuteData func(Consumer<ItemEntity> func){this.func=func;return this;}
        public TransmuteData into(ItemConvertible item){this.func= s->s.setStack(new ItemStack(item,s.getStack().getCount()));return this;}
        public TransmuteData into(ItemStack item){
            this.func=s->{
                ItemStack res = item.copy();
                res.setCount(s.getStack().getCount());
                s.setStack(res);
            };return this;}

        public boolean test(ItemEntity ent){
            return predicate.apply(ent);
        }

        public void run(ItemEntity ent){
            func.accept(ent);
        }
    }
}
