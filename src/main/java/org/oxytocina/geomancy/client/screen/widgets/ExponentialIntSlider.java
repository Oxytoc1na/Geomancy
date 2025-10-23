package org.oxytocina.geomancy.client.screen.widgets;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class ExponentialIntSlider extends SliderWidget {
    public int min;
    public int max;
    public Consumer<Integer> applyVal;
    public Text baseMessage;
    public float exponent = 2;

    public static ExponentialIntSlider create(int x, int y, int width, int height, Text text, int value, int min, int max, float exponent, Consumer<Integer> applyVal){
        //value = Math.round((float)getValueUnscaled(value,min,max));
        return new ExponentialIntSlider(x,y,width,height,text,value,min,max,exponent,applyVal);
    }

    protected ExponentialIntSlider(int x, int y, int width, int height, Text text, int value, int min, int max, float exponent, Consumer<Integer> applyVal) {
        super(x, y, width, height, text,getValueUnscaled(value,min,max,exponent));
        this.baseMessage=text;
        this.min=min;
        this.max=max;
        this.applyVal=applyVal;
        this.exponent=exponent;
        updateMessage();
    }

    public void updateMessage() {
        this.setMessage(Text.literal("").append(baseMessage).append(" : "+
                Math.round(getValueScaled()*10)/10f));
    }

    protected void applyValue() {
        applyVal.accept(getValueScaled());
    }

    public double getProgress() {
        return this.value;
    }

    public void setProgress(float val) {
        this.value = val;
    }

    public void setValue(float val) {
        this.value = val;
    }

    public int getValueScaled(){
        return MathHelper.lerp((float)Math.pow((float)value,exponent),min,max);
    }

    public static double getValueUnscaled(float val, int min, int max, float exponent) {
        return Math.pow((val-(double)min)/(max-min),1/exponent);
    }
}
