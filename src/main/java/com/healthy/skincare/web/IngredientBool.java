package com.healthy.skincare.web;

import java.util.List;

public class IngredientBool {
    public Ingredient ingredient;
    public Boolean isOnList ;
    public IngredientBool(Ingredient ingredient){
        this.ingredient = ingredient;
        isOnList = false;
    }
}
