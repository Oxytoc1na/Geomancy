package org.oxytocina.geomancy.spells;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.ISpellSelectorItem;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.armor.CastingArmorItem;
import org.oxytocina.geomancy.items.tools.IVariableStoringItem;
import org.oxytocina.geomancy.items.tools.SoulBoreItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.networking.packet.S2C.CastParticlesS2CPacket;
import org.oxytocina.geomancy.registries.ModRecipeTypes;
import org.oxytocina.geomancy.sound.ModSoundEvents;
import org.oxytocina.geomancy.util.*;

import java.util.*;
import java.util.function.*;

public class SpellBlocks {

    // WARNING: this file is large and i'm sorry

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
    public static final SpellBlock ENTITY_DELEGATE;
    public static final SpellBlock BLOCKPOS_CASTER;
    public static final SpellBlock POS_CASTER;
    public static final SpellBlock EYEPOS_CASTER;
    public static final SpellBlock DIR_CASTER;
    public static final SpellBlock CASTER_SLOT;
    public static final SpellBlock GET_WEATHER;
    public static final SpellBlock GET_TIME;

    // arithmetic
    public static final SpellBlock SUM;
    public static final SpellBlock SUBTRACT;
    public static final SpellBlock MULTIPLY;
    public static final SpellBlock DIVIDE;
    public static final SpellBlock SIN;
    public static final SpellBlock COS;
    public static final SpellBlock TAN;
    public static final SpellBlock EXP;
    public static final SpellBlock LOG;
    public static final SpellBlock MOD;
    public static final SpellBlock VECTOR_SPLIT;
    public static final SpellBlock VECTOR_BUILD;
    public static final SpellBlock VECTOR_ENTITYPOS;
    public static final SpellBlock VECTOR_ENTITYSPAWN;
    public static final SpellBlock VECTOR_ENTITYEYEPOS;
    public static final SpellBlock VECTOR_ENTITYDIR;
    public static final SpellBlock VECTOR_ENTITYVEL;
    public static final SpellBlock RAYCAST_POS;
    public static final SpellBlock RAYCAST_DIR;
    public static final SpellBlock RAYCAST_ENTITY;
    public static final SpellBlock BOOL_ENTITYGROUNDED;
    public static final SpellBlock ENTITY_NEAREST;
    public static final SpellBlock TEXT_ENTITY_ID;
    public static final SpellBlock TEXT_BLOCK_ID;
    public static final SpellBlock INVERT;
    public static final SpellBlock AND;
    public static final SpellBlock EQUALS;
    public static final SpellBlock OR;
    public static final SpellBlock XOR;
    public static final SpellBlock GREATER;
    public static final SpellBlock LESS;
    public static final SpellBlock ENTITY_HAS_EFFECT;
    public static final SpellBlock ENTITY_HEALTH;
    public static final SpellBlock RANDOM_INTEGER;
    public static final SpellBlock PARSE;
    public static final SpellBlock TO_TEXT;
    public static final SpellBlock TRANSLATE;

    // effectors
    public static final SpellBlock PRINT;
    public static final SpellBlock FIREBALL;
    public static final SpellBlock DEBUG;
    public static final SpellBlock SILENT;
    public static final SpellBlock LIGHTNING;
    public static final SpellBlock PLACE;
    public static final SpellBlock BREAK;
    public static final SpellBlock IMBUE;
    public static final SpellBlock TELEPORT;
    public static final SpellBlock DIMHOP;
    public static final SpellBlock PUSH;
    public static final SpellBlock SET_SPELL;
    public static final SpellBlock DEGRADE_BLOCK;
    public static final SpellBlock REPLACE;
    public static final SpellBlock IGNITE;
    public static final SpellBlock PLAY_SOUND;
    public static final SpellBlock DELEGATE;
    public static final SpellBlock SET_WEATHER;
    public static final SpellBlock SET_TIME;
    public static final SpellBlock GROW;
    public static final SpellBlock ACTIVATE;
    public static final SpellBlock TRANSMUTE_ITEM;

    // reference
    public static final SpellBlock ACTION;
    public static final SpellBlock PROVIDER;
    public static final SpellBlock FUNCTION;
    public static final SpellBlock FUNCTION2;
    public static final SpellBlock REF_OUTPUT;
    public static final SpellBlock REF_INPUT;
    public static final SpellBlock VAR_OUTPUT;
    public static final SpellBlock VAR_INPUT;
    public static final SpellBlock VAR_DELETE;
    public static final SpellBlock VAR_EXISTS;

    // lists
    public static final SpellBlock FOREACH;
    public static final SpellBlock SPLIT;
    public static final SpellBlock POP;
    public static final SpellBlock SIZE;
    public static final SpellBlock GET_ELEMENT;
    public static final SpellBlock SET_ELEMENT;
    public static final SpellBlock ENTITIES_NEAR;
    public static final SpellBlock BLOCK_BOX;

    private static final HashMap<Identifier, ImbueData> imbueData = new HashMap();
    private static final List<TransmuteData> transmuteData = new ArrayList<>();
    private static final HashMap<Function<BlockState,Boolean>, BlockState> degradeBlockData = new LinkedHashMap<>();
    private static final HashMap<Function<BlockState,Boolean> , BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult>> igniteBehavior = new LinkedHashMap<>();

