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

import java.util.function.Function;

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
                ModBlocks.OCTANGULITE_BRICK_WALL,
                ModBlocks.SOUL_OAK_PLANKS,
                ModBlocks.SOUL_OAK_LOG,
                ModBlocks.STRIPPED_SOUL_OAK_LOG,
                ModBlocks.SOUL_OAK_WOOD,
                ModBlocks.STRIPPED_SOUL_OAK_WOOD,
                ModBlocks.SOUL_OAK_BUTTON,
                ModBlocks.SOUL_OAK_PRESSURE_PLATE,
                ModBlocks.SOUL_OAK_FENCE,
                ModBlocks.SOUL_OAK_FENCE_GATE,
                ModBlocks.SOUL_OAK_SIGN,
                ModBlocks.SOUL_OAK_HANGING_SIGN,
                ModBlocks.SOUL_OAK_WALL_SIGN,
                ModBlocks.SOUL_OAK_WALL_HANGING_SIGN,
                ModBlocks.SOUL_OAK_SLAB,
                ModBlocks.SOUL_OAK_STAIRS,
                ModBlocks.SOUL_OAK_TRAPDOOR,
                ModBlocks.SOUL_OAK_DOOR,
                ModBlocks.SOUL_OAK_LOG,
                ModBlocks.STRIPPED_SOUL_OAK_LOG,
                ModBlocks.STRIPPED_SOUL_OAK_WOOD
                );

        // soul oak wood only has one tint index, but should use tint index 1 for the bark
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
                    if (view == null || pos == null) {return 0xFFFFFFFF;} else {return octanguliteNoise(pos,tintIndex+1,0.03f);}},
                ModBlocks.SOUL_OAK_WOOD);

        // similarly, have leaves offset their tint index by 2
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
                    if (view == null || pos == null) {return 0xFFFFFFFF;} else {return octanguliteNoise(pos,tintIndex+2,0.03f);}},
                ModBlocks.SOUL_OAK_LEAVES);

        // similarly, have saplings morph their tint indices from 0 (leaves) -> 2, and 1 (bark) -> 1
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
                    if (view == null || pos == null) {return 0xFFFFFFFF;} else {return octanguliteNoise(pos,2-tintIndex,0.03f);}},
                ModBlocks.SOUL_OAK_SAPLING, ModBlocks.POTTED_SOUL_OAK_SAPLING);

        // octangulite ores
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
            if (view == null || pos == null || tintIndex == 0) {return 0xFFFFFFFF;} else {return octanguliteNoise(pos,tintIndex,0.003f);}
        }, ModBlocks.OCTANGULITE_ORE,ModBlocks.DEEPSLATE_OCTANGULITE_ORE);

        // tourmaline ores
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> {
                    if (view == null || pos == null) {return 0xFFFFFFFF;} else {return tintIndex==0?0xFFFFFFFF:tourmalineNoise(pos,tintIndex,0.3f);}},
                ModBlocks.TOURMALINE_ORE,ModBlocks.DEEPSLATE_TOURMALINE_ORE);
        ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> tintIndex==0?0xFFFFFFFF:tourmalineNoise(BlockPos.ORIGIN,tintIndex, 0.3f),
                ModBlocks.TOURMALINE_ORE.asItem(),ModBlocks.DEEPSLATE_TOURMALINE_ORE.asItem());

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
        addOctanguliteItem(ModBlocks.SOUL_OAK_PLANKS.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_LOG.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.STRIPPED_SOUL_OAK_LOG.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_WOOD.asItem(),0.03F,false,t->t+1);
        addOctanguliteItem(ModBlocks.STRIPPED_SOUL_OAK_WOOD.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_BUTTON.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_DOOR.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_TRAPDOOR.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_FENCE.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_FENCE_GATE.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_PRESSURE_PLATE.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_SAPLING.asItem(),0.03F,false,t->2-t);
        addOctanguliteItem(ModBlocks.SOUL_OAK_LEAVES.asItem(),0.03F,false,t->t+2);
        //addOctanguliteItem(ModBlocks.POTTED_SOUL_OAK_SAPLING.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_SIGN.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_HANGING_SIGN.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_STAIRS.asItem(),0.03F,false);
        addOctanguliteItem(ModBlocks.SOUL_OAK_SLAB.asItem(),0.03F,false);

        addOctanguliteOreItem(ModBlocks.OCTANGULITE_ORE.asItem(),0.003F,false);
        addOctanguliteOreItem(ModBlocks.DEEPSLATE_OCTANGULITE_ORE.asItem(),0.003F,false);

        addOctanguliteToolItem(ModItems.OCTANGULITE_SWORD);
        addOctanguliteToolItem(ModItems.OCTANGULITE_SHOVEL);
        addOctanguliteToolItem(ModItems.OCTANGULITE_PICKAXE);
        addOctanguliteToolItem(ModItems.OCTANGULITE_AXE);
        addOctanguliteToolItem(ModItems.OCTANGULITE_HOE);
        addOctanguliteArmorItem(ModItems.OCTANGULITE_BOOTS);
        addOctanguliteArmorItem(ModItems.OCTANGULITE_LEGGINGS);
        addOctanguliteArmorItem(ModItems.OCTANGULITE_CHESTPLATE);
        addOctanguliteArmorItem(ModItems.OCTANGULITE_HELMET);
    }

    private static void addOctanguliteItem(Item item,float zoom,boolean withSlotOffset){
        addOctanguliteItem(item,zoom,withSlotOffset,t->t);
    }

    private static void addOctanguliteItem(Item item, float zoom, boolean withSlotOffset, Function<Integer,Integer> indices){
        ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> octanguliteItemNoise(stack, indices.apply(tintIndex), zoom,withSlotOffset),item);
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

    private static void addOctanguliteArmorItem(Item item){
        ColorProviderRegistry.ITEM.register(
                (stack, tintIndex) -> {
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

        final float hueShift = 83/360f;

        hue = (float)(org.oxytocina.geomancy.util.SimplexNoise.noise(x,y,z)+1)/2*(1-hueShift) + hueShift;
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

        // desaturate emptier bars
        float sat = MathHelper.lerp(0.3f+0.7f*progress,0,(float) (1-Math.pow(1F-((SimplexNoise.noise(x2,y2,z2)+1)/2),2)));

        // ensure its readable
        float val = 0.4f + 0.6f * (float) (1-Math.pow(1F-((SimplexNoise.noise(x3,y3,z3)+1)/2),2));

        return hsvToRgb(
                (float)(org.oxytocina.geomancy.util.SimplexNoise.noise(x,y,z)+1)/2,
                sat,val

        );

    }

    private static int tourmalineNoise(BlockPos pos, int tintIndex, float zoom){
        float x = zoom*(pos.getX() * (1+tintIndex*0.3f) + tintIndex*16);
        float y = zoom*(pos.getY() * (1+tintIndex*0.3f) + tintIndex*16);
        float z = zoom*(pos.getZ() * (1+tintIndex*0.3f) + tintIndex*16);
        float n = (float)(org.oxytocina.geomancy.util.SimplexNoise.noise(x,y,z)+1)/2;

        final Function<Float, Integer> sup = (a)->{
            if(a<0.27f)
                return 0x15D4E6; // cyan
            else if(a< 0.5f)
                return 0x01B497; // teal;
            else if(a < 0.65f)
                return 0xAE6D35; // brown
            else if(a < 0.8f)
                return 0xFF2D41; // red
            else if(a < 0.9f)
                return 0x00293F; // dark blue
            else
                return 0x452E38; // dark purple
        };

        return sup.apply(n);

    }


    public static int hsvToRgb(float hue, float saturation, float value) {

        hue = Toolbox.clampF(hue,0,1);
        saturation = Toolbox.clampF(saturation,0,1);
        value = Toolbox.clampF(value,0,1);

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
