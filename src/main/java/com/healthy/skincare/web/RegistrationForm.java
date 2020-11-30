package com.healthy.skincare.web;
import com.healthy.skincare.User;
import com.healthy.skincare.validator.FieldsValueMatch;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class RegistrationForm {

    @Size(min=3, message=" zła nazwa użytkownika")
    @NotNull
    private String username;
    @NotNull
    @Size(min=5, message=" za któtkie hasło")
    ///@ValidPassword
    private String password;
    @NotNull
    @Size(min=5, message="za krótkie hasło")
    private String confirm;
    @NotBlank(message = "należy podać imie i nazwisko")
    //tutaj dodać sówaki
    private String fullname;
    private int comed;
    private int irr;
    private int safety;
    //private List<String> unwanted;
    //private List<String> wanted;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(
                username, passwordEncoder.encode(password),
                fullname, comed, irr, safety);
    }


}
