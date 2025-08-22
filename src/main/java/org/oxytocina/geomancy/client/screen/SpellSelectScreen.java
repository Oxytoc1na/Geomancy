package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Vector2f;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.client.screen.slots.SpellSelectSlot;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.util.DrawHelper;
import org.oxytocina.geomancy.util.SimplexNoise;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpellSelectScreen extends HandledScreen<SpellSelectScreenHandler> {

    private final SpellSelectScreenHandler handler;

    public final static int bgWidth=200;
    public final static int bgHeight=200;

    public SpellSelectScreen(SpellSelectScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        handler.screen = this;
        //addDrawableChild()
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        handler.tick();
    }

    @Override
    protected void init() {
        this.backgroundWidth = bgWidth;
        this.backgroundHeight = bgHeight;

        //titleX = 0;
        //titleY = 0;
        //playerInventoryTitleX = 0;
        this.playerInventoryTitleY = -1000;//backgroundHeight - 94;

        super.init();

        clearChildren();
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }

    public static final Identifier SLOT_BG = Geomancy.locate("textures/gui/slot.png");

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);//,mouseX,mouseY,delta);
        super.render(context, mouseX, mouseY, delta);

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.setShaderTexture(0, SLOT_BG);

        // render slot connectivity
        int[] ringCountsFull = new int[]{
                2,8,18,handler.getInventory().size()-2-8-18
        };
        int[] ringCounts = new int[]{
                Math.min(2,handler.getInventory().size()),
                Math.min(8,handler.getInventory().size()-2),
                Math.min(18,handler.getInventory().size()-2-8),
                handler.getInventory().size()-2-8-18
        };
        {
            for(int ring = 0; ring < 4; ring++)
            {
                // dont draw empty rings
                if(ringCounts[ring]<=0) break;

                int segments = Math.max(32,ringCountsFull[ring]*8);
                for (int i = 0; i < segments; i++) {
                    int j = (i+1)%segments;
                    float angle = (float)(((float)i/segments+0.25f)*Math.PI*2);
                    Vector2f offset1 = Toolbox.rotateVector(new Vector2f(SpellSelectScreenHandler.DISTANCES_FROM_CENTER[ring],0),angle);
                    angle = (float)(((float)j/segments+0.25f)*Math.PI*2);
                    Vector2f offset2 = Toolbox.rotateVector(new Vector2f(SpellSelectScreenHandler.DISTANCES_FROM_CENTER[ring],0),angle);

                    offset1.add(getNoiseOffset(offset1).mul(4+ring*2));
                    offset2.add(getNoiseOffset(offset2).mul(4+ring*2));

                    DrawHelper.drawLine(context,
                            x+SpellSelectScreenHandler.CENTER_POS_X + offset1.x,
                            y+SpellSelectScreenHandler.CENTER_POS_Y + offset1.y,
                            x+SpellSelectScreenHandler.CENTER_POS_X + offset2.x,
                            y+SpellSelectScreenHandler.CENTER_POS_Y + offset2.y,
                            1,Toolbox.colorFromRGBA(0.6f,0.8f,0.8f,1));

                }
            }
        }

        // render slot backgrounds
        {

            for(int ring = 0; ring < 4; ring++)
            {
                for (int i = 0; i <ringCounts[ring] ; ++i)
                {
                    float angle = (float)(((float)i/ringCounts[ring]+0.25f)*Math.PI*2);
                    Vector2f offset = Toolbox.rotateVector(new Vector2f(SpellSelectScreenHandler.DISTANCES_FROM_CENTER[ring],0),angle);
                    context.drawTexture(SLOT_BG,
                            x+SpellSelectScreenHandler.CENTER_POS_X + (int)offset.x-18/2,
                            y+SpellSelectScreenHandler.CENTER_POS_Y + (int)offset.y-18/2,
                            0,0,18,18,18,18
                    );
                }
            }

            // storage access
            context.drawTexture(SLOT_BG,
                    x+SpellSelectScreenHandler.CENTER_POS_X -18/2,
                    y+SpellSelectScreenHandler.CENTER_POS_Y -18/2,
                    0,0,18,18,18,18
            );
        }

        // draw tooltip
        if (this.handler.getCursorStack().isEmpty() && this.focusedSlot != null && this.focusedSlot.hasStack()) {
            List<Text> tooltip = new ArrayList<>();
            var grid = handler.grids.get(this.focusedSlot);
            if(grid!=null)
                tooltip.add(grid.getName());
            else
                tooltip.add(Text.translatable("geomancy.spellstorage.open_storage"));

            context.drawTooltip(this.textRenderer, tooltip, Optional.empty(), mouseX, mouseY);
        }
    }

    private Vector2f getNoiseOffset(Vector2f where){
        final double scale = 0.02;
        double x = where.x*scale;
        double y = where.y*scale;
        double z = GeomancyClient.tick/20f/3;
        return new Vector2f(
                SimplexNoise.noiseNormalized(x,y,z)-0.5f,
                SimplexNoise.noiseNormalized(x+0.3f,y+0.6f,z+0.2f)-0.5f
        );
    }
}
