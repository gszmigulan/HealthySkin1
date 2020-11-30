package com.healthy.skincare.data;

import com.healthy.skincare.SafeProduct;
import com.healthy.skincare.web.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class Common {
    public List<Ingredient> toIngredientList(List<String> user, List<Ingredient> allIngr ){
        List<Ingredient> result = new ArrayList<>();
        try {
            for (Ingredient ingr : allIngr) {
                for (String userIngr : user) {
                    if (ingr.getRowid() == Integer.parseInt(userIngr)) {
                        result.add(ingr);
                    }
                }
            }
        }catch (NullPointerException e){
            System.out.println(e);
        }
        return  result;

    }

    public boolean addIngrWU(String next , List<Ingredient> list){
        if(next != null){
            if(list != null){
                for(Ingredient a: list){
                    if(a.getRowid() == Integer.parseInt(next)){
                        return false;
                    }
                }
                return  true;
            }else {
                return  true;
            }
        }
        return false;
    }


    public List<String> checkIfOnBothLists(SafeProduct safeProduct , String message){
        List<String> onBothLists = new ArrayList<>();
        for(Ingredient unwanted : safeProduct.getUnwanted()){
            for(Ingredient wanted: safeProduct.getWanted()){
                if(wanted.getRowid() == unwanted.getRowid()){
                    onBothLists.add("ERROR : "+ wanted.name + " " + message);
                }
            }
        }
        return  onBothLists;
    }

    public List<String> checkIfIsOnAnother( List<Ingredient> listOfIngredients, String next, String message){
        List<String> result = new ArrayList<>();
        for(Ingredient ingr: listOfIngredients){
            if(ingr.getRowid() == Long.parseLong(next)){
                result.add(message);
            }
        }
        return  result;
    }
}


