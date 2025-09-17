package org.oxytocina.geomancy.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

import java.util.HashMap;

public class ModSoundEvents {
    public static final HashMap<Identifier,ExtraData> EVENTS = new HashMap<>();

    public static final SoundEvent MUSIC_DISC_DIGGY = register("music_disc.diggy",new ExtraData().prefix("geomancy:records/diggy"));
    public static final SoundEvent USE_HAMMER = register("use_hammer",new ExtraData().count(4).prefix("geomancy:anvil_use"));
    public static final SoundEvent SMITHERY_FINISHED = register("smithery_finished",new ExtraData().prefix("geomancy:anvil_use_4"));
    public static final SoundEvent USE_HAMMER_FAIL = register("use_hammer_fail",new ExtraData().prefix("geomancy:anvil_break"));
    public static final SoundEvent USE_HAMMER_SLIP = register("use_hammer_slip",new ExtraData().count(6).prefix("geomancy:hammer_slip"));

    public static final SoundEvent BLOCK_STONE_WHISPERS_BREAK =     register("block_stone_whispers_break",new ExtraData().count(4).prefix("geomancy:block/stone_whispers/break"));
    //public static final SoundEvent BLOCK_STONE_WHISPERS_STEP =      register("block_stone_whispers_step");
    public static final SoundEvent BLOCK_STONE_WHISPERS_PLACE =     register("block_stone_whispers_place",new ExtraData().count(6).prefix("geomancy:block/stone_whispers/place"));
    //public static final SoundEvent BLOCK_STONE_WHISPERS_HIT =       register("block_stone_whispers_hit");
    //public static final SoundEvent BLOCK_STONE_WHISPERS_FALL =      register("block_stone_whispers_fall");
    public static final SoundEvent BLOCK_DEEPSLATE_WHISPERS_BREAK = register("block_deepslate_whispers_break",new ExtraData().count(4).prefix("geomancy:block/deepslate_whispers/break"));
    //public static final SoundEvent BLOCK_DEEPSLATE_WHISPERS_STEP =  register("block_deepslate_whispers_step");
    public static final SoundEvent BLOCK_DEEPSLATE_WHISPERS_PLACE = register("block_deepslate_whispers_place",new ExtraData().count(6).prefix("geomancy:block/deepslate_whispers/place"));
    //public static final SoundEvent BLOCK_DEEPSLATE_WHISPERS_HIT =   register("block_deepslate_whispers_hit");
    //public static final SoundEvent BLOCK_DEEPSLATE_WHISPERS_FALL =  register("block_deepslate_whispers_fall");
    public static final SoundEvent BLOCK_METAL_WHISPERS_BREAK =     register("block_metal_whispers_break",new ExtraData().count(6).prefix("geomancy:block/metal_whispers/place"));
    //public static final SoundEvent BLOCK_METAL_WHISPERS_STEP =      register("block_metal_whispers_step");
    public static final SoundEvent BLOCK_METAL_WHISPERS_PLACE =     register("block_metal_whispers_place",new ExtraData().count(6).prefix("geomancy:block/metal_whispers/place"));
    //public static final SoundEvent BLOCK_METAL_WHISPERS_HIT =       register("block_metal_whispers_hit");
    //public static final SoundEvent BLOCK_METAL_WHISPERS_FALL =      register("block_metal_whispers_fall");

    public static final SoundEvent ENTITY_STELLGE_ENGINEER_TALK =   register("entity_stellge_engineer_talk",new ExtraData().count(7).prefix("geomancy:mob/stellge_engineer/talk"));
    public static final SoundEvent ENTITY_STELLGE_ENGINEER_ANGRY =  register("entity_stellge_engineer_angry",new ExtraData().count(1).prefix("geomancy:mob/stellge_engineer/angry"));
    public static final SoundEvent ENTITY_STELLGE_ENGINEER_HURT =   register("entity_stellge_engineer_hurt",new ExtraData().count(1).prefix("geomancy:mob/stellge_engineer/hurt"));
    public static final SoundEvent ENTITY_STELLGE_ENGINEER_DEATH =  register("entity_stellge_engineer_death",new ExtraData().count(1).prefix("geomancy:mob/stellge_engineer/death"));

    public static final SoundEvent WHISPERS = register("whispers",new ExtraData().count(4).prefix("geomancy:octangulite/whispers_single"));
    public static final SoundEvent CAST_FAILURE_BROKE = register("cast_fail_broke",new ExtraData().count(1).prefix("geomancy:casting/fail_broke"));
    public static final SoundEvent CAST_SUCCESS_CHEAP = register("cast_success_cheap",new ExtraData().count(1).prefix("geomancy:casting/small"));
    public static final SoundEvent CAST_SUCCESS_MEDIUM = register("cast_success_medium",new ExtraData().count(1).prefix("geomancy:casting/medium"));
    public static final SoundEvent CAST_SUCCESS_EXPENSIVE = register("cast_success_expensive",new ExtraData().count(1).prefix("geomancy:casting/large"));

    // spellmaker ui
    public static final SoundEvent SPELLMAKER_INSERT_CRADLE = register("spellmaker_insert_cradle",new ExtraData().count(1).prefix("geomancy:spellmaker/insert_cradle"));
    public static final SoundEvent SPELLMAKER_REMOVE_CRADLE = register("spellmaker_remove_cradle",new ExtraData().count(1).prefix("geomancy:spellmaker/remove_cradle"));
    public static final SoundEvent SPELLMAKER_INSERT_COMPONENT = register("spellmaker_insert_component",new ExtraData().count(1).prefix("geomancy:spellmaker/insert_component"));
    public static final SoundEvent SPELLMAKER_REMOVE_COMPONENT = register("spellmaker_remove_component",new ExtraData().count(1).prefix("geomancy:spellmaker/remove_component"));
    public static final SoundEvent SPELLMAKER_ROTATE = register("spellmaker_rotate",new ExtraData().count(1).prefix("geomancy:spellmaker/rotate"));
    public static final SoundEvent SPELLMAKER_TYPE = register("spellmaker_type",new ExtraData().count(1).prefix("geomancy:spellmaker/type"));
    public static final SoundEvent SPELLMAKER_TYPE_BACK = register("spellmaker_type_back",new ExtraData().count(1).prefix("geomancy:spellmaker/type_back"));
    public static final SoundEvent SPELLMAKER_CHANGE_VAR = register("spellmaker_change_var",new ExtraData().count(1).prefix("geomancy:spellmaker/change_var"));
    public static final SoundEvent SPELLMAKER_TEXTFIELD_FINISHED = register("spellmaker_textfield_finished",new ExtraData().count(1).prefix("geomancy:spellmaker/textfield_finished"));

    public static void register(){

    }

    private static SoundEvent register(String id) {return register(new Identifier(Geomancy.MOD_ID,id),new ExtraData());}
    private static SoundEvent register(String id,ExtraData data) {return register(new Identifier(Geomancy.MOD_ID,id),data);}
    private static SoundEvent register(Identifier id,ExtraData data) {
        return register(id, id,data);
    }
    private static SoundEvent register(Identifier id, Identifier soundId,ExtraData data) {
        SoundEvent res = Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
        data.event = res;
        EVENTS.put(id,data);
        return res;
    }

    public static class ExtraData{
        public SoundEvent event;
        public int plurality = 1;
        public String pathPrefix = null;

        public ExtraData count(int count){plurality=count;return this;}
        public ExtraData prefix(String pathPrefix){this.pathPrefix=pathPrefix;return this;}
    }
}
