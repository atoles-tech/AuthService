package atl.web.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import atl.web.auth_service.dto.client.UserRequest;
import atl.web.auth_service.dto.client.UserResponse;
import jakarta.validation.Valid;

@FeignClient(
    name = "user-service",
    url = "${user.service.url:http://localhost:8081}"
)
public interface UserServiceClient {
    
    @PostMapping("/api/v1/users")
    UserResponse createUser(@RequestBody @Valid UserRequest request);
}
