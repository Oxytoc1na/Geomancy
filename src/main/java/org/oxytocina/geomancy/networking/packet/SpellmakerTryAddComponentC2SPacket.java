package org.oxytocina.geomancy.networking.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.client.screen.SpellmakerScreen;
import org.oxytocina.geomancy.items.SpellComponentStoringItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.spells.SpellComponent;

public class SpellmakerTryAddComponentC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        NbtCompound nbt = buf.readNbt();
        BlockPos blockEntityPos = buf.readBlockPos();

        server.execute(()->{
            if(player==null||player.getWorld()==null) return;

            BlockEntity blockEntity = player.getWorld().getBlockEntity(blockEntityPos);
            if(blockEntity instanceof SpellmakerBlockEntity spellmaker){
                SpellComponent component = new SpellComponent(null,nbt);
                var function = component.function;
                // look for matching components in player inventory
                boolean canAfford = false;
                var availableComponents = spellmaker.getComponentItems(player.getInventory());
                for (int i = 0; i < availableComponents.size(); i++) {
                    ItemStack contender = availableComponents.getStack(i);
                    if(!(contender.getItem() instanceof SpellComponentStoringItem storer)) continue;
                    var contenderComponent = SpellComponentStoringItem.readComponent(contender);
                    if(contenderComponent==null) continue;
                    if(contenderComponent.function == function){
                        canAfford=true;
                        break;
                    }
                }

                if(canAfford){
                    ItemStack storageStack = spellmaker.getOutput();
                    if(storageStack.getItem() instanceof SpellStoringItem){
                        var grid = SpellStoringItem.getOrCreateGrid(storageStack);
                        if(grid.tryAddComponent(component)){

                            // successfully added to grid!!
                            SpellStoringItem.writeGrid(storageStack,grid);

                            // remove ingredient from player
                            for (int i = 0; i < player.getInventory().size(); i++) {
                                var stack = player.getInventory().getStack(i);
                                if(!(stack.getItem() instanceof SpellComponentStoringItem)) continue;
                                var contenderComp = SpellComponentStoringItem.readComponent(stack);
                                if(contenderComp.function!=function) continue;
                                stack.decrement(1);
                                break;
                            }

                            // send update package to client
                            PacketByteBuf data = PacketByteBufs.create();
                            data.writeBlockPos(blockEntityPos);
                            ServerPlayNetworking.send(player,ModMessages.SPELLMAKER_REFRESH, data);
                        }
                    }
                }
            }
        });


    }
}
