package exceptions;

public class DeletingBankAccountWithMoneyException extends Exception{
	String accountName;
	public DeletingBankAccountWithMoneyException(String accountName) {
		super("Error, " + accountName + " still contains money, you cannot delete this account right now.");
		this.accountName = accountName;
	}
}
