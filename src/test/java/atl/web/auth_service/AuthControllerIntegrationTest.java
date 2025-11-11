package atl.web.auth_service;

import com.fasterxml.jackson.databind.ObjectMapper;

import atl.web.auth_service.client.UserServiceClient;
import atl.web.auth_service.dto.AuthReponseDto;
import atl.web.auth_service.dto.AuthRequestDto;
import atl.web.auth_service.dto.RegistrationRequestDto;
import atl.web.auth_service.dto.RegistrationResponseDto;
import atl.web.auth_service.dto.client.UserRequest;
import atl.web.auth_service.dto.client.UserResponse;
import atl.web.auth_service.model.Credential;
import atl.web.auth_service.model.util.Role;
import atl.web.auth_service.repositories.CredentialRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CredentialRepository credentialRepository;

    @Autowired
    private PasswordEncoder encoder;

    @MockitoBean
    private UserServiceClient client;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);

        registry.add("spring.liquibase.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    void createCredential() {
        Credential credential = new Credential(null, 1L, "username", encoder.encode("username"), Role.ROLE_USER, null);
        credentialRepository.save(credential);
    }

    @AfterAll
    static void closeContainer() {
        postgreSQLContainer.close();
    }

    @AfterEach
    void clearDb() {
        credentialRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return token when user exists")
    void shouldReturnToken_WhenCredentialExists() throws Exception {
        createCredential();
        AuthRequestDto requestDto = new AuthRequestDto("username", "username");

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = login.getResponse().getContentAsString();

        AuthReponseDto responseDto = objectMapper.readValue(responseContent, AuthReponseDto.class);
        assertNotNull(responseDto.getAccessToken());
        assertFalse(responseDto.getAccessToken().isEmpty());
        assertNotNull(responseDto.getRefreshToken());
        assertFalse(responseDto.getRefreshToken().isEmpty());
    }

    @Test
    @DisplayName("Should return conflict status")
    void shouldReturnConflictStatus_WhenPasswordIsIncorrect() throws Exception {
        createCredential();
        AuthRequestDto requestDto = new AuthRequestDto("username", "useruseruser");

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict())
                .andReturn();
        String responseContent = login.getResponse().getContentAsString();
        System.out.println(responseContent);

        assertEquals("Password is incorrect", responseContent);
    }

    @Test
    @DisplayName("Should return not found status when credential not found")
    void shouldReturnNotFoundStatus_WhenCredentialNotFound() throws Exception {
        AuthRequestDto requestDto = new AuthRequestDto("username", "useruseruser");

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andReturn();

        String responseContent = login.getResponse().getContentAsString();
        System.out.println(responseContent);

        assertEquals("Username 'username' not found", responseContent);
    }

    @Test
    @DisplayName("Should return same refresh token when user trying auth more than 1 time")
    void shouldReturnSameRefreshToken_WhenUserHasMoreThan1Auth() throws Exception {
        createCredential();
        AuthRequestDto requestDto = new AuthRequestDto("username", "username");

        MvcResult login = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent = login.getResponse().getContentAsString();
        AuthReponseDto responseDto = objectMapper.readValue(responseContent, AuthReponseDto.class);
        String refreshTokenExcepted = responseDto.getRefreshToken();
        MvcResult login2 = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();
        String responseContent2 = login2.getResponse().getContentAsString();
        AuthReponseDto responseDto2 = objectMapper.readValue(responseContent2, AuthReponseDto.class);
        String refreshToken = responseDto2.getRefreshToken();

        assertEquals(refreshTokenExcepted, refreshToken);
    }

    @Test
    @DisplayName("Should return register response when register request is correct")
    void shouldReturnRegisterResponse_WhenUserTryingRegisterCorrect() throws Exception {
        RegistrationRequestDto request = new RegistrationRequestDto("name", "surname", LocalDate.of(2006, 1, 1),
                "evgenijkhodosok@gmail.com", "username", "username", Role.ROLE_USER);
        UserRequest userRequest = new UserRequest("name", "surname", LocalDate.of(2006, 1, 1),
                "evgenijkhodosok@gmail.com");
        UserResponse userResponse = new UserResponse(1L, "name", "surname", LocalDate.of(2006, 1, 1),
                "evgenijkhodosok@gmail.com");

        when(client.createUser(userRequest)).thenReturn(userResponse);
        MvcResult register = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = register.getResponse().getContentAsString();
        RegistrationResponseDto response = objectMapper.readValue(responseContent, RegistrationResponseDto.class);

        assertEquals("username", response.getUsername());
        assertEquals(1L, response.getId());
        assertEquals(Role.ROLE_USER, response.getRole());
    }

    @Test
    @DisplayName("Should return conflict status when username already exists")
    void shouldReturnConflictStatus_WhenUsernameAlreadyExists() throws Exception{
        createCredential();
        RegistrationRequestDto request = new RegistrationRequestDto("name", "surname", LocalDate.of(2006, 1, 1),
                "evgenijkhodosok@gmail.com", "username", "username", Role.ROLE_USER);

        MvcResult register = mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andReturn();

        String responseContent = register.getResponse().getContentAsString();
       
        assertEquals("Username 'username' already exists", responseContent);
    }

}