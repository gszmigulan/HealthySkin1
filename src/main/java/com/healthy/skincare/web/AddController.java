package com.healthy.skincare.web;

import com.healthy.skincare.Comed_irr_safe;
import com.healthy.skincare.IngredientsNew;
import com.healthy.skincare.Product;
import com.healthy.skincare.SafeProduct;
import com.healthy.skincare.data.IngredientRepository;
import com.healthy.skincare.data.ProductRepository;
import com.healthy.skincare.data.designIngredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
//import com.healthy.skincare.web.Ingredient.Type;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@Controller
//@RequestMapping("/add")
@SessionAttributes("ingredientsNew")//ProductData")

public class AddController {
    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private String alert;

    @ModelAttribute(name = "ingredientsNew")
    public IngredientsNew ingredientsNew() {
        return new IngredientsNew();
    }
    @ModelAttribute(name = "productData")
    public ProductData productData() { return new ProductData(); }

    @Autowired
    public AddController(IngredientRepository ingredientRepository, ProductRepository productRepository){
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
    }


    @GetMapping(value = "/add")
    public String addingForm( @ModelAttribute ProductData productData, @Valid IngredientsNew ingredientsNew, Model model){
        List<designIngredient> ingr = new ArrayList<>();

        for(String name : ingredientsNew.getNames()){
            designIngredient d = new designIngredient();
            d.setName(name);
            ingr.add(d);
        }
        ingredientsNew.setIngr(ingr);
        model.addAttribute("unknownList", ingr);
        System.out.println(productData);
        System.out.println(ingredientsNew);
        productData.setBrand(ingredientsNew.getBrand());
        productData.setName(ingredientsNew.getName());
        productData.setIngredients(ingredientsNew.getInciList());
        System.out.println("to wyświetla: " + model.getAttribute("unknownList"));
        String message  = alert;
        alert = "";
        model.addAttribute("alert", message);

        return  "addingForm";
    }

    @PostMapping(value="/add", params = "addIngr")
    public String processIngredient(@Valid IngredientsNew ingredientsNew,@Valid String name,/* Errors errors,*/ HttpServletRequest httpServletRequest){
        name = httpServletRequest.getParameter("addIngr");
        ingredientsNew.setChose(name);
        return "redirect:/add/ingredient";
    }


    @PostMapping(value = "/add", params = "add")
    public  String processAdding(/*@ModelAttribute*/ @Valid ProductData productData, Errors errors,/*, BindingResult result, */@ModelAttribute IngredientsNew ingredientsNew, Model model){
        System.out.println("zaczynam dodawanie");
        //System.out.println("RESULT : " + result);
        System.out.println("POST PRODUCT DATA: " + productData);

        if(errors.hasErrors()){
            alert= "wszystkie pola muszą być wypełnione";
            System.out.println("errors");
            return "redirect:/add";
        }
        Product prod = productRepository.findByBrandAndName(productData.getBrand(), productData.getName());
        System.out.println(prod.getName());
        if(!prod.getName().equals("-1")){
            alert = "ten produkt jest już w bazie";
            // błąd, że taki kosmetyk już jest w bazie
            return "addingForm";
        }

        // tu dodaje funkcję przetwarzające dane i wstawiające do bazy danych
        //Product product = addNewProduct(productData);
        String inci = productData.getIngredients();

        // wstawiam tu to co było w ingredientList
        List<String> nieznane = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        inci = inci./*replaceAll(".$", "").*/replaceAll("\\*" , "");
        String[] ingr = inci.split(", ");

        //pętla sprawdzająca wszystkie składniki i dodaje albo do listy składników, albo do listy nieznanych
        for( int i =0; i < ingr.length; i++ ){
            String skladnik = ingr[i].trim();
            String after = skladnik.replaceAll("\\(([^)]+)\\)", "")
                    .replaceAll("  ", " ").trim().toLowerCase();
            try{
                Ingredient ingredient = ingredientRepository.findByName(after);
                try { // ten chatch cyba jest niepotrzebny
                    System.out.println("znaleziono składnik: " + after);
                    ingredients.add(ingredient);
                }catch (NullPointerException ex){
                    System.out.print("bład");
                }
            }
            catch (EmptyResultDataAccessException e){
                System.out.print("brak składnika: "+ after + "\n");
                nieznane.add(after);

            }

        }
        productData.setUnknown(nieznane);
        model.addAttribute("productData",productData );

        ingredientsNew.setNames(nieznane);
        ingredientsNew.setName(productData.getName());
        ingredientsNew.setBrand(productData.getBrand());
        ingredientsNew.setInciList(productData.getIngredients());

        if(nieznane.isEmpty()){
            Product product = addNewProduct(productData, ingredients);
            productRepository.save(product);
            log.info("dodano produkt: " + productData.getBrand() + " ");
            return "redirect:/";
        }

        else {
            //System.out.println("tu je znam: " + ingredientsNew.getNames());//System.out.println("powracam go gettera");
            return "redirect:/add";/*/ingredients";*/ // tu chyba adding form z dodanymi polami do dodawania składników
        }

    }

    public Product addNewProduct(ProductData productData, List<Ingredient> ingredientList){
        String brand = productData.getBrand().toLowerCase();
        String nazwa = productData.getName().toLowerCase();
        Comed_irr_safe cis = new Comed_irr_safe();

        for(Ingredient ingr: ingredientList){
            if(ingr.comed_max > cis.comed_max){cis.comed_max = ingr.comed_max;}
            if(ingr.comed_min > cis.comed_min) {cis.comed_min = ingr.comed_min;}
            if(ingr.irr_max > cis.irr_max){cis.irr_max = ingr.irr_max; }
            if(ingr.irr_min > cis.irr_min){cis.irr_min = ingr.irr_min; }
            if(ingr.safety_max > cis.safe_max){cis.safe_max = ingr.safety_max; }
            if(ingr.safety_min > cis.safe_min){cis.safe_min = ingr.safety_min; }
        }
        Product product = new Product(brand, nazwa, cis.comed_max, cis.comed_min,
                cis.irr_max, cis.irr_min, cis.safe_max, cis.safe_min);
        product.setIngredients(ingredientList);
        return product;
    }

}
