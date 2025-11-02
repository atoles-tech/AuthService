package atl.web.auth_service.jwt;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import atl.web.auth_service.model.Credential;
import atl.web.auth_service.repositories.CredentialRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{

    private CredentialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credential credential = credentialRepository
            .findByUsername(username).orElseThrow(()->new UsernameNotFoundException(username));
        
        return User.builder()
                .username(credential.getUsername())
                .password(credential.getPassword())
                .authorities(credential.getRole().name())
                .build();
    }
    
}
