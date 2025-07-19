package com.BankApp.controller;

import com.BankApp.model.Account;
import com.BankApp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class BankController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/dashboard")
    public String dashboard(Model model){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("userName ==="+userName);
        Account account = accountService.findAccountByUsername(userName);
        model.addAttribute("account", account);
        return "dashboard";
    }
}
