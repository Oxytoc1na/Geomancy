package org.oxytocina.geomancy.client.screen.widgets;

import me.shedaniel.clothconfig2.gui.entries.IntegerSliderEntry;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;

public class FloatSlider extends SliderWidget {
    public float min;
    public float max;
    public Consumer<Float> applyVal;
    public Text baseMessage;

    public static FloatSlider create(int x, int y, int width, int height, Text text, float value, float min, float max,Consumer<Float> applyVal){
        value = (float)getValueUnscaled(value,min,max);
        return new FloatSlider(x,y,width,height,text,value,min,max,applyVal);
    }

    protected FloatSlider(int x, int y, int width, int height, Text text, float value, float min, float max,Consumer<Float> applyVal) {
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

    public float getValueScaled(){
        return MathHelper.lerp((float)value,min,max);
    }

    public static double getValueUnscaled(float val, float min, float max) {
        return val-min/(max-min);
    }
}
