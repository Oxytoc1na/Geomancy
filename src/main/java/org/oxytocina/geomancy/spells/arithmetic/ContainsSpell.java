package org.oxytocina.geomancy.spells.arithmetic;

import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.registry.Registries;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlockResult;
import org.oxytocina.geomancy.spells.SpellSignal;

public class ContainsSpell {
    public static SpellBlock get() {
        return SpellBlock.Builder.create("contains")
                .inputs(SpellSignal.createAny().named("what"),SpellSignal.createAny().named("within"))
                .outputs(SpellSignal.createBoolean().named("contained"))
                .func((comp,vars)->{
                    var what = vars.get("what");
                    var within = vars.get("within");
                    var res = SpellBlockResult.empty();
                    switch(within.type){
                        case List :
                            boolean contained = false;
                            for(var sig : within.getListValueOrEmpty())
                                if(sig.softEquals(what)){contained=true; break;}
                            res.add("contained",contained);break;
                        case Text: res.add("contained",within.getTextValue().contains(what.getTextValue()));
                        case Vector:
                            // check inventory
                            var blockPos = vars.getBlockPos("signal");
                            if(comp.world().isPosLoaded(blockPos.getX(),blockPos.getZ()) && comp.world().getBlockEntity(blockPos) instanceof LootableContainerBlockEntity container){
                                switch(what.type){
                                    case Number :
                                        // check item count
                                        int count = 0;
                                        for (int i = 0; i < container.size(); i++) {
                                            var stack = container.getStack(i);
                                            if(!stack.isEmpty()) count+=stack.getCount();
                                            if(count>=what.getNumberValue()){
                                                res.add("contained",true);
                                                return res;
                                            }
                                        }
                                        res.add("contained",false);
                                        break;
                                    case Text:
                                        // check for custom item name or identifier
                                        boolean c = false;
                                        for (int i = 0; i < container.size(); i++) {
                                            var stack = container.getStack(i);
                                            if(stack.isEmpty()) continue;
                                            if(
                                                    stack.getName().getString().equals(what.getTextValue())
                                                    || Registries.ITEM.getId(stack.getItem()).toString().equals(what.getTextValue())
                                            )
                                            {
                                                c=true;
                                                break;
                                            }
                                        }
                                        res.add("contained",c);
                                        break;
                                }
                            }
                            break;
                    }
                    return res;
                })
                .category(SpellBlock.Category.Arithmetic).build();
    }
}
