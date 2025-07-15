package org.oxytocina.geomancy.spells;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;

public class SpellBlocks {
    public static HashMap<Identifier, SpellBlock> functions = new HashMap<>();

    public static SpellBlock SUM = register(SpellBlock.create("sum",new SpellSignal[]{
            SpellSignal.createNumber(0).named("a"),
            SpellSignal.createNumber(0).named("b"),
    }, SpellSignal.createNumber(0).named("res"),
            ((comp,vars) -> {
                HashMap<String, SpellSignal> res = new HashMap<>();
                res.put("res", SpellSignal.createNumber(
                        vars.get("a").getNumberValue() +
                        vars.get("b").getNumberValue()
                ));
                return res;
            }),
            ()->{
                SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                for (int i = 0; i < 5; i++) {
                    res[i] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Input,SpellComponent.directions[i]);
                }
                res[5] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Output,SpellComponent.directions[5]);
                return res;
            }
    ));

    public static SpellBlock PRINT = register(SpellBlock.create("print",new SpellSignal[]{
                    SpellSignal.createAny().named("a"),},
            ((comp,vars) -> {
                if(comp.casterEntity != null)
                {
                    World world = comp.casterEntity.getWorld();
                    if(world instanceof ServerWorld serverWorld){
                        if(comp.casterEntity instanceof ServerPlayerEntity player)
                            player.sendMessage(Text.literal(vars.get("a").toString()));
                    }
                    else if(world instanceof ClientWorld clientWorld){
                        if(comp.casterEntity instanceof ClientPlayerEntity player)
                            player.sendMessage(Text.literal(vars.get("a").toString()));
                    }
                }

                return new HashMap<>();
            })
            ,()->SpellBlock.sidesInput("a")
    ));

    public static SpellBlock CONST_NUM = register(SpellBlock.create("constant_number",new SpellSignal[]{},
            new SpellBlock.Parameter[]{
                    SpellBlock.Parameter.createNumber("val",1,-1000,1000)
            },
            ((comp,vars) -> {
                HashMap<String,SpellSignal> res = new HashMap<>();
                res.put("val",vars.get("val"));
                return res;
            })
            ,()->SpellBlock.sidesOutput("val")
    ));

    public static void register(){

    }

    public static SpellBlock register(SpellBlock function){
        functions.put(function.identifier,function);
        return function;
    }

    public static SpellBlock get(String func) {
        return get(Identifier.tryParse(func));
    }

    public static SpellBlock get(Identifier id){
        if(functions.containsKey(id)) return functions.get(id);
        return PRINT;
    }
}
