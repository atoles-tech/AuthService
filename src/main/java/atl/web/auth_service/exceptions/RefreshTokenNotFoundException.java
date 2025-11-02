package atl.web.auth_service.exceptions;

public class RefreshTokenNotFoundException extends RuntimeException{
    
    public RefreshTokenNotFoundException(String token){
        super("Token '" + token + "' not found");
    }

}
