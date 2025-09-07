package org.oxytocina.geomancy.registries;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.World;
import org.oxytocina.geomancy.Geomancy;

public class ModDamageTypes {

    /*
     * Store the RegistryKey of our DamageType into a new constant called CUSTOM_DAMAGE_TYPE
     * The Identifier in use here points to our JSON file we created earlier.
     */
    public static final RegistryKey<DamageType> DUPLICATE_TRINKETS = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Geomancy.locate( "duplicate_trinkets"));
    public static final RegistryKey<DamageType> PLUMBOMETER = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Geomancy.locate( "plumbometer"));
    public static final RegistryKey<DamageType> MOLTEN_GOLD = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Geomancy.locate( "molten_gold"));
    public static final RegistryKey<DamageType> RESTRICTED_ACTION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Geomancy.locate( "restricted_action"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}