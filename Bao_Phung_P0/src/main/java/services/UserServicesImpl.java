package services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import daos.AccountDAO;
import daos.AccountDAOImpl;
import daos.UserDAO;
import daos.UserDAOImpl;
import entities.Account;
import entities.Transaction;
import entities.User;
import exceptions.DeleteUserWithAccountsException;
import exceptions.DeletingBankAccountWithMoneyException;
import exceptions.InvalidLoginException;
import exceptions.InvalidPasswordException;
import exceptions.InvalidWithdrawException;
import exceptions.NoSuchBankExistsException;
import exceptions.NoSuchUserExistsException;
import exceptions.SameUsernameException;
import util.DBConnection;

public class UserServicesImpl implements UserServices{
	
	// Create a User DAO
	private static UserDAO users;
	
	public UserServicesImpl() {
		users = new UserDAOImpl();
	}
	
	public UserServicesImpl(int key) {
		users = new UserDAOImpl(key);
	}
	
	/**
	 * This method simply retrieves a specific User given their username.
	 * @throws NoUserExistsException is thrown if a given user 
	 */
	public User getUserByUsername(String username) throws NoSuchUserExistsException {
		return users.getUserByUsername(username);
	}
	
	/**
	 * Given a user, view all of their accounts that they own.
	 * @param user : the User to view all account information from.
	 */
	@Override
	public void viewAccounts(User user) {
		System.out.println("\nViewing all of [" + user.getUsername() + "]'s bank accounts");
		List<Account> accounts = user.getAllAccounts();
		for(int i = 0; i < accounts.size(); i++) {
			System.out.println("Account " + (i+1) + ":\n" + accounts.get(i));
			
			// For formatting purposes, if we are NOT at the last Account, add a newline.
			if(i != accounts.size() - 1)
				System.out.println();
		}
	}
	
	/**
	 * Given a user, view their 10 most recent transactions.
	 * @param user : the User to view the 10 most recent transactions from
	 */
	@Override
	public void viewTransactions(User user) {
		List<Transaction> transactions = user.getAllRecentTransactions();
		for(int i = transactions.size() - 1; i >= 0; i--) 
			System.out.println("Transaction " + (transactions.size() - i) + ": " + transactions.get(i).getMessage());
	}
	
	/**
	 * Given a user, add a brand new account, but only if the account name is unique.
	 * @param user : the User to add a new banking account from.
	 * @param newAccount : the new Account to add to the User.
	 * @throws SameUsernameException if an account name from newAccount already exists in the User's list of accounts.
	 * @throws NoSuchUserExistsException should never be thrown, but is needed anyways.
	 */
	@Override
	public User registerBankAccount(User user, Account newAccount) throws SameUsernameException{
		// Get a list of all of the User's bank accounts that are stored in the User object itself. 
		// If the name of the inputted account is the same as any one of the Accounts, then throw an exception!
		List<Account> userBankAccounts = user.getAllAccounts();
		for(int i = 0; i < userBankAccounts.size(); i++) {
			if(newAccount.equals(userBankAccounts.get(i)))
				throw new SameUsernameException();
		}
		
		// Next, add the new Account to the database
		// If successful, retrieve it via a query and add it to the User account.
		if(users.createBankAccount(user.getId(), newAccount)) {
			user.addAccount(users.getAccount(user.getId(), newAccount.getName()));
			user.addTransaction(new Transaction(user.getId(), "Created a new " + newAccount.getName() + " Bank Account"));
		}
		
		// Finally, return the updated User.
		return user;
	}

	/**
	 * Given the current logged in User and an inputted accountName and balance, increase the User's bank account by the given balance
	 * @param user : the current User that is logged in
	 * @param accountName : the accountName to search for
	 * @param balance : the amount to increase on the User's bank account.
	 * @throws NoSuchUserExistsException is thrown if there is no user with the given username.
	 * @throws SameUsernameException should never be thrown in this method.
	 * @throws NoSuchBankExistsException should be thrown if the User has no bank account with the given account name.
	 */
	@Override
	public User deposit(User user, String accountName, double balance) throws NoSuchBankExistsException{
		// First, attempt to find the account with the provided account name
		Account account = user.getAccount(accountName);
		
		// Second, update the balance (in this case, it's an increment)
		account.setBalance(account.getBalance() + balance);
		
		// Third, update the User's specific account
		user.updateAccount(account);
		
		// Fourth, update the database with the new balance
		users.updateUserTransactions(user.getId(), accountName, 0, balance); 
		users.updateBankAccount(user.getId(), account);
		user.addTransaction(new Transaction("Deposited $" + (Math.round(balance)*100./100.) + " to the " + accountName + " Bank Account"));
		
		// And return the user itself.
		System.out.println("Money successfully deposited onto your account!");
		return user;
	}

