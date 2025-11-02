package atl.web.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthRequestDto {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 5,message = "Username must be more than 5 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8,message = "Password must be more than 8 characters")
    private String password;
}
