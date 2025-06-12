package org.oxytocina.geomancy.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

public class ModSoundEvents {
    public static final SoundEvent MUSIC_DISC_DIGGY = register("music_disc.diggy");

    public static void initialize(){

    }

    private static SoundEvent register(String id) {return register(new Identifier(Geomancy.MOD_ID,id));}
    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }
    private static SoundEvent register(Identifier id, Identifier soundId) {
        return (SoundEvent) Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }
}
