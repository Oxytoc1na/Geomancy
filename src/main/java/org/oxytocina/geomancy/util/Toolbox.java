package org.oxytocina.geomancy.util;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.oxytocina.geomancy.Geomancy;

import java.awt.*;
import java.util.*;

public class Toolbox {

    public static Random random = new Random();

    public static Vec3d RandomItemDropVelocity(float speed){
        float angle = random.nextFloat()*2*(float)Math.PI;
        return new Vec3d(1,0.5,0).rotateY(angle).multiply(speed);
    }

    public static ItemEntity spawnItemStackAsEntity(World world, Vec3d pos, ItemStack itemStack) {
        return spawnItemStackAsEntity(world, pos, itemStack, new Vec3d(0,0,0));
    }

    public static ItemEntity spawnItemStackAsEntity(World world, Vec3d pos, ItemStack itemStack, Vec3d velocity) {
        return spawnItemStackAsEntity(world, pos, itemStack, velocity, true, null);
    }

    public static ItemEntity spawnItemStackAsEntity(World world, Vec3d pos, ItemStack itemStack, Vec3d velocity, boolean neverDespawn, @Nullable Entity owner) {

            ItemStack resultStack = itemStack.copy();
            ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), resultStack);
            itemEntity.setVelocity(velocity);
            itemEntity.setPickupDelay(20);
            if (neverDespawn) {
                itemEntity.setNeverDespawn();
            }
            if (owner != null) {
                itemEntity.setOwner(owner.getUuid());
            }
            world.spawnEntity(itemEntity);
            return itemEntity;
    }

    public static int selectWeightedRandomIndex(int[] weights){
        Map<Integer,Integer> m = new HashMap<>();
        for (int i = 0; i < weights.length; i++) {
            m.put(i,weights[i]);
        }

        return selectWeightedRandomIndex(m,-1);
    }


    public static <T> T selectWeightedRandomIndex(Map<T,Integer> weights, T def){
        if(weights.isEmpty()) return def;
        int weightsum = weights.values().stream().mapToInt(a->a).sum();
        int weightpick = random.nextInt(weightsum);
        for(T key : weights.keySet()){
            weightpick-=weights.get(key);
            if(weightpick<=0) return key;
        }
        return def;
    }

    public static Identifier locate(String string){return Geomancy.locate(string);}

    public static int colorFromRGB(Vector3f colVec){
        return colorFromRGB(colVec.x,colVec.y,colVec.z);
    }

    public static int colorFromRGB(float r, float g, float b){
        return colorFromRGBA(r,g,b,1);
    }

    public static int colorFromRGBA(float r, float g, float b, float a){
        int red = ((int)(r*255))&255;
        int green = ((int)(g*255))&255;
        int blue = ((int)(b*255))&255;
        int alpha = ((int)(a*255))&255;
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static boolean itemStacksAreEqual(ItemStack a, ItemStack b){
        if(a.getItem()!=b.getItem()) return false;
        if(a.hasNbt()!=b.hasNbt()) return false;
        if(a.hasNbt() && !a.getNbt().equals(b.getNbt())) return false;
        return true;
    }

    @NotNull
    public static Vector3f colorIntToVec(int color) {
        Color colorObj = new Color(color);
        float[] argb = new float[4];
        colorObj.getColorComponents(argb);
        return new Vector3f(argb[0], argb[1], argb[2]);
    }

    public static int clampI(int v, int m, int m2){
        return Math.min(Math.max(v,m), m2);
    }

    public static float clampF(float v, float m, float m2){
        return Math.min(Math.max(v,m), m2);
    }

    public static float Lerp(float value, float target, float t){
        return (value * (1.0f - t)) + (target * t);
    }

    public static int LerpColor(int col1, int col2, float t){
        Vector3f vec1 = colorIntToVec(col1);
        Vector3f vec2 = colorIntToVec(col2);
        return colorFromRGB(lerpVector(vec1,vec2,t));
    }

    public static Vector3f lerpVector(Vector3f vec1, Vector3f vec2, float t){
        return new Vector3f(
                Lerp(vec1.x,vec2.x,t),
                Lerp(vec1.y,vec2.y,t),
                Lerp(vec1.z,vec2.z,t)
        );
    }

    public static double log(double base, double val){
        return Math.log(val)/Math.log(base);
    }

    public static <T> T ifNotNullThenElse(T val, T def){
        if(val==null) return def;
        return val;
    }

    public static int sign(float f){
        return f>0?1:f<0?-1:0;
    }

    public static GradientBuilder gradient(){return new GradientBuilder();}

    public static class GradientBuilder{
        public ArrayList<KeyFrame> keyFrames = new ArrayList<>();

        public GradientBuilder(){

        }

        public GradientBuilder add(float p, int col){
            keyFrames.add(new KeyFrame(p,col));
            keyFrames.sort(((o1, o2) -> Toolbox.sign(o1.position-o2.position)));
            return this;
        }

        public int get(float position){
            if(keyFrames.isEmpty()) return 0x000000;

            for (int i = 0; i < keyFrames.size()-1; i++) {
                if(keyFrames.get(i).position>position){
                    KeyFrame frame1 = keyFrames.get(i-1);
                    KeyFrame frame2 = keyFrames.get(i);

                    return LerpColor(frame1.color,frame2.color,(position-frame1.position)/(frame2.position-frame1.position));
                }
            }

            return keyFrames.get(keyFrames.size()-1).color;
        }

        public static class KeyFrame{
            public float position;
            public int color;

            public KeyFrame(float p, int col){
                this.position = p;
                this.color = col;
            }
        }
    }

    public static void playSound(SoundEvent event, World world, BlockPos pos, SoundCategory cat, float volume, float pitch){
        world.playSound(null,pos,event,cat,volume,pitch);
    }

    public static Vector2f rotateVector(Vector2f v1, double a){
        return new Vector2f(
                (float)(Math.cos(a)*v1.x-Math.sin(a)*v1.y),
                (float)(Math.sin(a)*v1.x-Math.cos(a)*v1.y)
                );
    }

    public static int floor(double d){
        return Math.round((float)Math.floor(d));
    }

    public static BlockPos posToBlockPos(Vec3d v){
        return new BlockPos(floor(v.x),floor(v.y),floor(v.z));
    }
}
