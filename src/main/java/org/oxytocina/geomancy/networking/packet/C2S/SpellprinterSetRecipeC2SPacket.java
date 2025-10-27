package org.oxytocina.geomancy.networking.packet.C2S;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellprinterBlockEntity;
import org.oxytocina.geomancy.items.SpellStoringItem;

public class SpellprinterSetRecipeC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        BlockPos blockEntityPos = buf.readBlockPos();
        String recipe = buf.readString();

        server.execute(()->{
            if(player==null||player.getWorld()==null) return;

            BlockEntity blockEntity = player.getWorld().getBlockEntity(blockEntityPos);
            if(!(blockEntity instanceof SpellprinterBlockEntity spellPrinter)) return;

            spellPrinter.setRecipe(recipe);
        });


    }
}
