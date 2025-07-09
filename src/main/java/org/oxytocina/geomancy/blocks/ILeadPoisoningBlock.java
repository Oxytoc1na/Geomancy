package org.oxytocina.geomancy.blocks;

public interface ILeadPoisoningBlock {
    float getAmbientPoisoningSpeed();
    default float getSteppedOnPoisoningSpeed(){return getAmbientPoisoningSpeed()*5;}
    default float getInventoryPoisoningSpeed(){return getSteppedOnPoisoningSpeed();}
}
