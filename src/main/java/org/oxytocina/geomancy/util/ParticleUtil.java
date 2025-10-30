package org.oxytocina.geomancy.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.LocalRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import org.oxytocina.geomancy.blocks.blockEntities.SmitheryBlockEntity;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.networking.packet.S2C.ParticlesS2CPacket;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellContext;

public class ParticleUtil {
    public static class ParticleData {
        public Type type = Type.CAST_SOUL;
        public int amount = 10;
        public float dispersion = 0.5f;
        public Vec3d pos;
        public Vec3d dir;
        public Vec3d velMin;
        public Vec3d velMax;
        public Identifier world;
        public String data = "";
        public World worldObj;

        private ParticleData(Type type, int amount, Vec3d pos, Vec3d dir, Identifier world,World worldObj, float dispersion){
            this.type=type;
            this.amount=amount;
            this.pos=pos;
            this.dir=dir;
            this.world=world;
            this.dispersion=dispersion;
            this.worldObj=worldObj;
            this.velMin = new Vec3d(0,0,0);
            this.velMax = new Vec3d(0,0,0);
        }

        public static ParticleData createGenericCastSuccess(SpellComponent comp, Vec3d pos){
            return create(comp.world(),pos).type(Type.CAST_SOUL).amount(10);
        }

        public static ParticleData createGenericCastBroke(SpellComponent comp, Vec3d pos){
            return create(comp.world(),pos).type(Type.CAST_SOUL_FIRE).amount(10);
        }

        public static ParticleData createGenericCastMuzzle(SpellContext ctx, Vec3d pos, Vec3d dir){
            return create(ctx.getWorld(),pos).type(Type.CAST_MUZZLE).amount(5).dir(dir).dispersion(0.2f);
        }


        public static ParticleData createGenericCastFail(SpellComponent comp, Vec3d pos){
            return create(comp.world(),pos).type(Type.CAST_SOUL_FIRE).amount(10);
        }

        public static ParticleData createSmithingProgress(World world, Vec3d pos){
            return create(world,pos.add(0,0.6f,0)).type(Type.SMITHING_PROGRESS).amount(5).vel(new Vec3d(0,0,0),new Vec3d(0,0,0)).dispersion(0.3f);
        }
        public static ParticleData createSmithingComplete(World world, Vec3d pos){
            return create(world,pos.add(0,0.6f,0)).type(Type.SMITHING_COMPLETE).amount(10).vel(new Vec3d(-0.2f,0,-0.2f),new Vec3d(0.2f,0.4f,0.2f)).dispersion(0.3f);
        }
        public static ParticleData createSmithingFailure(World world, Vec3d pos){
            return create(world,pos.add(0,0.6f,0)).type(Type.SMITHING_FAILURE).amount(10).vel(new Vec3d(-0.2f,0,-0.2f),new Vec3d(0.1f,0.2f,0.1f)).dispersion(0.3f);
        }
        public static ParticleData createSoulFlare(World world, Vec3d pos){
            return create(world,pos.add(0,0.6f,0)).type(Type.SOUL_FLARE).amount(10).vel(new Vec3d(-0.1f,0.05f,-0.1f),new Vec3d(0.1f,0.5f,0.1f)).dispersion(0.3f);
        }
        public static ParticleData createSoulDud(World world, Vec3d pos){
            return create(world,pos.add(0,0.6f,0)).type(Type.SOUL_DUD).amount(10).vel(new Vec3d(-0.1f,0.05f,-0.1f),new Vec3d(0.1f,0.05f,0.1f)).dispersion(0.3f);
        }
        public static ParticleData createInstability(World world, Vec3d pos){
            return create(world,pos.add(0,0.6f,0)).type(Type.INSTABILITY).amount(10).vel(new Vec3d(-0.1f,0.05f,-0.1f),new Vec3d(0.1f,0.05f,0.1f)).dispersion(0.3f);
        }
        public static ParticleData createForgeConsume(World world, Vec3d from, Vec3d to){
            return create(world,from).type(Type.FORGE_CONSUME).amount(1).vel(to,to);
        }
        public static ParticleData createGeneric(World world, ParticleType<?> type, Vec3d pos, Vec3d vel, int count, float disp){
            return create(world,pos).type(Type.GENERIC).amount(count).dispersion(disp).vel(vel,vel).data(Registries.PARTICLE_TYPE.getId(type).toString());
        }
        public static ParticleData createRestrictedAction(World world, Vec3d where){
            return create(world,where).type(Type.RESTRICTED_ACTION).amount(20).dispersion(0.75f).vel(new Vec3d(-0.2,-0.2,-0.2), new Vec3d(0.2,0.2,0.2));
        }
        public static ParticleData createBlock(World world, BlockState state, Vec3d pos, Vec3d vel, int count, float disp){
            return create(world,pos).type(Type.BLOCK).amount(count).dispersion(disp).vel(vel,vel).data(NetworkingUtil.serializeBlockState(state));
        }

        
        public static ParticleData create(World world, Vec3d pos){
            return new ParticleData(Type.CAST_SOUL, 10,pos,new Vec3d(0,0,0),world.getRegistryKey().getValue(),world,0.5f);
        }

