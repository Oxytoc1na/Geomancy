package org.oxytocina.geomancy.spells;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.util.BlockHelper;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class SpellBlocks {
    public static LinkedHashMap<Identifier, SpellBlock> functions = new LinkedHashMap<>();
    public static LinkedHashMap<Identifier, Integer> functionOrder = new LinkedHashMap<>();

    // flow control
    public static final SpellBlock CONVEYOR;
    public static final SpellBlock GATE;
    public static final SpellBlock FOR;
    public static final SpellBlock NOT;

    // getters
    public static final SpellBlock CONST_NUM;
    public static final SpellBlock CONST_TEXT;
    public static final SpellBlock CONST_BOOLEAN;
    public static final SpellBlock ENTITY_CASTER;
    public static final SpellBlock CASTER_SLOT;

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
    public static final SpellBlock RAYCAST_POS;
    public static final SpellBlock RAYCAST_DIR;
    public static final SpellBlock BOOL_ENTITYGROUNDED;

    // effectors
    public static final SpellBlock PRINT;
    public static final SpellBlock FIREBALL;
    public static final SpellBlock DEBUG;
    public static final SpellBlock LIGHTNING;
    public static final SpellBlock PLACE;
    public static final SpellBlock BREAK;
    public static final SpellBlock IMBUE;

    // reference
    public static final SpellBlock ACTION;
    public static final SpellBlock PROVIDER;
    public static final SpellBlock FUNCTION;
    public static final SpellBlock FUNCTION2;
    public static final SpellBlock REF_OUTPUT;
    public static final SpellBlock REF_INPUT;
    public static final SpellBlock TELEPORT;
    public static final SpellBlock DIMHOP;
    public static final SpellBlock PUSH;

    private static SpellBlock.Category cat;

    private static HashMap<Identifier, ImbueData> imbueData = new HashMap();
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

            NOT = register(SpellBlock.Builder.create("not")
                    .inputs(SpellSignal.createAny().named("prevent"))
                    .outputs(SpellSignal.createBoolean(false).named("signal"))
                    .func((component, args) ->
                    {
                        component.castClearedData.put("prevent","1");
                        return SpellBlockResult.empty();
                    })
                    .post((component) ->
                    {
                        if(!component.castClearedData.containsKey("prevent")) return;

                        SpellBlockResult res = new SpellBlockResult();
                        res.depth=component.context.highestRecordedDepth;
                        res.add(SpellSignal.createBoolean(true).named("signal"));
                        component.pushSignals(res);
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for (int i = 0; i < 3; i++) {
                            res[i] = SpellComponent.SideConfig.createToggleableInput(comp
                                    ,SpellComponent.directions[i]).named("prevent");
                        }
                        for (int i = 3; i < 6; i++) {
                            res[i] = SpellComponent.SideConfig.createToggleableOutput(comp
                                    ,SpellComponent.directions[i]).named("signal");
                        }
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

            CASTER_SLOT = register(SpellBlock.Builder.create("caster_slot")
                    .inputs()
                    .outputs(SpellSignal.createNumber(0).named("slot"))
                    .func((component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        if(component.context.caster instanceof PlayerEntity pe && component.context.casterItem != null)
                            res.add("slot",pe.getInventory().getSlotWithStack(component.context.casterItem));return res;
                    })
                    .sideConfigGetter((c)->SpellBlock.SideUtil.sidesOutput(c,"slot"))
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
                                SpellSignal.createNumber(0).named("dotproduct"),
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

                RAYCAST_POS = register(SpellBlock.Builder.create("raycast_pos")
                        .inputs(SpellSignal.createVector(null).named("from"),
                                SpellSignal.createVector(null).named("dir"),
                                SpellSignal.createNumber(0).named("length"))
                        .outputs(SpellSignal.createVector(null).named("hitPos"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var hit = raycast(comp,vars);
                            if(hit.getType()== HitResult.Type.BLOCK){
                                var hitPos = hit.getBlockPos();
                                res.add(SpellSignal.createVector(hitPos.toCenterPos()).named("hitPos"));
                            }
                            return res;
                        })
                        .sideConfigGetter(SpellBlock.SideUtil::sidesFreeform)
                        .category(cat).build());

                RAYCAST_DIR = register(SpellBlock.Builder.create("raycast_dir")
                        .inputs(SpellSignal.createVector(null).named("from"),
                                SpellSignal.createVector(null).named("dir"),
                                SpellSignal.createNumber(0).named("length"))
                        .outputs(SpellSignal.createVector(null).named("hitDir"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var hit = raycast(comp,vars);
                            if(hit.getType()== HitResult.Type.BLOCK){
                                var hitPos = hit.getSide().getVector();
                                res.add(SpellSignal.createVector(new Vec3d(hitPos.getX(), hitPos.getY(),hitPos.getZ())).named("hitDir"));
                            }
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

                BOOL_ENTITYGROUNDED = register(SpellBlock.Builder.create("bool_entitygrounded")
                        .inputs(SpellSignal.createUUID(null).named("entity"))
                        .outputs(SpellSignal.createBoolean(false).named("grounded"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            if(!(vars.get("entity").getEntity(component.world()) instanceof LivingEntity ent)) return res;
                            res.add(SpellSignal.createBoolean(ent.isOnGround()).named("grounded"));
                            return res;
                        })
                        .sideConfigGetter(SpellBlock.SideUtil::sidesFreeform)
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
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,pos));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,pos));
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
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,pos));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,pos));
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

            TELEPORT = register(SpellBlock.Builder.create("teleport")
                    .inputs(
                            SpellSignal.createUUID(null).named("entity"),
                            SpellSignal.createVector(null).named("position")
                    )
                    .outputs()
                    .parameters()
                    .func((comp,vars) -> {
                        var uuid = vars.getUUID("entity");
                        var pos = vars.getVector("position");
                        Entity ent = comp.world() instanceof ServerWorld sw ? sw.getEntity(uuid) : null;
                        if(ent==null) return SpellBlockResult.empty();

                        float manaCost = 0
                                +castOffsetSoulCost(comp,pos,0.1f);

                        if(trySpendSoul(comp,manaCost)){
                            //comp.world().setBlockState(Toolbox.posToBlockPos(pos), Blocks.GLOWSTONE.getDefaultState());
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,ent.getPos()));
                            ent.teleport(pos.x,pos.y,pos.z);
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,pos));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.caster().getPos()));
                        }

                        return SpellBlockResult.empty();
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for(int i = 0; i <6; i++) res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(i)).named(i%2==0?"position":"entity");
                        return res;
                    })
                    .category(cat).build());

            DIMHOP = register(SpellBlock.Builder.create("dimhop")
                    .inputs(
                            SpellSignal.createUUID(null).named("entity"),
                            SpellSignal.createNumber(0).named("dimension")
                    )
                    .outputs()
                    .parameters()
                    .func((comp,vars) -> {
                        var uuid = vars.getUUID("entity");
                        var dim = vars.getNumber("dimension");
                        Entity ent = comp.world() instanceof ServerWorld sw ? sw.getEntity(uuid) : null;
                        if(ent==null) return SpellBlockResult.empty();

                        float manaCost = 100;

                        if(trySpendSoul(comp,manaCost)){

                            if(!(comp.world() instanceof ServerWorld sw)) return SpellBlockResult.empty();

                            ServerWorld destination = switch (Math.round(dim)) {
                                case 1 -> sw.getServer().getWorld(World.END);
                                case -1 -> sw.getServer().getWorld(World.NETHER);
                                default -> sw.getServer().getOverworld();
                            };

                            if(destination==null) return SpellBlockResult.empty();
                            if(sw.getRegistryKey() == destination.getRegistryKey()) return SpellBlockResult.empty();

                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,ent.getPos()));
                            ent.teleport(destination,ent.getX(),ent.getY(),ent.getZ(),null,ent.getYaw(),ent.getPitch());
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.caster().getPos()));
                        }

                        return SpellBlockResult.empty();
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for(int i = 0; i <6; i++) res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(i)).named(i%2==0?"dimension":"entity");
                        return res;
                    })
                    .category(cat).build());

            PUSH = register(SpellBlock.Builder.create("push")
                    .inputs(
                            SpellSignal.createUUID(null).named("entity"),
                            SpellSignal.createVector(null).named("velocity")
                    )
                    .outputs()
                    .parameters()
                    .func((comp,vars) -> {
                        var uuid = vars.getUUID("entity");
                        var vel = vars.getVector("velocity");
                        if(!(comp.world() instanceof ServerWorld serverWorld)) return SpellBlockResult.empty();
                        var entity = serverWorld.getEntity(uuid);
                        if(entity==null) return SpellBlockResult.empty();

                        float manaCost = 0
                                +(float)Math.pow((vel.length()+1),2)*2;

                        if(trySpendSoul(comp,manaCost)){
                            entity.setVelocity(vel);
                            entity.setVelocityClient(vel.x,vel.y,vel.z);
                            entity.velocityModified=true;
                            entity.velocityDirty=true;
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,entity.getPos()));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.caster().getPos()));
                        }

                        return SpellBlockResult.empty();
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for(int i = 0; i <6; i++) res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(i)).named(i%2==0?"velocity":"entity");
                        return res;
                    })
                    .category(cat).build());

            PLACE = register(SpellBlock.Builder.create("place")
                    .inputs(
                            SpellSignal.createVector(null).named("position"),
                            SpellSignal.createNumber(0).named("slot")
                    )
                    .outputs().parameters()
                    .func((comp,vars) -> {
                        if(!(comp.context.caster instanceof PlayerEntity pe)) return SpellBlockResult.empty(); // not a player
                        var pos = vars.getVector("position");
                        var slot = vars.getNumber("slot");
                        int slotInt = Math.round(slot);
                        if(slotInt <0||slotInt>=pe.getInventory().size()) { tryLogDebugSlotOOB(comp,slotInt); return SpellBlockResult.empty();} // slot OOB
                        var stack = pe.getInventory().getStack(slotInt);
                        if(!(stack.getItem() instanceof BlockItem bi)) { tryLogDebugNotPlaceable(comp,stack); return SpellBlockResult.empty(); } // not a block

                        float manaCost = 1
                                +castOffsetSoulCost(comp,pos,0.05f);

                        if(canAfford(comp,manaCost)){
                            var blockPos = Toolbox.posToBlockPos(pos);

                            BlockState targetState = comp.world().getBlockState(blockPos);
                            if(!targetState.isReplaceable())
                            {
                                // couldnt replace
                                tryLogDebugNotReplaceable(comp,targetState);
                                return SpellBlockResult.empty();
                            }

                            comp.world().setBlockState(Toolbox.posToBlockPos(pos), bi.getBlock().getDefaultState());
                            Toolbox.playSound(bi.getBlock().getSoundGroup(targetState).getPlaceSound(),comp.world(),blockPos, SoundCategory.BLOCKS,1,1);
                            trySpendSoul(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,pos));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,pos));
                        }

                        return SpellBlockResult.empty();
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for(int i = 0; i <6; i++) res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(i)).named(i%2==0?"position":"slot");
                        return res;
                    })
                    .category(cat).build());

            BREAK = register(SpellBlock.Builder.create("break")
                    .inputs(
                            SpellSignal.createVector(null).named("position"),
                            SpellSignal.createBoolean(true).named("silk touch")
                    )
                    .outputs().parameters()
                    .func((comp,vars) -> {
                        if(!(comp.context.caster instanceof PlayerEntity pe)) return SpellBlockResult.empty(); // not a player
                        var pos = vars.getVector("position");
                        var silkTouch = vars.getBoolean("silk touch");
                        var blockPos = Toolbox.posToBlockPos(pos);

                        // calculate breaking cost
                        BlockState targetState = comp.world().getBlockState(blockPos);

                        float manaCost = 0.2f
                                +targetState.getBlock().getHardness()/5f* (silkTouch?2:1)
                                +castOffsetSoulCost(comp,pos,0.05f);

                        if(canAfford(comp,manaCost)){

                            ItemStack stack = new ItemStack(Items.DIRT);
                            if(targetState.isToolRequired()){
                                if(targetState.isIn(BlockTags.PICKAXE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_PICKAXE);
                                else if(targetState.isIn(BlockTags.AXE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_AXE);
                                else if(targetState.isIn(BlockTags.SHOVEL_MINEABLE)) stack = new ItemStack(Items.NETHERITE_SHOVEL);
                                else if(targetState.isIn(BlockTags.HOE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_HOE);
                                else if(targetState.isIn(BlockTags.SWORD_EFFICIENT)) stack = new ItemStack(Items.NETHERITE_SWORD);
                            }

                            if(silkTouch)
                                stack.addEnchantment(Enchantments.SILK_TOUCH,1);

                            final ItemStack s2 = stack.copy();

                            Predicate<BlockState> minableBlocksPredicate = s -> !s.isToolRequired() || s2.isSuitableFor(s);

                            if (!minableBlocksPredicate.test(targetState)) {
                                // couldnt mine
                                tryLogDebugNotbreakable(comp,targetState);
                                return SpellBlockResult.empty();
                            }

                            if(!BlockHelper.breakBlockWithDrops(pe,stack,comp.world(),blockPos,minableBlocksPredicate)){
                                // couldnt mine... again?
                                tryLogDebugNotbreakable(comp,targetState);
                                return SpellBlockResult.empty();
                            }

                            trySpendSoul(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,pos));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,pos));
                        }

                        return SpellBlockResult.empty();
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for(int i = 0; i <6; i++) res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(i)).named(i%2==0?"position":"silk touch");
                        return res;
                    })
                    .category(cat).build());

            addImbueData(StatusEffects.REGENERATION,new ImbueData(10,1));
            addImbueData(StatusEffects.POISON,new ImbueData(10,1,1.5f));
            addImbueData(StatusEffects.WITHER,new ImbueData(10,1,1.5f));
            addImbueData(StatusEffects.STRENGTH,new ImbueData(10,2));
            addImbueData(StatusEffects.WEAKNESS,new ImbueData(10,2));
            addImbueData(StatusEffects.SPEED,new ImbueData(10,2));
            addImbueData(StatusEffects.SLOWNESS,new ImbueData(10,2,1.5f));
            addImbueData(StatusEffects.JUMP_BOOST,new ImbueData(10,2));
            addImbueData(StatusEffects.NIGHT_VISION,new ImbueData(0,1,1));
            addImbueData(StatusEffects.BLINDNESS,new ImbueData(0,1,1));
            addImbueData(StatusEffects.WATER_BREATHING,new ImbueData(0,1,1));
            addImbueData(StatusEffects.DOLPHINS_GRACE,new ImbueData(0,3,1));
            addImbueData(StatusEffects.FIRE_RESISTANCE,new ImbueData(0,1,1));
            addImbueData(StatusEffects.INVISIBILITY,new ImbueData(0,0.5f,1));
            addImbueData(StatusEffects.GLOWING,new ImbueData(0,0.25f,1f));
            addImbueData(StatusEffects.RESISTANCE,new ImbueData(4,2f,2));
            addImbueData(StatusEffects.LUCK,new ImbueData(10,0.5f,1.5f));
            addImbueData(StatusEffects.UNLUCK,new ImbueData(10,0.25f,1.2f));
            addImbueData(StatusEffects.SLOW_FALLING,new ImbueData(0,0.5f,1f));
            addImbueData(StatusEffects.LEVITATION,new ImbueData(10,2,2f));
            addImbueData(StatusEffects.HERO_OF_THE_VILLAGE,new ImbueData(3,3f,2f));
            addImbueData(StatusEffects.BAD_OMEN,new ImbueData(0,0.1f,1f));
            addImbueData(StatusEffects.HUNGER,new ImbueData(10,0.5f,1.5f));
            addImbueData(StatusEffects.SATURATION,new ImbueData(10,10f,2f));
            addImbueData(StatusEffects.HASTE,new ImbueData(10,2));
            addImbueData(StatusEffects.MINING_FATIGUE,new ImbueData(10,2));
            addImbueData(StatusEffects.ABSORPTION,new ImbueData(10,1,1.5f));
            addImbueData(StatusEffects.HEALTH_BOOST,new ImbueData(10,1,1.5f));
            addImbueData(StatusEffects.INSTANT_HEALTH,new ImbueData(10,1,1f,true));
            addImbueData(StatusEffects.INSTANT_DAMAGE,new ImbueData(10,2,1f,true));
            IMBUE = register(SpellBlock.Builder.create("imbue")
                    .inputs(
                            SpellSignal.createUUID(null).named("entity"),
                            SpellSignal.createNumber(0).named("amp"),
                            SpellSignal.createNumber(10).named("duration")
                    )
                    .outputs()
                    .parameters(SpellBlock.Parameter.createText("effect","regeneration"))
                    .func((comp,vars) -> {
                        var uuid = vars.getUUID("entity");
                        var effect = vars.getText("effect");
                        var amp = vars.getInt("amp");
                        var duration = vars.getNumber("duration");
                        LivingEntity ent = comp.world() instanceof ServerWorld sw ? (sw.getEntity(uuid) instanceof LivingEntity le ? le : null) : null;
                        if(ent==null) return SpellBlockResult.empty(); // invalid entity
                        if(duration<0||amp<0) return SpellBlockResult.empty(); // invalid amp or duration
                        Identifier id = Identifier.tryParse(effect);
                        if(id==null) { tryLogDebugNoSuchEffect(comp,effect); return SpellBlockResult.empty();  } // invalid status effect
                        if(!imbueData.containsKey(id)) { tryLogDebugUnimbuableEffect(comp,Registries.STATUS_EFFECT.get(id).getName()); return SpellBlockResult.empty();  } // unimbuable status effect;
                        var data = imbueData.get(id);
                        amp = Toolbox.clampI(amp,0,data.maxAmp);
                        if(data.instant) duration = 1/20f;

                        float manaCost = 0.5f + data.getCost(amp,duration);

                        if(trySpendSoul(comp,manaCost)){
                            var effectInst = new StatusEffectInstance(Registries.STATUS_EFFECT.get(id),Math.round(duration*20),amp);
                            ent.addStatusEffect(effectInst);
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,ent.getPos()));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.caster().getPos()));
                        }

                        return SpellBlockResult.empty();
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for(int i = 0; i <6; i++) res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDir(i)).named(i%3==0?"amp":i%3==1?"entity":"duration");
                        return res;
                    })
                    .category(cat).build());

        }

        // reference
        cat = SpellBlock.Category.Reference;
        {
            ACTION = register(SpellBlock.Builder.create("action")
                    .inputs(SpellSignal.createAny().named("trigger"))
                    .outputs()
                    .parameters(SpellBlock.Parameter.createText("function","helloworld"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof SoulCastingItem)) return SpellBlockResult.empty();
                        var funcName = vars.getText("function");
                        // check if specified function exists
                        var refSpell = SoulCastingItem.getSpell(comp.context.casterItem,funcName);
                        if(refSpell==null)
                        {
                            tryLogDebugNoSuchFunction(comp,funcName);
                            return SpellBlockResult.empty();
                        }

                        // run referenced
                        return refSpell.runReferenced(comp.context,comp,vars);
                    })
                    .sideConfigGetter((c)->SpellBlock.SideUtil.sidesInput(c,"trigger"))
                    .category(cat).build());

            PROVIDER = register(SpellBlock.Builder.create("provider")
                    .inputs()
                    .outputs(SpellSignal.createAny().named("res"))
                    .parameters(SpellBlock.Parameter.createText("function","helloworld"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof SoulCastingItem)) return SpellBlockResult.empty();
                        var funcName = vars.getText("function");
                        // check if specified function exists
                        var refSpell = SoulCastingItem.getSpell(comp.context.casterItem,funcName);
                        if(refSpell==null)
                        {
                            tryLogDebugNoSuchFunction(comp,funcName);
                            return SpellBlockResult.empty();
                        }

                        // run referenced
                        return refSpell.runReferenced(comp.context,comp,vars);
                    })
                    .sideConfigGetter((c)->SpellBlock.SideUtil.sidesOutput(c,"res"))
                    .category(cat).build());

            FUNCTION = register(SpellBlock.Builder.create("function")
                    .inputs(SpellSignal.createAny().named("arg"))
                    .outputs(SpellSignal.createAny().named("res"))
                    .parameters(SpellBlock.Parameter.createText("spell","helloworld"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof SoulCastingItem)) return SpellBlockResult.empty();
                        var funcName = vars.getText("spell");
                        // check if specified function exists
                        var refSpell = SoulCastingItem.getSpell(comp.context.casterItem,funcName);
                        if(refSpell==null)
                        {
                            tryLogDebugNoSuchFunction(comp,funcName);
                            return SpellBlockResult.empty();
                        }

                        // run referenced
                        return refSpell.runReferenced(comp.context,comp,vars);
                    })
                    .sideConfigGetter(SpellBlock.SideUtil::sidesFreeform)
                    .category(cat).build());

            FUNCTION2 = register(SpellBlock.Builder.create("function_two")
                    .inputs(SpellSignal.createAny().named("arg"),SpellSignal.createAny().named("arg2"))
                    .outputs(SpellSignal.createAny().named("res"))
                    .parameters(SpellBlock.Parameter.createText("spell","helloworld"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof SoulCastingItem)) return SpellBlockResult.empty();
                        var funcName = vars.getText("spell");
                        // check if specified function exists
                        var refSpell = SoulCastingItem.getSpell(comp.context.casterItem,funcName);
                        if(refSpell==null)
                        {
                            tryLogDebugNoSuchFunction(comp,funcName);
                            return SpellBlockResult.empty();
                        }

                        // run referenced
                        return refSpell.runReferenced(comp.context,comp,vars);
                    })
                    .sideConfigGetter(SpellBlock.SideUtil::sidesFreeform)
                    .category(cat).build());

            // outputs an internal variable from the parent call
            REF_OUTPUT = register(SpellBlock.Builder.create("ref_output")
                    .inputs()
                    .outputs(SpellSignal.createAny().named("var"))
                    .parameters(SpellBlock.Parameter.createText("varName","a"))
                    .func((comp,vars) -> {
                        var parentVar = comp.context.getParentVar(vars.getText("varName"));
                        if(parentVar==null) return SpellBlockResult.empty();
                        SpellBlockResult res = new SpellBlockResult();
                        res.add(parentVar.clone().named("var"));
                        return res;
                    })
                    .sideConfigGetter((c)->SpellBlock.SideUtil.sidesOutput(c,"var"))
                    .category(cat).build());

            // inputs a variable into the reference call result
            REF_INPUT = register(SpellBlock.Builder.create("ref_input")
                    .inputs(SpellSignal.createAny().named("var"))
                    .outputs()
                    .parameters(SpellBlock.Parameter.createText("varName","a"))
                    .func((comp,vars) -> {
                        if(!comp.context.isChild()) return SpellBlockResult.empty();
                        comp.context.referenceResult.add(vars.get("var").clone().named(vars.getText("varName")));
                        return SpellBlockResult.empty();
                    })
                    .sideConfigGetter((c)->SpellBlock.SideUtil.sidesInput(c,"var"))
                    .category(cat).build());
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
    // raycasts
    private static BlockHitResult raycast(SpellComponent comp, SpellBlockArgs vars){
        var from = vars.getVector("from");
        var dir = vars.getVector("dir");
        var length = vars.getNumber("length");
        RaycastContext ctx = new RaycastContext(from,from.add(dir.multiply(length)), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.ANY,comp.caster());
        var hit = comp.world().raycast(ctx);
        return hit;
    }

    private static void logDebug(LivingEntity player, Text text){
        if(player != null)
        {
            World world = player.getWorld();
            if(world instanceof ServerWorld){
                if(player instanceof ServerPlayerEntity p2)
                    p2.sendMessage(text);
            }
            else if(world instanceof ClientWorld){
                if(player instanceof ClientPlayerEntity p3)
                    p3.sendMessage(text);
            }
        }
    }

    public static void tryLogDebug(SpellComponent comp, Text msg){
        if(!comp.context.debugging) return;
        logDebug(comp.context.caster,msg);
    }

    public static void tryLogDebugWrongSignal(SpellComponent comp, SpellSignal.Type got, SpellSignal.Type expected){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.wrongsignal",
                comp.getRuntimeName(),
                Text.translatable("geomancy.spellmaker.types."+got.toString().toLowerCase()).formatted(Formatting.DARK_AQUA),
                Text.translatable("geomancy.spellmaker.types."+expected.toString().toLowerCase()).formatted(Formatting.DARK_AQUA)
                ));
    }

    public static void tryLogDebugDepthLimitReached(SpellComponent comp){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.depthlimit",comp.getRuntimeName()));
    }

    private static void tryLogDebugBroke(SpellComponent comp,float cost){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.broke",comp.getRuntimeName(),cost,comp.context.availableSoul));
    }

    private static void tryLogDebugNoSuchFunction(SpellComponent comp, String spellname){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.nosuchfunction",
                comp.getRuntimeName(),spellname));
    }

    private static void tryLogDebugSlotOOB(SpellComponent comp,int slot){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.slotoob",
                comp.getRuntimeName(),slot));
    }

    private static void tryLogDebugNotPlaceable(SpellComponent comp, ItemStack stack){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.notplaceable",
                comp.getRuntimeName(),stack.getName()));
    }

    private static void tryLogDebugNotReplaceable(SpellComponent comp, BlockState state){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.notreplaceable",
                comp.getRuntimeName(),state.getBlock().getName()));
    }

    private static void tryLogDebugNotbreakable(SpellComponent comp, BlockState state){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.notbreakable",
                comp.getRuntimeName(),state.getBlock().getName()));
    }

    private static void tryLogDebugNoSuchEffect(SpellComponent comp, String text){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.invalideffect",
                comp.getRuntimeName(),Text.literal(text)));
    }

    private static void tryLogDebugUnimbuableEffect(SpellComponent comp, Text text){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.notimbuable",
                comp.getRuntimeName(),text));
    }

    private static void spawnCastParticles(SpellComponent comp,CastParticleData data){
        PacketByteBuf buf = PacketByteBufs.create();
        data.write(buf);
        ModMessages.sendToAllClients((comp.world() instanceof ServerWorld sw) ? sw.getServer() : null,ModMessages.CAST_PARTICLES,buf,serverPlayerEntity ->
                serverPlayerEntity.getWorld().getRegistryKey().getValue().equals(data.world));
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
        functionOrder.put(function.identifier,functionOrder.size());
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

    public static boolean canImbueEffect(String effect){
        return true;
    }

    private static void addImbueData(StatusEffect effect, ImbueData data){
        imbueData.put(Registries.STATUS_EFFECT.getId(effect),data);
    }
    public static class ImbueData{
        public final int maxAmp;
        public final float ampExponent;
        public final float costMult;
        public final boolean instant;

        public ImbueData(int maxAmp, float costMult){
            this(maxAmp,costMult,2,false);
        }

        public ImbueData(int maxAmp, float costMult, float ampExponent){
            this(maxAmp,costMult,ampExponent,false);
        }

        public ImbueData(int maxAmp, float costMult, float ampExponent,boolean instant){
            this.maxAmp=maxAmp;
            this.costMult=costMult;
            this.ampExponent=ampExponent;
            this.instant=instant;
        }

        public float getCost(int amp, float duration) {
            return (float)Math.pow((amp+1),ampExponent) * duration * costMult * 0.2f;
        }
    }
    public static class CastParticleData{
        public Type type = Type.SOUL;
        public int amount = 10;
        public float dispersion = 0.5f;
        public Vec3d pos;
        public Identifier world;

        private CastParticleData(Type type, int amount,Vec3d pos, Identifier world,float dispersion){
            this.type=type;
            this.amount=amount;
            this.pos=pos;
            this.world=world;
            this.dispersion=dispersion;
        }

        public static CastParticleData genericSuccess(SpellComponent comp,Vec3d pos){
            return create(comp,pos).type(Type.SOUL).amount(10);
        }

        public static CastParticleData genericBroke(SpellComponent comp,Vec3d pos){
            return create(comp,pos).type(Type.SOUL_FIRE).amount(10);
        }

        public static CastParticleData genericFail(SpellComponent comp,Vec3d pos){
            return create(comp,pos).type(Type.SOUL_FIRE).amount(10);
        }

        public static CastParticleData create(SpellComponent comp,Vec3d pos){
            return new CastParticleData(Type.SOUL, 10,pos,comp.world().getRegistryKey().getValue(),0.5f);
        }

        public CastParticleData amount(int amount){this.amount = amount;return this;}
        public CastParticleData dispersion(int dispersion){this.dispersion = dispersion;return this;}
        public CastParticleData type(Type type){this.type = type;return this;}

        public void write(PacketByteBuf buf){
            buf.writeString(type.toString());
            buf.writeInt(amount);
            buf.writeFloat(dispersion);
            buf.writeVector3f(pos.toVector3f());
            buf.writeIdentifier(world);
        }

        public static CastParticleData from(PacketByteBuf buf){
            Type type = Type.valueOf(buf.readString());
            int amount = buf.readInt();
            float dispersion = buf.readFloat();
            Vec3d pos = new Vec3d(buf.readVector3f());
            Identifier world = buf.readIdentifier();
            return new CastParticleData(type,amount,pos,world,dispersion);
        }

        public void run(){
            World worldObj = MinecraftClient.getInstance().world;
            if(!worldObj.getRegistryKey().getValue().equals(world)) return; // ignore particle spawns in different worlds
            Random rand = new LocalRandom(GeomancyClient.tick);
            for (int i = 0; i < amount; i++) {
                Vec3d pPos = new Vec3d(
                        pos.x+(rand.nextFloat()*2-1)*dispersion,
                        pos.y+(rand.nextFloat()*2-1)*dispersion,
                        pos.z+(rand.nextFloat()*2-1)*dispersion);
                switch(type){
                    case SOUL:{
                        worldObj.addParticle(ParticleTypes.SOUL,pPos.x,pPos.y,pPos.z,0,0,0);
                        break;
                    }
                    case SOUL_FIRE:{
                        Vec3d randVel = new Vec3d(0,0,0).addRandom(rand,0.08f);
                        worldObj.addParticle(ParticleTypes.SOUL_FIRE_FLAME,pPos.x,pPos.y,pPos.z,randVel.x,randVel.y,randVel.z);
                        break;
                    }
                }
            }
        }

        public enum Type{
            SOUL,
            SOUL_FIRE,
        }
    }
}
