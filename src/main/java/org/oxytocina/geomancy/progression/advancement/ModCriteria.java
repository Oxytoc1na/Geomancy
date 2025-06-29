package org.oxytocina.geomancy.progression.advancement;

import net.minecraft.advancement.criterion.Criteria;
import org.oxytocina.geomancy.Geomancy;

public class ModCriteria {

    public static SimpleCriterion SIMPLE = Criteria.register(new SimpleCriterion());
    public static ModAdvancementCriterion ADVANCEMENT = Criteria.register(new ModAdvancementCriterion());

    public static void initialize(){

    }
}
