package org.oxytocina.geomancy.progression.advancement;

import com.google.gson.JsonObject;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.advancement.criterion.AbstractCriterionConditions;
import net.minecraft.predicate.entity.AdvancementEntityPredicateDeserializer;
import net.minecraft.predicate.entity.AdvancementEntityPredicateSerializer;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.oxytocina.geomancy.Geomancy;

import java.util.Objects;

public class SimpleCriterion extends AbstractCriterion<SimpleCriterion.Conditions> {


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

    public static class Conditions extends AbstractCriterionConditions {

        String name;

        public Conditions(String name,LootContextPredicate playerPredicate) {
            super(getID(),playerPredicate);
            this.name=name;
        }

        public Conditions(String name) {
            super(getID(),LootContextPredicate.EMPTY);
            this.name=name;
        }

        boolean requirementsMet(String name) {
            return Objects.equals(name, this.name);
        }

        @Override
        public Identifier getId() {
            return getID();
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", name);
            return jsonObject;
        }
    }

    public void trigger(ServerPlayerEntity player,String name) {
        trigger(player, conditions -> conditions.requirementsMet(name));
    }


}