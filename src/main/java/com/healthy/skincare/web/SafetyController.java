package com.healthy.skincare.web;

import com.healthy.skincare.User;
import com.healthy.skincare.data.Common;
import com.healthy.skincare.data.IngredientRepository;
import com.healthy.skincare.data.ProductRepository;
import com.healthy.skincare.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.healthy.skincare.SafeProduct;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@Controller
//@SessionAttributes("safeProduct")
@SessionAttributes("safeProduct")
@RequestMapping("/design")
public class SafetyController {
    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private boolean defined;
    private Common c = new Common();

    @Autowired
    public SafetyController(IngredientRepository ingredientRepository, ProductRepository productRepository, UserRepository userRepository){
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        defined = false;
    }

    @ModelAttribute(name = "safeProduct")
    public SafeProduct safeProduct() {
        System.out.println("zeuje safeProdukt");
        return new SafeProduct();
    }

    @GetMapping()
    public String showDesignForm(Model model, Principal principal,@Valid SafeProduct safeProduct ,@ModelAttribute SafeProduct design){

        List<Ingredient> allIngr = new ArrayList<>();
        ingredientRepository.findAll().forEach(i -> allIngr.add(i));

        User user = userRepository.findByUsername(principal.getName());
        System.out.println("WRACAM DO DESIGN : \n design : " + design + "\n safeprod: " + safeProduct);
        System.out.println("is defined: "+ defined);
        /// jak pierwszy raz wchodzę to ni jest zdefiniowane
        /// potem już korzystam z moich danych
        if(defined == false) {
            //SafeProduct safeProduct = new SafeProduct();
            List<String> userWanted = userRepository.getWantedList(user.getId());
            List<String> userUnwanted = userRepository.getUnwantedList(user.getId());

            List<Ingredient> WantedList = c.toIngredientList(userWanted, allIngr);//new ArrayList<>();
            List<Ingredient> UnwantedList = c.toIngredientList(userUnwanted, allIngr);
            List<String> warnings = new ArrayList<>();

            design.setUnwanted(UnwantedList);
            design.setWanted(WantedList);
            safeProduct.setWanted(WantedList);
            safeProduct.setUnwanted(UnwantedList);
            safeProduct.setWarnings(warnings);
            safeProduct.setComedogenic(user.getComed());
            safeProduct.setIrritation(user.getIrr());
            safeProduct.setSafety(user.getSafety());

            //safeProduct.setIrritation(5);
            defined = true;
        }
        // safe pole defined =  ? jeśli nie jest to  definiuję za pomocą bazy danych jeśli jest to biorę obiekt
        /*SafeProduct*/  //safeProduct = new SafeProduct();
        design.setWanted(safeProduct.getWanted());
        design.setUnwanted(safeProduct.getUnwanted());
        System.out.println(" w safe product mam w getterze : "+ safeProduct.getWanted());
        System.out.println(" w design mam w getterze : "+ design.getWanted());

        model.addAttribute("allIngr", allIngr);
        model.addAttribute("design", safeProduct);

        /*List<Ingredient> unwanted = new ArrayList<>();
        ingredientRepository.findAll().forEach(i -> unwanted.add(i));

        model.addAttribute("wrong", unwanted); // dodaje wszystkie tu
        model.addAttribute("design", new SafeProduct());
        //System.out.println( "jj");
        return "design";*/
        return  "design";
    }
    /*public List<Ingredient> toIngredientList(List<String> user, List<Ingredient> allIngr ){
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

    }*/


    @PostMapping(params = "deleteW")
    public String deleteWanted(HttpServletRequest request, @Valid SafeProduct design, Errors errors, @ModelAttribute SafeProduct safeProduct, Principal principal){

        // TO USÓWA Z LISTY TAK JAK CHCIAŁAM

        System.out.println( "delete . safe: " + safeProduct.getWanted());
        System.out.println(  "delete . design :" + design.getWanted());
        //System.out.println("DELETE : " + request.getParameter("deleteW"));
        Long ingr_id = Long.parseLong(request.getParameter("deleteW"));
        List<Ingredient> tmp = new ArrayList<>();//safeProduct.getWanted();
        for(Ingredient wanted: safeProduct.getWanted()){
            if(wanted.getRowid() != ingr_id){
                tmp.add(wanted);
            }
        }
        safeProduct.setWanted(tmp);
        //System.out.println(tmp);
        System.out.println("usunięto :" + ingr_id);

        return "redirect:/design";
    }

