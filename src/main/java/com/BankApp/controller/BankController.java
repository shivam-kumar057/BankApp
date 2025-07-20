package com.BankApp.controller;

import com.BankApp.model.Account;
import com.BankApp.model.Login;
import com.BankApp.model.LoginRequest;
import com.BankApp.service.AccountService;
import com.BankApp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController

public class BankController {

    @Autowired
    private AccountService accountService;

    @Autowired
   private JwtUtil  jwtUtil;

    @GetMapping("/dashboard")
    public String dashboard(Model model){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("userName ==="+userName);
        Account account = accountService.findAccountByUsername(userName);
        model.addAttribute("account", account);
        return "dashboard";
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String username, @RequestParam String password) {
        accountService.registerAccount(username, password);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public Login login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        boolean success = accountService.login(username, password);
        if (success) {
//            String token = jwtUtil.generateToken(username);
            return new Login("success", "Login successful","");
        } else {
            return new Login("fail", "Invalid credentials" ,"");
        }
    }

}
