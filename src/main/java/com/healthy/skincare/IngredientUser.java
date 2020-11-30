package com.healthy.skincare;

import com.healthy.skincare.web.Ingredient;

public class IngredientUser {
    public final Ingredient ingredient;
    public boolean checked;
    public boolean cisHigher;
    public String comed;
    public String irr;
    public String safety;

    public IngredientUser(Ingredient ingredient){
        this.ingredient = ingredient;
        checked = false;
        cisHigher = false;
        if(ingredient.comed_min != ingredient.comed_max){
            comed = ingredient.comed_min + "-" + ingredient.comed_max;
        }else { comed = Long.toString(ingredient.comed_max);}
        if(ingredient.irr_min != ingredient.irr_max){
            irr = ingredient.irr_min + "-" + ingredient.irr_max;
        }else{ irr = Long.toString(ingredient.irr_max); }
        if(ingredient.safety_min != ingredient.safety_max){
            safety = ingredient.safety_min + "-" + ingredient.safety_min;
        }else { safety = Long.toString(ingredient.safety_max);}

    }

    public void CheckSafety(User user){
        if(user.getComed() < ingredient.getComed_max()){
            cisHigher = true;
        }
        if(user.getIrr() < ingredient.getIrr_max()){
            cisHigher = true;
        }
        if(user.getSafety() < ingredient.getSafety_max()){
            cisHigher = true;
        }
    }

}
