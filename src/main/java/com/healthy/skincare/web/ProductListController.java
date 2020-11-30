package com.healthy.skincare.web;

import com.healthy.skincare.IngredientUser;
import com.healthy.skincare.Product;
import com.healthy.skincare.User;
import com.healthy.skincare.data.CommText;
import com.healthy.skincare.data.IngredientRepository;
import com.healthy.skincare.data.ProductRepository;
import com.healthy.skincare.data.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.Errors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import com.healthy.skincare.SafeProduct;
import org.springframework.web.bind.support.SessionStatus;

@Slf4j
@Controller
@SessionAttributes("safeProduct")
@RequestMapping("/productList")
public class ProductListController {
    public ProductRepository productRepository;
    public IngredientRepository ingredientRepository;
    public UserRepository userRepository;

    @Autowired
    public ProductListController(ProductRepository productRepository, IngredientRepository ingredientRepository, UserRepository userRepository){
        this.productRepository = productRepository;
        this.ingredientRepository = ingredientRepository;
        this.userRepository = userRepository;
    }


    @GetMapping("/filtred")
    public String ProductsFiltred(@Valid SafeProduct safeProduct, Errors errors, SessionStatus sessionStatus, Model model){

        System.out.println("Lista GET");
        System.out.println("Safe product w list: " + safeProduct);
        if(errors.hasErrors()){
            System.out.println(errors + "error");
        }
        model.addAttribute("safeProduct", safeProduct);

        //System.out.println("wanted: " + safeProduct.getWanted());
        //System.out.println("unwanted : " + safeProduct.getUnwanted() +"is empty: "+  safeProduct.getUnwanted().isEmpty());
        //List<Ingredient> tmp = new ArrayList<>();
        //System.out.println("lista: " + tmp + " is empty: " + tmp.isEmpty());

        List<Product> products = new ArrayList<>();
        // było == null zmienam na .isEmpty
        if(safeProduct.getWanted().isEmpty() && safeProduct.getUnwanted().isEmpty()){
            System.out.println("tylko filtry");
            productRepository.findByInci(safeProduct).forEach(i -> products.add(i));
        } else {
            if(safeProduct.getWanted().isEmpty()){
                System.out.println("tylko wanted");
                productRepository.findByInciU(safeProduct).forEach(i -> products.add(i));
            } else {
                if (safeProduct.getUnwanted().isEmpty()){
                    System.out.println("tylko unwanted");
                    productRepository.findByInciW(safeProduct).forEach(i -> products.add(i));

                } else {
                    System.out.println("po wszystkim");
                    productRepository.findByInciWU(safeProduct).forEach(i -> products.add(i));

                }
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("one", new SafeProduct());
        //sessionStatus.setComplete();
        return "ProductsFiltred";
    }

    @PostMapping("/filtred")
    public String processClick(@Valid SafeProduct one, @Valid SafeProduct safeProduct, Errors errors){
        if(errors.hasErrors()){
            return  "ProductFiltred";
        }
        return  "redirect:/productList/one";
    }


 /// wyświetlanie ulubionych
    @GetMapping("/favorite")
    public String ProductsFavorite(@Valid SafeProduct safeProduct,Principal principal, Errors errors, SessionStatus sessionStatus, Model model){

        if(errors.hasErrors()){
            System.out.println(errors + "error");
        }
        List<Product> products = new ArrayList<>();
        User user = userRepository.findByUsername(principal.getName());
        productRepository.getUserFaves(user.getId()).forEach(i -> products.add(i));
        model.addAttribute("products", products);
        model.addAttribute("one",new SafeProduct() );
        sessionStatus.setComplete();
        return "ProductsFiltred";
    }

    @PostMapping("/favorite")
    public String processFave(@Valid SafeProduct one, Errors errors){
        if(errors.hasErrors()){
            return  "ProductsFiltred";
        }
        return  "redirect:/productList/favorite";
    }


    @GetMapping("/one")
    public String ShowInfo(@Valid SafeProduct one, @Valid SafeProduct safeProduct, Errors errors, Model model, Principal principal){
        System.out.println( "w zakładce one " + one);
        if(one.getId() == null){
            //błąd
        }
        Long id = one.getId();
        Product product = productRepository.findById(id);
        if(product.getName().equals("-1")){
            //błąd
            return "ProductsFiltred";
        }

        List<IngredientUser> ingredients = new ArrayList<>();
        ingredientRepository.getProductIngredients(id).forEach(i -> ingredients.add(new IngredientUser(i)));
        User user = userRepository.findByUsername(principal.getName());
         // Sprawdzam czy sładnik ma parametry nie przekraczające cis użytkownika
        for(IngredientUser i : ingredients){
            i.CheckSafety(user);
        }

        String liked = "dodaj do ulubionych";
        boolean isLiked = productRepository.isLiked(user.getId(), product.getId());
        if(isLiked == true){
            liked = "usuń z ulubionych";
        }
        model.addAttribute("liked", liked);
        List<Comment> comments = userRepository.getComments(user.getId(), product.getId());
        model.addAttribute("comments", comments);
        // żeby można było je  usówać to muszę znać rowid tego komentarza

        model.addAttribute("product", product);
        model.addAttribute("ingredients", ingredients);
        model.addAttribute("commentU", new CommText());
        return "one";
    }

    @PostMapping(value = "/one", params = "like")
    public String LikeClick(HttpServletRequest request, @Valid SafeProduct one, @Valid SafeProduct safeProduct, Principal principal) {
        Long id = one.getId();
        Product product = productRepository.findById(id);
        User user = userRepository.findByUsername(principal.getName());
        boolean isLiked = productRepository.isLiked(user.getId(), product.getId());
        if(isLiked == false){
            userRepository.addToLiked(user.getId(), product.getId());
        }else{
            userRepository.deleteFromLiked(user.getId(), product.getId());
        }
        return "redirect:/productList/one";
    }

    @PostMapping(value = "/one", params = "comment")
    public String CommentClick(HttpServletRequest request, @Valid SafeProduct one, @Valid SafeProduct safeProduct, @ModelAttribute CommText commentU, Model model, Principal principal ) {
        model.addAttribute("commnetU", commentU);
        Long id = one.getId();
        Product product = productRepository.findById(id);
        User user = userRepository.findByUsername(principal.getName());
        userRepository.addComment(user.getId(), product.getId(), commentU.text, commentU.score);
        return "redirect:/productList/one";
    }

    @PostMapping(value = "/one", params = "delete")
    public String CommentDeleteClick(HttpServletRequest request, @Valid SafeProduct one, @Valid SafeProduct safeProduct) {
        System.out.println(request.getParameter("delete"));
        userRepository.deleteComment(Long.parseLong(request.getParameter("delete")));
        // jeszcze nic nie robi
        return "redirect:/productList/one";
    }


}
