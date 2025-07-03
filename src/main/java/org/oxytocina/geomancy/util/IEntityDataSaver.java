package org.oxytocina.geomancy.util;

import com.ibm.icu.impl.UResource;
import net.minecraft.nbt.NbtCompound;

public interface IEntityDataSaver {
    NbtCompound getPersistentData();
}
