package org.oxytocina.geomancy.items.jewelry;

import org.oxytocina.geomancy.items.ExtraItemSettings;

public class JewelryItemSettings {
    public int gemSlotCount = 1;
    public TrinketSlot slot = TrinketSlot.ANY;

    public JewelryItemSettings(){
    }

    public static JewelryItemSettings create(){
        return new JewelryItemSettings();
    }

    public static JewelryItemSettings createOf(TrinketSlot slot){
        return create().setSlot(slot);
    }

    public JewelryItemSettings withGemCount(int count){ gemSlotCount=count; return this;}
    public JewelryItemSettings setSlot(TrinketSlot slot){ this.slot=slot; return this;}

    public enum TrinketSlot{
        ANY,
        RING,
        NECKLACE
    }
}