        public ParticleData amount(int amount){this.amount = amount;return this;}
        public ParticleData dir(Vec3d dir){this.dir = dir;return this;}
        public ParticleData vel(Vec3d min,Vec3d max){this.velMin = min; this.velMax=max;return this;}
        public ParticleData dispersion(float dispersion){this.dispersion = dispersion;return this;}
        public ParticleData type(Type type){this.type = type;return this;}
        public ParticleData data(String data){this.data = data;return this;}

        public void send(){
            ParticlesS2CPacket.send(worldObj,this);
        }

        public void write(PacketByteBuf buf){
            buf.writeString(type.toString());
            buf.writeInt(amount);
            buf.writeFloat(dispersion);
            buf.writeVector3f(pos.toVector3f());
            buf.writeVector3f(dir.toVector3f());
            buf.writeVector3f(velMin.toVector3f());
            buf.writeVector3f(velMax.toVector3f());
            buf.writeIdentifier(world);
            buf.writeString(data);
        }

        public static ParticleData from(PacketByteBuf buf){
            Type type = Type.valueOf(buf.readString());
            int amount = buf.readInt();
            float dispersion = buf.readFloat();
            Vec3d pos = new Vec3d(buf.readVector3f());
            Vec3d dir = new Vec3d(buf.readVector3f());
            Vec3d velMin = new Vec3d(buf.readVector3f());
            Vec3d velMax = new Vec3d(buf.readVector3f());
            Identifier world = buf.readIdentifier();
            String data = buf.readString();
            var res = new ParticleData(type,amount,pos,dir,world,null,dispersion);
            res.velMin = velMin;
            res.velMax = velMax;
            res.data=data;
            return res;
        }

