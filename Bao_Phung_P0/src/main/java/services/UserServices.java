package services;

import entities.Account;
import entities.User;
import exceptions.DeleteUserWithAccountsException;
import exceptions.DeletingBankAccountWithMoneyException;
import exceptions.InvalidLoginException;
import exceptions.InvalidPasswordException;
import exceptions.InvalidWithdrawException;
import exceptions.NoSuchBankExistsException;
import exceptions.NoSuchUserExistsException;
import exceptions.SameUsernameException;

/**
 * Interface that declares various actions a logged in User can take.
 * @author baoph
 *
 */
public interface UserServices {
	// Methods to retrieve a specific user (or related info)
	User getUserByUsername(String username) throws NoSuchUserExistsException;
	
	// Methods to work with each user's bank accounts
	void viewAccounts(User user);
	void viewTransactions(User user);
	User registerBankAccount(User user, Account account) throws SameUsernameException;
	User deposit(User user, String accountName, double balance) throws NoSuchBankExistsException;
	User withdraw(User user, String accountName, double balance) throws NoSuchBankExistsException, InvalidWithdrawException;
	
	// Method to register an account
	boolean registerUser(User user) throws InvalidPasswordException;
	
	// Method to get rid of a User or their bank accounts
	User deleteUser(User user) throws NoSuchUserExistsException, DeleteUserWithAccountsException;
	User deleteBankAccount(User user, String accountName) throws NoSuchBankExistsException, DeletingBankAccountWithMoneyException;
	
	// Method to log in given a username and password
	User login(String username, String password) throws InvalidLoginException;
				
	// Method to retrieve the cipher key for future uses in this project.
	int getKey();
}
