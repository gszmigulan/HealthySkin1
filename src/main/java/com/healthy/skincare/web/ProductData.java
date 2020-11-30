package com.healthy.skincare.web;

import java.util.List;
import com.healthy.skincare.web.Ingredient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class ProductData {
    @NotNull
    @Size(min=2, message="marka produktu musi mieć przynajmniej 3 znaki")
    private String brand;
    @NotNull
    @Size(min=3, message="Nazwa produktu musi mieć przynajmniej 4 znaki")
    private String name;
    @NotNull
    @Size(min=3, message="Produkt misi mieć przynajmniej 1 składnik")
    private String ingredients;

    private List<String> unknown;
}
