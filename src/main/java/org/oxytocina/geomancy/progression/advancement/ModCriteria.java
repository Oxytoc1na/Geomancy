package org.oxytocina.geomancy.progression.advancement;

import net.minecraft.advancement.criterion.Criteria;

public class ModCriteria {

    public static SimpleCriterion SIMPLE = Criteria.register(new SimpleCriterion());
    public static ModAdvancementCriterion ADVANCEMENT = Criteria.register(new ModAdvancementCriterion());

    public static void register(){

    }
}
