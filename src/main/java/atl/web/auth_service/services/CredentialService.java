package atl.web.auth_service.services;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import atl.web.auth_service.client.UserServiceClient;
import atl.web.auth_service.dto.RegistrationRequestDto;
import atl.web.auth_service.dto.RegistrationResponseDto;
import atl.web.auth_service.dto.client.UserRequest;
import atl.web.auth_service.dto.client.UserResponse;
import atl.web.auth_service.exceptions.UserNotFoundException;
import atl.web.auth_service.exceptions.UsernameAlreadyExistsException;
import atl.web.auth_service.model.Credential;
import atl.web.auth_service.repositories.CredentialRepository;
import lombok.AllArgsConstructor;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class CredentialService {
    
    private CredentialRepository credentialRepository;

    private UserServiceClient userServiceClient;

    private PasswordEncoder encoder;

    @Transactional
    public RegistrationResponseDto createCredential(RegistrationRequestDto request){
        if(credentialRepository.existsByUsername(request.getUsername())){
            throw new UsernameAlreadyExistsException(request.getUsername());
        }

        UserResponse response = userServiceClient.createUser(UserRequest.builder()
            .name(request.getName())
            .surname(request.getSurname())
            .email(request.getEmail())
            .birthDate(request.getBirthDate())
            .build());

        Credential credential = new Credential
            (null, response.getId(), request.getUsername(), encoder.encode(request.getPassword()), request.getRole(),null);
    
        Credential c = credentialRepository.save(credential);
        return new RegistrationResponseDto(c.getUserId(), c.getUsername(),c.getRole());
    }

    public Credential findByUsername(String username){
        return credentialRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public Credential findByUserId(Long userId){
        return credentialRepository.findByUserId(userId)
            .orElseThrow(()->new UserNotFoundException(userId));
    }
}
