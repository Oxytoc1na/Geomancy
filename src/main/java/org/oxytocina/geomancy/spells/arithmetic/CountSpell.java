package org.oxytocina.geomancy.spells.arithmetic;

import net.minecraft.block.entity.LootableContainerBlockEntity;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellSignal;

public class CountSpell {
    public static SpellBlock get() {
        return SpellBlock.Builder.create("count")
                .inputs(SpellSignal.createAny().named("signal"))
                .outputs(SpellSignal.createNumber().named("count"))
                .func((comp,vars)->{
                    var sig = vars.get("signal");
                    var res = SpellBlockResult.empty();
                    switch(sig.type){
                        case List : res.add("count",sig.getListValueOrEmpty().size()); break;
                        case Text: res.add("count",sig.getTextValue().length()); break;
                        case Boolean: res.add("count",sig.getBooleanValue() ? 1 : 0); break;
                        case Vector:
                            // check inventory amount of block entity at position
                            var blockPos = vars.getBlockPos("signal");
                            if(comp.world().isPosLoaded(blockPos.getX(),blockPos.getZ()) && comp.world().getBlockEntity(blockPos) instanceof LootableContainerBlockEntity container){
                                int count = 0;
                                for (int i = 0; i < container.size(); i++) {
                                    var stack = container.getStack(i);
                                    if(!stack.isEmpty()) count+=stack.getCount();
                                }
                                res.add("count",count);
                                break;
                            }
                            // fallback: count non-0 components of the vector
                            //int i = 0; var vec = sig.getVectorValue(); if(vec.x!=0)i++; if(vec.y!=0)i++; if(vec.z!=0)i++;  res.add("count",i); break;
                            break;
                    }
                    return res;
                })
                .category(SpellBlock.Category.Arithmetic).build();
    }
}
