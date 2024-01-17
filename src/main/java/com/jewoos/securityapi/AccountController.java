package com.jewoos.securityapi;

import com.jewoos.securityapi.dto.request.Signup;
import com.jewoos.securityapi.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@Valid @RequestBody Signup signup) {
        accountService.signup(signup);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
