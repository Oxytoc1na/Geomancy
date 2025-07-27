package org.oxytocina.geomancy.client.rendering;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TimeHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.util.SimplexNoise;
import org.oxytocina.geomancy.util.Toolbox;
import org.oxytocina.geomancy.blocks.ModBlocks;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;

public class ModColorizationHandler {

    public static void register(){

        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
                    if (view == null || pos == null) {return 0xFFFFFFFF;} else {return octanguliteNoise(pos,tintIndex,0.03f);}
                },
                ModBlocks.RAW_OCTANGULITE_BLOCK,
                ModBlocks.OCTANGULITE_BLOCK,
                ModBlocks.CUT_OCTANGULITE,
                ModBlocks.OCTANGULITE_BRICKS,
                ModBlocks.OCTANGULITE_BRICK_STAIRS,
                ModBlocks.OCTANGULITE_BRICK_SLABS,
                ModBlocks.OCTANGULITE_BRICK_WALL
        );
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            if (view == null || pos == null || tintIndex == 0) {return 0xFFFFFFFF;} else {return octanguliteNoise(pos,tintIndex,0.003f);}
        }, ModBlocks.OCTANGULITE_ORE,ModBlocks.DEEPSLATE_OCTANGULITE_ORE);

        // jewelry
        for(JewelryItem jewelry : JewelryItem.List){ColorProviderRegistry.ITEM.register(jewelry::getColor,jewelry);}

        // octangulite items
        addOctanguliteItem(ModItems.RAW_OCTANGULITE,0.01F,true);
        addOctanguliteItem(ModItems.OCTANGULITE_INGOT,0.01F,true);
        addOctanguliteItem(ModItems.OCTANGULITE_NUGGET,0.01F,true);
        addOctanguliteItem(ModBlocks.OCTANGULITE_BLOCK.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.RAW_OCTANGULITE_BLOCK.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.CUT_OCTANGULITE.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.OCTANGULITE_BRICKS.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.OCTANGULITE_BRICK_STAIRS.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.OCTANGULITE_BRICK_SLABS.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.OCTANGULITE_BRICK_WALL.asItem(),0.03F,false);

        addOctanguliteOreItem(ModBlocks.OCTANGULITE_ORE.asItem(),0.003F,false);
        addOctanguliteOreItem(ModBlocks.DEEPSLATE_OCTANGULITE_ORE.asItem(),0.003F,false);

        addOctanguliteToolItem(ModItems.OCTANGULITE_SWORD);
        addOctanguliteToolItem(ModItems.OCTANGULITE_SHOVEL);
        addOctanguliteToolItem(ModItems.OCTANGULITE_PICKAXE);
        addOctanguliteToolItem(ModItems.OCTANGULITE_AXE);
        addOctanguliteToolItem(ModItems.OCTANGULITE_HOE);
    }

    private static void addOctanguliteItem(Item item,float zoom,boolean withSlotOffset){
        ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> {return octanguliteItemNoise(stack,tintIndex,zoom,withSlotOffset);},item);
    }

    private static void addOctanguliteOreItem(Item item,float zoom,boolean withSlotOffset){
        ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> {
                    if(tintIndex == 0) return 0xFFFFFFFF;
                    return octanguliteItemNoise(stack,tintIndex-1,zoom,withSlotOffset);
                    },item);
    }

    private static void addOctanguliteToolItem(Item item){
        ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> {
                    if(tintIndex == 0) return 0xFFFFFFFF;
                    return octanguliteToolNoise(stack);
                },item);
    }

    private static int octanguliteNoise(BlockPos pos, int tintIndex, float zoom){
        float x = zoom*(pos.getX() * (1+tintIndex*0.3f) + tintIndex*16);
        float y = zoom*(pos.getY() * (1+tintIndex*0.3f) + tintIndex*16);
        float z = zoom*(pos.getZ() * (1+tintIndex*0.3f) + tintIndex*16);

        float x2 = zoom*1.5f*((pos.getX()+230) * (1+tintIndex*0.3f) + tintIndex*16);
        float y2 = zoom*1.5f*((pos.getY()+590) * (1+tintIndex*0.3f) + tintIndex*16);
        float z2 = zoom*1.5f*((pos.getZ()+367) * (1+tintIndex*0.3f) + tintIndex*16);

        float x3 = zoom*2f*((pos.getX()+129) * (1+tintIndex*0.3f) + tintIndex*16);
        float y3 = zoom*2f*((pos.getY()+395) * (1+tintIndex*0.3f) + tintIndex*16);
        float z3 = zoom*2f*((pos.getZ()+529) * (1+tintIndex*0.3f) + tintIndex*16);

        return hsvToRgb(
                (float)(org.oxytocina.geomancy.util.SimplexNoise.noise(x,y,z)+1)/2,
                (float) (1-Math.pow(1F-((SimplexNoise.noise(x2,y2,z2)+1)/2),2)),
                (float) (1-Math.pow(1F-((SimplexNoise.noise(x3,y3,z3)+1)/2),2))
        );

    }

    public static int octanguliteItemNoise(ItemStack stack, int tintIndex,float zoom, boolean withSlotOffset){
        float baseX, baseY, baseZ;

        if(stack.getHolder()!=null){
            Vec3d pos = stack.getHolder().getPos();

            if(withSlotOffset && stack.getHolder() instanceof PlayerEntity player){

                int slot = player.getInventory().getSlotWithStack(stack);

                baseX = (float)pos.getX()+slot;
                baseY = (float)pos.getY()+13+slot*2;
                baseZ = (float)pos.getZ()+54+slot*3;
            }
            else{
                baseX = (float)pos.getX();
                baseY = (float)pos.getY();
                baseZ = (float)pos.getZ();
            }
        }
        else if(MinecraftClient.getInstance() != null)
        {
            if(withSlotOffset && MinecraftClient.getInstance().player != null)
            {
                Vec3d pos = MinecraftClient.getInstance().player.getPos();
                int slot = MinecraftClient.getInstance().player.getInventory().getSlotWithStack(stack);

                baseX = (float)pos.getX()+slot;
                baseY = (float)pos.getY()+13+slot*2;
                baseZ = (float)pos.getZ()+54+slot*3;
            }
            else if(MinecraftClient.getInstance().cameraEntity!=null) {
                Vec3d pos = MinecraftClient.getInstance().cameraEntity.getPos();
                baseX = (float) pos.getX();
                baseY = (float) pos.getY();
                baseZ = (float) pos.getZ();
            }
            else{
                baseX = 0;
                baseY = 0;
                baseZ = 0;
            }
        }
        else{
            baseX = 0;
            baseY = 0;
            baseZ = 0;
        }

        float x = zoom*baseX * (1+tintIndex*0.3f) + tintIndex*16;
        float y = zoom*baseY * (1+tintIndex*0.3f) + tintIndex*16;
        float z = zoom*baseZ * (1+tintIndex*0.3f) + tintIndex*16;

        float x2 = zoom*1.5f*((baseX+230) * (1+tintIndex*0.3f) + tintIndex*16);
        float y2 = zoom*1.5f*((baseY+590) * (1+tintIndex*0.3f) + tintIndex*16);
        float z2 = zoom*1.5f*((baseZ+367) * (1+tintIndex*0.3f) + tintIndex*16);

        float x3 = zoom*2f*((baseX+129) * (1+tintIndex*0.3f) + tintIndex*16);
        float y3 = zoom*2f*((baseY+395) * (1+tintIndex*0.3f) + tintIndex*16);
        float z3 = zoom*2f*((baseZ+529) * (1+tintIndex*0.3f) + tintIndex*16);

        return hsvToRgb(
                (float)(org.oxytocina.geomancy.util.SimplexNoise.noise(x,y,z)+1)/2,
                (float) (1-Math.pow(1F-((SimplexNoise.noise(x2,y2,z2)+1)/2),2)),
                (float) (1-Math.pow(1F-((SimplexNoise.noise(x3,y3,z3)+1)/2),2))
        );

    }

    public static int octanguliteToolNoise(ItemStack stack){

        final float zoom = 0.03f;
        final float speed = 0.01f;
        final int tintIndex = 0;
        final boolean withSlotOffset = true;

        float hue = 0;
        float sat = 1;
        float val = 1;

        float baseX, baseY, baseZ;

        if(stack.getHolder()!=null){
            Vec3d pos = stack.getHolder().getPos();

            if(withSlotOffset && stack.getHolder() instanceof PlayerEntity player){

                int slot = player.getInventory().getSlotWithStack(stack);

                baseX = (float)pos.getX()+slot;
                baseY = (float)pos.getY()+13+slot*2;
                baseZ = (float)pos.getZ()+54+slot*3;
            }
            else{
                baseX = (float)pos.getX();
                baseY = (float)pos.getY();
                baseZ = (float)pos.getZ();
            }
        }
        else if(MinecraftClient.getInstance() != null)
        {
            if(withSlotOffset && MinecraftClient.getInstance().player != null)
            {
                Vec3d pos = MinecraftClient.getInstance().player.getPos();
                int slot = MinecraftClient.getInstance().player.getInventory().getSlotWithStack(stack);

                baseX = (float)pos.getX()+slot;
                baseY = (float)pos.getY()+13+slot*2;
                baseZ = (float)pos.getZ()+54+slot*3;
            }
            else if(MinecraftClient.getInstance().cameraEntity!=null) {
                Vec3d pos = MinecraftClient.getInstance().cameraEntity.getPos();
                baseX = (float) pos.getX();
                baseY = (float) pos.getY();
                baseZ = (float) pos.getZ();
            }
            else{
                baseX = 0;
                baseY = 0;
                baseZ = 0;
            }


        }
        else{
            baseX = 0;
            baseY = 0;
            baseZ = 0;
        }

        baseX += speed*GeomancyClient.tick;

        float x = zoom*baseX * (1+tintIndex*0.3f) + tintIndex*16;
        float y = zoom*baseY * (1+tintIndex*0.3f) + tintIndex*16;
        float z = zoom*baseZ * (1+tintIndex*0.3f) + tintIndex*16;

        float x2 = zoom*1.5f*((baseX+230) * (1+tintIndex*0.3f) + tintIndex*16);
        float y2 = zoom*1.5f*((baseY+590) * (1+tintIndex*0.3f) + tintIndex*16);
        float z2 = zoom*1.5f*((baseZ+367) * (1+tintIndex*0.3f) + tintIndex*16);

        float x3 = zoom*2f*((baseX+129) * (1+tintIndex*0.3f) + tintIndex*16);
        float y3 = zoom*2f*((baseY+395) * (1+tintIndex*0.3f) + tintIndex*16);
        float z3 = zoom*2f*((baseZ+529) * (1+tintIndex*0.3f) + tintIndex*16);

        hue = (float)(org.oxytocina.geomancy.util.SimplexNoise.noise(x,y,z)+1)/2;
        sat = (float) (1-Math.pow(1F-((SimplexNoise.noise(x2,y2,z2)+1)/2),2));
        val = (float) (1-Math.pow(1F-((SimplexNoise.noise(x3,y3,z3)+1)/2),2));

        float durability = 1;
        if(stack.isDamageable()){
            durability = 1f-(stack.getDamage()/(float)stack.getMaxDamage());
        }
        hue = MathHelper.lerp(durability,0,hue);

        return hsvToRgb(
                hue,sat,val
        );

    }

    public static int octanguliteItemBarNoise(float progress){
        float baseX = 0, baseY = 0, baseZ = 0;
        float zoom = 0.008f;

        baseX = GeomancyClient.tick;

        float x = zoom*baseX * (1+0.3f);
        float y = zoom*baseY * (1+0.3f);
        float z = zoom*baseZ * (1+0.3f);

        float x2 = zoom*1.5f*((baseX+230) * (1+0.3f));
        float y2 = zoom*1.5f*((baseY+590) * (1+0.3f));
        float z2 = zoom*1.5f*((baseZ+367) * (1+0.3f));

        float x3 = zoom*2f*((baseX+129) * (1+0.3f));
        float y3 = zoom*2f*((baseY+395) * (1+0.3f));
        float z3 = zoom*2f*((baseZ+529) * (1+0.3f));

        return hsvToRgb(
                (float)(org.oxytocina.geomancy.util.SimplexNoise.noise(x,y,z)+1)/2,
                (float) (1-Math.pow(1F-((SimplexNoise.noise(x2,y2,z2)+1)/2),2)),
                (float) (1-Math.pow(1F-((SimplexNoise.noise(x3,y3,z3)+1)/2),2))
        );

    }


    public static int hsvToRgb(float hue, float saturation, float value) {

        int h = (int)(hue * 6);
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0: return Toolbox.colorFromRGB(value, t, p);
            case 1: return Toolbox.colorFromRGB(q, value, p);
            case 2: return Toolbox.colorFromRGB(p, value, t);
            case 3: return Toolbox.colorFromRGB(p, q, value);
            case 4: return Toolbox.colorFromRGB(t, p, value);
            case 5: return Toolbox.colorFromRGB(value, p, q);
            default: throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
    }
}
