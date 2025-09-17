package org.oxytocina.geomancy.client.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.sound.ModSoundEvents;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public class ModSoundProvider implements DataProvider {
    protected final FabricDataOutput dataOutput;

    public ModSoundProvider(FabricDataOutput dataOutput) {
        this.dataOutput=dataOutput;
    }

    public void generateSounds(SoundBuilder soundBuilder)
    {
        for(var key : ModSoundEvents.EVENTS.keySet()){
            var event = ModSoundEvents.EVENTS.get(key);
            soundBuilder.add(key,event);
        }
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        TreeMap<String, SoundData> eventEntries = new TreeMap<>();

        generateSounds((String key, SoundData value) -> {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);

            if (eventEntries.containsKey(key)) {
                throw new RuntimeException("Existing event key found - " + key + " - Duplicate will be ignored.");
            }

            eventEntries.put(key, value);
        });

        JsonObject soundsJson = new JsonObject();

        for (Map.Entry<String, SoundData> entry : eventEntries.entrySet()) {
            soundsJson.add(entry.getKey(), entry.getValue().toJson());
        }

        return DataProvider.writeToPath(writer, soundsJson, getSoundFilePath());
    }

    private Path getSoundFilePath() {
        var pathPre = dataOutput
                .getResolver(DataOutput.OutputType.RESOURCE_PACK, "..")
                .resolveJson(new Identifier(dataOutput.getModId(), "sounds"));
        return Path.of(pathPre.toString().replace("\\..\\","\\"));
    }

    @Override
    public String getName() {
        return "Sound";
    }

    /**
     * A consumer used by {@link net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider#generateTranslations(net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder)}.
     */
    @FunctionalInterface
    public interface SoundBuilder {
        /**
         * Adds a translation.
         *
         * @param name           The key of the event.
         * @param value          The value of the entry.
         */
        void add(String name, SoundData value);

        default void add(Identifier id, ModSoundEvents.ExtraData data){
            SoundData res = new SoundData();

            for (int i = 0; i < data.plurality; i++) {
                SoundData.Single sound = new SoundData.Single();
                sound.name = data.pathPrefix!=null?data.pathPrefix:id.getPath();
                if(data.plurality>1) sound.name+="_"+(i+1);
                res.sounds.add(sound);
            }

            add(id.getPath(),res);
        }
    }

    public static class SoundData{
        public boolean replace = false;
        public String subtitle = null;
        public ArrayList<Single> sounds = new ArrayList<>();

        public SoundData withSound(Single sound){
            sounds.add(sound);
            return this;
        }

        public JsonObject toJson(){
            JsonObject eventJson = new JsonObject();

            if(replace) eventJson.addProperty("replace",replace);
            if(subtitle!=null) eventJson.addProperty("subtitle",subtitle);

            JsonArray soundsArray = new JsonArray();
            for(var s : sounds)
                soundsArray.add(s.toJson());

            eventJson.add("sounds",soundsArray);

            return eventJson;
        }

        public static class Single{
            public String name;
            public float volume = 1;
            public float pitch = 1;
            public int weight = 1;
            public int attenuation_distance = 16;
            public boolean stream = false;
            public boolean preload = false;
            public String type ="file";

            public JsonObject toJson(){
                JsonObject soundJson = new JsonObject();

                soundJson.addProperty("name",name);
                soundJson.addProperty("volume",volume);
                soundJson.addProperty("pitch",pitch);
                soundJson.addProperty("weight",weight);
                soundJson.addProperty("attenuation_distance",attenuation_distance);
                soundJson.addProperty("stream",stream);
                soundJson.addProperty("preload",preload);
                soundJson.addProperty("type",type);

                return soundJson;
            }
        }
    }
}