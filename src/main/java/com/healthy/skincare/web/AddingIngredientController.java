package com.healthy.skincare.web;

import com.healthy.skincare.Comed_irr_safe;
import com.healthy.skincare.IngredientsNew;
import com.healthy.skincare.data.IngredientRepository;
import com.healthy.skincare.data.ProductRepository;
import com.healthy.skincare.data.designIngredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/add")
@SessionAttributes("ingredientsNew")

public class AddingIngredientController {
    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;

    private String name;
    @Autowired
    public AddingIngredientController(IngredientRepository ingredientRepository, ProductRepository productRepository){
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
    }

    @ModelAttribute(name = "newIngr")
    public designIngredient newIngr() {
        return new designIngredient();
    }


    @GetMapping("/ingredient")
    public String IngredientParameters( @Valid IngredientsNew ingredientsNew , Model model){

        List<Ingredient> ingrList = new ArrayList<>();
        ingredientRepository.findAll().forEach(i -> ingrList.add(i));
        model.addAttribute("ingrList", ingrList); // dodaje wszystkie
        model.addAttribute("chose", ingredientsNew );
        System.out.println("BŁĘDY: " + ingredientsNew.getWarnings());
        List<String> warnings = ingredientsNew.getWarnings();
        ingredientsNew.setWarnings(new ArrayList<>());
        model.addAttribute("WarningsList", warnings);

        designIngredient newIngr = new designIngredient();
        newIngr.setName(ingredientsNew.getChose());
        int i = 0 ;
        for(String name : ingredientsNew.getNames()){

            if(name.equals(ingredientsNew.getChose())){
                newIngr.setId(i); // tego chyba nie potrebuje
                ingredientsNew.setId(i);
            }
            i++;
        }
        model.addAttribute("newIngr", newIngr);
        System.out.println("NEW INGR" + newIngr);
        name = newIngr.getName();

        return "addIngredientName";
    }


    @PostMapping(value = "/ingredient", params="connect")
    public  String addNewName(@ModelAttribute designIngredient newIngr, @Valid IngredientsNew ingredientsNew){

        ingredientRepository.save_next_name(newIngr.getName(), ingredientsNew.getExisting());
        List<String> tmp = updateList(ingredientsNew);
        ingredientsNew.setNames(tmp);

        return "redirect:/add";
    }


    @PostMapping(value = "/ingredient" , params="new")
    public String addNewIngredient(HttpServletRequest request, @ModelAttribute designIngredient newIngr, @Valid IngredientsNew ingredientsNew){
        //System.out.println("nazwa: " + newIngr.getName() + " albo: " + ingredientsNew.getName());
        //String ingr_name = request.getParameter("ingr-name");
        //System.out.println("request: " + ingr_name);
        Comed_irr_safe cis = transformData(newIngr);
        if(name == null){
            cis.isOk = false;
        }
        if(!cis.isOk){
            System.out.println("BŁĄD, NIEPOPRAWNE DANE");
            List<String> warnings = new ArrayList<>();
            warnings.add("NIEPOPRAWNE DANE");
            ingredientsNew.setWarnings(warnings);
            return "redirect:/add/ingredient";
        }
        // tu dodaje składnik do bazy
        Ingredient ingredient = new Ingredient(
                name,
                newIngr.getTypes(),
                cis.comed_max,
                cis.comed_min,
                cis.irr_max,
                cis.irr_min,
                cis.safe_max,
                cis.safe_min);
        ingredientRepository.save(ingredient);
        System.out.println("dodano : " + ingredient);

        List<String> tmp = updateList(ingredientsNew);
        ingredientsNew.setNames(tmp);
        return "redirect:/add";
    }

    public List<String> updateList(IngredientsNew ingredientsNew){
        List<String> tmp = new ArrayList<>();
        int i = 0;
        for(String name : ingredientsNew.getNames()){
            if(i != ingredientsNew.getId()){
                tmp.add(name);
            }
            i++;
        }
        return tmp;
    }
    // jeśli błąd to zwracam isOk = false
    public Comed_irr_safe transformData(designIngredient ingr){

        Comed_irr_safe cis = new Comed_irr_safe();
        Comed_irr_safe cis_tmp = new Comed_irr_safe();
        cis_tmp = checkLength(ingr.getComed(), 5); // comed
        if(cis_tmp.isOk){
            cis.comed_min = cis_tmp.comed_min;
            cis.comed_max = cis_tmp.comed_max;
        }else {cis.isOk= false; System.out.println("błąd w comed "); return cis; }
        cis_tmp = checkLength(ingr.getIrr(), 5);
        if(cis_tmp.isOk){
            cis.irr_min = cis_tmp.comed_min;
            cis.irr_max = cis_tmp.comed_max;
        }else { cis.isOk= false;System.out.println("błąd w IRR"); return  cis;}
        cis_tmp = checkLength(ingr.getSafety(), 10);
        if(cis_tmp.isOk){
            cis.safe_min = cis_tmp.comed_min;
            cis.safe_max = cis_tmp.comed_max;
        }else{ cis.isOk= false; System.out.println("błąd w safety");return cis; }


        return cis;
    }

    public Comed_irr_safe checkLength(String data, int max_value){
        Comed_irr_safe cis = new Comed_irr_safe();
        String[] xs = data.split("-");
        int min;
        int max;
        if(data == ""){ return cis;}
        if(xs.length == 1){
            min = checkNumbers(xs[0], max_value);
            if(min == -1){
                cis.isOk=false;
                return  cis;
            }
            cis.comed_min = min;
            cis.comed_max = min;
        }else {
            if (xs.length == 2) {
                min = checkNumbers(xs[0], max_value);
                max = checkNumbers(xs[1], max_value);
                if (min == -1 || max == -1) {
                    cis.isOk = false;
                    return cis;
                } else {
                    cis.comed_min = min;
                    cis.comed_max = max;
                }
            } else {
                cis.isOk = false;
                return cis;
            }
        }
        return  cis;
    }

    public int checkNumbers(String nr, int max){
        try{
            int result = Integer.parseInt(nr);
            if(result >= 0 && result <= max){
                return result;
            }else {
                return -1;
            }

        }catch (NumberFormatException e ){
            return -1;
        }

    }






}
