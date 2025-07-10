package org.oxytocina.geomancy.entity;


import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.mob.MobEntity;
import org.oxytocina.geomancy.items.ModItems;

public class ExtraEntitySettings {

    private boolean shouldAddSpawnEgg = false;
    public int spawnEggColorMain = 0xFFFFFFFF;
    public int spawnEggColorSecond = 0xFFFFFFFF;

    public SpawnGroup group = SpawnGroup.MISC;
    public float width = 0.75f;
    public float height = 0.75f;

    public EntityType<? extends MobEntity> mobEntity;


    public ExtraEntitySettings(){

    }

    public static ExtraEntitySettings create(){
        return new ExtraEntitySettings();
    }

    public ExtraEntitySettings setMobEntityType(EntityType<? extends MobEntity> entity){
        this.mobEntity= entity;
        return this;
    }


    public ExtraEntitySettings spawnEgg(int main, int second){this.shouldAddSpawnEgg=true; this.spawnEggColorMain=main; this.spawnEggColorSecond=second; return this;}
    public ExtraEntitySettings dim(float width, float height){this.width=width; this.height=height; return this;}
    public ExtraEntitySettings group(SpawnGroup group){this.group=group; return this;}

    public void apply()
    {

        if(shouldAddSpawnEgg)
            ModItems.registerSpawnEgg(this);

    }
}
