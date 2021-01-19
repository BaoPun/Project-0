package exceptions;

public class InvalidPasswordException extends Exception{
	public InvalidPasswordException() {
		super("Error, your password is invalid; it must be at least 8 characters long, and it can only contain alpha-numeric characters.");
	}
}
