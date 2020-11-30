package com.healthy.skincare;

import com.healthy.skincare.data.designIngredient;
import com.healthy.skincare.web.Ingredient;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class IngredientsNew {
    private List<String> names = new ArrayList<>();
    private List<designIngredient> ingr = new ArrayList<>();
    private String chose;
    private int id;
    private Long existing;
    private String brand;
    private String name;
    private String inciList;
    private List<String> warnings;
}
