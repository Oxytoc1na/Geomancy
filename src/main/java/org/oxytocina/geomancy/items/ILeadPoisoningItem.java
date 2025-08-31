package org.oxytocina.geomancy.items;

public interface ILeadPoisoningItem {
    float getInInventoryPoisoningSpeed();
    default float getWornPoisoningSpeed(){return getInInventoryPoisoningSpeed();}
    default float getInHandPoisoningSpeed(){return getInInventoryPoisoningSpeed();}
}
