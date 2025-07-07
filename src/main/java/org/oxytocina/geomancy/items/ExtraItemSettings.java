package org.oxytocina.geomancy.items;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.oxytocina.geomancy.items.jewelry.JewelryItem;

import java.util.ArrayList;

public class ExtraItemSettings {

    public static final ArrayList<Item> GeneratedModel = new ArrayList<Item>();
    public static final ArrayList<Item> HandheldModel = new ArrayList<Item>();
    public static final ArrayList<JewelryItem> JewelryModel = new ArrayList<JewelryItem>();
    public static final ArrayList<Item> ItemsInGroup = new ArrayList<Item>();

    public Item item;

    private ModelType model = ModelType.Generated;

    private boolean shouldAddItemToGroup = true;



    public ExtraItemSettings(){

    }

    public static ExtraItemSettings create(){
        return new ExtraItemSettings();
    }

    public ExtraItemSettings setItem(Item item){
        this.item= item;
        return this;
    }


    public ExtraItemSettings dontGroupItem(){shouldAddItemToGroup=false; return this;}
    public ExtraItemSettings modelType(ModelType type){ model=type;return this;}

    public void apply(){
        switch(model){
            case Generated:GeneratedModel.add(item);break;
            case Handheld:HandheldModel.add(item);break;
            case Jewelry:JewelryModel.add((JewelryItem) item);break;
            default: break;
        }

        if(shouldAddItemToGroup)
            ItemsInGroup.add(item);

    }

    public enum ModelType{
        Generated,
        Handheld,
        Jewelry,
        Custom
    }
}
