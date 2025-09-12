package org.oxytocina.geomancy.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;
import org.oxytocina.geomancy.client.toast.GeomancyToast;
import org.oxytocina.geomancy.client.toast.StellgeKnowledgeToast;
import org.oxytocina.geomancy.entity.PlayerData;
import org.oxytocina.geomancy.networking.ModMessages;
import org.oxytocina.geomancy.spells.SpellBlocks;

import java.util.HashMap;

public class StellgeUtil {

    public static float clientAdvancementKnowledge = 0;

    public static MutableText stellgify(MutableText t){
        return t.setStyle(t.getStyle().withFont(Geomancy.locate("stellgian")));
    }

    @Environment(EnvType.CLIENT)
    public static MutableText stellgify(MutableText t, float knowledgeRequired){
        return stellgify(t,knowledgeRequired,0);
    }

    @Environment(EnvType.CLIENT)
    public static MutableText stellgify(MutableText t, float knowledgeRequired, float knowledgeBonus){
        if(MinecraftClient.getInstance()==null||MinecraftClient.getInstance().player==null) return t;
        return stellgify(t,knowledgeRequired,knowledgeBonus, MinecraftClient.getInstance().player);
    }

    public static MutableText stellgify(MutableText t, float requiredKnowledge, float knowledgeBonus, PlayerEntity pe) {
        float knowledge = getKnowledge(pe);
        return stellgify(t,requiredKnowledge,knowledgeBonus,knowledge);
    }

    public static MutableText stellgify(MutableText t, float requiredKnowledge, float knowledgeBonus, float knowledge){
        float knowledgeFraction = (knowledge+knowledgeBonus)/requiredKnowledge;
        if(knowledgeFraction<=0) return stellgify(t);
        if(knowledgeFraction>=1) return t;

        // per character
        var tString = t.getString();
        final MutableText resText = Text.literal("");
        t.asOrderedText().accept((index, style, codePoint) -> {
            var charText = Text.literal(tString.substring(index,index+1)).setStyle(style);
            if(Toolbox.seededRandom(index*473).nextFloat()>knowledgeFraction)
                charText = stellgify(charText);
            resText.append(charText);
            return true;
        });

        // per word
        //var tString = t.getString();
        //var words = tString.split(" ");
        //final MutableText resText = Text.literal("");
        //for (int i = 0; i < words.length; i++) {
        //    var wordText = Text.literal(words[i]+(i<words.length-1?" ":"")).setStyle(t.getStyle());
        //    if(Toolbox.seededRandom(i*473).nextFloat()>knowledgeFraction)
        //        wordText = stellgify(wordText);
        //    resText.append(wordText);
        //}

        return resText;
    }

    // does NOT sync data to clients
    public static boolean setItemKnowledge(PlayerEntity player, float amount){
        if(player==null) return false;
        PlayerData data = PlayerData.from(player);
        float old = data.stellgianKnowledge;
        if(old==amount) return false;
        data.stellgianKnowledge = amount;
        return true;
    }

    /// client method only used by sync packets
    @Environment(EnvType.CLIENT)
    public static void setAdvancementKnowledge(float amount)
    {
        if(clientAdvancementKnowledge<amount)
        {
            // show toast
            GeomancyToast.show(new StellgeKnowledgeToast());
        }
        clientAdvancementKnowledge=amount;
    }

    public static float getKnowledge(PlayerEntity player){
        return getItemKnowledge(player) + getAdvancementKnowledge(player);
    }

    public static float getItemKnowledge(PlayerEntity player){
        return PlayerData.from(player).stellgianKnowledge;
    }

    public static float getAdvancementKnowledge(PlayerEntity player){
        if(player instanceof ServerPlayerEntity spe){
            return calculateAdvancementKnowledge(spe);
        }

        return clientAdvancementKnowledge;
    }

    private static float calculateAdvancementKnowledge(ServerPlayerEntity spe){
        float res = 0;
        HashMap<String,Float> vals = new HashMap<>();

        // spell components each give 0.2 knowledge per unlock
        for(var id : SpellBlocks.functions.keySet())
            vals.put("geomancy:spellcomponents/get_"+id.getPath(),0.2f);

        // having unlocked any spell component gives 1.8 (2) knowledge
        vals.put("geomancy:octangulite/get_spellcomponent",1.8f);

        // being mad grants 2 knowledge
        vals.put("geomancy:main/simple_maddened",2f);

        // having visited the stellge structures grants 1 knowledge each
        vals.put("geomancy:location/octangula",1f);
        vals.put("geomancy:location/digsite",1f);

        // having learned about souls grants 5 knowledge
        vals.put("geomancy:milestones/milestone_souls",5f);

        // enlightenment gives 3 each
        vals.put("geomancy:spells/simple_enlightenment_1",3f);
        vals.put("geomancy:spells/simple_enlightenment_2",3f);
        vals.put("geomancy:spells/simple_enlightenment_3",3f);
        vals.put("geomancy:spells/simple_enlightenment_4",3f);
        vals.put("geomancy:spells/simple_enlightenment_5",3f);

        for(var s : vals.keySet())
        {
            if(AdvancementHelper.hasAdvancementServer(spe, Identifier.tryParse(s)))
                res+=vals.get(s);
        }
        return res;
    }

    public static void syncKnowledge(PlayerEntity player){
        if(!(player instanceof ServerPlayerEntity spe)) return;

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(getAdvancementKnowledge(player));
        buf.writeFloat(getItemKnowledge(player));
        ServerPlayNetworking.send(spe, ModMessages.STELLGE_KNOWLEDGE_SYNC,buf);
    }


}
