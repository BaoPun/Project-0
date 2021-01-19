package daos;

import java.util.List;

import entities.Account;
import entities.User;

public interface AccountDAO {
	
	// Create
	boolean createAccount(int id, Account account);
	
	// Read/get
	List<Account> getAllAccounts(int id);
	Account getAccount(int id, String accountName);
	
	// Update
	void updateAccount(int id, Account account);
	
	// Delete
	boolean deleteAccount(int id, String accountName);
}
