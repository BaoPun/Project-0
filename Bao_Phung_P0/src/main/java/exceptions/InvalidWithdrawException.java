package exceptions;

public class InvalidWithdrawException extends Exception{
	double amountWithdrawn;
	public InvalidWithdrawException(double amountWithdrawn) {
		super("Error, you cannot withdraw $" + (Math.round(amountWithdrawn * 100.0)/100.0) + ", you don't have this much on your account.");
		this.amountWithdrawn = amountWithdrawn;
	}
}
