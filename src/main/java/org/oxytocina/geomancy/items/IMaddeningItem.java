package org.oxytocina.geomancy.items;

public interface IMaddeningItem {
    float getInInventoryMaddeningSpeed();
    default float getWornMaddeningSpeed(){return getInInventoryMaddeningSpeed();}
    default float getInHandMaddeningSpeed(){return getInInventoryMaddeningSpeed();}
}
