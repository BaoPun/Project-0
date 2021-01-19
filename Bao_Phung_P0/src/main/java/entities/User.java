package entities;

import java.util.ArrayList;
import java.util.List;

import exceptions.NoSuchBankExistsException;

// User is a Bean
/**
 * This User class is simply a Bean <br>
 * It contains the following fields: full name, username, password, balance
 * @author baoph
 *
 */
public class User {
	// Data variables
	private int id;
	private String fullName, username, password;
	private boolean isSuperUser;
	private List<Account> accounts;
	
	// As bonus, record all transactions made by users.
	private List<Transaction> transactions;
	
	// Constructors
	/**
	 * A default constructor that initializes all fields of the User to either "" (String) or 0 (int)
	 */
	public User() {
		this.id = -1;
		this.fullName = this.username = this.password = "";
		this.isSuperUser = false;
		this.accounts = new ArrayList<Account>();
		this.transactions = new ArrayList<Transaction>();
		
		// Also add a Checkings and Savings account initially, both with a balance of 0
		accounts.add(new Account("Checkings", 0));
		accounts.add(new Account("Savings", 0));
		
		// And add a brand new Transaction message for both Checkings and Savings
		transactions.add(new Transaction("Created a new Checkings account"));
		transactions.add(new Transaction("Created a new Savings account"));
	}
	
	/**
	 * A 3-parameterized constructor that takes in all fields but the id and the superuser status. <br>
	 * This is mainly used for registering a user via the Console.
	 * @param fullName : the real life name of the user
	 * @param username : the person's username
	 * @param password : the person's password (encrypted)
	 */
	public User(String fullName, String username, String password) {
		this.id = -1;
		this.fullName = fullName;
		this.username = username;
		this.password = password;
		this.isSuperUser = false;
		this.accounts = new ArrayList<Account>();
		this.transactions = new ArrayList<Transaction>();
		
		// Also add a Checkings and Savings account initially, both with a balance of 0
		accounts.add(new Account("Checkings", 0));
		accounts.add(new Account("Savings", 0));
		
		// And add a brand new Transaction message for both Checkings and Savings
		transactions.add(new Transaction("Created a new Checkings account"));
		transactions.add(new Transaction("Created a new Savings account"));
	}
	
	/**
	 * A 4-parameterized constructor that takes in every single field except for the superuser status.  <br>
	 * Used for post-JDBC operations, and assumes that the user is NOT a superuser.
	 * @param id : the unique identifier generated by SQL's sequences
	 * @param fullName : the real life name of the user
	 * @param username : the person's username
	 * @param password : the person's password
	 */
	public User(int id, String fullName, String username, String password) {
		this.id = id;
		this.fullName = fullName;
		this.username = username;
		this.password = password;
		this.isSuperUser = false;
		this.accounts = new ArrayList<Account>();
		this.transactions = new ArrayList<Transaction>();
		
		// Also add a Checkings and Savings account initially, both with a balance of 0
		accounts.add(new Account("Checkings", 0));
		accounts.add(new Account("Savings", 0));
		
		// And add a brand new Transaction message for both Checkings and Savings
		transactions.add(new Transaction("Created a new Checkings account"));
		transactions.add(new Transaction("Created a new Savings account"));
	}
	
	/**
	 * A 4-parameterized constructor that takes in every single field except for the id.  <br>
	 * Assumes that the user is a superuser, and is ONLY used to register the superuser at the beginning.
	 * @param id : the unique identifier generated by SQL's sequences
	 * @param fullName : the real life name of the user
	 * @param username : the person's username
	 * @param password : the person's password
	 */
	public User(String fullName, String username, String password, boolean isSuperUser) {
		this.id = -1;
		this.fullName = fullName;
		this.username = username;
		this.password = password;
		this.isSuperUser = isSuperUser;
		this.accounts = new ArrayList<Account>();
		this.transactions = new ArrayList<Transaction>();
		
		// Also add a Checkings and Savings account initially, both with a balance of 0
		accounts.add(new Account("Checkings", 0));
		accounts.add(new Account("Savings", 0));
		
		// And add a brand new Transaction message for both Checkings and Savings
		transactions.add(new Transaction("Created a new Checkings account"));
		transactions.add(new Transaction("Created a new Savings account"));
	}
	
	
	
	/**
	 * A 5-parameterized constructor that takes in every single field and makes the user a Superuser.  <br>
	 * Used for post-JDBC operations.
	 * @param id : the unique identifier generated by SQL's sequences
	 * @param fullName : the real life name of the user
	 * @param username : the person's username
	 * @param password : the person's password
	 */
	public User(int id, String fullName, String username, String password, boolean isSuperUser) {
		this.id = id;
		this.fullName = fullName;
		this.username = username;
		this.password = password;
		this.isSuperUser = isSuperUser;
		this.accounts = new ArrayList<Account>();
		this.transactions = new ArrayList<Transaction>();
		
		// Also add a Checkings and Savings account initially, both with a balance of 0
		accounts.add(new Account("Checkings", 0));
		accounts.add(new Account("Savings", 0));
		
		// And add a brand new Transaction message for both Checkings and Savings
		transactions.add(new Transaction("Created a new Checkings account"));
		transactions.add(new Transaction("Created a new Savings account"));
	}
	
