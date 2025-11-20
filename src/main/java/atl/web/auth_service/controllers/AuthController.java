package atl.web.auth_service.controllers;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import atl.web.auth_service.dto.AuthReponseDto;
import atl.web.auth_service.dto.AuthRequestDto;
import atl.web.auth_service.dto.RefreshTokenRequestDto;
import atl.web.auth_service.dto.RefreshTokenResponseDto;
import atl.web.auth_service.dto.RegistrationRequestDto;
import atl.web.auth_service.dto.RegistrationResponseDto;
import atl.web.auth_service.dto.ValidateTokenRequestDto;
import atl.web.auth_service.services.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {
    
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthReponseDto> login(@RequestBody @Valid AuthRequestDto request){
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegistrationResponseDto> register(@RequestBody @Valid RegistrationRequestDto request){
        return new ResponseEntity<>(authService.register(request),HttpStatus.CREATED);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestBody @Valid ValidateTokenRequestDto request){
        return ResponseEntity.ok(authService.validate(request));
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponseDto> refreshToken(@RequestBody @Valid RefreshTokenRequestDto request){
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/extract-role")
    public ResponseEntity<String> extractRole(@RequestBody @Valid ValidateTokenRequestDto request){
        return ResponseEntity.ok(authService.extractRole(request.getToken()));
    }

    @PostMapping("/extract-email")
    public ResponseEntity<String> extractEmail(@RequestBody @Valid ValidateTokenRequestDto request){
        return ResponseEntity.ok(authService.extractEmail(request.getToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Principal principal){
        authService.logout(principal.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
