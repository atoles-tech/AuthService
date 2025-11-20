package atl.web.auth_service.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException{
    public UsernameAlreadyExistsException(String username){
        super("Email '" + username +"' already exists");
    }
}
