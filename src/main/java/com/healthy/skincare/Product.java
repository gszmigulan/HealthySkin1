package com.healthy.skincare;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.healthy.skincare.web.Ingredient;
import lombok.Data;

@Data
public class Product {
    // daruję sobie narazie kategoryzowanie a początkowo niech będzie filtrowanie po nazwie (opisie) i marce

    private long id;
    private final String brand;
    private final String name;

    private final int comed_max;
    private final int comed_min;
    private final int irr_max;
    private final int irr_min;
    private final int safety_max;
    private final int safety_min;
    //@Size(min=1, message="Produkt musi mieć przynajmniej 1 składnik")
    private  List<Ingredient> ingredients;

}
