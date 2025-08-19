package org.oxytocina.geomancy.networking.packet.C2S;

import com.klikli_dev.modonomicon.api.multiblock.Multiblock;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.oxytocina.geomancy.blocks.MultiblockCrafter;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellComponent;

public class SpellmakerTryRemoveComponentC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        NbtCompound nbt = buf.readNbt();
        BlockPos blockEntityPos = buf.readBlockPos();

        server.execute(()->{
            if(player==null||player.getWorld()==null) return;

            BlockEntity blockEntity = player.getWorld().getBlockEntity(blockEntityPos);
            if(blockEntity instanceof SpellmakerBlockEntity spellmaker){
                SpellComponent component = new SpellComponent(null,nbt);
                ItemStack storageStack = spellmaker.getOutput();
                if(storageStack.getItem() instanceof SpellStoringItem storer){
                    var function = component.function;
                    var grid = SpellStoringItem.getOrCreateGrid(storageStack);
                    if(grid.tryRemoveComponent(component.position)){

                        // successfully removed!!
                        SpellStoringItem.writeGrid(storageStack,grid);

                        // give the player the ingredient back
                        if(!player.isCreative())
                        {
                            MultiblockCrafter.spawnOutputAsItemEntity(player.getWorld(),blockEntityPos.up(), storer.getDefaultStack());
                        }

                        // send update package to client
                        PacketByteBuf data = PacketByteBufs.create();
                        data.writeBlockPos(blockEntityPos);
                        ServerPlayNetworking.send(player, ModMessages.SPELLMAKER_REFRESH, data);
                    }
                }
            }
        });


    }
}