    private static SpellBlock.Category cat;
    static{

        // flow control
        cat = SpellBlock.Category.FlowControl;
        {
            CONVEYOR = register(SpellBlock.Builder.create("conveyor")
                    .inputs(SpellSignal.createAny().named("signal"))
                    .outputs(SpellSignal.createAny().named("signal"))
                    .func(SpellBlocks::mirrorInToOut)
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
                        if(component.castClearedData.containsKey("prevent")) return;

                        SpellBlockResult res = new SpellBlockResult();
                        res.depth=component.context.highestRecordedDepth;
                        res.add(SpellSignal.createBoolean(true).named("signal"));
                        component.pushSignals(res);
                    })
                    .category(cat)
                    .build());
        }

        // getters
        cat = SpellBlock.Category.Provider;
        {
            CONST_NUM = register(SpellBlock.Builder.create("constant_number")
                    .outputs(SpellSignal.createNumber(0).named("val"))
                    .parameters(SpellBlock.Parameter.createNumber("val",1,-1000,1000))
                    .func(SpellBlocks::mirrorInToOut)
                    .category(cat).build());

            CONST_TEXT = register(SpellBlock.Builder.create("constant_text")
                    .outputs(SpellSignal.createText("").named("val"))
                    .parameters(SpellBlock.Parameter.createText("val",""))
                    .func(SpellBlocks::mirrorInToOut)
                    .category(cat).build());

            CONST_BOOLEAN = register(SpellBlock.Builder.create("constant_boolean")
                    .outputs(SpellSignal.createBoolean(true).named("val"))
                    .parameters(SpellBlock.Parameter.createBoolean("val",true))
                    .func(SpellBlocks::mirrorInToOut)
                    .category(cat).build());

            ENTITY_CASTER = register(SpellBlock.Builder.create("entity_caster")
                    .outputs(SpellSignal.createUUID().named("caster"))
                    .func((component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        if(component.context.caster!=null) res.add("caster",component.context.caster.getUuid());return res;
                    })
                    .category(cat).build());

            ENTITY_DELEGATE = register(SpellBlock.Builder.create("entity_delegate")
                    .outputs(SpellSignal.createUUID().named("delegate"))
                    .func((component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        if(component.context.delegate!=null) res.add("delegate",component.context.delegate.getUuid());return res;
                    })
                    .category(cat).build());

            BLOCKPOS_CASTER = register(SpellBlock.Builder.create("blockpos_caster")
                    .outputs(SpellSignal.createVector().named("position"))
                    .func((component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        if(component.context.casterBlock!=null) res.add("position",component.context.casterBlock.getPos());return res;
                    })
                    .category(cat).build());

            CASTER_SLOT = register(SpellBlock.Builder.create("caster_slot")
                    .outputs(SpellSignal.createNumber(0).named("slot"))
                    .func((component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        switch(component.context.sourceType)
                        {
                            case Caster : {
                                if(component.context.caster instanceof PlayerEntity pe && component.context.casterItem != null)
                                    res.add("slot",pe.getInventory().getSlotWithStack(component.context.casterItem));
                                break;
                            }

                            case Block : {
                                if(component.context.casterItem != null)
                                    res.add("slot",component.context.casterBlock.getSlotWithStack(component.context.casterItem));
                                break;
                            }
                        }
                        return res;
                    })
                    .category(cat).build());

            POS_CASTER = register(SpellBlock.Builder.create("pos_caster")
                    .outputs(SpellSignal.createVector().named("position"))
                    .func((component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        res.add("position",component.context.getOriginPos());
                        return res;
                    })
                    .category(cat).build());

            EYEPOS_CASTER = register(SpellBlock.Builder.create("eyepos_caster")
                    .outputs(SpellSignal.createVector().named("position"))
                    .func((component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        switch(component.context.sourceType)
                        {
                            case Caster:
                                res.add("position",component.caster().getEyePos());
                                break;
                            case Block:
                            default:
                                res.add("position",component.context.getOriginPos());
                                break;
                        }
                        return res;
                    })
                    .category(cat).build());

            DIR_CASTER = register(SpellBlock.Builder.create("dir_caster")
                    .outputs(SpellSignal.createVector().named("direction"))
                    .func((component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        res.add("direction",component.context.getDirection());
                        return res;
                    })
                    .category(cat).build());

            GET_WEATHER = register(SpellBlock.Builder.create("get_weather")
                    .outputs(SpellSignal.createNumber().named("rainyness"))
                    .func((component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        var props = component.world().getLevelProperties();
                        res.add("rainyness",props.isThundering()?2:props.isRaining()?1:0);
                        return res;
                    })
                    .category(cat).build());

            GET_TIME = register(SpellBlock.Builder.create("get_time")
                    .outputs(SpellSignal.createNumber().named("fraction"))
                    .func((component, stringSpellSignalHashMap) -> {
                        SpellBlockResult res = new SpellBlockResult();
                        var props = component.world().getLevelProperties();
                        res.add("fraction",props.getTimeOfDay()/24000f);
                        return res;
                    })
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
                            var a = vars.get("a");
                            var b = vars.get("b");
                            // summing signals of identical types
                            if(a.type == b.type){
                                switch(a.type){
                                    case Vector:
                                        res.add(SpellSignal.createVector(a.getVectorValue().add(b.getVectorValue())).named("sum"));
                                        break;
                                    case Number:
                                        res.add(SpellSignal.createNumber(a.getNumberValue()+b.getNumberValue()).named("sum"));
                                        break;
                                    case Text:
                                        res.add(SpellSignal.createText(a.getTextValue(comp.context)+b.getTextValue(comp.context)).named("sum"));
                                        break;
                                    case List:
                                        var newlist = a.getListValueOrEmpty();
                                        newlist.addAll(b.getListValueOrEmpty());
                                        res.add(SpellSignal.createList(newlist).named("sum"));
                                        break;
                                    case Boolean:
                                        res.add("sum",a.getBooleanValue() || b.getBooleanValue());
                                        break;
                                }
                            }
                            else{
                                // append signal to list
                                if(a.type == SpellSignal.Type.List || b.type== SpellSignal.Type.List){
                                    if(b.type== SpellSignal.Type.List){
                                        var temp = a;
                                        a=b;
                                        b=temp;
                                    }
                                    var resList = a.getListValueOrEmpty();
                                    resList.add(b);
                                    res.add("sum",resList);
                                }
                                // append generic to text
                                else if(a.type == SpellSignal.Type.Text || b.type== SpellSignal.Type.Text)
                                {
                                    if(b.type== SpellSignal.Type.Text){
                                        var temp = a;
                                        a=b;
                                        b=temp;
                                    }
                                    res.add("sum",a.getTextValue(comp.context)+b.getTextValue(comp.context));
                                }
                            }
                            return res;
                        })
                        .category(cat).build());

                SUBTRACT = register(SpellBlock.Builder.create("subtract")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("diff"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var a = vars.get("a");
                            var b = vars.get("b");
                            // subtracting equal types
                            if(a.type == b.type){
                                switch(a.type){
                                    case Vector:
                                        res.add(SpellSignal.createVector(a.getVectorValue().subtract(b.getVectorValue())).named("diff"));
                                        break;
                                    case Number:
                                        res.add(SpellSignal.createNumber(a.getNumberValue()-b.getNumberValue()).named("diff"));
                                        break;
                                    case Boolean:
                                        res.add("diff",a.getBooleanValue() && !b.getBooleanValue());
                                        break;
                                    case Text:
                                        res.add("diff",a.getTextValue(comp.context).replaceAll(b.getTextValue(comp.context),""));
                                        break;
                                    case List:
                                        var reslist = a.getListValueOrEmpty();
                                        var sublist = b.getListValueOrEmpty();
                                        reslist.removeAll(sublist);
                                        res.add("diff",reslist);
                                        break;
                                }
                            }
                            else{
                                // remove signal from list
                                if(a.type == SpellSignal.Type.List || b.type== SpellSignal.Type.List){
                                    if(b.type== SpellSignal.Type.List){
                                        var temp = a;
                                        a=b;
                                        b=temp;
                                    }
                                    var resList = a.getListValueOrEmpty();
                                    for(int i = 0; i < resList.size();i++){
                                        if(resList.get(i).equals(b))
                                        {
                                            resList.remove(i);
                                            break;
                                        }
                                    }
                                    res.add("diff",resList);
                                }
                                // remove generic from text
                                else if(a.type == SpellSignal.Type.Text || b.type== SpellSignal.Type.Text)
                                {
                                    if(b.type== SpellSignal.Type.Text){
                                        var temp = a;
                                        a=b;
                                        b=temp;
                                    }
                                    res.add("diff",a.getTextValue(comp.context).replaceAll(b.getTextValue(comp.context),""));
                                }
                            }
                            return res;
                        })
                        .category(cat).build());

                MULTIPLY = register(SpellBlock.Builder.create("multiply")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("product"),
                                SpellSignal.createNumber(0).named("dotproduct"),
                                SpellSignal.createVector().named("crossproduct")
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
                                if(a.type== SpellSignal.Type.Vector||b.type== SpellSignal.Type.Vector){
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

                            }
                            return res;
                        })
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
                        .category(cat).build());

                LOG = register(SpellBlock.Builder.create("log")
                        .inputs(SpellSignal.createNumber(0).named("a"),
                                SpellSignal.createNumber(0).named("base"))
                        .outputs(SpellSignal.createNumber(0).named("log"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var a = vars.get("a");
                            var base = vars.get("base");
                            res.add(SpellSignal.createNumber(Toolbox.log(base.getNumberValue(),a.getNumberValue())).named("log"));
                            return res;
                        })
                        .category(cat).build());

                MOD = register(SpellBlock.Builder.create("mod")
                        .inputs(SpellSignal.createNumber().named("a"),
                                SpellSignal.createNumber().named("b"))
                        .outputs(SpellSignal.createNumber().named("remainder"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            res.add("remainder",vars.getNumber("a")%vars.getNumber("b"));
                            return res;
                        })
                        .category(cat).build());

                RAYCAST_POS = register(SpellBlock.Builder.create("raycast_pos")
                        .inputs(SpellSignal.createVector().named("from"),
                                SpellSignal.createVector().named("dir"),
                                SpellSignal.createNumber(0).named("length"))
                        .outputs(SpellSignal.createVector().named("hitPos"))
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
                        .category(cat).build());

                RAYCAST_DIR = register(SpellBlock.Builder.create("raycast_dir")
                        .inputs(SpellSignal.createVector().named("from"),
                                SpellSignal.createVector().named("dir"),
                                SpellSignal.createNumber(0).named("length"))
                        .outputs(SpellSignal.createVector().named("hitDir"))
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
                        .category(cat).build());

                INVERT = register(SpellBlock.Builder.create("invert")
                        .inputs(SpellSignal.createAny().named("signal"))
                        .outputs(SpellSignal.createAny().named("langis"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var sig = vars.get("signal");
                            switch(sig.type){
                                case Text: res.add("langis",new StringBuilder(sig.getTextValue(comp.context)).reverse().toString()); break;
                                case Number: res.add("langis",-sig.getNumberValue()); break;
                                case Vector: res.add("langis",sig.getVectorValue().negate()); break;
                                case Boolean: res.add("langis",!sig.getBooleanValue()); break;
                                case List:{
                                    if(sig.getListValue()!=null)
                                        res.add("langis",Toolbox.reverseList(sig.getListValue()));
                                } break;
                                default:break;
                            }
                            return res;
                        })
                        .category(cat).build());

                RANDOM_INTEGER = register(SpellBlock.Builder.create("random_integer")
                        .inputs(SpellSignal.createNumber().named("exclusivemax"))
                        .outputs(SpellSignal.createNumber().named("random"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var exclusivemax = vars.getInt("exclusivemax");
                            res.add("random",Toolbox.random.nextInt(exclusivemax));
                            return res;
                        })
                        .category(cat).build());

                PARSE = register(SpellBlock.Builder.create("parse")
                        .inputs(SpellSignal.createText().named("text"))
                        .outputs(SpellSignal.createAny().named("res"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var text = vars.getText("text");

                            // parse number
                            try{
                                float f = Float.parseFloat(text);
                                res.add("res",f);
                                return res;
                            }
                            catch(Exception ignored){}

                            // parse vector
                            try{
                                var temp = text.replaceAll("[()]","");
                                var args = temp.split(",");
                                float[] argsF = new float[3];
                                for (int i = 0; i < 3; i++) {
                                    argsF[i] = Float.parseFloat(args[i]);
                                }
                                res.add("res",new Vec3d(argsF[0],argsF[1],argsF[22]));
                                return res;
                            }
                            catch(Exception ignored){}

                            // parse uuid
                            try{
                                var temp = UUID.fromString(text);
                                res.add("res",temp);
                                return res;
                            }
                            catch(Exception ignored){}

                            return res;
                        })
                        .category(cat).build());

                TO_TEXT = register(SpellBlock.Builder.create("to_text")
                        .inputs(SpellSignal.createAny().named("signal"))
                        .outputs(SpellSignal.createText().named("res"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            res.add("res",vars.get("signal").getTextValue());
                            return res;
                        })
                        .category(cat).build());


            }

            // entities
            {
                VECTOR_ENTITYSPAWN = register(SpellBlock.Builder.create("vector_entityspawn")
                        .inputs(SpellSignal.createUUID().named("entity"))
                        .outputs(SpellSignal.createVector().named("position"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            if(!(vars.get("entity").getEntity(component.world()) instanceof ServerPlayerEntity player)) return res;

                            var playerSpawn = player.getSpawnPointPosition();
                            if(playerSpawn==null) playerSpawn = player.getServerWorld().getSpawnPos();

                            res.add(SpellSignal.createVector(playerSpawn.toCenterPos()).named("position"));
                            return res;
                        })
                        .category(cat).build());

                VECTOR_ENTITYPOS = register(SpellBlock.Builder.create("vector_entitypos")
                        .inputs(SpellSignal.createUUID().named("entity"))
                        .outputs(SpellSignal.createVector().named("position"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            var entity = vars.get("entity").getEntity(component.world()); if(entity==null) return res;
                            res.add("position",entity.getPos());
                            return res;
                        })
                        .category(cat).build());

                VECTOR_ENTITYVEL = register(SpellBlock.Builder.create("vector_entityvel")
                        .inputs(SpellSignal.createUUID().named("entity"))
                        .outputs(SpellSignal.createVector().named("velocity"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            var entity = vars.get("entity").getEntity(component.world()); if(entity==null) return res;
                            res.add("velocity",entity.getVelocity());
                            return res;
                        })
                        .category(cat).build());

                VECTOR_ENTITYEYEPOS = register(SpellBlock.Builder.create("vector_entityeyepos")
                        .inputs(SpellSignal.createUUID().named("entity"))
                        .outputs(SpellSignal.createVector().named("eye position"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            var entity = vars.get("entity").getEntity(component.world()); if(entity==null) return res;
                            res.add("eye position",entity.getEyePos());
                            return res;
                        })
                        .category(cat).build());

                VECTOR_ENTITYDIR = register(SpellBlock.Builder.create("vector_entitydir")
                        .inputs(SpellSignal.createUUID().named("entity"))
                        .outputs(SpellSignal.createVector().named("direction"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            var entity = vars.get("entity").getEntity(component.world()); if(entity==null) return res;
                            res.add("direction",entity.getRotationVector());
                            return res;
                        })
                        .category(cat).build());

                BOOL_ENTITYGROUNDED = register(SpellBlock.Builder.create("bool_entitygrounded")
                        .inputs(SpellSignal.createUUID().named("entity"))
                        .outputs(SpellSignal.createBoolean(false).named("grounded"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var entity = vars.get("entity").getEntity(component.world()); if(entity==null) return res;
                            res.add(SpellSignal.createBoolean(entity.isOnGround()).named("grounded"));
                            return res;
                        })
                        .category(cat).build());

                ENTITY_NEAREST = register(SpellBlock.Builder.create("entity_nearest")
                        .inputs(SpellSignal.createVector().named("position"))
                        .outputs(SpellSignal.createUUID().named("entity"))
                        .parameters(SpellBlock.Parameter.createNumber("range",5,0,20))
                        .func((comp, vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var pos = vars.getVector("position");
                            var range = vars.getNumber("range");

                            var ents = comp.world().getEntitiesByClass(Entity.class, Box.from(pos).expand(range),entity -> true);
                            if(ents==null||ents.isEmpty()) return res;
                            Entity ent = null;
                            double minDist = 1000000;
                            for(var cont : ents){
                                double dist = cont.getPos().subtract(pos).length();
                                if(ent==null) {ent=cont;minDist=dist;continue;}
                                if(dist<minDist){minDist=dist;ent=cont;}
                            }
                            if(ent==null) return res;

                            res.add("entity",ent.getUuid());
                            return res;
                        })
                        .category(cat).build());

                TEXT_ENTITY_ID = register(SpellBlock.Builder.create("text_entityid")
                        .inputs(SpellSignal.createUUID().named("entity"))
                        .outputs(SpellSignal.createText().named("id"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            var entity = vars.get("entity").getEntity(component.world()); if(entity==null) return res;
                            res.add("id",Registries.ENTITY_TYPE.getId(entity.getType()).toString());
                            return res;
                        })
                        .category(cat).build());

                ENTITY_HAS_EFFECT = register(SpellBlock.Builder.create("entity_has_effect")
                        .inputs(SpellSignal.createUUID().named("entity"),SpellSignal.createText().named("effect"))
                        .outputs(SpellSignal.createBoolean().named("present"),
                                SpellSignal.createNumber().named("amp"),
                                SpellSignal.createNumber().named("duration"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            if(!(vars.get("entity").getEntity(component.world()) instanceof LivingEntity entity)) return res;
                            Identifier id = Identifier.tryParse(vars.getText("effect"));
                            if(id==null) { tryLogDebugNoSuchEffect(component,vars.getText("effect")); return SpellBlockResult.empty();  } // invalid status effect
                            var effect = Registries.STATUS_EFFECT.get(id);
                            var inst = entity.getStatusEffect(effect);
                            res.add("present",inst!=null);
                            if(inst==null) return res;
                            res.add("amp",inst.getAmplifier());
                            res.add("duration",inst.getDuration()/20f);
                            return res;
                        })
                        .category(cat).build());

                ENTITY_HEALTH = register(SpellBlock.Builder.create("entity_health")
                        .inputs(SpellSignal.createUUID().named("entity"))
                        .outputs(SpellSignal.createNumber().named("health"),
                                SpellSignal.createNumber().named("maxHealth"),
                                SpellSignal.createNumber().named("air"),
                                SpellSignal.createNumber().named("maxAir"),
                                SpellSignal.createNumber().named("absorption"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            if(!(vars.get("entity").getEntity(component.world()) instanceof LivingEntity entity)) return res;
                            res.add("health",entity.getHealth());
                            res.add("maxHealth",entity.getMaxHealth());
                            res.add("absorption",entity.getAbsorptionAmount());
                            res.add("air",entity.getAir()/20f);
                            res.add("maxAir",entity.getAir()/20f);
                            return res;
                        })
                        .category(cat).build());

                RAYCAST_ENTITY = register(SpellBlock.Builder.create("raycast_entity")
                        .inputs(SpellSignal.createVector().named("from"),
                                SpellSignal.createVector().named("dir"),
                                SpellSignal.createNumber().named("length"))
                        .outputs(SpellSignal.createUUID().named("entity"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var hit = raycastEntity(comp,vars.getVector("from"),vars.getVector("from").add(vars.getVector("dir").multiply(vars.getNumber("length"))));
                            if(hit.getType()== HitResult.Type.ENTITY){
                                var hitEnt = hit.getEntity();
                                res.add(SpellSignal.createUUID(hitEnt).named("entity"));
                            }
                            return res;
                        })
                        .category(cat).build());


            }

            // vector math
            {
                VECTOR_SPLIT = register(SpellBlock.Builder.create("vector_split")
                        .inputs(SpellSignal.createVector().named("vector"))
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
                        .category(cat).build());

                VECTOR_BUILD = register(SpellBlock.Builder.create("vector_build")
                        .inputs(SpellSignal.createNumber(0).named("x"),
                                SpellSignal.createNumber(0).named("y"),
                                SpellSignal.createNumber(0).named("z"))
                        .outputs(SpellSignal.createVector().named("vec"))
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
                        .category(cat).build());
            }

            // bit math
            {
                AND = register(SpellBlock.Builder.create("and")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("res"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var a = vars.get("a");
                            var b = vars.get("b");
                            // summing signals of identical types
                            if(a.type == b.type){
                                switch(a.type){
                                    case Number:
                                        res.add(SpellSignal.createNumber(a.getIntValue()&b.getIntValue()).named("res"));
                                        break;
                                    case List:
                                        var alist = a.getListValueOrEmpty();
                                        var blist = b.getListValueOrEmpty();
                                        List<SpellSignal> reslist = new ArrayList<>();
                                        for(var as : alist){
                                            for(var bs : blist){
                                                if(as.equals(bs))
                                                {
                                                    reslist.add(as);
                                                    break;
                                                }
                                            }
                                        }
                                        res.add(SpellSignal.createList(reslist).named("res"));
                                        break;
                                    case Boolean:
                                        res.add("res",a.getBooleanValue() && b.getBooleanValue());
                                        break;
                                }
                            }
                            else{
                                if(a.type == SpellSignal.Type.Boolean || b.type== SpellSignal.Type.Boolean){
                                    if(b.type== SpellSignal.Type.Boolean){
                                        var temp = a;
                                        a=b;
                                        b=temp;
                                    }
                                    if(a.getBooleanValue())
                                        res.add(b.named("res"));
                                }
                            }
                            return res;
                        })
                        .category(cat).build());

                OR = register(SpellBlock.Builder.create("or")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("res"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var a = vars.get("a");
                            var b = vars.get("b");
                            // summing signals of identical types
                            if(a.type == b.type){
                                switch(a.type){
                                    case Number:
                                        res.add(SpellSignal.createNumber(a.getIntValue()|b.getIntValue()).named("res"));
                                        break;
                                    case List:
                                        var reslist = a.getListValueOrEmpty();
                                        var blist = b.getListValueOrEmpty();
                                        for(var bs : blist){
                                            boolean added = false;
                                            for(var as : reslist){
                                                if(bs.equals(as))
                                                {
                                                    added = true;
                                                    break;
                                                }
                                            }
                                            if(!added)
                                                reslist.add(bs);
                                        }
                                        res.add(SpellSignal.createList(reslist).named("res"));
                                        break;
                                    case Boolean:
                                        res.add("res",a.getBooleanValue() || b.getBooleanValue());
                                        break;
                                }
                            }
                            return res;
                        })
                        .category(cat).build());

                XOR = register(SpellBlock.Builder.create("xor")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("res"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var a = vars.get("a");
                            var b = vars.get("b");
                            // summing signals of identical types
                            if(a.type == b.type){
                                switch(a.type){
                                    case Number:
                                        res.add(SpellSignal.createNumber(a.getIntValue()^b.getIntValue()).named("res"));
                                        break;
                                    case List:
                                        var reslist = a.getListValueOrEmpty();
                                        var blist = b.getListValueOrEmpty();
                                        for(var bs : blist){
                                            boolean added = false;
                                            for(var as : reslist){
                                                if(bs.equals(as))
                                                {
                                                    added = true;
                                                    break;
                                                }
                                            }
                                            if(!added)
                                                reslist.add(bs);
                                        }
                                        res.add(SpellSignal.createList(reslist).named("res"));
                                        break;
                                    case Boolean:
                                        res.add("res",a.getBooleanValue() ^ b.getBooleanValue());
                                        break;
                                }
                            }
                            return res;
                        })
                        .category(cat).build());

                EQUALS = register(SpellBlock.Builder.create("equals")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("res"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var a = vars.get("a");
                            var b = vars.get("b");
                            res.add("res",a.softEquals(b));
                            return res;
                        })
                        .category(cat).build());

                GREATER = register(SpellBlock.Builder.create("greater")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("res"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var a = vars.get("a");
                            var b = vars.get("b");
                            if(a.type==b.type){
                                switch(a.type){
                                    case Number : res.add("res",a.getNumberValue() > b.getNumberValue()); break;
                                    case List:res.add("res",a.getListValueOrEmpty().size() > b.getListValueOrEmpty().size()); break;
                                    case Text:res.add("res",a.getTextValue().length()>b.getTextValue().length());break;
                                    case Vector:res.add("res",a.getVectorValue().length()>b.getVectorValue().length()); break;
                                    case Boolean:res.add("res",a.getBooleanValue() && !b.getBooleanValue()); break;
                                }
                            }
                            return res;
                        })
                        .category(cat).build());

                LESS = register(SpellBlock.Builder.create("less")
                        .inputs(SpellSignal.createAny().named("a"),
                                SpellSignal.createAny().named("b"))
                        .outputs(SpellSignal.createAny().named("res"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var a = vars.get("a");
                            var b = vars.get("b");
                            if(a.type==b.type){
                                switch(a.type){
                                    case Number : res.add("res",a.getNumberValue() < b.getNumberValue()); break;
                                    case List:res.add("res",a.getListValueOrEmpty().size() < b.getListValueOrEmpty().size()); break;
                                    case Text:res.add("res",a.getTextValue().length()<b.getTextValue().length());break;
                                    case Vector:res.add("res",a.getVectorValue().length()<b.getVectorValue().length()); break;
                                    case Boolean:res.add("res",!a.getBooleanValue() && b.getBooleanValue()); break;
                                }
                            }
                            return res;
                        })
                        .category(cat).build());
            }

            //blocks
            {
                TEXT_BLOCK_ID = register(SpellBlock.Builder.create("text_blockid")
                        .inputs(SpellSignal.createVector().named("position"))
                        .outputs(SpellSignal.createText().named("id"))
                        .parameters()
                        .func((component, vars) -> {
                            SpellBlockResult res = new SpellBlockResult();
                            var pos = vars.getBlockPos("position");
                            var state = component.world().getBlockState(pos);
                            if(state==null) return res;
                            res.add("id",Registries.BLOCK.getId(state.getBlock()).toString());
                            return res;
                        })
                        .category(cat).build());
            }

            //misc
            {
                TRANSLATE = register(SpellBlock.Builder.create("translate")
                        .inputs(SpellSignal.createText().named("key"))
                        .outputs(SpellSignal.createAny().named("translated"))
                        .parameters()
                        .func((comp,vars) -> {
                            SpellBlockResult res = SpellBlockResult.empty();
                            var in = vars.getText("key");
                            res.add("translated",Text.translatable(in).getString());
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
                    .func((comp,vars) -> {
                        if(comp.context.caster != null)
                        {
                            World world = comp.context.caster.getWorld();
                            if(world instanceof ServerWorld serverWorld){
                                if(comp.context.caster instanceof ServerPlayerEntity player)
                                    player.sendMessage(Text.literal(vars.get("val").toString(comp.context)));
                            }
                        }
                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            DEBUG = register(SpellBlock.Builder.create("debug")
                    .func((comp,vars) -> SpellBlockResult.empty())
                    .init(component -> { component.context.debugging=true;})
                    .category(cat).build());

            SILENT = register(SpellBlock.Builder.create("silent")
                    .func((comp,vars) -> SpellBlockResult.empty())
                    .init(component -> { component.context.silent=true;})
                    .category(cat).build());

            FIREBALL = register(SpellBlock.Builder.create("fireball")
                    .inputs(SpellSignal.createVector().named("position"),
                            SpellSignal.createVector().named("direction"),
                            SpellSignal.createNumber(0).named("speed"),
                            SpellSignal.createNumber(0).named("power"))
                    .func((comp,vars) -> {
                        var dir = vars.getVector("direction");
                        var speed =vars.getNumber("speed");
                        var power =vars.getInt("power");
                        var pos = vars.getVector("position");

                        float manaCost = 2
                                +normalCastOffsetSoulCost(comp,pos)
                                +(float)Math.pow(power*1.5,1.5);

                        if(trySpendSoul(comp,manaCost)){
                            var vel = dir.multiply(speed);
                            FireballEntity fireball = new FireballEntity(comp.world(),comp.context.caster,vel.x,vel.y,vel.z,power);
                            fireball.setPosition(pos);
                            comp.world().spawnEntity(fireball);
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,pos));
                            if(comp.context.root().grid.name=="fireball" && comp.context.casterItem.getItem() instanceof CastingArmorItem cai && cai.getType() == ArmorItem.Type.BOOTS)
                            {
                                if(comp.context.root().internalVars.has("message") && comp.context.root().internalVars.getText("message").contains("fireball"))
                                {
                                    tryUnlockSpellAdvancement(comp,"fireball");
                                }
                            }
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,pos));
                        }

                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            LIGHTNING = register(SpellBlock.Builder.create("lightning")
                    .inputs(SpellSignal.createVector().named("position"))
                    .func((comp,vars) -> {
                        var pos = vars.getVector("position");

                        float manaCost = 10
                                +normalCastOffsetSoulCost(comp,pos);

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
                    .category(cat).build());

            TELEPORT = register(SpellBlock.Builder.create("teleport")
                    .inputs(
                            SpellSignal.createUUID().named("entity"),
                            SpellSignal.createVector().named("position")
                    )
                    .func((comp,vars) -> {
                        var uuid = vars.getUUID("entity");
                        var pos = vars.getVector("position");
                        Entity ent = comp.world() instanceof ServerWorld sw ? sw.getEntity(uuid) : null;
                        if(ent==null) return SpellBlockResult.empty();

                        float manaCost = 0
                                +normalCastOffsetSoulCost(comp,pos);

                        if(trySpendSoul(comp,manaCost)){
                            //comp.world().setBlockState(Toolbox.posToBlockPos(pos), Blocks.GLOWSTONE.getDefaultState());
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,ent.getPos()));
                            ent.teleport(pos.x,pos.y,pos.z);
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,pos));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.context.getOriginPos()));
                        }

                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            DIMHOP = register(SpellBlock.Builder.create("dimhop")
                    .inputs(
                            SpellSignal.createUUID().named("entity"),
                            SpellSignal.createText().named("dimension")
                    )
                    .func((comp,vars) -> {
                        var uuid = vars.getUUID("entity");
                        var dim = vars.getText("dimension");
                        Entity ent = comp.world() instanceof ServerWorld sw ? sw.getEntity(uuid) : null;
                        if(ent==null) return SpellBlockResult.empty();

                        float manaCost = 200;

                        Identifier destinationID = Identifier.tryParse(dim);
                        if(destinationID==null) try
                        { destinationID = new Identifier(Identifier.DEFAULT_NAMESPACE,dim); } catch (Exception ignored) {}

                        // malformed destination identifier
                        if(destinationID==null) return SpellBlockResult.empty();
                        if(!(comp.world() instanceof ServerWorld sw)) return SpellBlockResult.empty();

                        ServerWorld destination = null;
                        for(var worldKey : sw.getServer().getWorldRegistryKeys()){
                            if(worldKey.getValue().equals(destinationID)){
                                destination = sw.getServer().getWorld(worldKey);
                                break;
                            }
                        }

                        // non-existent destination
                        if(destination == null) return SpellBlockResult.empty();
                        // trying to travel to the dimension we're already in
                        if(sw.getRegistryKey() == destination.getRegistryKey()) return SpellBlockResult.empty();

                        if(trySpendSoul(comp,manaCost)){
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,ent.getPos()));
                            ent.teleport(destination,ent.getX(),ent.getY(),ent.getZ(),null,ent.getYaw(),ent.getPitch());
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.context.getOriginPos()));
                        }

                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            PUSH = register(SpellBlock.Builder.create("push")
                    .inputs(
                            SpellSignal.createUUID().named("entity"),
                            SpellSignal.createVector().named("velocity")
                    )
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
                            if(vel.y>=1)
                                tryUnlockSpellAdvancement(comp,"liftoff");
                            if(entity==comp.caster()){
                                if(!entity.isOnGround() && comp.context.casterItem.getItem() instanceof CastingArmorItem cai && cai.getType()== ArmorItem.Type.LEGGINGS)
                                    tryUnlockSpellAdvancement(comp,"brazilian");
                                if(comp.context.isActivatedByHotkey())
                                    tryUnlockSpellAdvancement(comp,"celeste");
                            }
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.context.getOriginPos()));
                        }

                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            PLACE = register(SpellBlock.Builder.create("place")
                    .inputs(
                            SpellSignal.createVector().named("position"),
                            SpellSignal.createNumber().named("slot")
                    ).parameters()
                    .func((comp,vars) -> {
                        var pos = vars.getVector("position");
                        var slot = vars.getNumber("slot");
                        int slotInt = Math.round(slot);
                        ItemStack stack = ItemStack.EMPTY;
                        BlockItem bi = null;
                        switch (comp.context.sourceType){
                            case Caster :
                                if(!(comp.caster() instanceof PlayerEntity pe)) break;
                                if(slotInt <0||slotInt>=pe.getInventory().size()) { tryLogDebugSlotOOB(comp,slotInt); return SpellBlockResult.empty();} // slot OOB
                                stack = pe.getInventory().getStack(slotInt);
                                if(!(stack.getItem() instanceof BlockItem)) { tryLogDebugNotPlaceable(comp,stack); return SpellBlockResult.empty(); } // not a block
                                bi = (BlockItem)stack.getItem();
                                break;
                            case Block:
                                if(slotInt <0||slotInt>=comp.context.casterBlock.size()) { tryLogDebugSlotOOB(comp,slotInt); return SpellBlockResult.empty();} // slot OOB
                                stack = comp.context.casterBlock.getStack(slotInt);
                                if(!(stack.getItem() instanceof BlockItem)) { tryLogDebugNotPlaceable(comp,stack); return SpellBlockResult.empty(); } // not a block
                                bi = (BlockItem)stack.getItem();
                                break;
                            case Delegate:
                                Inventory inv = comp.context.getInventory();
                                if(inv==null) break;
                                if(slotInt <0||slotInt>=inv.size()) { tryLogDebugSlotOOB(comp,slotInt); return SpellBlockResult.empty();} // slot OOB
                                stack = inv.getStack(slotInt);
                                if(!(stack.getItem() instanceof BlockItem)) { tryLogDebugNotPlaceable(comp,stack); return SpellBlockResult.empty(); } // not a block
                                bi = (BlockItem)stack.getItem();
                                break;
                        }
                        if(stack.isEmpty()) return SpellBlockResult.empty();

                        float manaCost = 1
                                +normalCastOffsetSoulCost(comp,pos);

                        if(canAfford(comp,manaCost)){
                            var blockPos = Toolbox.posToBlockPos(pos);

                            BlockState targetState = comp.world().getBlockState(blockPos);
                            if(!targetState.isReplaceable())
                            {
                                // couldnt replace
                                tryLogDebugNotReplaceable(comp,targetState);
                                return SpellBlockResult.empty();
                            }

                            // place block in world
                            comp.world().setBlockState(Toolbox.posToBlockPos(pos), bi.getBlock().getDefaultState());

                            // remove block from inventory
                            if(!(comp.context.sourceType== SpellContext.SourceType.Caster && ((PlayerEntity)comp.caster()).isCreative()))
                                stack.decrement(1);

                            Toolbox.playSound(bi.getBlock().getSoundGroup(targetState).getPlaceSound(),comp.world(),blockPos, SoundCategory.BLOCKS,1,1);
                            if(comp.caster()!=null && EntityUtil.distanceTo(comp.caster(),pos) >7)
                                tryUnlockSpellAdvancement(comp,"long_arms");
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
                    .category(cat).build());

            BREAK = register(SpellBlock.Builder.create("break")
                    .inputs(
                            SpellSignal.createVector().named("position"),
                            SpellSignal.createBoolean(true).named("silk touch"),
                            SpellSignal.createBoolean(true).named("autocollect")
                    ).parameters()
                    .func((comp,vars) -> {
                        var pos = vars.getVector("position");
                        var silkTouch = vars.getBoolean("silk touch");
                        var autocollect = vars.getBoolean("autocollect");
                        var blockPos = Toolbox.posToBlockPos(pos);

                        // calculate breaking cost
                        BlockState targetState = comp.world().getBlockState(blockPos);

                        float manaCost = 0.2f
                                +targetState.getBlock().getHardness()/5f* (silkTouch?2:1)
                                +(autocollect?0.2f:0f)
                                +normalCastOffsetSoulCost(comp,pos);

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

                            PlayerEntity pe = switch(comp.context.sourceType){
                                case Caster -> (PlayerEntity) comp.caster();
                                case Delegate -> (PlayerEntity) comp.caster();
                                default->null;
                            };

                            boolean broke = BlockHelper.breakBlock(pe,stack,comp.world(),blockPos,minableBlocksPredicate,!autocollect);

                            if(!broke){
                                // couldnt mine... again?
                                tryLogDebugNotbreakable(comp,targetState);
                                return SpellBlockResult.empty();
                            }

                            if(autocollect){
                                // give the caster the drops
                                var stacks = Block.getDroppedStacks(targetState,(ServerWorld)comp.world(),blockPos,comp.world().getBlockEntity(blockPos),comp.caster(),stack);
                                var casterPos = comp.context.getOriginPos();
                                switch(comp.context.sourceType){
                                    case Block:
                                    {
                                        for(var s : stacks){
                                            s = comp.context.casterBlock.tryCollect(s);
                                            if(s.isEmpty()) continue;

                                            ItemEntity ie = new ItemEntity(comp.world(),casterPos.x,casterPos.y,casterPos.z,s);
                                            comp.world().spawnEntity(ie);
                                        }
                                        break;
                                    }

                                    case Delegate:
                                    {
                                        if(comp.context.casterBlock!=null){
                                            for(var s : stacks){
                                                s = comp.context.casterBlock.tryCollect(s);
                                                if(s.isEmpty()) continue;

                                                ItemEntity ie = new ItemEntity(comp.world(),casterPos.x,casterPos.y,casterPos.z,s);
                                                comp.world().spawnEntity(ie);
                                            }
                                        }
                                        else{
                                            for(var s : stacks){
                                                s = comp.context.casterBlock.tryCollect(s);
                                                if(s.isEmpty()) continue;

                                                ItemEntity ie = new ItemEntity(comp.world(),casterPos.x,casterPos.y,casterPos.z,s);
                                                comp.world().spawnEntity(ie);
                                            }
                                        }
                                        break;
                                    }

                                    default : {
                                        for(var s : stacks){
                                            ItemEntity ie = new ItemEntity(comp.world(),casterPos.x,casterPos.y,casterPos.z,s);
                                            comp.world().spawnEntity(ie);
                                        }
                                        break;
                                    }
                                }

                            }

                            if(comp.caster()!=null && EntityUtil.distanceTo(comp.caster(),pos) >7)
                                tryUnlockSpellAdvancement(comp,"long_arms");
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
                    .category(cat).build());

            SET_SPELL = register(SpellBlock.Builder.create("set_spell")
                    .inputs(
                            SpellSignal.createText().named("spell")
                    ).parameters()
                    .func((comp,vars) -> {
                        var spell = vars.getText("spell");
                        if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                        final float manaCost = 3;
                        if(canAfford(comp,manaCost)){
                            if(!sps.setSelectedSpell(comp.context.casterItem,spell))
                            {
                                // couldnt set spell
                                //tryLogDebugNotReplaceable(comp,targetState);
                                return SpellBlockResult.empty();
                            }

                            trySpendSoul(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,comp.context.getOriginPos()));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.context.getOriginPos()));
                        }
                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            // degrade block data
            {
                addDegradeBlockData(Blocks.COBWEB,Blocks.TRIPWIRE);
                addDegradeBlockData(Blocks.TNT,Blocks.SAND);
                addDegradeBlockData(Blocks.COBBLED_DEEPSLATE,Blocks.STONE);
                addDegradeBlockData(Blocks.COBBLESTONE,Blocks.GRAVEL);
                addDegradeBlockData(Blocks.GRAVEL,Blocks.SAND);
                addDegradeBlockData(Blocks.SAND,Blocks.DIRT);
                addDegradeBlockData(Blocks.ANVIL,Blocks.CHIPPED_ANVIL);
                addDegradeBlockData(Blocks.CHIPPED_ANVIL,Blocks.DAMAGED_ANVIL);
                addDegradeBlockData(Blocks.DAMAGED_ANVIL,Blocks.AIR);
                addDegradeBlockData(Blocks.BOOKSHELF,Blocks.CHISELED_BOOKSHELF);
                // ore blocks to ores
                addDegradeBlockData(Blocks.COAL_BLOCK,Blocks.COAL_ORE);
                addDegradeBlockData(Blocks.IRON_BLOCK,Blocks.IRON_ORE);
                addDegradeBlockData(Blocks.GOLD_BLOCK,Blocks.GILDED_BLACKSTONE);
                addDegradeBlockData(Blocks.REDSTONE_BLOCK,Blocks.REDSTONE_ORE);
                addDegradeBlockData(Blocks.LAPIS_BLOCK,Blocks.LAPIS_ORE);
                addDegradeBlockData(Blocks.COPPER_BLOCK,Blocks.COPPER_ORE);
                addDegradeBlockData(Blocks.DIAMOND_BLOCK,Blocks.DIAMOND_ORE);
                addDegradeBlockData(Blocks.DIAMOND_ORE,Blocks.COAL_ORE);
                addDegradeBlockData(Blocks.DEEPSLATE_DIAMOND_ORE,Blocks.DEEPSLATE_COAL_ORE);
                addDegradeBlockData(Blocks.EMERALD_BLOCK,Blocks.EMERALD_ORE);
                addDegradeBlockData(Blocks.QUARTZ_BLOCK,Blocks.NETHER_QUARTZ_ORE);
                addDegradeBlockData(ModBlocks.LEAD_BLOCK,ModBlocks.LEAD_ORE);
                addDegradeBlockData(ModBlocks.TITANIUM_BLOCK,ModBlocks.TITANIUM_ORE);
                addDegradeBlockData(ModBlocks.MOLYBDENUM_BLOCK,ModBlocks.MOLYBDENUM_ORE);
                addDegradeBlockData(ModBlocks.MITHRIL_BLOCK,ModBlocks.MITHRIL_ORE);
                addDegradeBlockData(ModBlocks.OCTANGULITE_BLOCK,ModBlocks.OCTANGULITE_ORE);
                addDegradeBlockData(ModBlocks.TOURMALINE_BLOCK,ModBlocks.TOURMALINE_ORE);
                addDegradeBlockData(ModBlocks.ORTHOCLASE_BLOCK,ModBlocks.ORTHOCLASE_ORE);
                addDegradeBlockData(ModBlocks.AXINITE_BLOCK,ModBlocks.AXINITE_ORE);
                addDegradeBlockData(ModBlocks.PERIDOT_BLOCK,ModBlocks.PERIDOT_ORE);

                addDegradeBlockData(Blocks.STONE,Blocks.COBBLESTONE);
                addDegradeBlockData(b->b.isIn(BlockTags.STONE_ORE_REPLACEABLES),Blocks.STONE.getDefaultState());
                addDegradeBlockData(Blocks.DEEPSLATE,Blocks.COBBLED_DEEPSLATE);
                addDegradeBlockData(b->b.isIn(BlockTags.DEEPSLATE_ORE_REPLACEABLES),Blocks.DEEPSLATE.getDefaultState());

                // ores to stone
                addDegradeBlockData(b->Registries.BLOCK.getId(b.getBlock()).getPath().contains("_ore") && Registries.BLOCK.getId(b.getBlock()).getPath().contains("deepslate"),Blocks.DEEPSLATE.getDefaultState());
                addDegradeBlockData(b->Registries.BLOCK.getId(b.getBlock()).getPath().contains("_ore"),Blocks.STONE.getDefaultState());
            }
            DEGRADE_BLOCK = register(SpellBlock.Builder.create("degrade_block")
                    .inputs(
                            SpellSignal.createVector().named("position")
                    ).parameters()
                    .func((comp,vars) -> {
                        if(!(comp.world() instanceof ServerWorld sw)) return SpellBlockResult.empty(); // not in a server world
                        var pos = vars.getVector("position");
                        var blockPos = vars.getBlockPos("position");

                        // calculate breaking cost
                        BlockState targetState = comp.world().getBlockState(blockPos);

                        float manaCost = 1f
                                +targetState.getBlock().getHardness()/10f
                                +normalCastOffsetSoulCost(comp,pos);

                        if(canAfford(comp,manaCost)){

                            // special interactions
                            for(var predicate : degradeBlockData.keySet())
                                if(predicate.apply(targetState))
                                {
                                    if (!BlockHelper.replaceBlock(comp.world(),blockPos,degradeBlockData.get(predicate))) {
                                        // couldnt replace
                                        tryLogDebugNotbreakable(comp,targetState);
                                        return SpellBlockResult.empty();
                                    }

                                    trySpendSoul(comp,manaCost);
                                    spawnCastParticles(comp,CastParticleData.genericSuccess(comp,pos));
                                    return SpellBlockResult.empty();
                                }

                            // replace with mined variant
                            {
                                ItemStack stack = new ItemStack(Items.DIRT);
                                if(targetState.isToolRequired()){
                                    if(targetState.isIn(BlockTags.PICKAXE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_PICKAXE);
                                    else if(targetState.isIn(BlockTags.AXE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_AXE);
                                    else if(targetState.isIn(BlockTags.SHOVEL_MINEABLE)) stack = new ItemStack(Items.NETHERITE_SHOVEL);
                                    else if(targetState.isIn(BlockTags.HOE_MINEABLE)) stack = new ItemStack(Items.NETHERITE_HOE);
                                    else if(targetState.isIn(BlockTags.SWORD_EFFICIENT)) stack = new ItemStack(Items.NETHERITE_SWORD);
                                }
                                final ItemStack s2 = stack.copy();
                                Predicate<BlockState> minableBlocksPredicate = s -> !s.isToolRequired() || s2.isSuitableFor(s);
                                if (!minableBlocksPredicate.test(targetState)) {
                                    // couldnt mine
                                    tryLogDebugNotbreakable(comp,targetState);
                                    return SpellBlockResult.empty();
                                }

                                // fetch replacement state
                                var droppedStacks = Block.getDroppedStacks(targetState,sw,blockPos,comp.context.casterBlock,comp.caster(),stack);
                                BlockState replacementState = Blocks.AIR.getDefaultState();
                                for(var droppedStack:droppedStacks){
                                    if(!(droppedStack.getItem() instanceof BlockItem bi)) continue;
                                    replacementState = bi.getBlock().getDefaultState();
                                    break;
                                }

                                // replacing a block with itself, unnecessary
                                if(targetState.isOf(replacementState.getBlock()))
                                {
                                    return SpellBlockResult.empty();
                                }

                                if (!BlockHelper.replaceBlock(comp.world(),blockPos,replacementState)) {
                                    // couldnt replace
                                    tryLogDebugNotbreakable(comp,targetState);
                                    return SpellBlockResult.empty();
                                }

                                trySpendSoul(comp,manaCost);
                                spawnCastParticles(comp,CastParticleData.genericSuccess(comp,pos));
                            }
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,pos));
                        }

                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            // imbue data
            {
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
                addImbueData(StatusEffects.INSTANT_HEALTH,new ImbueData(10,50,1f,true));
                addImbueData(StatusEffects.INSTANT_DAMAGE,new ImbueData(10,70,1f,true));
            }
            IMBUE = register(SpellBlock.Builder.create("imbue")
                    .inputs(
                            SpellSignal.createUUID().named("entity"),
                            SpellSignal.createNumber(0).named("amp"),
                            SpellSignal.createNumber(10).named("duration")
                    )
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
                        if(data.instant) duration = 1f;

                        float manaCost = 0.5f + data.getCost(amp,duration);

                        if(trySpendSoul(comp,manaCost)){
                            var effectInst = new StatusEffectInstance(Registries.STATUS_EFFECT.get(id),Math.round(duration*20),amp);
                            ent.addStatusEffect(effectInst);
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,ent.getPos()));

                            if(ent instanceof PlayerEntity pe && pe!=comp.caster() && List.of(
                                    StatusEffects.INSTANT_HEALTH,StatusEffects.REGENERATION).contains(effectInst.getEffectType()))
                                tryUnlockSpellAdvancement(comp,"medic");
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.context.getOriginPos()));
                        }

                        return SpellBlockResult.empty();
                    })
                    .sideConfigGetter((comp)->{
                        SpellComponent.SideConfig[] res = new SpellComponent.SideConfig[6];
                        for(int i = 0; i <6; i++) res[i] = SpellComponent.SideConfig.createToggleableInput(comp,SpellComponent.getDirString(i)).named(i%3==0?"amp":i%3==1?"entity":"duration");
                        return res;
                    })
                    .category(cat).build());

            REPLACE = register(SpellBlock.Builder.create("replace")
                    .inputs(
                            SpellSignal.createVector().named("position"),
                            SpellSignal.createBoolean(true).named("silk touch"),
                            SpellSignal.createNumber().named("slot")
                    ).parameters()
                    .func((comp,vars) -> {
                        var pos = vars.getVector("position");
                        var silkTouch = vars.getBoolean("silk touch");
                        var slotInt = vars.getInt("slot");
                        ItemStack replaceWithStack = ItemStack.EMPTY;
                        BlockItem bi = null;
                        switch (comp.context.sourceType){
                            case Caster :
                                if(!(comp.caster() instanceof PlayerEntity pe)) break;
                                if(slotInt <0||slotInt>=pe.getInventory().size()) { tryLogDebugSlotOOB(comp,slotInt); return SpellBlockResult.empty();} // slot OOB
                                replaceWithStack = pe.getInventory().getStack(slotInt);
                                if(!(replaceWithStack.getItem() instanceof BlockItem)) { tryLogDebugNotPlaceable(comp,replaceWithStack); return SpellBlockResult.empty(); } // not a block
                                bi = (BlockItem)replaceWithStack.getItem();
                                break;
                            case Block:
                                if(slotInt <0||slotInt>=comp.context.casterBlock.size()) { tryLogDebugSlotOOB(comp,slotInt); return SpellBlockResult.empty();} // slot OOB
                                replaceWithStack = comp.context.casterBlock.getStack(slotInt);
                                if(!(replaceWithStack.getItem() instanceof BlockItem)) { tryLogDebugNotPlaceable(comp,replaceWithStack); return SpellBlockResult.empty(); } // not a block
                                bi = (BlockItem)replaceWithStack.getItem();
                                break;
                            case Delegate:
                                if(slotInt <0||slotInt>=comp.context.getInventory().size()) { tryLogDebugSlotOOB(comp,slotInt); return SpellBlockResult.empty();} // slot OOB
                                replaceWithStack = comp.context.getInventory().getStack(slotInt);
                                if(!(replaceWithStack.getItem() instanceof BlockItem)) { tryLogDebugNotPlaceable(comp,replaceWithStack); return SpellBlockResult.empty(); } // not a block
                                bi = (BlockItem)replaceWithStack.getItem();
                                break;
                        }
                        if(replaceWithStack.isEmpty()) return SpellBlockResult.empty();

                        var blockPos = Toolbox.posToBlockPos(pos);

                        // calculate breaking cost
                        BlockState targetState = comp.world().getBlockState(blockPos);

                        float manaCost = 1.2f
                                +targetState.getBlock().getHardness()/5f* (silkTouch?2:1)
                                +normalCastOffsetSoulCost(comp,pos);

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

                            PlayerEntity pe = switch (comp.context.sourceType){
                                case Caster -> (PlayerEntity) comp.caster();
                                case Delegate -> (PlayerEntity) comp.caster();
                                default->null;
                            };

                            if(!BlockHelper.replaceBlockWithDrops(pe,stack,comp.world(),blockPos,bi.getBlock().getDefaultState(),minableBlocksPredicate)){
                                // couldnt mine... again?
                                tryLogDebugNotbreakable(comp,targetState);
                                return SpellBlockResult.empty();
                            }

                            if(comp.caster()!=null && EntityUtil.distanceTo(comp.caster(),pos) >7)
                                tryUnlockSpellAdvancement(comp,"long_arms");
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
                    .category(cat).build());

            // ignite behaviors
            {
                BiConsumer<World,BlockPos> playUseSound = (World world, BlockPos pos) -> Toolbox.playSound(SoundEvents.ITEM_FIRECHARGE_USE,world,pos,SoundCategory.BLOCKS,0.2f,0.8f+Toolbox.random.nextFloat()*0.4f);

                addIgniteBehavior(b->b.isOf(Blocks.FURNACE),(comp,vars)->{
                    var be = ((FurnaceBlockEntity)(comp.world().getBlockEntity(vars.getBlockPos("position"))));
                    if(be.burnTime<800) {be.burnTime=800;be.fuelTime=800;be.markDirty();}
                    playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                    return SpellBlockResult.empty();
                });
                addIgniteBehavior(b->b.isOf(Blocks.BLAST_FURNACE),(comp,vars)->{
                    var be = ((BlastFurnaceBlockEntity)(comp.world().getBlockEntity(vars.getBlockPos("position"))));
                    if(be.burnTime<800) {be.burnTime=800;be.fuelTime=800;be.markDirty();}
                    playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                    return SpellBlockResult.empty();
                });
                addIgniteBehavior(b->b.isIn(BlockTags.STONE_ORE_REPLACEABLES)||b.isIn(BlockTags.DEEPSLATE_ORE_REPLACEABLES),(comp,vars)->{
                    comp.world().setBlockState(vars.getBlockPos("position"),Blocks.MAGMA_BLOCK.getDefaultState());
                    playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                    return SpellBlockResult.empty();
                });
                addIgniteBehavior(b->b.isOf(Blocks.MAGMA_BLOCK),(comp,vars)->{
                    comp.world().setBlockState(vars.getBlockPos("position"),Blocks.LAVA.getDefaultState());
                    playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                    return SpellBlockResult.empty();
                });
                addIgniteBehavior(b->b.getBlock() instanceof CandleBlock,(comp,vars)->{
                    comp.world().setBlockState(vars.getBlockPos("position"),comp.world().getBlockState(vars.getBlockPos("position")).with(CandleBlock.LIT, true), 11);
                    playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                    return SpellBlockResult.empty();
                });
                addIgniteBehavior(b->b.getBlock() instanceof CampfireBlock,(comp,vars)->{
                    comp.world().setBlockState(vars.getBlockPos("position"),comp.world().getBlockState(vars.getBlockPos("position")).with(CampfireBlock.LIT, true), 11);
                    playUseSound.accept(comp.world(),vars.getBlockPos("position"));
                    return SpellBlockResult.empty();
                });
                addIgniteBehavior(b->b.isReplaceable(),(comp,vars)->{
                    var pos = vars.getBlockPos("position");
                    var state = comp.world().getBlockState(pos);
                    if(!state.isReplaceable()) return SpellBlockResult.empty();
                    comp.world().setBlockState(pos,Blocks.FIRE.getDefaultState());
                    playUseSound.accept(comp.world(),pos);
                    return SpellBlockResult.empty();
                });
                addIgniteBehavior(b->true,(comp,vars)->{
                    var pos = vars.getBlockPos("position");
                    for(var dir : Direction.values())
                    {
                        var pos2 = pos.add(dir.getOffsetX(),dir.getOffsetY(),dir.getOffsetZ());
                        if(!comp.world().getBlockState(pos2).isReplaceable()) continue;
                        comp.world().setBlockState(pos2,Blocks.FIRE.getDefaultState());
                    }
                    playUseSound.accept(comp.world(),pos);
                    return SpellBlockResult.empty();
                });
            }
            IGNITE = register(SpellBlock.Builder.create("ignite")
                    .inputs(SpellSignal.createVector().named("position")).parameters()
                    .func((comp,vars) -> {
                        var pos = vars.getVector("position");
                        var blockPos = vars.getBlockPos("position");

                        // calculate breaking cost
                        BlockState targetState = comp.world().getBlockState(blockPos);

                        float manaCost = 30f
                                +normalCastOffsetSoulCost(comp,pos);

                        if(canAfford(comp,manaCost)){
                            for(var pred : igniteBehavior.keySet()){
                                if(!pred.apply(targetState)) continue;
                                igniteBehavior.get(pred).apply(comp,vars);
                                trySpendSoul(comp,manaCost);
                                spawnCastParticles(comp,CastParticleData.genericSuccess(comp,pos));
                                tryUnlockSpellAdvancement(comp,"ignition");
                                break;
                            }
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,pos));
                        }

                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            PLAY_SOUND = register(SpellBlock.Builder.create("play_sound")
                    .inputs(
                            SpellSignal.createVector().named("position"),
                            SpellSignal.createText().named("sound"),
                            SpellSignal.createNumber().named("volume"),
                            SpellSignal.createNumber().named("pitch")
                    ).parameters()
                    .func((comp,vars) -> {
                        var soundID = vars.getIdentifier("sound");
                        if(soundID==null) return SpellBlockResult.empty();
                        var soundEvent = Registries.SOUND_EVENT.get(soundID);
                        if(soundEvent==null) return SpellBlockResult.empty();
                        var pos = vars.getVector("position");
                        var blockPos = vars.getBlockPos("position");
                        var vol = Toolbox.clampF(vars.getNumber("volume"),0,2);
                        var pitch = vars.getNumber("pitch");

                        // calculate cost
                        float manaCost = 1f
                                +vol*10
                                +normalCastOffsetSoulCost(comp,pos);

                        if(canAfford(comp,manaCost)){

                            Toolbox.playSound(soundEvent,comp.world(),blockPos,
                                    switch(comp.context.sourceType){
                                        case Block -> SoundCategory.BLOCKS;
                                        case Delegate -> comp.context.caster!=null?SoundCategory.PLAYERS:SoundCategory.BLOCKS;
                                        default->SoundCategory.PLAYERS;
                                    },vol,pitch);

                            trySpendSoul(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,pos));
                            if(comp.context.root().silent) tryUnlockSpellAdvancement(comp,"deception");
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,pos));
                        }

                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            DELEGATE = register(SpellBlock.Builder.create("delegate")
                    .inputs(
                            SpellSignal.createVector().named("position"),
                            SpellSignal.createVector().named("direction"),
                            SpellSignal.createText().named("spell"),
                            SpellSignal.createNumber().named("delay")
                    )
                    .outputs(SpellSignal.createUUID().named("delegate")).parameters()
                    .func((comp,vars) -> {
                        var spellName = vars.getText("spell");
                        var spell =comp.context.getSpellSelector().getSpell(comp.context.casterItem,spellName);
                        if(spell==null) return SpellBlockResult.empty();
                        var pos = vars.getVector("position");
                        var dir = vars.getVector("direction");
                        int delay = Math.round(20*vars.getNumber("delay"));

                        // calculate cost
                        float manaCost = 3f
                                +normalCastOffsetSoulCost(comp,pos);

                        if(canAfford(comp,manaCost)){
                            // spawn delegate
                            var d = dir.horizontalLength();
                            Vec2f rot = new Vec2f(
                                    (float)(MathHelper.atan2(dir.x, dir.z) * (double)(180F / (float)Math.PI)),
                                    (float)(MathHelper.atan2(dir.y, d) * (double)(180F / (float)Math.PI))
                            );

                            spell.spawnDelegate(comp.context,pos,rot,delay);

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
                    .category(cat).build());

            SET_TIME = register(SpellBlock.Builder.create("set_time")
                    .inputs(
                            SpellSignal.createNumber().named("fraction")
                    ).parameters()
                    .func((comp,vars) -> {
                        var fraction = ((vars.getNumber("fraction")%1)+1)%1;

                        // calculate cost
                        float manaCost = 500f;

                        if(canAfford(comp,manaCost)){

                            ((ServerWorld)comp.world()).setTimeOfDay(Math.round(fraction*24000.0));

                            trySpendSoul(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,comp.context.getOriginPos()));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.context.getOriginPos()));
                        }

                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            SET_WEATHER = register(SpellBlock.Builder.create("set_weather")
                    .inputs(
                            SpellSignal.createNumber().named("rainyness")
                    ).parameters()
                    .func((comp,vars) -> {
                        var rainyness = Toolbox.clampI(vars.getInt("rainyness"),0,2);

                        // calculate cost
                        float manaCost = 500f;

                        if(canAfford(comp,manaCost)){

                            var sw = ((ServerWorld)comp.world());
                            sw.setWeather(
                                    rainyness<=0?24000:0,
                                    rainyness>0?24000:0,
                                    rainyness>0,
                                    rainyness>1
                                    );

                            trySpendSoul(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,comp.context.getOriginPos()));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.context.getOriginPos()));
                        }

                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            GROW = register(SpellBlock.Builder.create("grow")
                    .inputs(SpellSignal.createVector().named("position")).parameters()
                    .func((comp,vars) -> {
                        var pos = vars.getVector("position");
                        var blockPos = vars.getBlockPos("position");

                        // calculate cost
                        float manaCost = 10f
                                +normalCastOffsetSoulCost(comp,pos);

                        if(canAfford(comp,manaCost)){
                            ItemStack meal = Items.BONE_MEAL.getDefaultStack();
                            if(!BoneMealItem.useOnFertilizable(meal,comp.world(),blockPos))
                                BoneMealItem.useOnGround(meal,comp.world(),blockPos.down(),null);

                            trySpendSoul(comp,manaCost);
                            tryUnlockSpellAdvancement(comp,"bones");
                            spawnCastParticles(comp,CastParticleData.genericSuccess(comp,comp.context.getOriginPos()));
                        }
                        else{
                            // too broke
                            tryLogDebugBroke(comp,manaCost);
                            spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.context.getOriginPos()));
                        }

                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            ACTIVATE = register(SpellBlock.Builder.create("activate")
                    .inputs(
                            SpellSignal.createVector().named("position")
                    ).parameters()
                    .func((comp,vars) -> {
                        var pos = vars.getVector("position");
                        var blockPos = vars.getBlockPos("position");

                        World world = comp.world();
                        BlockState targetState = world.getBlockState(blockPos);
                        Block targetBlock = targetState.getBlock();
                        BlockEntity targetEntity = world.getBlockEntity(blockPos);

                        float manaCost = 5f
                                +normalCastOffsetSoulCost(comp,pos);

                        if(canAfford(comp,manaCost)){

                            // open doors, trapdoors, press butons, flip levers
                            if(
                                    targetBlock instanceof DoorBlock
                                            || targetBlock instanceof TrapdoorBlock
                                            || targetBlock instanceof ButtonBlock
                                            || targetBlock instanceof LeverBlock
                                            || targetBlock instanceof FenceGateBlock
                            ){
                                try{
                                    targetBlock.onUse(targetState,world,blockPos,(PlayerEntity) comp.caster(),null,null);
                                }
                                catch(Exception ignored){
                                    // some modded variant wanted to use hand, hit, or the caster and errored because of it
                                }
                            }
                            // trigger pressure plates
                            else if(targetBlock instanceof PressurePlateBlock pp){
                                world.setBlockState(blockPos,targetState.with(PressurePlateBlock.POWERED,true));
                                world.scheduleBlockTick(blockPos, pp, pp.getTickRate());
                            }
                            // trigger tripwire
                            else if(targetBlock instanceof TripwireBlock tw){
                                if(!targetState.get(TripwireBlock.POWERED))
                                {
                                    var blockState = targetState.with(TripwireBlock.POWERED, true);
                                    tw.update(world, blockPos, blockState);
                                    world.setBlockState(blockPos,targetState.with(TripwireBlock.POWERED,true),3);
                                    world.scheduleBlockTick(blockPos, tw, 10);
                                }
                            }
                            // trigger tripwire hook
                            else if(targetBlock instanceof TripwireHookBlock tw){
                                if(!targetState.get(TripwireHookBlock.POWERED))
                                {
                                    var blockState = targetState.with(TripwireHookBlock.POWERED, true);
                                    tw.update(world, blockPos,blockState,false,true,-1,null);
                                    world.setBlockState(blockPos,blockState,3);
                                    world.scheduleBlockTick(blockPos, tw, 10);
                                }
                            }
                            // trigger tnt
                            else if(targetBlock instanceof TntBlock){
                                TntBlock.primeTnt(world, blockPos);
                                world.removeBlock(blockPos, false);
                            }
                            // trigger detector rail
                            else if(targetBlock instanceof DetectorRailBlock drb){
                                BlockState blockState = (BlockState)targetState.with(DetectorRailBlock.POWERED, true);
                                world.setBlockState(blockPos, blockState, 3);
                                drb.updateNearbyRails(world, blockPos, blockState, true);
                                world.updateNeighborsAlways(blockPos, drb);
                                world.updateNeighborsAlways(blockPos.down(), drb);
                                world.scheduleBlockTick(blockPos, drb, 20);
                            }
                            // trigger dispenser, dropper, observers
                            else if(
                                    targetBlock instanceof DispenserBlock
                                            || targetBlock instanceof ObserverBlock
                            ){
                                world.scheduleBlockTick(blockPos, targetBlock, 0);
                            }
                            // lamp
                            else if(targetBlock instanceof RedstoneLampBlock){
                                world.setBlockState(blockPos, (BlockState)targetState.cycle(RedstoneLampBlock.LIT), 2);
                            }
                            // note block
                            else if(targetBlock instanceof NoteBlock nb){
                                nb.playNote(null,targetState,world,blockPos);
                            }
                            // jukebox
                            else if(targetBlock instanceof JukeboxBlock){
                                if ((Boolean)targetState.get(JukeboxBlock.HAS_RECORD)) {
                                    if (world.getBlockEntity(blockPos) instanceof JukeboxBlockEntity jbe) {
                                        jbe.dropRecord();
                                    }
                                }
                            }
                            // bell
                            else if(targetBlock instanceof BellBlock bb){
                                bb.ring(world,blockPos,null);
                            }

                            if(comp.caster()!=null && EntityUtil.distanceTo(comp.caster(),pos) >=1000)
                                tryUnlockSpellAdvancement(comp,"ftl");

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
                    .category(cat).build());

            TRANSMUTE_ITEM = register(SpellBlock.Builder.create("transmute_item")
                    .inputs(SpellSignal.createUUID().named("item")).parameters()
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
                                spawnCastParticles(comp,CastParticleData.genericSuccess(comp,comp.context.getOriginPos()));
                            }
                            else{
                                // too broke
                                tryLogDebugBroke(comp,manaCost);
                                spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.context.getOriginPos()));
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
                                spawnCastParticles(comp,CastParticleData.genericSuccess(comp,comp.context.getOriginPos()));
                            }
                            else{
                                // too broke
                                tryLogDebugBroke(comp,manaCost);
                                spawnCastParticles(comp,CastParticleData.genericBroke(comp,comp.context.getOriginPos()));
                            }
                            return SpellBlockResult.empty();
                        }

                        // no fitting transmutation recipe found
                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());
        }

        // reference
        cat = SpellBlock.Category.Reference;
        {
            ACTION = register(SpellBlock.Builder.create("action")
                    .inputs(SpellSignal.createAny().named("trigger"))
                    .parameters(SpellBlock.Parameter.createText("function","helloworld"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                        var funcName = vars.getText("function");
                        // check if specified function exists
                        var refSpell = sps.getSpell(comp.context.casterItem,funcName);
                        if(refSpell==null)
                        {
                            tryLogDebugNoSuchFunction(comp,funcName);
                            return SpellBlockResult.empty();
                        }

                        // run referenced
                        return refSpell.runReferenced(comp.context,comp,vars);
                    })
                    .category(cat).build());

            PROVIDER = register(SpellBlock.Builder.create("provider")
                    .outputs(SpellSignal.createAny().named("res"))
                    .parameters(SpellBlock.Parameter.createText("function","helloworld"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                        var funcName = vars.getText("function");
                        // check if specified function exists
                        var refSpell = sps.getSpell(comp.context.casterItem,funcName);
                        if(refSpell==null)
                        {
                            tryLogDebugNoSuchFunction(comp,funcName);
                            return SpellBlockResult.empty();
                        }

                        // run referenced
                        return refSpell.runReferenced(comp.context,comp,vars);
                    })
                    .category(cat).build());

            FUNCTION = register(SpellBlock.Builder.create("function")
                    .inputs(SpellSignal.createAny().named("arg"))
                    .outputs(SpellSignal.createAny().named("res"))
                    .parameters(SpellBlock.Parameter.createText("spell","helloworld"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                        var funcName = vars.getText("spell");
                        // check if specified function exists
                        var refSpell = sps.getSpell(comp.context.casterItem,funcName);
                        if(refSpell==null)
                        {
                            tryLogDebugNoSuchFunction(comp,funcName);
                            return SpellBlockResult.empty();
                        }

                        // run referenced
                        return refSpell.runReferenced(comp.context,comp,vars);
                    })
                    .category(cat).build());

            FUNCTION2 = register(SpellBlock.Builder.create("function_two")
                    .inputs(SpellSignal.createAny().named("arg"),SpellSignal.createAny().named("arg2"))
                    .outputs(SpellSignal.createAny().named("res"))
                    .parameters(SpellBlock.Parameter.createText("spell","helloworld"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                        var funcName = vars.getText("spell");
                        // check if specified function exists
                        var refSpell = sps.getSpell(comp.context.casterItem,funcName);
                        if(refSpell==null)
                        {
                            tryLogDebugNoSuchFunction(comp,funcName);
                            return SpellBlockResult.empty();
                        }

                        // run referenced
                        return refSpell.runReferenced(comp.context,comp,vars);
                    })
                    .category(cat).build());

            // outputs an internal variable from the parent call
            REF_OUTPUT = register(SpellBlock.Builder.create("ref_output")
                    .outputs(SpellSignal.createAny().named("var"))
                    .parameters(SpellBlock.Parameter.createText("varName","a"))
                    .func((comp,vars) -> {
                        var parentVar = comp.context.getParentVar(vars.getText("varName"));
                        if(parentVar==null) return SpellBlockResult.empty();
                        SpellBlockResult res = new SpellBlockResult();
                        res.add(parentVar.clone().named("var"));
                        return res;
                    })
                    .category(cat).build());

            // input a variable into the reference call result
            REF_INPUT = register(SpellBlock.Builder.create("ref_input")
                    .inputs(SpellSignal.createAny().named("var"))
                    .parameters(SpellBlock.Parameter.createText("varName","a"))
                    .func((comp,vars) -> {
                        if(!comp.context.isChild()) return SpellBlockResult.empty();
                        comp.context.referenceResult.add(vars.get("var").clone().named(vars.getText("varName")));
                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            // gets a signal from variable storage items
            VAR_OUTPUT = register(SpellBlock.Builder.create("var_output")
                    .inputs(SpellSignal.createText().named("varID"))
                    .outputs(SpellSignal.createAny().named("var"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                        var varID = vars.getText("varID");
                        if(!varID.contains(":")) varID="default:"+varID;
                        var splitID = varID.split(":");
                        var varPrefix = splitID[0];
                        var varName = splitID[1];
                        var varStorerStack = sps.getVariableStorageItem(comp.context.casterItem,varPrefix);
                        if(varStorerStack==null) return SpellBlockResult.empty();
                        var sig = ((IVariableStoringItem) varStorerStack.getItem()).getSignal(varStorerStack,varName);
                        SpellBlockResult res = new SpellBlockResult();
                        if(sig!=null) res.add(sig.named("var"));
                        return res;
                    })
                    .category(cat).build());

            // sets a signal onto a variable storage item
            VAR_INPUT = register(SpellBlock.Builder.create("var_input")
                    .inputs(SpellSignal.createAny().named("var"),SpellSignal.createText().named("varID"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                        var varID = vars.getText("varID");
                        var sig = vars.get("var");
                        if(!varID.contains(":")) varID="default:"+varID;
                        var splitID = varID.split(":");
                        var varPrefix = splitID[0];
                        var varName = splitID[1];
                        var varStorerStack = sps.getVariableStorageItem(comp.context.casterItem,varPrefix);
                        if(varStorerStack==null) return SpellBlockResult.empty();
                        if(((IVariableStoringItem) varStorerStack.getItem()).setSignal(varStorerStack,sig.named(varName))){
                            sps.markDirty(comp.context.casterItem);
                        }
                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            // removes a signal from a var storage item
            VAR_DELETE = register(SpellBlock.Builder.create("var_delete")
                    .inputs(SpellSignal.createText().named("varID"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                        var varID = vars.getText("varID");
                        if(!varID.contains(":")) varID="default:"+varID;
                        var splitID = varID.split(":");
                        var varPrefix = splitID[0];
                        var varName = splitID[1];
                        var varStorerStack = sps.getVariableStorageItem(comp.context.casterItem,varPrefix);
                        if(varStorerStack==null) return SpellBlockResult.empty();
                        if(((IVariableStoringItem) varStorerStack.getItem()).setSignal(varStorerStack,SpellSignal.createNone().named(varName))){
                            sps.markDirty(comp.context.casterItem);
                        }
                        return SpellBlockResult.empty();
                    })
                    .category(cat).build());

            // checks if a signal from a var storage item exists
            VAR_EXISTS = register(SpellBlock.Builder.create("var_exists")
                    .inputs(SpellSignal.createText().named("varID"))
                    .outputs(SpellSignal.createBoolean().named("exists"))
                    .func((comp,vars) -> {
                        if(!(comp.context.casterItem.getItem() instanceof ISpellSelectorItem sps)) return SpellBlockResult.empty();
                        var varID = vars.getText("varID");
                        if(!varID.contains(":")) varID="default:"+varID;
                        var splitID = varID.split(":");
                        var varPrefix = splitID[0];
                        var varName = splitID[1];
                        var varStorerStack = sps.getVariableStorageItem(comp.context.casterItem,varPrefix);
                        if(varStorerStack==null) return SpellBlockResult.empty();
                        var sig = ((IVariableStoringItem) varStorerStack.getItem()).getSignal(varStorerStack,varName);
                        return SpellBlockResult.empty().add("exists",sig!=null);
                    })
                    .category(cat).build());
        }

        // lists
        cat = SpellBlock.Category.Lists;
        {
            FOREACH = register(SpellBlock.Builder.create("foreach")
                    .inputs(SpellSignal.createList().named("list"))
                    .outputs(SpellSignal.createAny().named("signal"))
                    .func((component, args) ->
                    {
                        SpellBlockResult res = SpellBlockResult.empty();
                        var list = args.getList("list");
                        if(list==null) return res;
                        for(var s : list)
                        {
                            var subRes = SpellBlockResult.empty();
                            subRes.add(s.clone().named("signal"));
                            res.addSubResult(subRes);
                        }
                        return res;
                    })
                    .category(cat)
                    .build());

            SPLIT = register(SpellBlock.Builder.create("split")
                    .inputs(SpellSignal.createList().named("list"),SpellSignal.createNumber().named("index"))
                    .outputs(SpellSignal.createList().named("a"),SpellSignal.createList().named("b"))
                    .func((component, args) ->
                    {
                        SpellBlockResult res = SpellBlockResult.empty();
                        var list = args.getList("list");
                        if(list==null) return res;
                        int index = args.getInt("index");
                        index = Toolbox.clampI(index,0,list.size()-1);
                        var listA = list.subList(0,index);
                        var listB = list.subList(index,list.size());
                        res.add("a",listA);
                        res.add("b",listB);
                        return res;
                    })
                    .category(cat)
                    .build());

            POP = register(SpellBlock.Builder.create("pop")
                    .inputs(SpellSignal.createList().named("list"))
                    .outputs(SpellSignal.createAny().named("signal"),SpellSignal.createList().named("list"))
                    .func((component, args) ->
                    {
                        SpellBlockResult res = SpellBlockResult.empty();
                        var list = args.getList("list");
                        if(list==null || list.isEmpty()) return res;
                        res.add(list.remove(0).named("signal"));
                        res.add("list",list);
                        return res;
                    })
                    .category(cat)
                    .build());

            SIZE = register(SpellBlock.Builder.create("size")
                    .inputs(SpellSignal.createList().named("list"))
                    .outputs(SpellSignal.createNumber().named("size"))
                    .func((component, args) ->
                    {
                        SpellBlockResult res = SpellBlockResult.empty();
                        var list = args.getList("list");
                        res.add("size",list==null?0:list.size());
                        return res;
                    })
                    .category(cat)
                    .build());

            GET_ELEMENT = register(SpellBlock.Builder.create("get_element")
                    .inputs(SpellSignal.createList().named("list"),SpellSignal.createNumber().named("index"))
                    .outputs(SpellSignal.createAny().named("signal"))
                    .func((component, args) ->
                    {
                        SpellBlockResult res = SpellBlockResult.empty();
                        var list = args.getList("list");
                        if(list==null || list.isEmpty()) return res;
                        int index = args.getInt("index");
                        if(index<0||index>=list.size()) return res;
                        res.add(list.get(index).named("signal"));
                        return res;
                    })
                    .category(cat)
                    .build());

            SET_ELEMENT = register(SpellBlock.Builder.create("set_element")
                    .inputs(SpellSignal.createList().named("list"),SpellSignal.createNumber().named("index"),SpellSignal.createAny().named("signal"))
                    .outputs(SpellSignal.createList().named("list"))
                    .func((component, args) ->
                    {
                        SpellBlockResult res = SpellBlockResult.empty();
                        var list = args.getList("list");
                        if(list==null || list.isEmpty()) return res;
                        int index = args.getInt("index");
                        if(index<0||index>=list.size()) return res;
                        list.set(index,args.get("signal"));
                        res.add("list",list);
                        return res;
                    })
                    .category(cat)
                    .build());

            ENTITIES_NEAR = register(SpellBlock.Builder.create("entities_near")
                    .inputs(SpellSignal.createVector().named("position"),SpellSignal.createNumber().named("range"))
                    .outputs(SpellSignal.createList().named("entities"))
                    .func((comp, vars) -> {
                        SpellBlockResult res = SpellBlockResult.empty();
                        var pos = vars.getVector("position");
                        var range = vars.getNumber("range");

                        var ents = comp.world().getEntitiesByClass(Entity.class,Box.of(pos,range,range,range), le -> true);
                        if(ents==null||ents.isEmpty()) return res;
                        ents.sort((o1, o2) ->
                                        Toolbox.signD(pos.distanceTo(o1.getPos()) - pos.distanceTo(o2.getPos()))
                                );

                        List<SpellSignal> sigs = new ArrayList<>();
                        for(var ent : ents){
                            sigs.add(SpellSignal.createUUID(ent.getUuid()));
                        }

                        res.add("entities",sigs);
                        return res;
                    })
                    .category(cat).build());

            BLOCK_BOX = register(SpellBlock.Builder.create("block_box")
                    .inputs(SpellSignal.createVector().named("start"),SpellSignal.createVector().named("end"))
                    .outputs(SpellSignal.createList().named("positions"))
                    .func((comp, vars) -> {
                        SpellBlockResult res = SpellBlockResult.empty();
                        var start = vars.getBlockPos("start");
                        var end = vars.getBlockPos("end");

                        List<SpellSignal> sigs = new ArrayList<>();
                        var box = new Box(start,end);
                        var vol = box.getXLength() * box.getYLength() * box.getZLength();
                        if(vol > 10000)
                        {
                            tryLogDebugTooBig(comp,vol);
                            return res;
                        }

                        BlockPos.stream(box)
                                .forEach((blockPos -> sigs.add(SpellSignal.createVector(blockPos.toCenterPos()))));

                        res.add("positions",sigs);
                        return res;
                    })
                    .category(cat).build());
        }
    }

    private static void tryUnlockSpellAdvancement(SpellComponent comp, String name) {
        tryUnlockSpellAdvancement(comp.caster(),name);
    }

    public static void tryUnlockSpellAdvancement(LivingEntity entity, String name) {
        if(!(entity instanceof ServerPlayerEntity spe)) return;
        AdvancementHelper.grantAdvancementCriterion(spe,"spells/simple_"+name,"simple_"+name);
    }

    // sin, cos, tan
    private static SpellBlock createAngleFunc(String name, String varName, Function<Double,Double> function){
        return SpellBlock.Builder.create(name)
                .inputs(SpellSignal.createNumber(0).named("rad"))
                .outputs(SpellSignal.createNumber(0).named(varName))
                .func((comp,vars) -> {
                    SpellBlockResult res = SpellBlockResult.empty();
                    res.add(SpellSignal.createNumber(function.apply((double)vars.getNumber("rad"))).named(varName));
                    return res;
                })
                .category(cat).build();
    }
    // raycasts
    private static BlockHitResult raycast(SpellComponent comp, SpellBlockArgs vars){
        var from = vars.getVector("from");
        var dir = vars.getVector("dir");
        var length = vars.getNumber("length");
        switch(comp.context.sourceType){
            case Caster :{
                var ctx = new RaycastContext(from,from.add(dir.multiply(length)), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.ANY,comp.caster());
                var hit = comp.world().raycast(ctx);
                return hit;
            }

            case Block:{
                var dirV = comp.context.casterBlock.getDirection().getVector();
                Vec3d end = from.add(dirV.getX()*length,dirV.getY()*length,dirV.getZ()*length);

                return BlockHelper.raycastBlock(comp.world(),from,end);
            }

            case Delegate:{
                var ctx = new RaycastContext(from,from.add(dir.multiply(length)), RaycastContext.ShapeType.VISUAL, RaycastContext.FluidHandling.ANY,comp.context.delegate);
                var hit = comp.world().raycast(ctx);
                return hit;
            }
        }

        return BlockHitResult.createMissed(from,Direction.NORTH,Toolbox.posToBlockPos(from));
    }

    private static EntityHitResult raycastEntity(SpellComponent comp, Vec3d start, Vec3d end) {
        double reachDistance = end.subtract(start).length();
        HitResult target = BlockHelper.raycastBlock(comp.world(),start,end);

        // cap entity reach by hit blocks, if any
        double squaredEntityReach = reachDistance;
        squaredEntityReach *= squaredEntityReach;
        if (target != null) {
            squaredEntityReach = target.getPos().squaredDistanceTo(start);
        }

        Box entityCheckBox = new Box(start,end).expand((double)1.0F, (double)1.0F, (double)1.0F);
        return EntityUtil.raycast(comp.world(), start, end, entityCheckBox, (entityx) -> !entityx.isSpectator() && entityx.canHit(), squaredEntityReach);
    }


    private static void logDebug(LivingEntity player, Text text){
        if(player != null)
        {
            World world = player.getWorld();
            if(world instanceof ServerWorld){
                if(player instanceof ServerPlayerEntity p2)
                    p2.sendMessage(text);
            }
        }
    }

    public static void tryLogDebug(SpellComponent comp, Text msg){
        if(!comp.context.debugging) return;
        logDebug(comp.caster(),msg);
    }

    public static void tryLogDebugWrongSignal(SpellComponent comp, SpellSignal.Type got, SpellSignal.Type expected){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.wrongsignal",
                comp.getRuntimeName(),
                Text.translatable("geomancy.spellmaker.types."+got.toString().toLowerCase()).formatted(Formatting.DARK_AQUA),
                Text.translatable("geomancy.spellmaker.types."+expected.toString().toLowerCase()).formatted(Formatting.DARK_AQUA)
                ));
    }

    public static void tryLogDebugDepthLimitReached(SpellContext ctx){
        if(!ctx.debugging) return;
        logDebug(ctx.caster,Text.translatable("geomancy.spells.debug.depthlimit",ctx.grid.getRuntimeName(ctx)));
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

    private static void tryLogDebugTooBig(SpellComponent comp, double vol){
        tryLogDebug(comp,Text.translatable("geomancy.spells.debug.toobig",
                comp.getRuntimeName(),vol));
    }

    public static void tryLogDebugTimedOut(SpellContext context) {
        if(context.debugging)logDebug(context.caster,Text.translatable("geomancy.spells.debug.timeout",context.grid.getRuntimeName(context),context.getExecutionTimeMS()));
    }

    private static void spawnCastParticles(SpellComponent comp,CastParticleData data){
        data.send(comp.world());
    }

    public static void playCastSound(SpellContext ctx){
        if(ctx.isSilent()) return;

        float fraction = ctx.soulConsumed / ctx.getCasterMaxSoul();
        SoundEvent event = null;
        if(fraction > 0.7f && ctx.soulConsumed > 200)
            event = ModSoundEvents.CAST_SUCCESS_EXPENSIVE;
        else if(fraction > 0.2f && ctx.soulConsumed > 50)
            event = ModSoundEvents.CAST_SUCCESS_MEDIUM;
        else if(ctx.soundBehavior == SpellContext.SoundBehavior.Full || ctx.soulConsumed>0)
            event = ModSoundEvents.CAST_SUCCESS_CHEAP;
        Toolbox.playSound(event,ctx.getWorld(), ctx.getOriginBlockPos(), switch(ctx.sourceType){
            case Caster -> SoundCategory.PLAYERS;

            case Block -> SoundCategory.BLOCKS;
            default -> SoundCategory.PLAYERS;
        },1,0.8f+Toolbox.random.nextFloat()*0.4f);
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

    private static float normalCastOffsetSoulCost(SpellComponent comp, Vec3d pos){
        return castOffsetSoulCost(comp,pos,0.1f);
    }

    private static float distanceToCaster(SpellContext context, Vec3d pos){
        return (float)context.getOriginPos().subtract(pos).length();
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

    private static void addDegradeBlockData(Block b, Block replacement){
        addDegradeBlockData(b.getDefaultState(),replacement);
    }
    private static void addDegradeBlockData(BlockState b, Block replacement){
        addDegradeBlockData(b1->b1.isOf(b.getBlock()),replacement.getDefaultState());
    }
    private static void addDegradeBlockData(Function<BlockState,Boolean> predicate, BlockState replacement){
        degradeBlockData.put(predicate,replacement);
    }

    private static void addIgniteBehavior(Function<BlockState,Boolean> predicate,java.util.function.BiFunction<SpellComponent,SpellBlockArgs,SpellBlockResult> func){
        igniteBehavior.put(predicate,func);
    }

    private static void addImbueData(StatusEffect effect, ImbueData data){
        imbueData.put(Registries.STATUS_EFFECT.getId(effect),data);
    }

    public static void spawnMuzzleParticles(SpellContext context) {
        if(context.isSilent()) return;
        if(context.soulConsumed<=0 && context.soundBehavior== SpellContext.SoundBehavior.Reduced) return;
        CastParticleData.genericMuzzle(context,context.getMuzzlePos(),context.getDirection()).send(context.getWorld());
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
        public Vec3d dir;
        public Identifier world;

        private CastParticleData(Type type, int amount,Vec3d pos,Vec3d dir, Identifier world,float dispersion){
            this.type=type;
            this.amount=amount;
            this.pos=pos;
            this.dir=dir;
            this.world=world;
            this.dispersion=dispersion;
        }

        public static CastParticleData genericSuccess(SpellComponent comp,Vec3d pos){
            return create(comp.world(),pos).type(Type.SOUL).amount(10);
        }

        public static CastParticleData genericBroke(SpellComponent comp,Vec3d pos){
            return create(comp.world(),pos).type(Type.SOUL_FIRE).amount(10);
        }

        public static CastParticleData genericMuzzle(SpellContext ctx,Vec3d pos,Vec3d dir){
            return create(ctx.getWorld(),pos).type(Type.MUZZLE).amount(10).dir(dir).dispersion(0.2f);
        }


        public static CastParticleData genericFail(SpellComponent comp,Vec3d pos){
            return create(comp.world(),pos).type(Type.SOUL_FIRE).amount(10);
        }

        public static CastParticleData create(World world,Vec3d pos){
            return new CastParticleData(Type.SOUL, 10,pos,new Vec3d(0,0,0),world.getRegistryKey().getValue(),0.5f);
        }

        public CastParticleData amount(int amount){this.amount = amount;return this;}
        public CastParticleData dir(Vec3d dir){this.dir = dir;return this;}
        public CastParticleData dispersion(float dispersion){this.dispersion = dispersion;return this;}
        public CastParticleData type(Type type){this.type = type;return this;}

        public void send(World world){
            CastParticlesS2CPacket.send(world,this);
        }

        public void write(PacketByteBuf buf){
            buf.writeString(type.toString());
            buf.writeInt(amount);
            buf.writeFloat(dispersion);
            buf.writeVector3f(pos.toVector3f());
            buf.writeVector3f(dir.toVector3f());
            buf.writeIdentifier(world);
        }

        public static CastParticleData from(PacketByteBuf buf){
            Type type = Type.valueOf(buf.readString());
            int amount = buf.readInt();
            float dispersion = buf.readFloat();
            Vec3d pos = new Vec3d(buf.readVector3f());
            Vec3d dir = new Vec3d(buf.readVector3f());
            Identifier world = buf.readIdentifier();
            return new CastParticleData(type,amount,pos,dir,world,dispersion);
        }

        @Environment(EnvType.CLIENT)
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
                        worldObj.addParticle(ParticleTypes.SCULK_SOUL,pPos.x,pPos.y,pPos.z,0,0,0);
                        break;
                    }
                    case SOUL_FIRE:{
                        Vec3d randVel = new Vec3d(0,0,0).addRandom(rand,0.08f);
                        worldObj.addParticle(ParticleTypes.SOUL_FIRE_FLAME,pPos.x,pPos.y,pPos.z,randVel.x,randVel.y,randVel.z);
                        break;
                    }
                    case MUZZLE:{
                        Vec3d randVel = dir.multiply(0.3f).addRandom(rand,0.08f);
                        worldObj.addParticle(ParticleTypes.SOUL_FIRE_FLAME,pPos.x,pPos.y,pPos.z,randVel.x,randVel.y,randVel.z);
                        break;
                    }
                }
            }
        }

        public enum Type{
            SOUL,
            SOUL_FIRE,
            MUZZLE,
        }
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
        public TransmuteData into(ItemConvertible item){this.func=s->s.setStack(new ItemStack(item,s.getStack().getCount()));return this;}
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
