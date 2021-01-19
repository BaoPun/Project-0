package exceptions;

public class DeleteUserWithAccountsException extends Exception{
	public DeleteUserWithAccountsException() {
		super("Error, you are trying to delete an account that still contains a Bank Account(s).");
	}
}
