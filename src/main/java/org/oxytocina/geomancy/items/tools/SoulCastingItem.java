package org.oxytocina.geomancy.items.tools;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.oxytocina.geomancy.items.*;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.registries.ModItemTags;
import org.oxytocina.geomancy.spells.SpellBlockArgs;
import org.oxytocina.geomancy.spells.SpellContext;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.List;

public class SoulCastingItem extends StorageItem implements ISoulStoringItem, IScrollListenerItem, ICustomRarityItem, ISpellSelectorItem {

    public final float internalSoulStorage;
    public final float rechargeSpeedMultiplier;

    public SoulCastingItem(Settings settings, int storageSize, float internalSoulStorage,float rechargeSpeedMultiplier) {
        super(settings, storageSize,ModItemTags.FITS_IN_CASTERS,false);
        this.internalSoulStorage=internalSoulStorage;
        this.rechargeSpeedMultiplier=rechargeSpeedMultiplier;
    }

    @Override
    public boolean autocollects() {
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {

            if(user.isSneaking())
            {
                // open spell selection
                if(user instanceof ServerPlayerEntity sp){
                    var stack = user.getStackInHand(hand);
                    sp.openHandledScreen((SoulCastingItem) stack.getItem());
                }
            }
            else{
                cast(user.getStackInHand(hand),user);

            }


            return TypedActionResult.consume(user.getStackInHand(hand));
        } else {

        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public void cast(ItemStack key, LivingEntity user){
        int index = getSelectedSpellIndex(key);
        var spells = getCastableSpellItems(key);
        if(spells.isEmpty()) return;
        ItemStack spellContainer = spells.get(index);

        if(!(spellContainer.getItem() instanceof SpellStoringItem storer)) return;

        storer.cast(key,spellContainer,user, SpellBlockArgs.empty(), SpellContext.SoundBehavior.Full,false);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        //super.appendTooltip(stack, world, tooltip, context);

        var selectedSpellIndex = getSelectedSpellIndex(stack);
        var spells = getCastableSpellItems(stack);
        if(!spells.isEmpty())
        {
            selectedSpellIndex = selectedSpellIndex%spells.size();
            for (int i = 0; i < spells.size(); i++) {
                var spell = spells.get(i);
                var grid = SpellStoringItem.readGrid(spell);
                String selectedString = "  ";
                if(selectedSpellIndex == i) selectedString = "> ";
                tooltip.add(
                        Text.literal(selectedString).formatted(Formatting.DARK_AQUA).append(
                                grid==null?Text.translatable("geomancy.spellstorage.empty").formatted(Formatting.DARK_GRAY)
                                        :grid.name==""?Text.translatable("geomancy.spellstorage.unnamed").formatted(Formatting.GRAY)
                                        : Text.literal(grid.name).formatted(Formatting.GRAY)));

            }
        }
        else{
            tooltip.add(Text.translatable("geomancy.caster.emptyhint1").formatted(Formatting.DARK_GRAY));
            tooltip.add(Text.translatable("geomancy.caster.emptyhint2").formatted(Formatting.DARK_GRAY));
            tooltip.add(Text.translatable("geomancy.caster.emptyhint3").formatted(Formatting.DARK_GRAY));
            tooltip.add(Text.translatable("geomancy.caster.emptyhint4").formatted(Formatting.DARK_GRAY));
        }

    }

    @Override
    public float getBaseSoulCapacity(ItemStack stack) {
        return internalSoulStorage;
    }

    @Override
    public float getRechargeSpeedMultiplier(World world, ItemStack stack, LivingEntity entity) {
        return rechargeSpeedMultiplier*ISoulStoringItem.super.getRechargeSpeedMultiplier(world, stack, entity);
    }

    @Override
    public Text getName(ItemStack stack) {
        var spells = getCastableSpellItems(stack);
        MutableText spellText = null;
        if(spells.isEmpty()){
            spellText = Text.translatable("geomancy.caster.nospells").formatted(Formatting.RED);
        }
        else{
            var nextIndex=getSelectedSpellIndex(stack);
            var spellItem = spells.get(nextIndex);
            var grid = SpellStoringItem.readGrid(spellItem);
            MutableText indexText = Text.literal((nextIndex+1)+"/"+spells.size()+"/"+getStorageSize(stack)+": ").formatted(Formatting.GRAY);
            if(grid==null)
            {
                spellText = indexText.append(Text.translatable("geomancy.spellstorage.empty").formatted(Formatting.GRAY));
            }
            else{
                spellText = indexText.append(grid.getName().formatted(Formatting.DARK_AQUA));
            }
        }

        return ((MutableText)colorizeName(stack,Text.translatable(this.getTranslationKey(stack)))).append(Text.literal(" [").append(
                spellText
                ).append("]").formatted(Formatting.GRAY));
    }


    @Override
    @Environment(EnvType.CLIENT)
    public boolean onScrolled(ItemStack stack, float delta,PlayerEntity player) {
        if(!player.isSneaking()) return false;

        int dir = -Toolbox.sign(delta);

        int nextIndex = getSelectedSpellIndex(stack)+dir;
        setSelectedSpellIndex(stack,nextIndex);
        nextIndex=getSelectedSpellIndex(stack);

        displaySelectedSpell(stack,player,nextIndex);

        // send packet to server
        PacketByteBuf data = PacketByteBufs.create();

        data.writeItemStack(stack);
        data.writeInt(player.getInventory().indexOf(stack));
        data.writeInt(nextIndex);
        ClientPlayNetworking.send(ModMessages.CASTER_CHANGE_SELECTED_SPELL, data);

        return true;
    }

    @Environment(EnvType.CLIENT)
    public void displaySelectedSpell(ItemStack stack, PlayerEntity player, int index){
        // display selected spell
        var spells = getCastableSpellItems(stack);
        if(spells.isEmpty()){
            player.sendMessage(Text.translatable("geomancy.caster.nospells").formatted(Formatting.RED),true);
        }
        else{
            var spellItem = spells.get(index);
            var grid = SpellStoringItem.readGrid(spellItem);
            MutableText indexText = Text.literal((index+1)+"/"+spells.size()+"/"+getStorageSize(stack)+": ").formatted(Formatting.GRAY);
            if(grid==null)
            {
                player.sendMessage(indexText.append(Text.translatable("geomancy.spellstorage.empty").formatted(Formatting.GRAY)),true);
            }
            else{
                player.sendMessage(indexText.append(grid.getName().formatted(Formatting.DARK_AQUA)),true);
            }
        }
    }

    // display selected spell if sneaking
    @Override
    @Environment(EnvType.CLIENT)
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if(selected && entity instanceof ClientPlayerEntity player && player.isSneaking())
            displaySelectedSpell(stack,player,getSelectedSpellIndex(stack));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean shouldBlockScrolling(ItemStack stack, PlayerEntity player) {
        return player.isSneaking();
    }

    @Override
    public Text getDisplayName() {
        return getName();
    }

    @Override
    public Rarity getRarity() {
        return Rarity.None;
    }

    public boolean tempOpenStorageScreenOverride = false;
    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        var stack = player.getStackInHand(player.getActiveHand());
        if(!(stack.getItem() instanceof StorageItem sci)) return null;

        if(tempOpenStorageScreenOverride)
            return super.createMenu(syncId,playerInventory,player);

        // if there are no spells installed, open the storage screen straight away
        if(stack.getItem() instanceof ISpellSelectorItem sps){
            var castables = sps.getCastableSpellItems(stack);
            if(castables.isEmpty()){
                return super.createMenu(syncId,playerInventory,player);
            }
        }

        // open spell selection screen
        if(player instanceof ServerPlayerEntity spe)
        {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeItemStack(stack);
            buf.writeInt(playerInventory.getSlotWithStack(stack));
            ServerPlayNetworking.send(spe, ModMessages.OPEN_SPELL_SELECT_SCREEN,buf);
        }
        return null;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onSpellChanged(ItemStack stack, ClientPlayerEntity player, int spellIndex) {
        displaySelectedSpell(stack,player,spellIndex);
    }
}
