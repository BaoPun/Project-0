package exceptions;

/**
 * This exception occurs when an attempt to query through a given username fails, i.e there exists no user with that username.
 * @author baoph
 *
 */
public class NoSuchUserExistsException extends Exception{
	String username;
	public NoSuchUserExistsException(String username) {
		super("Error, no such user exists with the following username: [" + username + "]");
		this.username = username;
	}
}
