package org.oxytocina.geomancy.util;

import com.mojang.serialization.Codec;
import folk.sisby.kaleido.api.ReflectiveConfig;
import folk.sisby.kaleido.api.WrappedConfig;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.Comment;
import folk.sisby.kaleido.lib.quiltconfig.api.annotations.SerializedName;
import folk.sisby.kaleido.lib.quiltconfig.api.values.TrackedValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.nio.file.Paths;

public class GeomancyConfig extends ReflectiveConfig {

    // The file is created when this field is initialized, so put it inside a class with early-run / initializer code.
    public static final GeomancyConfig CONFIG = GeomancyConfig.createToml(
            /* config path: */ Paths.get("config"),/* parent folder (for multiple config files): */ "",/* file name: */ "geomancy",
            GeomancyConfig.class);
    public static GeomancyConfig create(){
        return CONFIG;
    }

    @Comment("if set to true, removes flashy effects like rainbow rarity color cycling")
    @SerializedName("epilepsy_mode")
    public final TrackedValue<Boolean> epilepsyMode = this.value(false);
}
