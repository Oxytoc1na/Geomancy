package org.oxytocina.geomancy.spells;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;

public class SpellBlocks {
    public static HashMap<Identifier, SpellBlock> functions = new HashMap<>();

    // flow control
    public static final SpellBlock CONVEYOR;
    public static final SpellBlock GATE;
    public static final SpellBlock FOR;

    // getters
    public static final SpellBlock CONST_NUM;
    public static final SpellBlock CONST_TEXT;
    public static final SpellBlock CONST_BOOLEAN;
    public static final SpellBlock ENTITY_CASTER;

    // arithmetic
    public static final SpellBlock SUM;
    public static final SpellBlock VECTOR_SPLIT;
    public static final SpellBlock VECTOR_BUILD;
    public static final SpellBlock VECTOR_ENTITYPOS;
    public static final SpellBlock VECTOR_ENTITYEYEPOS;
    public static final SpellBlock VECTOR_ENTITYDIR;

    // effectors
    public static final SpellBlock PRINT;

    static{

        // flow control
        {
            CONVEYOR = register(SpellBlock.create("conveyor",new SpellSignal[]{
                    SpellSignal.createAny().named("val")},SpellSignal.createAny().named("val"),SpellBlocks::mirrorInToOut, ()->freeformSideConfigs("val")));

            GATE = register(SpellBlock.create("gate",
                    new SpellSignal[]{
                            SpellSignal.createAny().named("val"),
                            SpellSignal.createBoolean(false).named("gate")},
                    new SpellSignal[]{
                            SpellSignal.createAny().named("val"),
                            SpellSignal.createBoolean(false).named("gate")},
                    new SpellBlock.Parameter[]{}
                    ,(component, args) ->
                    {
                        SpellBlockResult res = new SpellBlockResult();
                        if(!args.vars.get("gate").getBooleanValue()) return res;
                        return args.toRes();
                    },
                    ()->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for (int i = 0; i < 2; i++) {
                            res[i] = SpellComponent.SideConfig.create(
                                    new SpellComponent.SideConfig.Mode[]{
                                            SpellComponent.SideConfig.Mode.Input,
                                            SpellComponent.SideConfig.Mode.Blocked
                                    }
                                    ,SpellComponent.directions[i]);
                        }
                        res[0].varName="val";
                        res[1].varName="gate";

                        for (int i = 2; i < 6; i++) {
                            res[i] = SpellComponent.SideConfig.create(
                                    new SpellComponent.SideConfig.Mode[]{
                                            SpellComponent.SideConfig.Mode.Output,
                                            SpellComponent.SideConfig.Mode.Blocked
                                    }
                                    ,SpellComponent.directions[i]);
                        }

                        res[2].varName="val";
                        res[3].varName="val";
                        res[4].varName="gate";
                        res[5].varName="gate";

                        return res;
                    }
            ));

            FOR = register(SpellBlock.create("for",
                    new SpellSignal[]{
                            SpellSignal.createNumber(1).named("count"),
                            SpellSignal.createAny().named("signal")},
                    new SpellSignal[]{
                            SpellSignal.createNumber(0).named("i"),
                            SpellSignal.createAny().named("signal")},
                    new SpellBlock.Parameter[]{}
                    ,(component, args) ->
                    {
                        SpellBlockResult res = new SpellBlockResult();
                        res=args.toRes();
                        res.iterations=args.getInt("count");
                        return res;
                    },
                    ()->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for (int i = 0; i < 2; i++) {
                            res[i] = SpellComponent.SideConfig.create(
                                    new SpellComponent.SideConfig.Mode[]{
                                            SpellComponent.SideConfig.Mode.Input,
                                            SpellComponent.SideConfig.Mode.Blocked
                                    }
                                    ,SpellComponent.directions[i]);
                        }
                        res[0].varName="val";
                        res[1].varName="gate";

                        for (int i = 2; i < 6; i++) {
                            res[i] = SpellComponent.SideConfig.create(
                                    new SpellComponent.SideConfig.Mode[]{
                                            SpellComponent.SideConfig.Mode.Output,
                                            SpellComponent.SideConfig.Mode.Blocked
                                    }
                                    ,SpellComponent.directions[i]);
                        }

                        res[2].varName="val";
                        res[3].varName="val";
                        res[4].varName="gate";
                        res[5].varName="gate";

                        return res;
                    }
            ));
        }

        // getters
        {
            CONST_NUM = register(SpellBlock.create("constant_number",new SpellSignal[]{},
                    new SpellBlock.Parameter[]{SpellBlock.Parameter.createNumber("val",1,-1000,1000)},SpellBlocks::mirrorInToOut,()->SpellBlock.sidesOutput("val")));

            CONST_TEXT = register(SpellBlock.create("constant_text",new SpellSignal[]{},
                    new SpellBlock.Parameter[]{SpellBlock.Parameter.createText("val","")}, SpellBlocks::mirrorInToOut,()->SpellBlock.sidesOutput("val")));

            CONST_BOOLEAN = register(SpellBlock.create("constant_boolean",new SpellSignal[]{},
                    new SpellBlock.Parameter[]{SpellBlock.Parameter.createBoolean("val",true)}, SpellBlocks::mirrorInToOut,()->SpellBlock.sidesOutput("val")));



            ENTITY_CASTER = register(SpellBlock.create("entity_caster",new SpellSignal[]{},new SpellBlock.Parameter[]{},
                    (component, stringSpellSignalHashMap) -> {
                        HashMap<String,SpellSignal> res = new HashMap<>();
                        if(component.casterEntity!=null) res.put("val",SpellSignal.createUUID(component.casterEntity.getUuid()).named("val"));return res;
                    },
                    ()->SpellBlock.sidesOutput("val")));
        }

        // arithmetic
        {
            SUM = register(SpellBlock.create("sum",new SpellSignal[]{
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

            VECTOR_ENTITYPOS = register(SpellBlock.create("vector_entitypos",
                    new SpellSignal[]{SpellSignal.createUUID(null).named("entity")},
                    new SpellSignal[]{SpellSignal.createVector(null).named("vector")},
                    new SpellBlock.Parameter[]{},
                    ((component, vars) -> {
                        HashMap<String,SpellSignal> res = new HashMap<>();
                        if(!(vars.get("entity").getEntity(component.world) instanceof LivingEntity entity)) return res;
                        res.put("vector",SpellSignal.createVector(entity.getPos()).named("vector"));
                        return res;
                    }),
                    (() -> {
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for (int i = 0; i < 3; i++) {
                            res[i] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Input,SpellComponent.directions[i]);
                            res[i].varName="entity";
                        }
                        for (int i = 3; i < 6; i++) {
                            res[i] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Output,SpellComponent.directions[i]);
                            res[i].varName="vector";
                        }
                        return res;
                    })
                    ));

            VECTOR_ENTITYEYEPOS = register(SpellBlock.create("vector_entityeyepos",
                    new SpellSignal[]{SpellSignal.createUUID(null).named("entity")},
                    new SpellSignal[]{SpellSignal.createVector(null).named("vector")},
                    new SpellBlock.Parameter[]{},
                    ((component, vars) -> {
                        HashMap<String,SpellSignal> res = new HashMap<>();
                        if(!(vars.get("entity").getEntity(component.world) instanceof LivingEntity entity)) return res;
                        res.put("vector",SpellSignal.createVector(entity.getEyePos()).named("vector"));
                        return res;
                    }),
                    (() -> {
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for (int i = 0; i < 3; i++) {
                            res[i] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Input,SpellComponent.directions[i]);
                            res[i].varName="entity";
                        }
                        for (int i = 3; i < 6; i++) {
                            res[i] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Output,SpellComponent.directions[i]);
                            res[i].varName="vector";
                        }
                        return res;
                    })
            ));

            VECTOR_ENTITYDIR = register(SpellBlock.create("vector_entitydir",
                    new SpellSignal[]{SpellSignal.createUUID(null).named("entity")},
                    new SpellSignal[]{SpellSignal.createVector(null).named("vector")},
                    new SpellBlock.Parameter[]{},
                    ((component, vars) -> {
                        HashMap<String,SpellSignal> res = new HashMap<>();
                        if(!(vars.get("entity").getEntity(component.world) instanceof LivingEntity entity)) return res;
                        res.put("vector",SpellSignal.createVector(entity.getRotationVector()).named("vector"));
                        return res;
                    }),
                    (() -> {
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for (int i = 0; i < 3; i++) {
                            res[i] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Input,SpellComponent.directions[i]);
                            res[i].varName="entity";
                        }
                        for (int i = 3; i < 6; i++) {
                            res[i] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Output,SpellComponent.directions[i]);
                            res[i].varName="vector";
                        }
                        return res;
                    })
            ));

            VECTOR_SPLIT = register(SpellBlock.create("vector_split",
                    new SpellSignal[]{SpellSignal.createVector(null).named("vector")},
                    new SpellSignal[]{
                            SpellSignal.createVector(null).named("x"),
                            SpellSignal.createVector(null).named("y"),
                            SpellSignal.createVector(null).named("z"),
                    },
                    new SpellBlock.Parameter[]{},
                    ((component, vars) -> {
                        HashMap<String,SpellSignal> res = new HashMap<>();
                        Vec3d vec = vars.get("vector").getVectorValue();
                        res.put("x",SpellSignal.createNumber(vec.x).named("x"));
                        res.put("y",SpellSignal.createNumber(vec.y).named("y"));
                        res.put("z",SpellSignal.createNumber(vec.z).named("z"));
                        return res;
                    }),
                    (() -> {
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for (int i = 0; i < 3; i++) {
                            res[i] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Input,SpellComponent.directions[i]).named("vector");
                        }
                        res[3] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Output,SpellComponent.directions[3]).named("x");
                        res[4] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Output,SpellComponent.directions[4]).named("y");
                        res[5] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Output,SpellComponent.directions[5]).named("z");
                        return res;
                    })
            ));

            VECTOR_BUILD = register(SpellBlock.create("vector_build",
                    new SpellSignal[]{
                            SpellSignal.createVector(null).named("x"),
                            SpellSignal.createVector(null).named("y"),
                            SpellSignal.createVector(null).named("z"),
                    },
                    new SpellSignal[]{SpellSignal.createVector(null).named("vector")},
                    new SpellBlock.Parameter[]{},
                    ((component, vars) -> {
                        HashMap<String,SpellSignal> res = new HashMap<>();
                        Vec3d vec = new Vec3d(
                                vars.get("x").getNumberValue(),
                                vars.get("y").getNumberValue(),
                                vars.get("z").getNumberValue()
                        );
                        res.put("vector",SpellSignal.createVector(vec).named("vector"));
                        return res;
                    }),
                    (() -> {
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for (int i = 0; i < 3; i++) {
                            res[i] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Input,SpellComponent.directions[i]).named("vector");
                        }
                        res[3] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Output,SpellComponent.directions[3]).named("x");
                        res[4] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Output,SpellComponent.directions[4]).named("y");
                        res[5] = SpellComponent.SideConfig.createSingle(SpellComponent.SideConfig.Mode.Output,SpellComponent.directions[5]).named("z");
                        return res;
                    })
            ));
        }

        // effectors
        {
            PRINT = register(SpellBlock.create("print",new SpellSignal[]{
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
        }
    }


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

    public static SpellBlockResult mirrorInToOut(SpellComponent comp, SpellBlockArgs vars){
        return new SpellBlockResult(vars);
    }

    public static SpellComponent.SideConfig[] freeformSideConfigs(String var){
        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
        for (int i = 0; i < 6; i++) {
            res[i] = SpellComponent.SideConfig.create(
                    new SpellComponent.SideConfig.Mode[]{
                            SpellComponent.SideConfig.Mode.Blocked,
                            SpellComponent.SideConfig.Mode.Input,
                            SpellComponent.SideConfig.Mode.Output
                    }
                    ,SpellComponent.directions[i]);
            res[i].varName=var;
        }
        return res;
    }
}
