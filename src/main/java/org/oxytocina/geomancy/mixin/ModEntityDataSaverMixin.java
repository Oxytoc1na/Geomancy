package org.oxytocina.geomancy.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.oxytocina.geomancy.util.IEntityDataSaver;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Entity.class)
public abstract class ModEntityDataSaverMixin implements IEntityDataSaver {
    private NbtCompound persistentData;

    @Override
    public NbtCompound getPersistentData() {
        if(this.persistentData==null){
            this.persistentData = new NbtCompound();
        }
        return persistentData;
    }
}
