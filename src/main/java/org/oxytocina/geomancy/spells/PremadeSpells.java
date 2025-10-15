package org.oxytocina.geomancy.spells;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.oxytocina.geomancy.items.ModItems;
import org.oxytocina.geomancy.items.SpellStoringItem;

import java.util.HashMap;
import java.util.Map;

public class PremadeSpells {

    public static final Map<String, Triple<SpellGrid,ItemStack,Integer>> SPELLS;

    public static final Triple<SpellGrid,ItemStack,Integer> HELLO_WORLD;
    public static final Triple<SpellGrid,ItemStack,Integer> SELF_LIGHTNING;
    public static final Triple<SpellGrid,ItemStack,Integer> FLAWED_TELEPORT;
    public static final Triple<SpellGrid,ItemStack,Integer> FIREBALL;
    public static final Triple<SpellGrid,ItemStack,Integer> CURSE;

    static{
        SPELLS = new HashMap<>();
        // hello world
        {
            HELLO_WORLD = addSpell("hello_world",SpellGrid.builder("hello world")
                    .dim(ModItems.SPELLSTORAGE_SMALL)
                    .add(SpellComponent.builder(SpellBlocks.CONST_TEXT)
                            .pos(0,1)
                            .param("val","hello world")
                            .conf(SpellComponent.confBuilder("e").mode(SpellComponent.SideConfig.Mode.Output))
                    )
                    .add(SpellComponent.builder(SpellBlocks.PRINT)
                            .pos(1,1)
                            .conf(SpellComponent.confBuilder("w").mode(SpellComponent.SideConfig.Mode.Input))
                    )
                    .buildStack(),1);
        }

        // into block teleport
        {
            FLAWED_TELEPORT = addSpell("flawed_teleport",SpellGrid.builder("teleport")
                    .dim(ModItems.SPELLSTORAGE_SMALL)
                    .add(SpellComponent.builder(SpellBlocks.ENTITY_CASTER)
                            .pos(0,1)
                            .conf(SpellComponent.confBuilder("ne").mode(SpellComponent.SideConfig.Mode.Output))
                    )
                    .add(SpellComponent.builder(SpellBlocks.TELEPORT)
                            .pos(1,0)
                            .conf(SpellComponent.confBuilder("sw","entity").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("se","position").mode(SpellComponent.SideConfig.Mode.Input))
                    )
                    .add(SpellComponent.builder(SpellBlocks.RAYCAST_POS)
                            .pos(1,1)
                            .conf(SpellComponent.confBuilder("nw").mode(SpellComponent.SideConfig.Mode.Output))
                            .conf(SpellComponent.confBuilder("sw","length").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("se","dir").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("e","from").mode(SpellComponent.SideConfig.Mode.Input))
                    )
                    .add(SpellComponent.builder(SpellBlocks.CONST_NUM)
                            .pos(1,2)
                            .conf(SpellComponent.confBuilder("ne").mode(SpellComponent.SideConfig.Mode.Output))
                            .param("val", SpellSignal.createNumber(100))
                    )
                    .add(SpellComponent.builder(SpellBlocks.DIR_CASTER)
                            .pos(2,2)
                            .conf(SpellComponent.confBuilder("nw").mode(SpellComponent.SideConfig.Mode.Output))
                    )
                    .add(SpellComponent.builder(SpellBlocks.EYEPOS_CASTER)
                            .pos(2,1)
                            .conf(SpellComponent.confBuilder("w").mode(SpellComponent.SideConfig.Mode.Output))
                    )
                    .buildStack(), 1);
        }

        // self lightning
        {
            SELF_LIGHTNING = addSpell("self_lightning",SpellGrid.builder("lightning")
                    .dim(ModItems.SPELLSTORAGE_SMALL)
                    .add(SpellComponent.builder(SpellBlocks.LIGHTNING)
                            .pos(1,1)
                            .conf(SpellComponent.confBuilder("w").mode(SpellComponent.SideConfig.Mode.Input))
                    )
                    .add(SpellComponent.builder(SpellBlocks.POS_CASTER)
                            .pos(0,1)
                            .conf(SpellComponent.confBuilder("e").mode(SpellComponent.SideConfig.Mode.Output))
                    )
                    .buildStack(),1);
        }

        // fireball
        {
            FIREBALL = addSpell("fireball",SpellGrid.builder("fireball")
                    .dim(ModItems.SPELLSTORAGE_MEDIUM)
                    .add(SpellComponent.builder(SpellBlocks.FIREBALL).pos(2,2)
                            .conf(SpellComponent.confBuilder("ne","position").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("e","direction").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("se","speed").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("sw","power").mode(SpellComponent.SideConfig.Mode.Input))
                    )
                    .add(SpellComponent.builder(SpellBlocks.SUM).pos(2,1)
                            .conf(SpellComponent.confBuilder("sw").mode(SpellComponent.SideConfig.Mode.Output))
                            .conf(SpellComponent.confBuilder("e","a").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("ne","b").mode(SpellComponent.SideConfig.Mode.Input))
                    )
                    .add(SpellComponent.builder(SpellBlocks.EYEPOS_CASTER).pos(3,1).conf(SpellComponent.confBuilder("w").mode(SpellComponent.SideConfig.Mode.Output)))
                    .add(SpellComponent.builder(SpellBlocks.DIR_CASTER).pos(3,0).conf(SpellComponent.confBuilder("sw").mode(SpellComponent.SideConfig.Mode.Output)))
                    .add(SpellComponent.builder(SpellBlocks.DIR_CASTER).pos(3,2).conf(SpellComponent.confBuilder("w").mode(SpellComponent.SideConfig.Mode.Output)))
                    .add(SpellComponent.builder(SpellBlocks.CONST_NUM).param("val",1).pos(2,3).conf(SpellComponent.confBuilder("nw").mode(SpellComponent.SideConfig.Mode.Output)))
                    .add(SpellComponent.builder(SpellBlocks.CONST_NUM).param("val",1).pos(1,3).conf(SpellComponent.confBuilder("ne").mode(SpellComponent.SideConfig.Mode.Output)))
                    .buildStack());
        }

        // curse (instant damage)
        {
            CURSE = addSpell("curse",SpellGrid.builder("curse")
                    .dim(ModItems.SPELLSTORAGE_MEDIUM)
                    .add(SpellComponent.builder(SpellBlocks.RAYCAST_ENTITY).pos(1,2)
                            .conf(SpellComponent.confBuilder("e","entity").mode(SpellComponent.SideConfig.Mode.Output))
                            .conf(SpellComponent.confBuilder("se","entity").mode(SpellComponent.SideConfig.Mode.Output))
                            .conf(SpellComponent.confBuilder("sw","length").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("w","dir").mode(SpellComponent.SideConfig.Mode.Input))
                            .conf(SpellComponent.confBuilder("nw","from").mode(SpellComponent.SideConfig.Mode.Input))
                    )
                    .add(SpellComponent.builder(SpellBlocks.CONST_NUM).param("val",20).pos(0,3).conf(SpellComponent.confBuilder("ne").mode(SpellComponent.SideConfig.Mode.Output)))
                    .add(SpellComponent.builder(SpellBlocks.EYEPOS_CASTER).pos(0,1).conf(SpellComponent.confBuilder("se").mode(SpellComponent.SideConfig.Mode.Output)))
                    .add(SpellComponent.builder(SpellBlocks.DIR_CASTER).pos(0,2).conf(SpellComponent.confBuilder("e").mode(SpellComponent.SideConfig.Mode.Output)))
                    .add(SpellComponent.builder(SpellBlocks.IMBUE).param("effect","instant_damage").pos(2,2)
                            .conf(SpellComponent.confBuilder("ne").mode(SpellComponent.SideConfig.Mode.Blocked))
                            .conf(SpellComponent.confBuilder("e","duration"))
                            .conf(SpellComponent.confBuilder("se","amp"))
                            .conf(SpellComponent.confBuilder("sw").mode(SpellComponent.SideConfig.Mode.Blocked))
                            .conf(SpellComponent.confBuilder("nw").mode(SpellComponent.SideConfig.Mode.Blocked))
                    )
                    .add(SpellComponent.builder(SpellBlocks.IMBUE).param("effect","poison").pos(1,3)
                            .conf(SpellComponent.confBuilder("ne").mode(SpellComponent.SideConfig.Mode.Blocked))
                            .conf(SpellComponent.confBuilder("e","amp"))
                            .conf(SpellComponent.confBuilder("sw").mode(SpellComponent.SideConfig.Mode.Blocked))
                            .conf(SpellComponent.confBuilder("w").mode(SpellComponent.SideConfig.Mode.Blocked))
                            .conf(SpellComponent.confBuilder("nw","entity"))
                    )
                    .add(SpellComponent.builder(SpellBlocks.CONST_NUM).param("val",5).pos(2,4).conf(SpellComponent.confBuilder("nw").mode(SpellComponent.SideConfig.Mode.Output)))
                    .add(SpellComponent.builder(SpellBlocks.CONST_NUM).param("val",0).pos(2,3)
                            .conf(SpellComponent.confBuilder("w").mode(SpellComponent.SideConfig.Mode.Output))
                            .conf(SpellComponent.confBuilder("nw").mode(SpellComponent.SideConfig.Mode.Output))
                    )
                    .add(SpellComponent.builder(SpellBlocks.CONST_NUM).param("val",1).pos(3,2).conf(SpellComponent.confBuilder("w").mode(SpellComponent.SideConfig.Mode.Output)))
                    .buildStack());
        }
    }

    public static HashMap<ItemStack,Integer> getPremadeSpells(){
        final HashMap<ItemStack,Integer> spells = new HashMap<>();

        for(var triplet : SPELLS.values()){
            spells.put(triplet.getMiddle(),triplet.getRight());
        }

        return spells;
    }

    private static Triple<SpellGrid,ItemStack,Integer> addSpell(String name, ItemStack stack){
        return addSpell(name,stack,0);
    }


        private static Triple<SpellGrid,ItemStack,Integer> addSpell(String name, ItemStack stack, int weight){
        var res = new ImmutableTriple<>(SpellStoringItem.readGrid(stack),stack,weight);
        SPELLS.put(name,res);
        return res;
    }
}
