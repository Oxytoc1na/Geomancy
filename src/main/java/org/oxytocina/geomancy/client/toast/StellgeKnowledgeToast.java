package org.oxytocina.geomancy.client.toast;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.oxytocina.geomancy.Geomancy;

import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class StellgeKnowledgeToast extends GeomancyToast {


    public StellgeKnowledgeToast() {
        super(new ItemStack(Items.KNOWLEDGE_BOOK), SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS);
    }

    @Override
    public Visibility draw(DrawContext drawContext, ToastManager manager, long startTime) {
        Text title = Text.translatable("geomancy.toast.stellgeknowledge.title");
        Text text = Text.translatable("geomancy.toast.stellgeknowledge.text");

        MinecraftClient client = manager.getClient();
        TextRenderer textRenderer = client.textRenderer;
        drawContext.drawTexture(TEXTURE, 0, 0, 0, 0, this.getWidth(), this.getHeight());

        List<OrderedText> wrappedText = textRenderer.wrapLines(text, 125);
        List<OrderedText> wrappedTitle = textRenderer.wrapLines(title, 125);
        int l;
        long toastTimeMilliseconds = 5000;//Geomancy.CONFIG.ToastTimeMilliseconds;
        if (startTime < toastTimeMilliseconds / 2) {
            l = MathHelper.floor(MathHelper.clamp((float) (toastTimeMilliseconds / 2 - startTime) / 300.0F, 0.0F, 1.0F) * 255.0F) << 24 | 67108864;
            int halfHeight = this.getHeight() / 2;
            int titleSize = wrappedTitle.size();
            int m = halfHeight - titleSize * 9 / 2;

            for (Iterator<OrderedText> it = wrappedTitle.iterator(); it.hasNext(); m += 9) {
                OrderedText orderedText = it.next();
                drawContext.drawText(textRenderer, orderedText, 30, m, 0x00FF00 | l, false);
            }
        } else {
            l = MathHelper.floor(MathHelper.clamp((float) (startTime - toastTimeMilliseconds / 2) / 300.0F, 0.0F, 1.0F) * 252.0F) << 24 | 67108864;
            int halfHeight = this.getHeight() / 2;
            int textSize = wrappedText.size();
            int m = halfHeight - textSize * 9 / 2;

            for (Iterator<OrderedText> var12 = wrappedText.iterator(); var12.hasNext(); m += 9) {
                OrderedText orderedText = var12.next();
                drawContext.drawText(textRenderer, orderedText, 30, m, 0x00FFFFFF | l, false);
            }
        }

        if (!this.soundPlayed && startTime > 0L) {
            this.soundPlayed = true;
            if (this.soundEvent != null) {
                manager.getClient().getSoundManager().play(PositionedSoundInstance.master(this.soundEvent, 1.0F, 0.6F));
            }
        }

        drawContext.drawItem(itemStack, 8, 8);
        return startTime >= toastTimeMilliseconds ? Visibility.HIDE : Visibility.SHOW;
    }

}
