package com.healthy.skincare;

import com.healthy.skincare.data.UserRepository;
import com.healthy.skincare.web.Ingredient;

import java.util.List;

public class IngredientUser {
    public final Ingredient ingredient;
    public boolean checked;
    public boolean cisHigher;
    public boolean onUnwanted;
    public String cisU; // K - ok , CIS - cis higher, U - on unwanted
    public String comed;
    public String irr;
    public String safety;

    public IngredientUser(Ingredient ingredient){
        this.ingredient = ingredient;
        checked = false;
        cisHigher = false;
        onUnwanted = false;
        cisU = "K";
        if(ingredient.comed_min != ingredient.comed_max){
            comed = ingredient.comed_min + "-" + ingredient.comed_max;
        }else { comed = Long.toString(ingredient.comed_max);}
        if(ingredient.irr_min != ingredient.irr_max){
            irr = ingredient.irr_min + "-" + ingredient.irr_max;
        }else{ irr = Long.toString(ingredient.irr_max); }
        if(ingredient.safety_min != ingredient.safety_max){
            safety = ingredient.safety_min + "-" + ingredient.safety_max;
        }else { safety = Long.toString(ingredient.safety_max);}

        if(comed.equals("-1")){comed= " ";}
        if(irr.equals("-1")){irr= " "; }
        if(safety.equals("-1")){safety = " ";}

    }

    public void CheckSafety(User user, UserRepository userRepository){
        if(user.getComed() < ingredient.getComed_max()){
            cisHigher = true;
            cisU = "CIS";
        }
        if(user.getIrr() < ingredient.getIrr_max()){
            cisHigher = true;
            cisU = "CIS";
        }
        if(user.getSafety() < ingredient.getSafety_max()){
            cisHigher = true;
            cisU = "CIS";
        }
        List<String> unwanted = userRepository.getUnwantedList(user.getId());
        System.out.println("unwanted: " + unwanted);
        if(!unwanted.isEmpty()){
            for(String i : unwanted){
                if(i.equals(Long.toString(ingredient.getRowid() ))){
                    onUnwanted = true;
                    cisU = "U";
                    System.out.println("TO JEST : " + i);
                }
            }
        }
    }
}
