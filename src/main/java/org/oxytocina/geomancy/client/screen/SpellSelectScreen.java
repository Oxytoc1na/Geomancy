package org.oxytocina.geomancy.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.GeomancyClient;
import org.oxytocina.geomancy.inventories.ImplementedInventory;
import org.oxytocina.geomancy.items.ISpellSelectorItem;
import org.oxytocina.geomancy.items.SpellStoringItem;
import org.oxytocina.geomancy.items.tools.SoulCastingItem;
import org.oxytocina.geomancy.networking.packet.C2S.CasterChangeSelectedSpellC2S;
import org.oxytocina.geomancy.networking.packet.C2S.OpenStorageItemScreenC2SPacket;
import org.oxytocina.geomancy.spells.SpellGrid;
import org.oxytocina.geomancy.util.DrawHelper;
import org.oxytocina.geomancy.util.SimplexNoise;
import org.oxytocina.geomancy.util.Toolbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpellSelectScreen extends Screen {

    public final static int bgWidth=200;
    public final static int bgHeight=200;

    public final PlayerInventory playerInventory;

    public int backgroundWidth;
    public int backgroundHeight;
    public int x;
    public int y;

    public final ItemStack parent;
    public final ItemStack initialParent;
    public final int parentSlot;
    public final PlayerEntity player;
    public final ISpellSelectorItem selector;

    public List<SlotInfo> slots;
    public List<SlotInfo> spellSlots;

    public SlotInfo focusedSlot;

    public SpellSelectScreen(PlayerEntity player, ItemStack stack, int slot, Text title) {
        super(title);
        this.playerInventory=player.getInventory();
        this.player = player;
        playerInventory.onOpen(this.player);

        this.parent = stack;
        this.selector = (ISpellSelectorItem) stack.getItem();
        this.initialParent=parent.copy();
        this.parentSlot = slot;

        ItemStack storageStack = new ItemStack(Items.CHEST);

        slots = new ArrayList<>();
        spellSlots = new ArrayList<>();
        rebuildSlots();
    }


    public static final int CENTER_POS_X = SpellSelectScreen.bgWidth/2;
    public static final int CENTER_POS_Y = SpellSelectScreen.bgHeight/2;
    public static final int[] DISTANCES_FROM_CENTER = new int[]{25,50,75,100};

    void rebuildSlots(){
        slots.clear();
        spellSlots.clear();
        var installedSpells = selector.getCastableSpellItems(parent);
        for (int i = 0; i < installedSpells.size(); i++) {

        }

        int[] ringCounts = new int[]{
                Math.min(2,installedSpells.size()),
                Math.min(8,installedSpells.size()-2),
                Math.min(18,installedSpells.size()-2-8),
                installedSpells.size()-2-8-18
        };
        int j = 0;
        for(int ring = 0; ring < 4; ring++)
        {
            for (int i = 0; i <ringCounts[ring] ; ++i)
            {
                var container = installedSpells.get(j);
                var grid = SpellStoringItem.readGrid(container);
                var display = grid.getDisplayStack(container);
                boolean selected = j==selector.getSelectedSpellIndex(parent);
                if(selected)
                    display.addEnchantment(Enchantments.UNBREAKING,1);
                List<Text> tooltip = new ArrayList<>();
                tooltip.add(grid.getName());

                float angle = (float)(((float)i/ringCounts[ring]+0.25f)*Math.PI*2);
                Vector2f offset = Toolbox.rotateVector(new Vector2f(DISTANCES_FROM_CENTER[ring],0),angle);
                var slot = new SlotInfo(this,
                        CENTER_POS_X + offset.x,
                        CENTER_POS_Y + offset.y,
                        display,grid,tooltip,selected);
                slots.add(slot);
                spellSlots.add(slot);
                j++;
            }
        }

        // storage
        var slot = new SlotInfo(this,
                CENTER_POS_X,
                CENTER_POS_Y,
                new ItemStack(Items.CHEST),null,List.of(Text.translatable("geomancy.spellstorage.open_storage")),false);
        slots.add(slot);
    }

    @Override
    protected void init() {
        this.backgroundWidth = bgWidth;
        this.backgroundHeight = bgHeight;

        super.init();

        this.x = this.width/2-this.backgroundWidth/2;
        this.y = this.height/2-this.backgroundHeight/2;

        clearChildren();
    }

    @Override
    public void tick() {
        super.tick();

        // check if somehow, the caster item got removed or changed
        // in that case, panic and close the screen
        if(parent.getItem() != initialParent.getItem()
                || parent.getCount() != initialParent.getCount())
            close();

        for(var s : slots)
            s.tick();
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        recalculateFocusedSlot(mouseX,mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(focusedSlot!=null){
            onSlotClicked(focusedSlot);
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void recalculateFocusedSlot(double mouseX, double mouseY){
        focusedSlot=null;
        float minDist = 100000;
        for (int i = 0; i < slots.size(); i++) {
            var slot = slots.get(i);
            var dist = Vector2f.distance((float)mouseX,(float)mouseY,slot.getDrawX(),slot.getDrawY());
            if(dist<minDist){
                focusedSlot=slot;
                minDist=dist;
            }
        }
        if(minDist>150) focusedSlot=null;
    }

    public void onSlotClicked(SlotInfo slot) {
        var grid = slot.grid;
        if(grid!=null){
            // select spell and close
            int index = ((ISpellSelectorItem) parent.getItem()).getSpellIndexOfSpell(parent,grid.name);
            CasterChangeSelectedSpellC2S.send(parent,parentSlot,index);
            close();
        }
        else{
            // open storage screen
            OpenStorageItemScreenC2SPacket.send(parentSlot);
            //close();
        }
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
                2,8,18,spellSlots.size()-2-8-18
        };
        int[] ringCounts = new int[]{
                Math.min(2,spellSlots.size()),
                Math.min(8,spellSlots.size()-2),
                Math.min(18,spellSlots.size()-2-8),
                spellSlots.size()-2-8-18
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
                    Vector2f offset1 = Toolbox.rotateVector(new Vector2f(DISTANCES_FROM_CENTER[ring],0),angle);
                    angle = (float)(((float)j/segments+0.25f)*Math.PI*2);
                    Vector2f offset2 = Toolbox.rotateVector(new Vector2f(DISTANCES_FROM_CENTER[ring],0),angle);

                    offset1.add(getNoiseOffset(offset1).mul(4+ring*2));
                    offset2.add(getNoiseOffset(offset2).mul(4+ring*2));

                    DrawHelper.drawLine(context,
                            x+CENTER_POS_X + offset1.x,
                            y+CENTER_POS_Y + offset1.y,
                            x+CENTER_POS_X + offset2.x,
                            y+CENTER_POS_Y + offset2.y,
                            1,Toolbox.colorFromRGBA(0.6f,0.8f,0.8f,1));

                }
            }
        }

        // render slots
        {
            for(int i = 0; i < slots.size();i++){
                var slot = slots.get(i);

                // bg

                // item
                drawSlot(context,slot);
            }
        }

        // draw tooltip
        if (this.focusedSlot != null) {
            List<Text> tooltip = new ArrayList<>();
            var grid = this.focusedSlot.grid;
            if(grid!=null)
                tooltip.add(grid.getName());
            else
                tooltip.add(Text.translatable("geomancy.spellstorage.open_storage"));

            context.drawTooltip(this.textRenderer, tooltip, Optional.empty(), (int)focusedSlot.getDrawX(), (int)focusedSlot.getDrawY());
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


    public static class SlotInfo{
        public SpellSelectScreen screen;

        public float x;
        public float y;
        public ItemStack displayStack;
        public SpellGrid grid;
        public List<Text> tooltip;
        public boolean selected;

        public float scale = 1;

        public SlotInfo(SpellSelectScreen screen,float x, float y, ItemStack stack,
                        SpellGrid grid, List<Text> tooltip, boolean selected){
            this.screen=screen;
            this.x=x;
            this.y=y;
            this.displayStack=stack;
            this.grid=grid;
            this.tooltip=tooltip;
            this.selected=selected;
        }

        public float getDrawX() {
            return x + screen.getDrawX();
        }

        public float getDrawY() {
            return y + screen.getDrawY();
        }

        public void tick(){
            float desiredScale = focused()?2:1;
            scale = MathHelper.lerp(0.3f,scale,desiredScale);
        }

        public boolean focused(){
            return screen.focusedSlot==this;
        }
    }

    private float getDrawY() {
        return y;
    }

    private float getDrawX() {
        return x;
    }

    public final void drawSlot(DrawContext context, SlotInfo slot) {
        ItemStack itemStack = slot.displayStack;
        String countOverride = null;

        float x = slot.getDrawX();
        float y = slot.getDrawY();

        var posMatrix = context.getMatrices().peek();
        Vector3f trans = new Vector3f();
        posMatrix.getPositionMatrix().getTranslation(trans);

        context.getMatrices().push();
        context.getMatrices().translate(0,0, 100.0F);
        context.getMatrices().scale(slot.scale,slot.scale,1);
        x /= slot.scale;
        y /= slot.scale;

        Vector3f col = Toolbox.colorIntToVec(Toolbox.colorFromHSV(0,0,slot.selected?0.5f:1f));

        DrawHelper.drawItem(context,null,MinecraftClient.getInstance().world,itemStack, x-8,y-8, (int)x+(int)y * this.backgroundWidth,0,col.x,col.y,col.z);
        RenderSystem.setShaderColor(1,1,1,1);
        //context.drawItemInSlot(this.textRenderer, itemStack,(int)x-8,(int)y-8 , countOverride);

        context.getMatrices().pop();
    }
}
