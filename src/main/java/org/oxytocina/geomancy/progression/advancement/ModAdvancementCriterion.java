package org.oxytocina.geomancy.progression.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.entity.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

import java.util.Objects;

public class ModAdvancementCriterion extends AbstractCriterion<ModAdvancementCriterion.Conditions> {


    @Override
    protected Conditions conditionsFromJson(JsonObject obj,
                                            LootContextPredicate playerPredicate,
                                            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        String name = obj.get("advancement").getAsString();
        Conditions conditions = conditionsFromAdvancement(new Identifier(name));
        return conditions;
    }

    public void trigger(ServerPlayerEntity player, Identifier id) {
        this.trigger(player, (conditions) -> conditions.requirementsMet(id));
    }

    @Override
    public Identifier getId() {
        return getID();
    }

    public static Identifier getID(){return new Identifier(Geomancy.MOD_ID,"advancement");}

    public static Conditions conditionsFromAdvancement(Identifier advancement){
        Conditions res =new Conditions(advancement.toString(),LootContextPredicate.create(
                EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS,
                        EntityPredicate.Builder.create()
                                .typeSpecific(PlayerPredicate.Builder.create()
                                        .advancement(advancement,true).build()).build()).build()
        ));
        return res;
    }

    public static class Conditions extends AbstractCriterionConditions {

        String advancement;

        public Identifier getAdvancement()  {return Identifier.tryParse(advancement);}

        public Conditions(String name,LootContextPredicate playerPredicate) {
            super(getID(),playerPredicate);
            this.advancement =name;
        }

        public Conditions(String name) {
            super(getID(),LootContextPredicate.EMPTY);
            this.advancement =name;
        }

        boolean requirementsMet(Identifier id) {
            return Objects.equals(id, getAdvancement());
        }

        @Override
        public Identifier getId() {
            return Geomancy.locate("advancement");
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = super.toJson(predicateSerializer);
            jsonObject.addProperty("advancement", advancement);
            return jsonObject;
        }
    }


}