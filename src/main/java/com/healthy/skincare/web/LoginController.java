package com.healthy.skincare.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@Controller
public class LoginController {


        // Login form
        @GetMapping("/login")
        public String login(Model model) {
            model.addAttribute("error", false);
            return "login";
        }

        // Login form with error
        @GetMapping("/login-error")
        public String loginError(Model model) {
            model.addAttribute("error", true);
            return "login";
        }

    }

