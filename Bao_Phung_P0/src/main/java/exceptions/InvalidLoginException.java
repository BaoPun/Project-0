package exceptions;

public class InvalidLoginException extends Exception{
	public InvalidLoginException() {
		super("Error, your login information was incorrect.  Please try again.");
	}
}
