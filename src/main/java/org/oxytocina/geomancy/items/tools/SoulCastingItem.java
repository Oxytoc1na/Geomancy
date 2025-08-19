package org.oxytocina.geomancy.items.tools;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.tag.TagKey;
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
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SoulCastingItem extends StorageItem implements IManaStoringItem, IScrollListenerItem, ICustomRarityItem {

    public SoulCastingItem(Settings settings, int storageSize) {
        super(settings, storageSize,ModItemTags.SPELL_STORING,false);
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
                // open spell storing interface
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
        ItemStack spellContainer = getStack(key,index);

        if(!(spellContainer.getItem() instanceof SpellStoringItem storer)) return;

        storer.cast(key,spellContainer,user, SpellBlockArgs.empty());
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

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

    public ArrayList<ItemStack> getCastableSpellItems(ItemStack stack){
        if(!(stack.getItem() instanceof  SoulCastingItem)) return null;

        ArrayList<ItemStack> res = new ArrayList<>();
        for (int i = 0; i < getStorageSize(stack); i++) {
            var spell = getStack(stack,i);
            if(!(spell.getItem() instanceof SpellStoringItem storer)) continue;
            var grid = SpellStoringItem.readGrid(spell);
            if(grid==null||grid.library) continue;
            res.add(spell);
        }
        return res;
    }

    @Override
    public float getBaseSoulCapacity(ItemStack stack) {
        return 0;
    }

    public int getSelectedSpellIndex(ItemStack stack){
        if(!stack.getOrCreateNbt().contains("selected", NbtElement.INT_TYPE)) return 0;
        int res = stack.getNbt().getInt("selected");
        int installed = getInstalledSpellsCount(stack);
        if(installed<=0)return 0;
        res = ((res%installed)+installed)%installed;
        return res;
    }

    public void setSelectedSpellIndex(ItemStack stack,int index){
        int installed = getInstalledSpellsCount(stack);
        if(installed<=0)index=0;
        else index = ((index%installed)+installed)%installed;
        stack.getOrCreateNbt().putInt("selected",index);
    }

    public int getInstalledSpellsCount(ItemStack stack){
        return getCastableSpellItems(stack).size();
    }

    public static SpellGrid getSpell(ItemStack casterItem,String name){
        if(!(casterItem.getItem() instanceof  SoulCastingItem caster)) return null;

        for (int i = 0; i < caster.getStorageSize(casterItem); i++) {
            var contender = caster.getStack(casterItem,i);
            if(!(contender.getItem() instanceof SpellStoringItem storer)) continue;
            var grid = SpellStoringItem.readGrid(contender);
            if(grid==null) continue;
            if(Objects.equals(grid.name, name)) return grid;
        }

        return null;
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
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if(selected && entity instanceof PlayerEntity player && player.isSneaking())
            displaySelectedSpell(stack,player,getSelectedSpellIndex(stack));
    }

    @Override
    public boolean shouldBlockScrolling(ItemStack stack, PlayerEntity player) {
        return player.isSneaking();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("container.geomancy.spellstorer_block");
    }

    @Override
    public Rarity getRarity() {
        return Rarity.None;
    }


}
