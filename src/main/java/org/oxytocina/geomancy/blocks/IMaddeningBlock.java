package org.oxytocina.geomancy.blocks;

public interface IMaddeningBlock {
    float getAmbientMaddeningSpeed();
    default float getSteppedOnMaddeningSpeed(){return getAmbientMaddeningSpeed()*5;}
    default float getInventoryMaddeningSpeed(){return getSteppedOnMaddeningSpeed();}
}
