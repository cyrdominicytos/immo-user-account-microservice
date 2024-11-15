package cyr.tos.immouseraccount.controller;

import cyr.tos.immouseraccount.dto.DefaultUserAccountDto;
import cyr.tos.immouseraccount.dto.UserAccountDto;
import cyr.tos.immouseraccount.model.UserAccount;
import cyr.tos.immouseraccount.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RestController
@RequestMapping("/user-account")
class AccountController {
    @Autowired
    private UserAccountService userAccountService;

    @GetMapping
    public ResponseEntity<?> getUserAccounts() {
       return ResponseEntity.ok(userAccountService.getAllUserAccount());
    }

    //TODO : Uniquement accesible via Auth-MICRO-SERVICE, pas d'appel direct
    @PostMapping
    public  ResponseEntity<?> createUserAccount(@RequestBody DefaultUserAccountDto  defaultUserAccountDto) {
        return ResponseEntity.ok(userAccountService.createUserAccount(defaultUserAccountDto));
    }

    @PutMapping("/{id}")
    public  ResponseEntity<?> updateUserAccount(@RequestBody UserAccountDto userAccountDto, @PathVariable Long id) {
        return ResponseEntity.ok(userAccountService.updateUserAccount(id, userAccountDto));
    }
}
