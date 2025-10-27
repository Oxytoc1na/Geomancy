package org.oxytocina.geomancy.networking.packet.C2S;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.oxytocina.geomancy.blocks.MultiblockCrafter;
import org.oxytocina.geomancy.blocks.blockEntities.SpellmakerBlockEntity;
import org.oxytocina.geomancy.blocks.blockEntities.SpellprinterBlockEntity;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.spells.SpellComponent;
import org.oxytocina.geomancy.spells.SpellGrid;

public class SpellprinterDesirePrintC2SPacket {

    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender){
        BlockPos blockEntityPos = buf.readBlockPos();
        String recipe = buf.readString();

        server.execute(()->{
            if(player==null||player.getWorld()==null) return;

            BlockEntity blockEntity = player.getWorld().getBlockEntity(blockEntityPos);
            if(!(blockEntity instanceof SpellprinterBlockEntity spellPrinter)) return;
            ItemStack outputStack = spellPrinter.getOutput();
            if(!(outputStack.getItem() instanceof SpellStoringItem storer))return; // output item isnt a cradle
            var existingGrid = SpellStoringItem.readGrid(outputStack);
            if(existingGrid!=null && !existingGrid.isEmpty()) return; // grid already exists!
            var target = SpellprinterBlockEntity.parseGrid(recipe);
            var targetGrid = target.getLeft();
            if(targetGrid==null) return; // invalid recipe
            var item = target.getRight();
            if(outputStack.getItem()!=item) return; // invalid cradle

            var ingredients = targetGrid.getIngredients();
            boolean creative = MinecraftClient.getInstance().player.isCreative();
            var ownedIngredients = creative?null:SpellmakerBlockEntity.getComponentAmountsIn(MinecraftClient.getInstance().player.getInventory());
            boolean canAfford = true;
            if(!creative)
                for(var func : ingredients.keySet()){
                    int needed = ingredients.get(func);
                    int owned = creative?1000000: ownedIngredients.getOrDefault(func, 0);
                    int deficit = needed-owned;
                    if(deficit>0){canAfford=false;break;}
                }
            if(!canAfford) return; // cant afford to print

            // take components
            if(!creative)
            {
                for(var ingredient : ingredients.keySet()){
                    SpellmakerBlockEntity.removeComponentFrom(ingredient,ingredients.get(ingredient),player.getInventory());
                }
                player.getInventory().markDirty();
            }

            // print
            SpellStoringItem.writeGrid(outputStack,targetGrid);
            spellPrinter.markDirty();
        });


    }
}
