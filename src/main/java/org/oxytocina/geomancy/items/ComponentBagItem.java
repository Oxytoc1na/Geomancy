package org.oxytocina.geomancy.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.oxytocina.geomancy.loottables.ModLootTables;
import org.oxytocina.geomancy.spells.SpellBlock;
import org.oxytocina.geomancy.spells.SpellBlocks;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.HashMap;
import java.util.Map;

public class ComponentBagItem extends Item {

    protected final Identifier lootTable;
    public ComponentBagItem(Settings settings, Identifier lootTable) {
        super(settings);
        this.lootTable=lootTable;
    }

    public LootTable getLootTable(World world){
        return world instanceof ServerWorld sw ? Toolbox.getLootTable(sw,lootTable) : null;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        var stack = user.getStackInHand(hand);
        if(user instanceof ServerPlayerEntity spe){
            Map<SpellBlock,Integer> lockedLootableComponents = new HashMap<>();
            Map<SpellBlock,Integer> lootableComponents = new HashMap<>();
            for(var block : SpellBlocks.functions.values()){
                if(block.defaultLootWeight>0){
                    lootableComponents.put(block,block.defaultLootWeight);
                    if(!block.recipeUnlocked(spe))
                        lockedLootableComponents.put(block,block.defaultLootWeight);
                }
            }

            LootTable lootTable = getLootTable(world);
            var loot = lootTable.generateLoot(new LootContextParameterSet.Builder(spe.getServerWorld()).build(LootContextType.create().build()));

            SpellBlock componentToDrop = Toolbox.selectWeightedRandomIndex(lockedLootableComponents.isEmpty()?lootableComponents:lockedLootableComponents,null);
            if(componentToDrop!=null) loot.add(componentToDrop.getItemStack().copyWithCount(Toolbox.random.nextInt(1,5)));

            // spawn loot
            for(var lStack : loot){
                // spawn component
                Toolbox.spawnItemStackAsEntity(world,user.getPos(),lStack);
            }

            Toolbox.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS,world,user.getBlockPos(), SoundCategory.PLAYERS,0.5f,Toolbox.randomPitch());
        }
        stack.decrement(1);
        return TypedActionResult.consume(stack);
    }
}