    @PostMapping(params = "deleteU")
    public String deleteUnwanted(HttpServletRequest request, @Valid SafeProduct design, Errors errors, @ModelAttribute SafeProduct safeProduct, Principal principal){

        Long ingr_id = Long.parseLong(request.getParameter("deleteU"));
        List<Ingredient> tmp = new ArrayList<>();
        for(Ingredient unwanted: safeProduct.getUnwanted()){
            if(unwanted.getRowid() != ingr_id){
                tmp.add(unwanted);
            }
        }
        safeProduct.setUnwanted(tmp);
        System.out.println("usunięto :" + ingr_id);

        return "redirect:/design";
    }

    @PostMapping(params = "addWanted")
    public String addToWanted(HttpServletRequest request, @Valid SafeProduct design, Errors errors, @ModelAttribute SafeProduct safeProduct, Principal principal){


        //User user = userRepository.findByUsername(principal.getName());
        boolean isOk = c.addIngrWU(design.getNextWanted(), design.getWanted());
        if(design.getNextWanted().equals("0")){isOk=false;}
        if(isOk){
            Ingredient next = ingredientRepository.findById(design.getNextWanted());
            List<Ingredient> tmp = new ArrayList<>();
            //tmp = safeProduct.getWanted();
            if(!safeProduct.getWanted().isEmpty()){
                for(Ingredient i: safeProduct.getWanted()){ tmp.add(i);}
            }
            //System.out.println("tmp: " + tmp );
            tmp.add(next);
            safeProduct.setWanted(tmp);
        }
        System.out.println("dodano do wanted : " + design.getNextWanted());
        return "redirect:/design";
    }

    @PostMapping(params = "addUnwanted")
    public String addToUnwanted(@Valid SafeProduct design, Errors errors, @ModelAttribute SafeProduct safeProduct, Principal principal){


        boolean isOk = c.addIngrWU(design.getNextUnwanted(), design.getUnwanted());
        if(design.getNextUnwanted().equals("0")){isOk=false;}
        if(isOk){
            Ingredient next = ingredientRepository.findById(design.getNextUnwanted());

            List<Ingredient> tmp = new ArrayList<>();//safeProduct.getUnwanted();
            if(!safeProduct.getUnwanted().isEmpty()){ // było != null
                for(Ingredient i: safeProduct.getUnwanted()){ tmp.add(i);}
            }
            tmp.add(next);
            safeProduct.setUnwanted(tmp);
        }
        System.out.println("dodano do unwanted : " + design.getNextUnwanted());
        return "redirect:/design";
    }

   /* private boolean addIngrWU(String next , List<Ingredient> list){
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
    }*/


    @PostMapping()
    public String processDesign(@Valid SafeProduct design, Errors errors, @ModelAttribute SafeProduct safeProduct){
        //validacja nie działa nwm czemu. tu jest nieporzeban. przy dodawaniu jest.
        System.out.println("safe p: " + safeProduct);

        /// POWINNAM TU SPRAWIDZIĆ CZY NIE MAM TEGO SAMEGO SKŁ NA WANTED I UNWANTED


        if(errors.hasErrors()){
            return "redirect:/";
        }
        safeProduct.setName(design.getName());
        safeProduct.setComedogenic(design.getComedogenic());
        safeProduct.setIrritation(design.getIrritation());
        safeProduct.setSafety(design.getSafety());
        // sprawdzam czy wanted i unwanted nie mają tego samego składnika
        safeProduct.setWanted(design.getWanted());
        safeProduct.setUnwanted(design.getUnwanted());

        List<String> onBothLists  = c.checkIfOnBothLists(safeProduct, " znajduje się na obu listach");/*= new ArrayList<>();
        for(Ingredient unwanted : safeProduct.getUnwanted()){
            for(Ingredient wanted: safeProduct.getWanted()){
                if(wanted.getRowid() == unwanted.getRowid()){
                    onBothLists.add("Błąd: składnik na obu listach : "+ wanted.name);
                }
            }
        }*/

       safeProduct.setWarnings(onBothLists);
       if(!onBothLists.isEmpty()){
           System.out.println("WARNINGS");
           return "redirect:/design";
       }




        defined = false;
        return "redirect:/productList/filtred";
    }
}


