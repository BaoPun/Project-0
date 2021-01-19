package daos;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import entities.Account;
import entities.User;
import exceptions.InvalidLoginException;
import exceptions.NoSuchUserExistsException;
import exceptions.SameUsernameException;

/**
 * Declare all CRUD operations in this interface. <br>
 * C = Create, R = Read, U = Update, D = Delete.
 * @author baoph
 */
public interface UserDAO {
	// Create
	boolean createUser(User user);
	boolean createBankAccount(int id, Account account);
	
	// Read
	User getUserByUsername(String username) throws NoSuchUserExistsException;
	User getUserByLoginInformation(String username, String password) throws InvalidLoginException;
	List<Account> getAllUserBankAccounts(User user);
	Account getAccount(int id, String accountName);
	int getKey();
	
	// Update
	void updateBankAccount(int id, Account account);
	void updateUserTransactions(int id, String accountName, int mode, double balance);
	
	// Delete
	void deleteUserTransactions(int id);
	void deleteUserByUsername(String username) throws NoSuchUserExistsException;
	boolean deleteBankAccount(int id, String accountName);
	
	
}
