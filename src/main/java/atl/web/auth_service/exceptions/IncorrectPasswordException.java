package atl.web.auth_service.exceptions;

public class IncorrectPasswordException extends RuntimeException{
    public IncorrectPasswordException(){
        super("Password is incorrect");
    }
}
