package org.oxytocina.geomancy.progression.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
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
        String name = obj.get("name").getAsString();
        Conditions conditions = new Conditions(name,playerPredicate);
        return conditions;
    }

    @Override
    public Identifier getId() {
        return getID();
    }

    public static Identifier getID(){return new Identifier(Geomancy.MOD_ID,"simple");}

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

        public Conditions(String name,LootContextPredicate playerPredicate) {
            super(getID(),playerPredicate);
            this.advancement =name;
        }

        public Conditions(String name) {
            super(getID(),LootContextPredicate.EMPTY);
            this.advancement =name;
        }

        boolean requirementsMet(String name) {
            return Objects.equals(name, this.advancement);
        }

        @Override
        public Identifier getId() {
            return getID();
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", advancement);
            return jsonObject;
        }
    }

    public void trigger(ServerPlayerEntity player,String name) {
        trigger(player, conditions -> conditions.requirementsMet(name));
    }


}