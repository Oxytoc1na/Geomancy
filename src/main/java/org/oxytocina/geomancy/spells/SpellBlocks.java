package org.oxytocina.geomancy.spells;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.function.Function;

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
    public static final SpellBlock SUBTRACT;
    public static final SpellBlock MULTIPLY;
    public static final SpellBlock DIVIDE;
    public static final SpellBlock SIN;
    public static final SpellBlock COS;
    public static final SpellBlock TAN;
    public static final SpellBlock EXP;
    public static final SpellBlock VECTOR_SPLIT;
    public static final SpellBlock VECTOR_BUILD;
    public static final SpellBlock VECTOR_ENTITYPOS;
    public static final SpellBlock VECTOR_ENTITYSPAWN;
    public static final SpellBlock VECTOR_ENTITYEYEPOS;
    public static final SpellBlock VECTOR_ENTITYDIR;

    // effectors
    public static final SpellBlock PRINT;
    public static final SpellBlock FIREBALL;
    public static final SpellBlock DEBUG;
    public static final SpellBlock LIGHTNING;

    // reference

    private static SpellBlock.Category cat;
    static{

        // flow control
        cat = SpellBlock.Category.FlowControl;
        {
            CONVEYOR = register(SpellBlock.Builder.create("conveyor")
                    .inputs(SpellSignal.createAny().named("signal"))
                    .outputs(SpellSignal.createAny().named("signal"))
                    .func(SpellBlocks::mirrorInToOut)
                    .sideConfigGetter((c)->freeformSideConfigs(c,"signal"))
                    .category(cat)
                    .build());

            GATE = register(SpellBlock.Builder.create("gate")
                    .inputs(SpellSignal.createAny().named("signal"),SpellSignal.createBoolean(false).named("gate"))
                    .outputs(SpellSignal.createAny().named("signal"),SpellSignal.createBoolean(false).named("gate"))
                    .func((component, args) ->
                    {
                        SpellBlockResult res = new SpellBlockResult();
                        if(!args.vars.get("gate").getBooleanValue()) return res;
                        return args.toRes();
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for (int i = 0; i < 2; i++) {
                            res[i] = SpellComponent.SideConfig.createToggleableInput(comp
                                    ,SpellComponent.directions[i]);
                        }
                        res[0].varName="signal";
                        res[1].varName="gate";

                        for (int i = 2; i < 6; i++) {
                            res[i] = SpellComponent.SideConfig.createToggleableOutput(comp
                                    ,SpellComponent.directions[i]);
                        }

                        res[2].varName="signal";
                        res[3].varName="signal";
                        res[4].varName="gate";
                        res[5].varName="gate";

                        return res;
                    })
                    .category(cat)
                    .build());

            FOR = register(SpellBlock.Builder.create("for")
                    .inputs(SpellSignal.createNumber(1).named("count"), SpellSignal.createAny().named("signal"))
                    .outputs(SpellSignal.createNumber(0).named("i"), SpellSignal.createAny().named("signal"))
                    .func((component, args) ->
                    {
                        SpellBlockResult res = new SpellBlockResult();
                        res.add(args.get("signal"));
                        res.iterations=args.getInt("count");
                        return res;
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for (int i = 0; i < 2; i++) {
                            res[i] = SpellComponent.SideConfig.createToggleableInput(comp
                                    ,SpellComponent.directions[i]);
                        }
                        res[0].varName="count";
                        res[1].varName="signal";

                        for (int i = 2; i < 6; i++) {
                            res[i] = SpellComponent.SideConfig.createToggleableOutput(comp
                                    ,SpellComponent.directions[i]);
                        }

                        res[2].varName="signal";
                        res[3].varName="signal";
                        res[4].varName="i";
                        res[5].varName="i";

                        return res;
                    })
                    .category(cat)
                    .build());
        }

        // getters
        cat = SpellBlock.Category.Provider;
        {
            CONST_NUM = register(SpellBlock.Builder.create("constant_number")
                    .inputs()
                    .outputs(SpellSignal.createNumber(0).named("val"))
                    .parameters(SpellBlock.Parameter.createNumber("val",1,-1000,1000))
                    .func(SpellBlocks::mirrorInToOut)
                    .sideConfigGetter((c)->SpellBlock.SideUtil.sidesOutput(c,"val"))
                    .category(cat).build());

            CONST_TEXT = register(SpellBlock.Builder.create("constant_text")
                    .inputs()
                    .outputs(SpellSignal.createText("").named("val"))
                    .parameters(SpellBlock.Parameter.createText("val",""))
                    .func(SpellBlocks::mirrorInToOut)
                    .sideConfigGetter((c)->SpellBlock.SideUtil.sidesOutput(c,"val"))
                    .category(cat).build());

            CONST_BOOLEAN = register(SpellBlock.Builder.create("constant_boolean")
                    .inputs()
                    .outputs(SpellSignal.createBoolean(true).named("val"))
                    .parameters(SpellBlock.Parameter.createBoolean("val",true))
                    .func(SpellBlocks::mirrorInToOut)
                    .sideConfigGetter((c)->SpellBlock.SideUtil.sidesOutput(c,"val"))
                    .category(cat).build());

            ENTITY_CASTER = register(SpellBlock.Builder.create("entity_caster")
                    .inputs()
                    .outputs(SpellSignal.createUUID(null).named("caster"))
                    .func((component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        if(component.context.caster!=null) res.add("caster",component.context.caster.getUuid());return res;
                    })
                    .sideConfigGetter((c)->SpellBlock.SideUtil.sidesOutput(c,"caster"))
                    .category(cat).build());
        }

        // arithmetic
        cat = SpellBlock.Category.Arithmetic;
        {
            // math
            {
                SUM = register(SpellBlock.Builder.create("sum")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("sum"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            if(vars.get("a").type == vars.get("b").type){
                                switch(vars.get("a").type){
                                    case Vector:
                                        res.add(SpellSignal.createVector(vars.getVector("a").add(vars.getVector("b"))).named("sum"));
                                        break;
                                    case Number:
                                        res.add(SpellSignal.createNumber(vars.getNumber("a")+vars.getNumber("b")).named("sum"));
                                        break;
                                }
                            }
                            return res;
                        })
                        .sideConfigGetter(SpellBlock.SideUtil::sidesFreeform)
                        .category(cat).build());

                SUBTRACT = register(SpellBlock.Builder.create("subtract")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("diff"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            if(vars.get("a").type == vars.get("b").type){
                                switch(vars.get("a").type){
                                    case Vector:
                                        res.add(SpellSignal.createVector(vars.getVector("a").subtract(vars.getVector("b"))).named("diff"));
                                        break;
                                    case Number:
                                        res.add(SpellSignal.createNumber(vars.getNumber("a")-vars.getNumber("b")).named("diff"));
                                        break;
                                }
                            }
                            return res;
                        })
                        .sideConfigGetter(SpellBlock.SideUtil::sidesFreeform)
                        .category(cat).build());

                MULTIPLY = register(SpellBlock.Builder.create("multiply")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("product"),
                                SpellSignal.createVector(null).named("dotproduct"),
                                SpellSignal.createVector(null).named("crossproduct")
                                )
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var a = vars.get("a");
                            var b = vars.get("b");
                            if(a.type == b.type){
                                switch(a.type){
                                    case Vector:
                                        res.add(SpellSignal.createNumber(a.getVectorValue().dotProduct(b.getVectorValue())).named("dotproduct"));
                                        res.add(SpellSignal.createVector(a.getVectorValue().crossProduct(b.getVectorValue())).named("crossproduct"));
                                        break;
                                    case Number:
                                        res.add(SpellSignal.createNumber(a.getNumberValue()*b.getNumberValue()).named("product"));
                                        break;
                                }
                            }
                            else{
                                if(b.type == SpellSignal.Type.Vector && a.type == SpellSignal.Type.Number)
                                {
                                    var temp = a;
                                    a = b;
                                    b = temp;
                                }

                                if(a.type == SpellSignal.Type.Vector && b.type == SpellSignal.Type.Number){
                                    res.add(SpellSignal.createVector(a.getVectorValue().multiply(b.getNumberValue())).named("product"));
                                }
                            }
                            return res;
                        })
                        .sideConfigGetter(SpellBlock.SideUtil::sidesFreeform)
                        .category(cat).build());

                DIVIDE = register(SpellBlock.Builder.create("divide")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("quotient"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var a = vars.get("a");
                            var b = vars.get("b");

                            if(b.type == SpellSignal.Type.Vector && a.type == SpellSignal.Type.Number)
                            {
                                var temp = a;
                                a = b;
                                b = temp;
                            }

                            if(b.type == SpellSignal.Type.Number){
                                switch(a.type){
                                    case Vector:
                                        res.add(SpellSignal.createVector(a.getVectorValue().multiply(1/b.getNumberValue())).named("quotient"));
                                        break;
                                    case Number:
                                        res.add(SpellSignal.createNumber(a.getNumberValue()/b.getNumberValue()).named("quotient"));
                                        break;
                                }
                            }

                            return res;
                        })
                        .sideConfigGetter(SpellBlock.SideUtil::sidesFreeform)
                        .category(cat).build());

                SIN = register(createAngleFunc("sin","sine",Math::sin));
                COS = register(createAngleFunc("cos","cosine",Math::sin));
                TAN = register(createAngleFunc("tan","tangent",Math::sin));

                EXP = register(SpellBlock.Builder.create("exp")
                        .inputs(SpellSignal.createNumber(0).named("a"),
                                SpellSignal.createNumber(0).named("b"))
                        .outputs(SpellSignal.createNumber(0).named("exp"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var a = vars.get("a");
                            var b = vars.get("b");
                            res.add(SpellSignal.createNumber(Math.pow(a.getNumberValue(),b.getNumberValue())).named("exp"));
                            return res;
                        })
                        .sideConfigGetter(SpellBlock.SideUtil::sidesFreeform)
                        .category(cat).build());
            }

            // entities
            {
                VECTOR_ENTITYSPAWN = register(SpellBlock.Builder.create("vector_entityspawn")
                        .inputs(SpellSignal.createUUID(null).named("entity"))
                        .outputs(SpellSignal.createVector(null).named("position"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            if(!(vars.get("entity").getEntity(component.world()) instanceof ServerPlayerEntity player)) return res;

                            var playerSpawn = player.getSpawnPointPosition();
                            if(playerSpawn==null) playerSpawn = player.getServerWorld().getSpawnPos();

                            res.add(SpellSignal.createVector(playerSpawn.toCenterPos()).named("position"));
                            return res;
                        })
                        .sideConfigGetter((comp) -> {
                            SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                            for (int i = 0; i < 3; i++) {
                                res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.directions[i]);
                                res[i].varName="entity";
                            }
                            for (int i = 3; i < 6; i++) {
                                res[i] = SpellComponent.SideConfig.createToggleableOutput(comp,SpellComponent.directions[i]);
                                res[i].varName="position";
                            }
                            return res;
                        })
                        .category(cat).build());

                VECTOR_ENTITYPOS = register(SpellBlock.Builder.create("vector_entitypos")
                        .inputs(SpellSignal.createUUID(null).named("entity"))
                        .outputs(SpellSignal.createVector(null).named("position"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            if(!(vars.get("entity").getEntity(component.world()) instanceof LivingEntity entity)) return res;
                            res.add("position",entity.getPos());
                            return res;
                        })
                        .sideConfigGetter((comp) -> {
                            SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                            for (int i = 0; i < 3; i++) {
                                res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.directions[i]);
                                res[i].varName="entity";
                            }
                            for (int i = 3; i < 6; i++) {
                                res[i] = SpellComponent.SideConfig.createToggleableOutput(comp,SpellComponent.directions[i]);
                                res[i].varName="position";
                            }
                            return res;
                        })
                        .category(cat).build());

                VECTOR_ENTITYEYEPOS = register(SpellBlock.Builder.create("vector_entityeyepos")
                        .inputs(SpellSignal.createUUID(null).named("entity"))
                        .outputs(SpellSignal.createVector(null).named("eye position"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            if(!(vars.get("entity").getEntity(component.world()) instanceof LivingEntity entity)) return res;
                            res.add("eye position",entity.getEyePos());
                            return res;
                        })
                        .sideConfigGetter((comp) -> {
                            SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                            for (int i = 0; i < 3; i++) {
                                res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.directions[i]);
                                res[i].varName="entity";
                            }
                            for (int i = 3; i < 6; i++) {
                                res[i] = SpellComponent.SideConfig.createToggleableOutput(comp,SpellComponent.directions[i]);
                                res[i].varName="eye position";
                            }
                            return res;
                        })
                        .category(cat).build());

                VECTOR_ENTITYDIR = register(SpellBlock.Builder.create("vector_entitydir")
                        .inputs(SpellSignal.createUUID(null).named("entity"))
                        .outputs(SpellSignal.createVector(null).named("direction"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            if(!(vars.get("entity").getEntity(component.world()) instanceof LivingEntity entity)) return res;
                            res.add("direction",entity.getRotationVector());
                            return res;
                        })
                        .sideConfigGetter((comp) -> {
                            SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                            for (int i = 0; i < 3; i++) {
                                res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.directions[i]);
                                res[i].varName="entity";
                            }
                            for (int i = 3; i < 6; i++) {
                                res[i] = SpellComponent.SideConfig.createToggleableOutput(comp,SpellComponent.directions[i]);
                                res[i].varName="direction";
                            }
                            return res;
                        })
                        .category(cat).build());
            }

            // vector math
            {
                VECTOR_SPLIT = register(SpellBlock.Builder.create("vector_split")
                        .inputs(SpellSignal.createVector(null).named("vector"))
                        .outputs(SpellSignal.createNumber(0).named("x"),
                                SpellSignal.createNumber(0).named("y"),
                                SpellSignal.createNumber(0).named("z"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            Vec3d vec = vars.get("vector").getVectorValue();
                            res.add("x", vec.x);
                            res.add("y", vec.y);
                            res.add("z", vec.z);
                            return res;
                        })
                        .sideConfigGetter((comp) -> {
                            SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                            for (int i = 0; i < 3; i++) {
                                res[i] = SpellComponent.SideConfig.createToggleableInput(comp, SpellComponent.directions[i]).named("vector");
                            }
                            res[3] = SpellComponent.SideConfig.createToggleableOutput(comp, SpellComponent.directions[3]).named("x");
                            res[4] = SpellComponent.SideConfig.createToggleableOutput(comp, SpellComponent.directions[4]).named("y");
                            res[5] = SpellComponent.SideConfig.createToggleableOutput(comp, SpellComponent.directions[5]).named("z");
                            return res;
                        })
                        .category(cat).build());

                VECTOR_BUILD = register(SpellBlock.Builder.create("vector_build")
                        .inputs(SpellSignal.createNumber(0).named("x"),
                                SpellSignal.createNumber(0).named("y"),
                                SpellSignal.createNumber(0).named("z"))
                        .outputs(SpellSignal.createVector(null).named("vec"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            Vec3d vec = new Vec3d(
                                    vars.get("x").getNumberValue(),
                                    vars.get("y").getNumberValue(),
                                    vars.get("z").getNumberValue()
                            );
                            res.add("vec", vec);
                            return res;
                        })
                        .sideConfigGetter((comp) -> {
                            SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                            for (int i = 0; i < 3; i++) {
                                res[i] = SpellComponent.SideConfig.createToggleableOutput(comp, SpellComponent.directions[i]).named("vec");
                            }
                            res[3] = SpellComponent.SideConfig.createToggleableInput(comp, SpellComponent.directions[3]).named("x");
                            res[4] = SpellComponent.SideConfig.createToggleableInput(comp, SpellComponent.directions[4]).named("y");
                            res[5] = SpellComponent.SideConfig.createToggleableInput(comp, SpellComponent.directions[5]).named("z");
                            return res;
                        })
                        .category(cat).build());
            }
        }

        // effectors
        cat = SpellBlock.Category.Effector;
        {
            PRINT = register(SpellBlock.Builder.create("print")
                    .inputs(SpellSignal.createAny().named("val"))
                    .outputs()
                    .parameters()
                    .func((comp,vars) -> {
                        if(comp.context.caster != null)
                        {
                            World world = comp.context.caster.getWorld();
                            if(world instanceof ServerWorld serverWorld){
                                if(comp.context.caster instanceof ServerPlayerEntity player)
                                    player.sendMessage(Text.literal(vars.get("val").toString()));
                            }
                            else if(world instanceof ClientWorld clientWorld){
                                if(comp.context.caster instanceof ClientPlayerEntity player)
                                    player.sendMessage(Text.literal(vars.get("val").toString()));
                            }
                        }

                        return SpellBlockResult.empty();
                    })
                    .sideConfigGetter((c)->SpellBlock.SideUtil.sidesInput(c,"val"))
                    .category(cat).build());

            DEBUG = register(SpellBlock.Builder.create("debug")
                    .inputs()
                    .outputs()
                    .parameters()
                    .func((comp,vars) -> SpellBlockResult.empty())
                    .sideConfigGetter(SpellBlock.SideUtil::sidesBlocked)
                    .init(component -> { component.context.debugging=true;})
                    .category(cat).build());

            FIREBALL = register(SpellBlock.Builder.create("fireball")
                    .inputs(SpellSignal.createVector(null).named("position"),
                            SpellSignal.createVector(null).named("direction"),
                            SpellSignal.createNumber(0).named("speed"),
                            SpellSignal.createNumber(0).named("power"))
                    .outputs()
                    .parameters()
                    .func((comp,vars) -> {
                        var dir = vars.getVector("direction");
                        var speed =vars.getNumber("speed");
                        var power =vars.getInt("power");
                        var pos = vars.getVector("position");

                        float manaCost = 2
                                +castOffsetSoulCost(comp,pos,0.1f)
                                +(float)Math.pow(power*1.5,1.5);

                        if(trySpendSoul(comp,manaCost)){
                            var vel = dir.multiply(speed);
                            FireballEntity fireball = new FireballEntity(comp.world(),comp.context.caster,vel.x,vel.y,vel.z,power);
                            fireball.setPosition(pos);
                            comp.world().spawnEntity(fireball);
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                        }

                        return SpellBlockResult.empty();
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        res[0] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(0)).named("position");
                        res[1] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(1)).named("direction");
                        res[2] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(2)).named("speed");
                        res[3] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(3)).named("power");
                        res[4] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(4)).disabled();
                        res[5] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(5)).disabled();
                        return res;
                    })
                    .category(cat).build());

            LIGHTNING = register(SpellBlock.Builder.create("lightning")
                    .inputs(SpellSignal.createVector(null).named("position"))
                    .outputs()
                    .parameters()
                    .func((comp,vars) -> {
                        var pos = vars.getVector("position");

                        float manaCost = 10
                                +castOffsetSoulCost(comp,pos,0.1f);

                        if(trySpendSoul(comp,manaCost)){
                            LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT,comp.world());
                            lightning.setPosition(pos);
                            comp.world().spawnEntity(lightning);
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                        }

                        return SpellBlockResult.empty();
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        res[0] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(0)).named("position");
                        for(int i = 1; i <6; i++) res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(i)).disabled();
                        return res;
                    })
                    .category(cat).build());

        }

        // reference
        cat = SpellBlock.Category.Reference;
        {

        }
    }
    // sin, cos, tan
    private static SpellBlock createAngleFunc(String name, String varName, Function<Double,Double> function){
        return SpellBlock.Builder.create(name)
                .inputs(SpellSignal.createNumber(0).named("rad"))
                .outputs(SpellSignal.createNumber(0).named(varName))
                .parameters()
                .func((comp,vars) -> {
                    SpellBlockResult res = SpellBlockResult.empty();
                    res.add(SpellSignal.createNumber(function.apply((double)vars.getNumber("rad"))).named(varName));
                    return res;
                })
                .sideConfigGetter(SpellBlock.SideUtil::sidesFreeform)
                .category(cat).build();
    }

    private static void tryLogDebug(SpellComponent comp, Object... args){
        if(!comp.context.debugging) return;

        var msg = Text.translatable("geomancy.spells.debug.error",comp.function.identifier.getPath(),args);

        if(comp.context.caster != null)
        {
            World world = comp.context.caster.getWorld();
            if(world instanceof ServerWorld){
                if(comp.context.caster instanceof ServerPlayerEntity player)
                    player.sendMessage(msg);
            }
            else if(world instanceof ClientWorld){
                if(comp.context.caster instanceof ClientPlayerEntity player)
                    player.sendMessage(msg);
            }
        }
    }

    public static void tryLogDebugWrongSignal(SpellComponent comp, SpellSignal.Type got, SpellSignal.Type expected){
        if(!comp.context.debugging) return;

        var msg = Text.translatable("geomancy.spells.debug.wrongsignal",
                Text.translatable("geomancy.spellcomponent."+comp.function.identifier.getPath()).formatted(Formatting.DARK_AQUA),
                Text.translatable("geomancy.spellmaker.types."+got.toString().toLowerCase()).formatted(Formatting.DARK_AQUA),
                Text.translatable("geomancy.spellmaker.types."+expected.toString().toLowerCase()).formatted(Formatting.DARK_AQUA)
                );

        if(comp.context.caster != null)
        {
            World world = comp.context.caster.getWorld();
            if(world instanceof ServerWorld){
                if(comp.context.caster instanceof ServerPlayerEntity player)
                    player.sendMessage(msg);
            }
            else if(world instanceof ClientWorld){
                if(comp.context.caster instanceof ClientPlayerEntity player)
                    player.sendMessage(msg);
            }
        }
    }

    private static void tryLogDebugBroke(SpellComponent comp,float cost){
        if(!comp.context.debugging) return;

        var msg = Text.translatable("geomancy.spells.debug.broke",cost,comp.context.availableSoul);

        if(comp.context.caster != null)
        {
            World world = comp.context.caster.getWorld();
            if(world instanceof ServerWorld){
                if(comp.context.caster instanceof ServerPlayerEntity player)
                    player.sendMessage(msg);
            }
            else if(world instanceof ClientWorld){
                if(comp.context.caster instanceof ClientPlayerEntity player)
                    player.sendMessage(msg);
            }
        }
    }

    private static boolean trySpendSoul(SpellComponent comp, float amount){
        return comp.context.tryConsumeSoul(amount);
    }

    private static boolean canAfford(SpellComponent comp, float amount){
        return comp.context.canAfford(amount);
    }

    private static float castOffsetSoulCost(SpellComponent comp, Vec3d pos, float perBlock){
        return distanceToCaster(comp.context,pos)*perBlock;
    }

    private static float distanceToCaster(SpellContext context, Vec3d pos){
        if(context.caster==null) return 0;
        return (float)context.caster.getPos().subtract(pos).length();
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

    public static SpellComponent.SideConfig[] freeformSideConfigs(SpellComponent comp,String var){
        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
        for (int i = 0; i < 6; i++) {
            res[i] = SpellComponent.SideConfig.createFreeform(comp
                    ,SpellComponent.directions[i]);
            res[i].varName=var;
        }
        return res;
    }
}
