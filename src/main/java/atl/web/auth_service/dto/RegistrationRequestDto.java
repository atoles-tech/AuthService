package atl.web.auth_service.dto;

import java.time.LocalDate;

import atl.web.auth_service.model.util.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegistrationRequestDto {
    
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Surname is required")
    private String surname;

    @NotNull(message = "Date of birth is required")
    private LocalDate birthDate;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Username must not be blank")
    @Size(min = 5,message = "Username must be more than 5 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8,message = "Password must be more than 8 characters")
    private String password;

    @NotNull(message = "Role must not be blank")
    private Role role;

}
