package atl.web.auth_service.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import atl.web.auth_service.dto.RegistrationRequestDto;
import atl.web.auth_service.dto.RegistrationResponseDto;
import atl.web.auth_service.exceptions.UsernameAlreadyExistsException;
import atl.web.auth_service.model.Credential;
import atl.web.auth_service.repositories.CredentialRepository;
import lombok.AllArgsConstructor;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CredentialService {
    
    private CredentialRepository credentialRepository;

    private PasswordEncoder encoder;

    @Transactional
    public RegistrationResponseDto createCredential(RegistrationRequestDto request){
        if(credentialRepository.existsByEmail(request.getEmail())){
            throw new UsernameAlreadyExistsException(request.getEmail());
        }

        Credential credential = new Credential
            (null, request.getEmail(), encoder.encode(request.getPassword()), request.getRole(),null);
    
        Credential c = credentialRepository.save(credential);
        return new RegistrationResponseDto(c.getId(), c.getEmail(),c.getRole());
    }

    public Credential findByEmail(String email){
        return credentialRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(email));
    }

}