	/**
	 * Given the current logged in User and an inputted accountName and balance, increase the User's bank account by the given balance
	 * @param user : the current User that is logged in
	 * @param accountName : the accountName to search for
	 * @param balance : the amount to decrease on the User's bank account
	 * @throws NoSuchUserExistsException is thrown if there is no user with the given username
	 * @throws SameUsernameException should never be thrown in this method
	 */
	@Override
	public User withdraw(User user, String accountName, double balance) throws NoSuchBankExistsException, InvalidWithdrawException{
		// First, attempt to find the account with the provided account name
		Account account = user.getAccount(accountName);
		
		// Second, update the balance (in this case, it's a decrement)
		// However, throw an exception if the input balance is larger than what the Account has in the bank
		if(balance > account.getBalance())
			throw new InvalidWithdrawException(balance);
		account.setBalance(account.getBalance() - balance);
		
		// Third, update the User's specific account
		user.updateAccount(account);
		
		// Fourth, update the database with the new balance
		users.updateUserTransactions(user.getId(), accountName, 1, balance); 
		users.updateBankAccount(user.getId(), account);
		user.addTransaction(new Transaction("Withdrawn $" + (Math.round(balance)*100./100.) + " fro the " + accountName + " Bank Account"));
		
		// And return the user itself.
		System.out.println("Money successfully withdrawn from your account!");
		return user;
	}
	
	/**
	 * Simply add the user to the database, but first checks if the password is valid.
	 * @param user : a User to add to the database
	 * @throws InvalidPasswordException is thrown if the password is invalid
	 */
	@Override
	public boolean registerUser(User user) throws InvalidPasswordException{
		
		// Before adding the user, make sure the password is valid (only alpha-numeric characters allowed, length >= 8)
		if(user.getPassword().length() < 8)
			throw new InvalidPasswordException();
		
		// Then, check every single character in the password to see if all characters are only alphanumeric (only letters and digits)
		// If a character is neither of the above, throw the InvalidPasswordException
		boolean onlyAlphaDigits = true;
		for(int i = 0; i < user.getPassword().length(); i++) {
			if(!Character.isAlphabetic(user.getPassword().charAt(i)) && !Character.isDigit(user.getPassword().charAt(i))) {
				onlyAlphaDigits = false;
				break;
			}
		}
		
		// If the password isn't valid, then throw an exception.  Otherwise, attempt to create the user!
		if(!onlyAlphaDigits)
			throw new InvalidPasswordException();
		else
			return users.createUser(user);
	}
	
	/**
	 * Delete the user from the database. <br>
	 * Only possible if they have no more accounts.
	 * @param user : the User in which to delete from
	 * @throws NoSuchUserExistsException if the input user doesn't exist in our database.
	 */
	public User deleteUser(User user) throws NoSuchUserExistsException, DeleteUserWithAccountsException{
		if(user.getNumBankAccounts() > 0)
			throw new DeleteUserWithAccountsException();
		users.deleteUserTransactions(user.getId());
		users.deleteUserByUsername(user.getUsername());
		return null;
	}
	
	/**
	 * Delete the specified Bank Account name from the User.  <br>
	 * Fails if the specified bank account doesn't exist or there still exists money.
	 * @throws NoSuchBankExistsException should NEVER be thrown in this method.
	 * @throws DeletingBankAccountWithMoneyException should be thrown if the User attempts to delete the specified account with money still on it.
	 * 
	 */
	public User deleteBankAccount(User user, String accountName) throws NoSuchBankExistsException, DeletingBankAccountWithMoneyException {
		// First, retrieve the Account from the user
		Account account = user.getAccount(accountName);
		
		// Then, check if their account is 0.  If so, delete it.  Otherwise, throw an exception stating that there is still money on the account
		if(account.getBalance() > 0)
			throw new DeletingBankAccountWithMoneyException(account.getName());
		
		// If the delete operation was successful, then also get rid of it from the User object
		if(users.deleteBankAccount(user.getId(), account.getName())) {
			user.addTransaction(new Transaction(user.getId(), "Deleted the " + accountName + " Bank Account"));
			user.deleteAccount(account.getName());
		}
		
		return user;
	}

	/**
	 * This method is already written fully in UserDaoImpl, via getUserByLoginInformation(username, password). <br>
	 * It simply acts as a container for that function.
	 * @param username : the input username
	 * @param password : the input password
	 * @return User: either return the User with the same username and password as the input or return null.
	 */
	@Override
	public User login(String username, String password) throws InvalidLoginException{
		// Simply return the result of getUserByLoginInformation
		return users.getUserByLoginInformation(username, password);
	}
	
	/**
	 * Method to retrieve the Cipher key to store into key.properties for future use of this program. 
	 * @return Int: return the key
	 */
	public int getKey() {
		return users.getKey();
	}
	
}
