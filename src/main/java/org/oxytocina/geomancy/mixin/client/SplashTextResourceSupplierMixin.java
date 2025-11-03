package org.oxytocina.geomancy.mixin.client;

import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextResourceSupplierMixin {

    @Inject(method= "get()Lnet/minecraft/client/gui/screen/SplashTextRenderer;",cancellable = true,at=@At(value="HEAD"))
    public void geomancy$get(CallbackInfoReturnable<SplashTextRenderer> cir)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (month == 3 && day == 5) {
            cir.setReturnValue(new SplashTextRenderer("Happy birthday, Tina!")); return;
        }
        if (month == 11 && day == 25) {
            cir.setReturnValue(new SplashTextRenderer("Happy birthday, Maxi!")); return;
        }
    }

    @Inject(method= "apply(Ljava/util/List;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V",at=@At(value="RETURN"))
    public void geomancy$apply(List<String> list, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci)
    {
        //((SplashTextResourceSupplier)(Object)this).splashTexts.addAll();
    }
}
