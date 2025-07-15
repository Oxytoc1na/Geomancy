package org.oxytocina.geomancy.spells;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
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
    public static final SpellBlock FIREBALL;

    static{

        // flow control
        SpellBlock.Category cat = SpellBlock.Category.FlowControl;
        {
            CONVEYOR = register(SpellBlock.create("conveyor",new SpellSignal[]{
                    SpellSignal.createAny().named("val")},SpellSignal.createAny().named("val"),SpellBlocks::mirrorInToOut, ()->freeformSideConfigs("val")).category(cat));

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
            ).category(cat));

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
                        res.add(args.get("signal"));
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
            ).category(cat));
        }

        // getters
        cat = SpellBlock.Category.Provider;
        {
            CONST_NUM = register(SpellBlock.create("constant_number",new SpellSignal[]{},
                    new SpellSignal[]{SpellSignal.createNumber(0).named("val")},
                    new SpellBlock.Parameter[]{SpellBlock.Parameter.createNumber("val",1,-1000,1000)}
                    ,SpellBlocks::mirrorInToOut,()->SpellBlock.sidesOutput("val")).category(cat));

            CONST_TEXT = register(SpellBlock.create("constant_text",new SpellSignal[]{},
                    new SpellSignal[]{SpellSignal.createText("").named("val")},
                    new SpellBlock.Parameter[]{SpellBlock.Parameter.createText("val","")}, SpellBlocks::mirrorInToOut,()->SpellBlock.sidesOutput("val")).category(cat));

            CONST_BOOLEAN = register(SpellBlock.create("constant_boolean",new SpellSignal[]{},
                    new SpellSignal[]{SpellSignal.createBoolean(true).named("val")},
                    new SpellBlock.Parameter[]{SpellBlock.Parameter.createBoolean("val",true)}, SpellBlocks::mirrorInToOut,()->SpellBlock.sidesOutput("val")).category(cat));



            ENTITY_CASTER = register(SpellBlock.create("entity_caster",new SpellSignal[]{},
                    new SpellSignal[]{SpellSignal.createUUID(null).named("caster")},
                    new SpellBlock.Parameter[]{},
                    (component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        if(component.casterEntity!=null) res.add("caster",component.casterEntity.getUuid());return res;
                    },
                    ()->SpellBlock.sidesOutput("caster")).category(cat));
        }

        // arithmetic
        cat = SpellBlock.Category.Arithmetic;
        {
            SUM = register(SpellBlock.create("sum",new SpellSignal[]{
                            SpellSignal.createNumber(0).named("a"),
                            SpellSignal.createNumber(0).named("b"),
                    }, SpellSignal.createNumber(0).named("sum"),
                    ((comp,vars) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        res.add(SpellSignal.createNumber(
                                vars.getNumber("a") +
                                        vars.getNumber("b")
                        ).named("sum"));
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
            ).category(cat));

            VECTOR_ENTITYPOS = register(SpellBlock.create("vector_entitypos",
                    new SpellSignal[]{SpellSignal.createUUID(null).named("entity")},
                    new SpellSignal[]{SpellSignal.createVector(null).named("position")},
                    new SpellBlock.Parameter[]{},
                    ((component, vars) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        if(!(vars.get("entity").getEntity(component.world) instanceof LivingEntity entity)) return res;
                        res.add("position",entity.getPos());
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
                            res[i].varName="position";
                        }
                        return res;
                    })
                    ).category(cat));

            VECTOR_ENTITYEYEPOS = register(SpellBlock.create("vector_entityeyepos",
                    new SpellSignal[]{SpellSignal.createUUID(null).named("entity")},
                    new SpellSignal[]{SpellSignal.createVector(null).named("eye position")},
                    new SpellBlock.Parameter[]{},
                    ((component, vars) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        if(!(vars.get("entity").getEntity(component.world) instanceof LivingEntity entity)) return res;
                        res.add("eye position",entity.getEyePos());
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
                            res[i].varName="eye position";
                        }
                        return res;
                    })
            ).category(cat));

            VECTOR_ENTITYDIR = register(SpellBlock.create("vector_entitydir",
                    new SpellSignal[]{SpellSignal.createUUID(null).named("entity")},
                    new SpellSignal[]{SpellSignal.createVector(null).named("direction")},
                    new SpellBlock.Parameter[]{},
                    ((component, vars) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        if(!(vars.get("entity").getEntity(component.world) instanceof LivingEntity entity)) return res;
                        res.add("direction",entity.getRotationVector());
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
                            res[i].varName="direction";
                        }
                        return res;
                    })
            ).category(cat));

            VECTOR_SPLIT = register(SpellBlock.create("vector_split",
                    new SpellSignal[]{SpellSignal.createVector(null).named("vector")},
                    new SpellSignal[]{
                            SpellSignal.createNumber(0).named("x"),
                            SpellSignal.createNumber(0).named("y"),
                            SpellSignal.createNumber(0).named("z"),
                    },
                    new SpellBlock.Parameter[]{},
                    ((component, vars) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        Vec3d vec = vars.get("vector").getVectorValue();
                        res.add("x",vec.x);
                        res.add("y",vec.y);
                        res.add("z",vec.z);
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
            ).category(cat));

            VECTOR_BUILD = register(SpellBlock.create("vector_build",
                    new SpellSignal[]{
                            SpellSignal.createNumber(0).named("x"),
                            SpellSignal.createNumber(0).named("y"),
                            SpellSignal.createNumber(0).named("z"),
                    },
                    new SpellSignal[]{SpellSignal.createVector(null).named("vector")},
                    new SpellBlock.Parameter[]{},
                    ((component, vars) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        Vec3d vec = new Vec3d(
                                vars.get("x").getNumberValue(),
                                vars.get("y").getNumberValue(),
                                vars.get("z").getNumberValue()
                        );
                        res.add("vector",vec);
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
            ).category(cat));
        }

        // effectors
        cat = SpellBlock.Category.Effector;
        {
            PRINT = register(SpellBlock.create("print",new SpellSignal[]{
                            SpellSignal.createAny().named("val"),},
                    ((comp,vars) -> {
                        if(comp.casterEntity != null)
                        {
                            World world = comp.casterEntity.getWorld();
                            if(world instanceof ServerWorld serverWorld){
                                if(comp.casterEntity instanceof ServerPlayerEntity player)
                                    player.sendMessage(Text.literal(vars.get("val").toString()));
                            }
                            else if(world instanceof ClientWorld clientWorld){
                                if(comp.casterEntity instanceof ClientPlayerEntity player)
                                    player.sendMessage(Text.literal(vars.get("val").toString()));
                            }
                        }

                        return SpellBlockResult.empty();
                    })
                    ,()->SpellBlock.sidesInput("val")
            ).category(cat));

            FIREBALL = register(SpellBlock.create("fireball",new SpellSignal[]{
                            SpellSignal.createVector(null).named("position"),
                            SpellSignal.createVector(null).named("direction"),
                            SpellSignal.createNumber(0).named("speed"),
                    },
                    ((comp,vars) -> {
                        FireballEntity fireball = new FireballEntity(EntityType.FIREBALL,comp.world);
                        fireball.setPosition(vars.getVector("position"));
                        fireball.setVelocity(vars.getVector("direction").multiply(vars.getNumber("speed")));
                        comp.world.spawnEntity(fireball);

                        return SpellBlockResult.empty();
                    })
                    ,()->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        res[0] = SpellComponent.SideConfig.createInput(SpellComponent.getDir(0)).named("position");
                        res[1] = SpellComponent.SideConfig.createInput(SpellComponent.getDir(1)).named("direction");
                        res[2] = SpellComponent.SideConfig.createInput(SpellComponent.getDir(2)).named("speed");
                        res[3] = SpellComponent.SideConfig.createBlocked(SpellComponent.getDir(3));
                        res[4] = SpellComponent.SideConfig.createBlocked(SpellComponent.getDir(4));
                        res[5] = SpellComponent.SideConfig.createBlocked(SpellComponent.getDir(5));
                        return res;
                    }
            ).category(cat));

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
