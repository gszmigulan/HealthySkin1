package com.healthy.skincare.web;

import com.healthy.skincare.Product;
import com.healthy.skincare.User;
import com.healthy.skincare.data.Common;
import com.healthy.skincare.data.IngredientRepository;
import com.healthy.skincare.data.ProductRepository;
import com.healthy.skincare.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
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
@SessionAttributes("safeProduct")
@RequestMapping("/parameters")
public class ParametersController {
    private final IngredientRepository ingredientRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    Common c = new Common();

    @Autowired
    public ParametersController(IngredientRepository ingredientRepository, ProductRepository productRepository, UserRepository userRepository){
        this.ingredientRepository = ingredientRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @ModelAttribute(name = "safeProduct")
    public SafeProduct safeProduct() {
        System.out.println("NOWY SAFE PRODUKT");return new SafeProduct();
    }
    @ModelAttribute(name = "design")
    public SafeProduct design() {
        return new SafeProduct();
    }

    @GetMapping()
    public String showParametersForm(Model model, @Valid SafeProduct safeProduct, Principal principal, @ModelAttribute SafeProduct design ){

        List<Ingredient> allIngr = new ArrayList<>();
        ingredientRepository.findAll().forEach(i -> allIngr.add(i));

        User user = userRepository.findByUsername(principal.getName());
        List<String> userUnwanted = userRepository.getUnwantedList(user.getId());
        List<String> userWanted = userRepository.getWantedList(user.getId());

        List<Ingredient> WantedList = c.toIngredientList(userWanted, allIngr);//new ArrayList<>();
        List<Ingredient> UnwantedList = c.toIngredientList(userUnwanted, allIngr);

        model.addAttribute("allIngr", allIngr);
        design.setWanted(WantedList);
        design.setUnwanted(UnwantedList);


        safeProduct.setComedogenic(user.getComed());
        safeProduct.setIrritation(user.getIrr());
        safeProduct.setSafety(user.getSafety());
        safeProduct.setWanted(WantedList);
        safeProduct.setUnwanted(UnwantedList);

        List<String> warnings = new ArrayList<>();
        warnings = safeProduct.getWarnings();
        safeProduct.setWarnings(new ArrayList<>());

        model.addAttribute("WarningsList", warnings);
        model.addAttribute("design", safeProduct);
        return "parameters";
    }

    @PostMapping(params = "deleteW")
    public String deleteWanted(HttpServletRequest request, @Valid SafeProduct design, Errors errors, @ModelAttribute SafeProduct safeProduct, Principal principal){

        System.out.println("DELETE : " + request.getParameter("deleteW"));
        Long ingr_id = Long.parseLong(request.getParameter("deleteW"));
        User user = userRepository.findByUsername(principal.getName());
        userRepository.deleteUserWanted(user.getId(), ingr_id);

        //design.setWarnings( new ArrayList<>());
        return "redirect:/parameters";
    }

    @PostMapping(params = "deleteU")
    public String deleteUnwanted(HttpServletRequest request, @Valid SafeProduct design, Errors errors, @ModelAttribute SafeProduct safeProduct, Principal principal){

        System.out.println("DELETE : " + request.getParameter("deleteW"));
        Long ingr_id = Long.parseLong(request.getParameter("deleteU"));
        User user = userRepository.findByUsername(principal.getName());
        userRepository.deleteUserUnwanted(user.getId(), ingr_id);

        //design.setWarnings( new ArrayList<>());
        return "redirect:/parameters";
    }

    @PostMapping(params = "addWanted")
    public String addToWanted(@Valid SafeProduct design, Errors errors, @ModelAttribute SafeProduct safeProduct, Principal principal){

        safeProduct.setWanted(design.getWanted());
        User user = userRepository.findByUsername(principal.getName());
        boolean isOk = c.addIngrWU(design.getNextWanted(), design.getWanted());
        List<String> warnings= c.checkIfIsOnAnother(safeProduct.getUnwanted(), design.getNextWanted(), "ERROR: ten składnik nie może być dodany bo jest już na liście niechcianych");
        safeProduct.setWarnings(warnings);
        if(!warnings.isEmpty()){isOk = false;}
        if(isOk){
            userRepository.setUserWanted(user.getId(), design.getNextWanted());
        }



        return "redirect:/parameters";
    }

    @PostMapping(params = "addUnwanted")
    public String addToUnwanted(@Valid SafeProduct design, Errors errors, @ModelAttribute SafeProduct safeProduct, Principal principal){

        safeProduct.setUnwanted(design.getUnwanted());
        User user = userRepository.findByUsername(principal.getName());
        boolean isOk = c.addIngrWU(design.getNextUnwanted(), design.getUnwanted());

        List<String> warnings= c.checkIfIsOnAnother(safeProduct.getUnwanted(), design.getNextWanted(), "ERROR: ten składnik nie może być dodany bo jest już na liście szukanych");
        safeProduct.setWarnings(warnings);
        if(!warnings.isEmpty()){isOk = false;}

        if(isOk){
            userRepository.setUserUnwanted(user.getId(), design.getNextUnwanted());
        }



        //List <String> warnings = new ArrayList<>();
        //warnings.add("dodano składnik z listy unwanted");
        //safeProduct.setWarnings(warnings);
        return "redirect:/parameters";
    }

    @PostMapping(params = "save")
    public String processParameters(@Valid SafeProduct design, Errors errors, @ModelAttribute SafeProduct safeProduct, Principal principal){

        if(errors.hasErrors()){
            return "redirect:/";
        }
        User user = userRepository.findByUsername(principal.getName());
        userRepository.setUserCIS(user.getId(), design.getComedogenic(), design.getIrritation(), design.getSafety());

        //to może być niepotrzebne
        safeProduct.setWanted(design.getWanted());
        safeProduct.setUnwanted(design.getUnwanted());

        return "redirect:/parameters";
    }
}