package atl.web.auth_service.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import atl.web.auth_service.dto.AuthReponseDto;
import atl.web.auth_service.dto.AuthRequestDto;
import atl.web.auth_service.dto.RefreshTokenRequestDto;
import atl.web.auth_service.dto.RefreshTokenResponseDto;
import atl.web.auth_service.dto.RegistrationRequestDto;
import atl.web.auth_service.dto.RegistrationResponseDto;
import atl.web.auth_service.dto.ValidateTokenRequestDto;
import atl.web.auth_service.exceptions.IncorrectPasswordException;
import atl.web.auth_service.model.Credential;
import lombok.AllArgsConstructor;


@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class AuthService {
    
    private CredentialService credentialService;
    private RefreshTokenService refreshTokenService;

    private PasswordEncoder encoder;

    @Transactional
    public AuthReponseDto login(AuthRequestDto request){
        Credential credential = credentialService.findByEmail(request.getEmail());


        if(!encoder.matches(request.getPassword(), credential.getPassword())){
            throw new IncorrectPasswordException();
        }

        String accessToken = refreshTokenService.getAccessToken(credential);
        String refreshToken = refreshTokenService.getRefreshToken(credential);

        return new AuthReponseDto(accessToken, refreshToken);
    }

    @Transactional
    public void deleteCredential(Long id){
        credentialService.deleteById(id);
    }

    @Transactional
    public RegistrationResponseDto register(RegistrationRequestDto request){
        return credentialService.createCredential(request);
    }

    @Transactional
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto request){
        return refreshTokenService.refreshToken(request);
    }

    public Boolean validate(ValidateTokenRequestDto request){
        return refreshTokenService.validateToken(request.getToken());
    }

    @Transactional
    public void logout(String email){
        refreshTokenService.deactiveRefreshToken(credentialService.findByEmail(email));
    }

    public String extractRole(String token){
        return refreshTokenService.extractRole(token);
    }

    public String extractEmail(String token){
        return refreshTokenService.extractEmail(token);
    }

}
