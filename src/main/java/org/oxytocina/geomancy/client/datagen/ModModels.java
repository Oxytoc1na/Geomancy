package org.oxytocina.geomancy.client.datagen;

import net.minecraft.data.client.Model;
import net.minecraft.data.client.TextureKey;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class ModModels {
    public static final Model TINTED_CUBE_ALL = block("geomancy:tinted_cube_all", TextureKey.ALL);
    public static final Model TINTED_STAIRS = block("geomancy:tinted_stairs", TextureKey.TOP,TextureKey.SIDE,TextureKey.BOTTOM);
    public static final Model TINTED_INNER_STAIRS = block("geomancy:tinted_inner_stairs","_inner", TextureKey.TOP,TextureKey.SIDE,TextureKey.BOTTOM);
    public static final Model TINTED_OUTER_STAIRS = block("geomancy:tinted_outer_stairs","_outer", TextureKey.TOP,TextureKey.SIDE,TextureKey.BOTTOM);
    public static final Model TINTED_SLAB = block("geomancy:tinted_slab", TextureKey.TOP,TextureKey.SIDE,TextureKey.BOTTOM);
    public static final Model TINTED_SLAB_TOP = block("geomancy:tinted_slab_top","_top", TextureKey.TOP,TextureKey.SIDE,TextureKey.BOTTOM);
    public static final Model TINTED_TEMPLATE_WALL_POST = block("geomancy:tinted_template_wall_post","_post", TextureKey.WALL);
    public static final Model TINTED_TEMPLATE_WALL_SIDE = block("geomancy:tinted_template_wall_side","_side", TextureKey.WALL);
    public static final Model TINTED_TEMPLATE_WALL_SIDE_TALL = block("geomancy:tinted_template_wall_side_tall","_side_tall", TextureKey.WALL);
    public static final Model TINTED_WALL_INVENTORY = block("geomancy:tinted_wall_inventory","_inventory", TextureKey.WALL);
    public static final Model TINTED_FENCE_POST = block("geomancy:tinted_fence_post","_post", TextureKey.TEXTURE);
    public static final Model TINTED_FENCE_SIDE = block("geomancy:tinted_fence_side","_side", TextureKey.TEXTURE);
    public static final Model TINTED_FENCE_INVENTORY = block("geomancy:tinted_fence_inventory","_inventory", TextureKey.TEXTURE);
    public static final Model TINTED_TEMPLATE_FENCE_GATE = block("geomancy:tinted_template_fence_gate", TextureKey.TEXTURE);
    public static final Model TINTED_TEMPLATE_FENCE_GATE_OPEN = block("geomancy:tinted_template_fence_gate_open","_open", TextureKey.TEXTURE);
    public static final Model TINTED_TEMPLATE_FENCE_GATE_WALL = block("geomancy:tinted_template_fence_gate_wall","_wall", TextureKey.TEXTURE);
    public static final Model TINTED_TEMPLATE_FENCE_GATE_WALL_OPEN = block("geomancy:tinted_template_fence_gate_wall_open","_wall_open", TextureKey.TEXTURE);
    public static final Model TINTED_PRESSURE_PLATE_UP = block("geomancy:tinted_pressure_plate_up","", TextureKey.TEXTURE);
    public static final Model TINTED_PRESSURE_PLATE_DOWN = block("geomancy:tinted_pressure_plate_down","_down", TextureKey.TEXTURE);
    public static final Model TINTED_BUTTON = block("geomancy:tinted_button", TextureKey.TEXTURE);
    public static final Model TINTED_BUTTON_PRESSED = block("geomancy:tinted_button_pressed","_pressed", TextureKey.TEXTURE);
    public static final Model TINTED_BUTTON_INVENTORY = block("geomancy:tinted_button_inventory","_inventory", TextureKey.TEXTURE);
    public static final Model TINTED_CUBE_COLUMN = block("geomancy:tinted_cube_column", TextureKey.END,TextureKey.SIDE);
    public static final Model TINTED_CUBE_COLUMN_HORIZONTAL = block("geomancy:tinted_cube_column_horizontal","_horizontal", TextureKey.END,TextureKey.SIDE);
    public static final Model DOUBLE_TINTED_FLOWER_POT_CROSS = block("geomancy:double_tinted_flower_pot_cross", TextureKey.PLANT, TextureKey.of("plant2"));
    public static final Model DOUBLE_TINTED_CROSS = block("geomancy:double_tinted_cross", TextureKey.CROSS, TextureKey.of("cross2"));


    private static Model block(String parent, TextureKey... requiredTextureKeys) {
        return new Model(Optional.ofNullable(Identifier.tryParse(parent).withPrefixedPath("block/")), Optional.empty(), requiredTextureKeys);
    }

    private static Model block(String parent, String variant, TextureKey... requiredTextureKeys) {
        return new Model(Optional.ofNullable(Identifier.tryParse(parent).withPrefixedPath("block/")), Optional.of(variant), requiredTextureKeys);
    }

}
