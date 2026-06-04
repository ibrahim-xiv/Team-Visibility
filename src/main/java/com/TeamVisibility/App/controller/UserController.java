package com.TeamVisibility.App.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TeamVisibility.App.model.User;
import com.TeamVisibility.App.service.LoginService;
import com.TeamVisibility.App.service.RegistrationService; // Neu importiert
import com.TeamVisibility.App.service.EditService;

@Controller 
public class UserController {

    private final RegistrationService registrationService;
    private final LoginService loginService; // Neu hinzugefügt
    private final EditService editService;

    // Der Konstruktor injiziert nun BEIDE Services automatisch
    public UserController(RegistrationService registrationService, LoginService loginService, EditService editService) {
        this.registrationService = registrationService;
        this.loginService = loginService;
        this.editService = editService;
    }

    /**
     * Verarbeitet die Registrierung
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {
        System.out.println("DEBUG: Registrierungs-Versuch für " + user.getUsername());
        
        try {
            registrationService.register(user);
            System.out.println("DEBUG: User erfolgreich in DB gespeichert.");
            return "redirect:/login.html?success"; 
        } catch (Exception e) {
            System.out.println("DEBUG-ERROR: Registrierung fehlgeschlagen: " + e.getMessage());
            return "redirect:/registration.html?error";
        }
    }

    /**
     * Verarbeitet den Login
     */
    @PostMapping("/login")
    public String processLogin(@RequestParam String username, @RequestParam String password) {
        System.out.println("DEBUG: Login-Versuch für " + username);
        
        boolean success = loginService.login(username, password);
        
        if (success) {
            System.out.println("DEBUG: Login erfolgreich!");
            // Leitet zur home.html im static-Ordner weiter
            return "redirect:/home.html";
        } else {
            System.out.println("DEBUG-WARN: Login fehlgeschlagen (Falsche Daten).");
            return "redirect:/login.html?error";
        }
    }
    @PostMapping("/edit")
    public String editUser(@RequestParam String username, @RequestParam  String firstName, @RequestParam String lastName, @RequestParam String password){
        System.out.println("DEBUG: Edit versuch für" + username);

        try{
            editService.edit(username, firstName, lastName, password);
            System.out.println("DEBUG: User erfolg editiert.");
            return "redirect:/home.html";
        } catch(Exception e) {
            System.out.println("DEBUG-ERROR: Edit fehlgeschlagen: " + e.getMessage());
            return "redirect:/edit.html?error";
        }
    }
}