        @Environment(EnvType.CLIENT)
        public void run(){
            ClientWorld worldObj = MinecraftClient.getInstance().world;
            if(!worldObj.getRegistryKey().getValue().equals(world)) return; // ignore particle spawns in different worlds
            Random rand = new LocalRandom(GeomancyClient.tick);
            BlockState state = switch(type) { case BLOCK-> NetworkingUtil.parseBlockState(data); default->null;};
            for (int i = 0; i < amount; i++) {
                Vec3d pPos = new Vec3d(
                        pos.x+(rand.nextFloat()*2-1)*dispersion,
                        pos.y+(rand.nextFloat()*2-1)*dispersion,
                        pos.z+(rand.nextFloat()*2-1)*dispersion);
                Vec3d vel = new Vec3d(
                        MathHelper.lerp(rand.nextFloat(),velMin.x,velMax.x),
                        MathHelper.lerp(rand.nextFloat(),velMin.y,velMax.y),
                        MathHelper.lerp(rand.nextFloat(),velMin.z,velMax.z));
                switch(type){
                    case CAST_SOUL:{
                        worldObj.addParticle(ParticleTypes.SCULK_SOUL,pPos.x,pPos.y,pPos.z,0,0,0);
                        break;
                    }
                    case CAST_SOUL_FIRE:{
                        Vec3d randVel = new Vec3d(0,0,0).addRandom(rand,0.08f);
                        worldObj.addParticle(ParticleTypes.SOUL_FIRE_FLAME,pPos.x,pPos.y,pPos.z,randVel.x,randVel.y,randVel.z);
                        break;
                    }
                    case CAST_MUZZLE:{
                        Vec3d randVel = dir.multiply(0.2f).addRandom(rand,0.08f);
                        worldObj.addParticle(ParticleTypes.SOUL_FIRE_FLAME,pPos.x,pPos.y,pPos.z,randVel.x,randVel.y,randVel.z);
                        break;
                    }
                    case SMITHING_PROGRESS:{
                        worldObj.addParticle(ParticleTypes.LAVA,pPos.x,pPos.y,pPos.z,vel.x,vel.y,vel.z);
                        break;
                    }
                    case SMITHING_COMPLETE:{
                        worldObj.addParticle(ParticleTypes.LAVA,pPos.x,pPos.y,pPos.z,0,0,0);
                        worldObj.addParticle(ParticleTypes.FLAME,pPos.x,pPos.y,pPos.z,vel.x,vel.y,vel.z);
                        break;
                    }
                    case SMITHING_FAILURE:{
                        worldObj.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,pPos.x,pPos.y,pPos.z,vel.x,vel.y,vel.z);
                        break;
                    }
                    case SOUL_FLARE:{
                        worldObj.addParticle(ParticleTypes.SOUL_FIRE_FLAME,pPos.x,pPos.y,pPos.z,vel.x,vel.y,vel.z);
                        worldObj.addParticle(ParticleTypes.SCULK_SOUL,pPos.x,pPos.y,pPos.z,0,0,0);
                        break;
                    }
                    case SOUL_DUD:{
                        worldObj.addParticle(ParticleTypes.SOUL_FIRE_FLAME,pPos.x,pPos.y,pPos.z,vel.x,vel.y,vel.z);
                        break;
                    }
                    case INSTABILITY:{
                        worldObj.addParticle(ParticleTypes.FIREWORK,pPos.x,pPos.y,pPos.z,vel.x,vel.y,vel.z);
                        worldObj.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,pPos.x,pPos.y,pPos.z,0,0,0);
                        break;
                    }
                    case FORGE_CONSUME:{
                        var dir = vel.subtract(pPos);
                        float distance = (float)dir.length();
                        for (int j = 0; j < distance*5; j++) {
                            Vec3d newPos = pPos.add(dir.multiply(j/5f/distance));
                            worldObj.addParticle(ParticleTypes.SCULK_SOUL,newPos.x,newPos.y,newPos.z,0,0,0);
                            worldObj.addParticle(ParticleTypes.SOUL_FIRE_FLAME,newPos.x,newPos.y,newPos.z,Toolbox.randomBetween(-0.1f,0.1f),Toolbox.randomBetween(0,0.1f),Toolbox.randomBetween(-0.1f,0.1f));
                        }
                        break;
                    }
                    case GENERIC:{
                        ParticleType<?> type = Registries.PARTICLE_TYPE.get(Identifier.tryParse(data));
                        if(type instanceof ParticleEffect effect){
                            worldObj.addParticle(effect,pPos.x,pPos.y,pPos.z,vel.x,vel.y,vel.z);
                        }
                        break;
                    }
                    case RESTRICTED_ACTION:{
                        worldObj.addParticle(ParticleTypes.SQUID_INK,pPos.x,pPos.y,pPos.z,0,0,0);
                        break;
                    }
                    case BLOCK:{
                        addBlockBreakParticle(worldObj,pPos,vel,state);
                        break;
                    }
                }
            }
        }

        public enum Type{
            CAST_SOUL,
            CAST_SOUL_FIRE,
            CAST_MUZZLE,
            SMITHING_PROGRESS,
            SMITHING_COMPLETE,
            SMITHING_FAILURE,
            SOUL_FLARE,
            SOUL_DUD,
            INSTABILITY,
            FORGE_CONSUME,
            GENERIC,
            RESTRICTED_ACTION,
            BLOCK
        }

        public enum Shape{
            DEFAULT,
            RING
        }
    }

    public static void addBlockBreakParticles(ClientWorld world, BlockPos pos, BlockState state) {
        if (!state.isAir() && state.hasBlockBreakParticles()) {
            VoxelShape voxelShape = state.getOutlineShape(world, pos);
            voxelShape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> {
                double d = Math.min((double)1.0F, maxX - minX);
                double e = Math.min((double)1.0F, maxY - minY);
                double f = Math.min((double)1.0F, maxZ - minZ);
                int i = Math.max(2, MathHelper.ceil(d / (double)0.25F));
                int j = Math.max(2, MathHelper.ceil(e / (double)0.25F));
                int k = Math.max(2, MathHelper.ceil(f / (double)0.25F));

                for(int l = 0; l < i; ++l) {
                    for(int m = 0; m < j; ++m) {
                        for(int n = 0; n < k; ++n) {
                            double g = ((double)l + (double)0.5F) / (double)i;
                            double h = ((double)m + (double)0.5F) / (double)j;
                            double o = ((double)n + (double)0.5F) / (double)k;
                            double p = g * d + minX;
                            double q = h * e + minY;
                            double r = o * f + minZ;
                            MinecraftClient.getInstance().particleManager.addParticle(new BlockDustParticle(world, (double)pos.getX() + p, (double)pos.getY() + q, (double)pos.getZ() + r, g - (double)0.5F, h - (double)0.5F, o - (double)0.5F, state, pos));
                        }
                    }
                }

            });
        }
    }

    public static void addBlockBreakParticle(ClientWorld world, Vec3d pos, Vec3d vel, BlockState state) {
        if (!state.isAir() && state.hasBlockBreakParticles()) {
            MinecraftClient.getInstance().particleManager.addParticle(new BlockDustParticle(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), vel.x, vel.y,vel.z, state, Toolbox.posToBlockPos(pos)));
        }
    }
}
