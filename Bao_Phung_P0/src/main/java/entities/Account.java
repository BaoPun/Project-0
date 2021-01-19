package entities;

/**
 * A single account simply contains the name of the account as well as the balance
 * This will also act as a Bean
 * @author baoph
 *
 */
public class Account {
	// Private fields
	private int id;
	private String name;
	private double balance;
	
	// Constructors
	/**
	 * Default constructor for Account; presets the id to -1, balance to 0, and name to "".
	 */ 	 	 
	public Account() {
		this.id = -1;
		this.name = "";
		this.balance = 0;
	}
	
	/**
	 * Presets the name and balance field, but initializes the id by default.
	 * @param name : the name of the bank account
	 * @param balance : the initial amount of money on this account
	 */
	public Account(String name, double balance) {
		this.id = -1;
		this.name = name;
		this.balance = balance;
	}
	
	/**
	 * Presets all 3 fields.
	 * @param id : the unique identifier of the account
	 * @param name : the name of the bank account 
	 * @param balance : the initial amount of money on this account
	 */
	public Account(int id, String name, double balance) {
		this.id = id;
		this.name = name;
		this.balance = balance;
	}
	
	// Getters
	/**
	 * Simply return the unique id of the Account
	 * @return the unique id of the Account
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Simply return the name of the Account
	 * @return the name of the Account
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Simply return the amount of money on the Account
	 * @return the amount of money on the Account
	 */
	public double getBalance() {
		return this.balance;
	}
	
	// Setters
	/**
	 * Simply change the name of the Account to w/e the parameter is
	 * @param name : the new name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Simply change the balance of the Account to w/e the parameter is
	 * @param balance : the new balance
	 */
	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	
	/**
	 *  2 accounts are the same if they have the same name.
	 *  @return True if 2 Accounts have the same name, False otherwise
	 */
	@Override
	public boolean equals(Object o) {
		return this.name.toLowerCase().equals(((Account) o).name.toLowerCase());
	}
	
	/**
	 * Return a String representation of a single Account.
	 * @return String: see description.
	 */
	@Override
	public String toString() {
		return "Account name: " + this.name + "\nBalance: $" + (Math.round(this.balance* 100.0)/100.0);
	}
}
