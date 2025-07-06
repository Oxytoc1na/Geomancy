package org.oxytocina.geomancy.progression.advancement;

import com.google.gson.JsonArray;
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

public class LocationCriterion extends AbstractCriterion<LocationCriterion.Conditions> {


    @Override
    protected Conditions conditionsFromJson(JsonObject obj,
                                            LootContextPredicate playerPredicate,
                                            AdvancementEntityPredicateDeserializer predicateDeserializer) {
        String location = obj.getAsJsonArray("player")
                .get(0).getAsJsonObject()
                .getAsJsonObject("predicate")
                .getAsJsonObject("location")
                .get("structure").getAsString();
        Conditions conditions = new Conditions(location,playerPredicate);
        return conditions;
    }

    @Override
    public Identifier getId() {
        return getID();
    }

    public static Identifier getID(){return new Identifier(Geomancy.MOD_ID,"location");}

    public static class Conditions extends AbstractCriterionConditions {

        String location;

        public Conditions(String location,LootContextPredicate playerPredicate) {
            super(getID(),playerPredicate);
            this.location =location;
        }

        public Conditions(String location) {
            super(getID(),LootContextPredicate.EMPTY);
            this.location=location;
        }

        boolean requirementsMet(String location) {
            return Objects.equals(location, this.location);
        }

        @Override
        public JsonObject toJson(AdvancementEntityPredicateSerializer predicateSerializer) {
            JsonObject jsonObject = new JsonObject();

            JsonObject loc = new JsonObject();
            loc.addProperty("structure", location);

            JsonObject pred = new JsonObject();
            pred.add("location", loc);

            JsonObject cond = new JsonObject();
            cond.addProperty("condition","minecraft:entity_properties");
            cond.addProperty("entity","this");
            cond.add("predicate",pred);

            JsonArray playerOBJ = new JsonArray();
            playerOBJ.add(cond);
            jsonObject.add("player", playerOBJ);

            return jsonObject;
        }

        @Override
        public Identifier getId() {
            return new Identifier("location");
        }
    }

    public void trigger(ServerPlayerEntity player,String location) {
        trigger(player, conditions -> conditions.requirementsMet(location));
    }


}