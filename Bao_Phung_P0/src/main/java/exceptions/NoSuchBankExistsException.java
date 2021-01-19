package exceptions;

public class NoSuchBankExistsException extends Exception{
	String accountName;
	public NoSuchBankExistsException(String accountName) {
		super("Error, no such bank account exists with the following name: [" + accountName + "]");
		this.accountName = accountName;
	}
}
