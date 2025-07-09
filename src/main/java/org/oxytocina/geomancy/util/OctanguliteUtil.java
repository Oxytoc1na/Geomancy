package org.oxytocina.geomancy.util;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.oxytocina.geomancy.blocks.ILeadPoisoningBlock;
import org.oxytocina.geomancy.blocks.IOctanguliteBlock;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.items.ILeadPoisoningItem;
import org.oxytocina.geomancy.items.IMaddeningItem;
import org.oxytocina.geomancy.networking.ModMessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.oxytocina.geomancy.util.LeadUtil.HeldInfluenceItem;

public class OctanguliteUtil {

    private static final ArrayList<PlayerEntity> queuedRecalcs = new ArrayList<>();

    private static final HashMap<BlockPos,Float> cachedAmbientMadness = new HashMap<>();

    /// DOESNT sync poisoning
    public static boolean setMadness(PlayerEntity player, float amount){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        float old = data.madness;
        if(old==amount) return false;
        data.madness = amount;
        return true;
    }

    /// DOESNT sync poisoning
    public static boolean setMaddeningSpeed(PlayerEntity player, float newVal){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        float old = data.madnessSpeed;
        if(old==newVal) return false;
        data.madnessSpeed = newVal;
        return true;
    }

    public static float getMadness(PlayerEntity player){
        return PlayerData.from(player).madness;
    }

    // returns only the poisoning speed from a players inventory, without ambiance added in
    public static float getMadnessSpeed(PlayerEntity player){
        return PlayerData.from(player).madnessSpeed;
    }

    /// call after equipping/unequipping lead items
    /// syncs poisoning
    private static void recalculateMadnessSpeed(PlayerEntity player){
        if(!(player instanceof ServerPlayerEntity)) return;

        float speed = 0;

        var items = getAllMaddeningItems(player);
        for(var item : items){
            if(item.stack.getItem() instanceof ILeadPoisoningItem poisoner){
                if(item.inHand)
                    speed += poisoner.getInHandPoisoningSpeed()*item.stack.getCount();
                else if(item.worn)
                    speed += poisoner.getWornPoisoningSpeed()*item.stack.getCount();
                else speed += poisoner.getInInventoryPoisoningSpeed()*item.stack.getCount();
            }
        }

        setMaddeningSpeed(player,speed);

        syncMadness(player);
    }

    private static long clearCacheCounter = 0;
    public static void tick(MinecraftServer server){
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            OctanguliteUtil.tickMadness(player);
        }

        // recalc mana
        for(PlayerEntity entity : queuedRecalcs){
            recalculateMadnessSpeed(entity);
        }
        queuedRecalcs.clear();

        if(++clearCacheCounter > 60){
            cachedAmbientMadness.clear();
            clearCacheCounter=0;
        }
    }

    public static void queueRecalculateMadnessSpeed(PlayerEntity player){
        if(!queuedRecalcs.contains(player))
            queuedRecalcs.add(player);
    }

    public static void syncMadness(PlayerEntity player){
        if(!(player instanceof ServerPlayerEntity spe)) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(getMadnessSpeed(player));
        buf.writeFloat(getMadness(player));
        ServerPlayNetworking.send(spe,ModMessages.MADNESS_SYNC,buf);
    }

    public static float getAmbientMadness(Entity entity){
        return getAmbientMadness(entity.getWorld(),entity.getBlockPos());
    }
    public static float getAmbientMadness(World world, BlockPos pos){
        if(cachedAmbientMadness.containsKey(pos)) return cachedAmbientMadness.get(pos);

        float res = 0;

        cachedAmbientMadness.put(pos,res);
        return res;
    }

    private static boolean tickMadness(ServerPlayerEntity player){
        boolean changed = false;

        float prevPoisoning = getMadness(player);
        float playerPoisoningSpeed = getMadnessSpeed(player);
        float ambientPoisoningSpeed = getAmbientMadness(player);

        float effectivePoisoningSpeed = playerPoisoningSpeed+ambientPoisoningSpeed;

        effectivePoisoningSpeed *= 0.01f;

        // prevents poisoning from climbing endlessly
        final float lerpPerTick = 0.001f;

        float newPoisoning = Toolbox.Lerp(prevPoisoning,0,lerpPerTick) + effectivePoisoningSpeed;

        if(newPoisoning!=prevPoisoning){
            changed=true;
            setMadness(player,newPoisoning);
        }

        if(changed) syncMadness(player);

        return changed;
    }

    public static boolean isMaddening(ItemStack stack){
        return stack.getItem() instanceof IMaddeningItem;
    }

    public static boolean isMaddening(BlockState block){
        return block.getBlock() instanceof IOctanguliteBlock;
    }

    public static List<HeldInfluenceItem> getAllMaddeningItems(PlayerEntity player){
        List<HeldInfluenceItem> res = new ArrayList<>();
        var inv = player.getInventory();
        var trinketComp = TrinketsApi.getTrinketComponent(player);
        var trinketInv = trinketComp.map(TrinketComponent::getAllEquipped).orElse(null);
        if(inv!=null){
            for(ItemStack s : inv.main) if(isMaddening(s)) res.add(new HeldInfluenceItem(s,player.getMainHandStack() == s,false));
            for(ItemStack s : inv.armor) if(isMaddening(s)) res.add(new HeldInfluenceItem(s,false,true));
            for(ItemStack s : inv.offHand) if(isMaddening(s)) res.add(new HeldInfluenceItem(s,true,false));
        }
        if(trinketInv!=null){
            for(var pair : trinketInv) if(isMaddening(pair.getRight())) res.add(new HeldInfluenceItem(pair.getRight(),false,true));
        }
        return res;
    }

}