	// Getters
	/**
	 * A simple getter for the user's id.  Note that there will be no setter since the id should always stay hidden from the user.
	 * @return Int: the user's unique id.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * A simple getter for the user's full name
	 * @return String: the user's full name
	 */
	public String getFullName() {
		return this.fullName;
	}
	
	/**
	 * A simple getter for the user's username
	 * @return String:the user's username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Return whether or not the User is a superuser.
	 * @return Boolean: True if the User is a superuser, False otherwise.
	 */
	public boolean getSuperUserStatus() {
		return this.isSuperUser;
	}
	
	/**
	 * A simple getter for the user's password
	 * @return String: the user's ENCRYPTED password
	 */
	public String getPassword() {
		return this.password;
	}
	
	/**
	 * Create a new list of accounts, and return a deep copy of it.
	 * @return ArrayList<Account>: return a deep copied version of the User's list of accounts
	 */
	public ArrayList<Account> getAllAccounts(){
		ArrayList<Account> newAccounts = new ArrayList<Account>();
		for(int i = 0; i < this.accounts.size(); i++)
			newAccounts.add(this.accounts.get(i));
		return newAccounts;
	}
	
	/**
	 * Create a new list of transactions, and return a deep copyo of it.
	 * @return ArrayList<Transaction>: return a deep copied version of the User's list of transactions
	 */
	public ArrayList<Transaction> getAllRecentTransactions(){
		ArrayList<Transaction> newTransactions = new ArrayList<Transaction>();
		
		// If there are less than or equal to 10 transactions, then add them all in 
		if(this.transactions.size() <= 10) {
			for(int i = 0; i < this.transactions.size(); i++)
				newTransactions.add(this.transactions.get(i));
		}
		
		// Otherwise, start the index depending on the size of the transactions.
		// In this case, since we want only 10 transactions, it should be size() - 10 at the beginning.
		else {
			for(int i = this.transactions.size() - 10; i < this.transactions.size(); i++)
				newTransactions.add(this.transactions.get(i));
		}
		
		return newTransactions;
	}
	
	/**
	 * Given an account name, search and return the Account name within the current User.  
	 * @param accountName : the name of the account to look for
	 * @return a unique account given the username
	 * @throws NoSuchBankException if there exists no Account with the given account name.
	 */
	public Account getAccount(String accountName) throws NoSuchBankExistsException{
		for(int i = 0; i < this.accounts.size(); i++) {
			if(accountName.toLowerCase().equals(this.accounts.get(i).getName().toLowerCase()))
				return this.accounts.get(i);
		}
		throw new NoSuchBankExistsException(accountName);
	}
	
	/**
	 * Simply return the number of Bank Accounts that the user has.
	 * @return int: the number of bank accounts
	 */
	public int getNumBankAccounts() {
		return this.accounts.size();
	}
	
	// Setters
	/**
	 * A simple setter to change the user's full name
	 * @param fullName: the new full name of the user
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	/**
	 * A simple setter to change the user's username. <br>
	 * Assume that the new username is unique.
	 * @param username : the new username of the user
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	/**
	 * A simple setter to change the user's password. <br>
	 * Note that the assumed password parameter is encrypted.
	 * @param password : the new ENCRYPTED password of the user
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * A simple setter to add a brand new banking account. <br>
	 * No error/exception handling will be done here, it should be done in the other implementation files.
	 * @param account : the new Account to add to the User.
	 */
	public void addAccount(Account account) {
		this.accounts.add(account);
	}
	
	/**
	 * A simple setter to update a specific Account. <br>
	 * The only update made will be changing the balance.
	 * @throws NoSuchBankExistsException if the User doesn't have an account with the same name.
	 */
	public void updateAccount(Account account) throws NoSuchBankExistsException {
		for(int i = 0; i < accounts.size(); i++) {
			if(accounts.get(i).equals(account)) {
				accounts.get(i).setBalance(account.getBalance());
				return;
			}
		}
		throw new NoSuchBankExistsException(account.getName());
	}
	
	/**
	 * A simple setter that simply overrides the current list of Banking accounts with a new list of Banking accounts.
	 * @param newAccounts : a new set of accounts to replace the old set with.
	 */
	public void setNewAccounts(List<Account> newAccounts) {
		this.accounts = newAccounts;
	}
	
	public void addTransaction(Transaction transaction) {
		this.transactions.add(transaction);
	}
	
	/**
	 * Given an account name, delete that account from the user if it exists.
	 * @param accountName : the name of the bank account to delete from the User.
	 * @throws NoSuchBankExistsException if the User doesn't have an account with the accountName.
	 */
	public void deleteAccount(String accountName) throws NoSuchBankExistsException{
		for(int i = 0; i < this.accounts.size(); i++) {
			if(this.accounts.get(i).getName().toLowerCase().equals(accountName.toLowerCase())) {
				this.accounts.remove(i);
				return;
			}
		}
		throw new NoSuchBankExistsException(accountName);
	}
	
	/**
	 * Override this method to check if two users have the username. <br>
	 * Just like in other account services, usernames have to be unique. <br>
	 * Note that case sensitivity is ignored.
	 */
	@Override
	public boolean equals(Object o) {
		return (this.username.toLowerCase().equals(((User) o).username.toLowerCase()));
	}
	
	/**
	 * Return a string representation of the User object.
	 * @return String: string representation of the User object
	 */
	@Override
	public String toString() {
		return "[Full name: " + this.fullName + ", Username: " + this.username + "]";
	}
}
