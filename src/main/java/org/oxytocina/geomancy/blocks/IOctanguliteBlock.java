package org.oxytocina.geomancy.blocks;

public interface IOctanguliteBlock {
    float getAmbientMaddeningSpeed();
    default float getSteppedOnMaddeningSpeed(){return getAmbientMaddeningSpeed()*5;}
    default float getInventoryMaddeningSpeed(){return getSteppedOnMaddeningSpeed();}
}
