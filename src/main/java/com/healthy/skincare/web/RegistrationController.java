package com.healthy.skincare.web;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import  com.healthy.skincare.User;
import com.healthy.skincare.data.UserRepository;

import javax.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private String alert;

    public RegistrationController(
            UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;

    }

    @ModelAttribute(name = "alert")
    public String alert() { return ""; }

    @GetMapping
    public String registerForm(Model model)
    {
        model.addAttribute("registrationForm",new RegistrationForm() );
        String message = alert;
        alert="";
        model.addAttribute("alert", message);
        return "registration";
    }

    @PostMapping
    public String processRegistration( @Valid RegistrationForm registrationForm, Errors errors  ) {

        System.out.println(registrationForm);
        if(errors.hasErrors()){
            System.out.println(errors);
            System.out.println("FAIL");
            return "registration";
        }
        if(!registrationForm.getPassword().equals(registrationForm.getConfirm())){
            alert =  "hasła różną się";
            return "redirect:/register";//"registration";
        }

        User u = userRepo.findByUsername(registrationForm.getUsername());
        if(u.getUsername().equals(registrationForm.getUsername())){
            alert = "użytkownik o takiej nazwie już istneje";
            return "redirect:/register";
        }


        userRepo.save(registrationForm.toUser(passwordEncoder));
        return "redirect:/login";
    }
}
