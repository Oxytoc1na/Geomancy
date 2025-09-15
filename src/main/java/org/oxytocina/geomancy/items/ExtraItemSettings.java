package org.oxytocina.geomancy.items;


import dev.emi.emi.api.EmiApi;
import net.minecraft.item.Item;
import org.oxytocina.geomancy.items.jewelry.IJewelryItem;

import java.util.ArrayList;
import java.util.List;

public class ExtraItemSettings {

    public static final ArrayList<Item> GeneratedModel = new ArrayList<Item>();
    public static final ArrayList<Item> HandheldModel = new ArrayList<Item>();
    public static final ArrayList<Item> ParentInventoryModel = new ArrayList<Item>();
    public static final ArrayList<Item> ParentBottomModel = new ArrayList<Item>();
    public static final ArrayList<IJewelryItem> JewelryModel = new ArrayList<IJewelryItem>();

    public static final ArrayList<Item> ITEMS_IN_MAIN_GROUP = new ArrayList<Item>();
    public static final ArrayList<Item> ITEMS_IN_LORE_GROUP = new ArrayList<Item>();
    public static final ArrayList<Item> ITEMS_IN_JEWELRY_GROUP = new ArrayList<Item>();
    public static final ArrayList<Item> ITEMS_IN_SPELLS_GROUP = new ArrayList<Item>();

    public Item item;

    private ModelType model = ModelType.Generated;

    private final List<Group> groups;


    public ExtraItemSettings(){
        groups = new ArrayList<>();
        groups.add(Group.Main);
    }

    public static ExtraItemSettings create(){
        return new ExtraItemSettings();
    }

    public ExtraItemSettings setItem(Item item){
        this.item= item;
        return this;
    }


    public ExtraItemSettings dontGroupItem(){groups.clear(); return this;}
    public ExtraItemSettings group(Group group){groups.clear(); groups.add(group); return this;}
    public ExtraItemSettings addGroup(Group group){groups.add(group); return this;}
    public ExtraItemSettings modelType(ModelType type){ model=type;return this;}

    public void apply(){
        switch(model){
            case Generated:GeneratedModel.add(item);break;
            case Handheld:HandheldModel.add(item);break;
            case Jewelry:JewelryModel.add((IJewelryItem) item);break;
            case BlockPlusInventory:ParentInventoryModel.add(item);break;
            case BlockPlusBottom:ParentBottomModel.add(item);break;
            default: break;
        }

        for(var group : groups){
            switch(group){
                case Main -> ITEMS_IN_MAIN_GROUP.add(item);
                case Lore -> ITEMS_IN_LORE_GROUP.add(item);
                case Jewelry -> ITEMS_IN_JEWELRY_GROUP.add(item);
                case Spells -> ITEMS_IN_SPELLS_GROUP.add(item);
            }
        }

    }

    public enum ModelType{
        Generated,
        Handheld,
        Jewelry,
        BlockPlusInventory,
        BlockPlusBottom,
        Custom
    }

    public enum Group{
        Main,
        Lore,
        Jewelry,
        Spells
    }
}
