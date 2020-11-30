package com.healthy.skincare.data;

import com.healthy.skincare.web.Ingredient;

public interface IngredientRepository {
    void /*Ingredient*/ save(Ingredient design);
    Iterable<Ingredient> findAll();
    Ingredient findById(String id);
    Ingredient findByName (String name);
    Boolean isInBase(String name);
    Iterable<Ingredient> getProductIngredients(Long id);
    void save_next_name(String name, Long id);
}
