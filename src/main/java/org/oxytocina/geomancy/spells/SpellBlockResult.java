package org.oxytocina.geomancy.spells;

import java.util.HashMap;

public class SpellBlockResult {
    public HashMap<String,SpellSignal> vars;
    public int iterations = 1;

    public SpellBlockResult(){
        vars = new HashMap<>();
    }

    public SpellBlockResult(HashMap<String,SpellSignal> vars){
        this.vars=vars;
    }

    public SpellBlockResult(SpellBlockArgs args){
        this.vars=args.vars;
    }
}
