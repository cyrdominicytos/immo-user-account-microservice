package cyr.tos.immouseraccount.controller;

import cyr.tos.immouseraccount.dto.UserAccountDto;
import cyr.tos.immouseraccount.model.UserAccount;
import cyr.tos.immouseraccount.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user-account")
class AccountController {
    @Autowired
    private UserAccountService userAccountService;

    @GetMapping
    public ResponseEntity<?> getUserAccounts() {
       return ResponseEntity.ok(userAccountService.getAllUserAccount());
    }

    @PostMapping
    public  ResponseEntity<?> createUserAccount(@RequestBody UserAccountDto userAccountDto) {
        return ResponseEntity.ok(userAccountService.createUserAccount(userAccountDto));
    }

    @PutMapping("/{id}")
    public  ResponseEntity<?> updateUserAccount(@RequestBody UserAccountDto userAccountDto, @PathVariable Long id) {
        return ResponseEntity.ok(userAccountService.updateUserAccount(id, userAccountDto));
    }
}
