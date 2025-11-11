package atl.web.auth_service.dto;

import atl.web.auth_service.model.util.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationResponseDto {
    
    private Long id;
    private String username;
    private Role role;

}
