package org.oxytocina.geomancy.client.screen.widgets;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class IntSlider extends SliderWidget {
    public int min;
    public int max;
    public Consumer<Integer> applyVal;
    public Text baseMessage;

    public static IntSlider create(int x, int y, int width, int height, Text text, int value, int min, int max, Consumer<Integer> applyVal){
        value = Math.round((float)getValueUnscaled(value,min,max));
        return new IntSlider(x,y,width,height,text,value,min,max,applyVal);
    }

    protected IntSlider(int x, int y, int width, int height, Text text, int value, int min, int max, Consumer<Integer> applyVal) {
        super(x, y, width, height, text,value);
        this.baseMessage=text;
        this.min=min;
        this.max=max;
        this.applyVal=applyVal;
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
        return MathHelper.lerp((float)value,min,max);
    }

    public static double getValueUnscaled(float val, int min, int max) {
        return val-(float)min/(max-min);
    }
}
