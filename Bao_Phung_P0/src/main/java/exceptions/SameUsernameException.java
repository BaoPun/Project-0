package exceptions;

/**
 * This exception returns an error if we try to check for username uniqueness and the query ends up being false.
 * @author baoph
 */
public class SameUsernameException extends Exception{
	public SameUsernameException() {
		super("Error, there already exists an account with that same name.");
	}
}
