package daos;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.List;

import ciphers.PasswordMasker;
import entities.Account;
import entities.User;
import exceptions.InvalidLoginException;
import exceptions.NoSuchUserExistsException;
import exceptions.SameUsernameException;
import util.DBConnection;

/**
 * Implement the DAO's of each user. <br>
 * @author baoph
 *
 */
public class UserDAOImpl implements UserDAO{
	
	// Only 1 of a passwork masker and 1 of an Account Table.
	private static PasswordMasker masker;
	private static AccountDAO accounts;
	
	// For the bonus, also 1 of a Transaction Table.
	private static TransactionDAO transactions;
	
	
	/**
	 * Creates a single instance of the DAO implementation of Users.
	 */
	public UserDAOImpl() {
		masker = new PasswordMasker();
		accounts = new AccountDAOImpl();
		transactions = new TransactionDAOImpl();
	}
	
	public UserDAOImpl(int key) {
		masker = new PasswordMasker(key);
		accounts = new AccountDAOImpl();
		transactions = new TransactionDAOImpl();
	}
	
	// Create
	/**
	 * Adds a single User to our database.
	 * @param user : a single user
	 * @return none: either the operation was successful or throw a SameUsernameException (non-unique username found)
	 */
	public boolean createUser(User user){
		try {
			// First, call the add_user procedure. 
			// It takes in 4 arguments: the user's full name, the user's username, the user's password, and 0 (by default)
			// This will fail if the username is NOT unique in the database.
			String callAddUserProcedure = "CALL add_user(?, ?, ?, ?)";
			CallableStatement cs = DBConnection.getConnection().prepareCall(callAddUserProcedure);	
			cs.setString(1, user.getFullName());
			cs.setString(2,  user.getUsername());
			cs.setString(3,  masker.encode(user.getPassword()));
			
			// Change from Boolean (True/False) to Int (1/0)
			if(user.getSuperUserStatus())
				cs.setInt(4, 1);
			else
				cs.setInt(4, 0);
			
			// Execute the procedure
			cs.execute();
			
			// And close out of it afterwards
			cs.close();
			
			// Make sure to display a success message
			if(!user.getSuperUserStatus())
				System.out.println("Success, you are now registered onto the Bank Application.  Try logging in now.");
			
			return true;
		}
		catch(SQLException e) {
			// To prevent the beginning of the program displaying an error (adding the Superuser very early on)
			// Only display the error message if the user is NOT a superuser.
			if(!user.getSuperUserStatus())
				System.out.println("Error, something happened during the query: " + e.getMessage());
		}
		return false;
	}
	
	/**
	 * Attempt to create a bank account using the User's id and the Account object.
	 * @param id : the user id
	 * @param account : the Account object that contains the desired account name and balance
	 * @return Boolean: True if the creation of a bank object is successful, False otherwise.
	 */
	public boolean createBankAccount(int id, Account account) {
		if(transactions.createTransaction(id, account.getName()))
			return accounts.createAccount(id, account);
		return false;
	}
	
	// Read
	/**
	 * Retrieve a specific User given a username.
	 * @param username : the username to search for
	 * @return User: either return the found user or throw a NoSuchUserExistsException (self explanatory)
	 */
	public User getUserByUsername(String username) throws NoSuchUserExistsException{
		try {
			// Prepare a simple Select statement, and make sure to ignore case sensitivity.
			String SQLStatement = "SELECT * FROM Users WHERE LOWER(username) = ?";
			PreparedStatement ps = DBConnection.getConnection().prepareStatement(SQLStatement);
			ps.setString(1, username.toLowerCase());
			ResultSet rs = ps.executeQuery();
			
			if(rs.next()) {
				// For the boolean field (isSuperUser), make it 1 or 0, depending on what the returned parameter is.
				// How to read (condition ? do this : do that) => if the condition holds true, return "do this"; otherwise, return "do that".
				boolean isSuperUser = (rs.getInt(5) == 1 ? true : false);
				
				// Then, create a new User based on the queried information.
				User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), isSuperUser);
				
				// Don't return the user yet!  Update the user's list of accounts
				user.setNewAccounts(accounts.getAllAccounts(user.getId()));
				
				// Now return the user
				return user;
			}
			else
				throw new NoSuchUserExistsException(username);
			
		}
		catch(SQLException e) {
			System.out.println("Error, there was a problem executing this query: " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Given a username and password as inputs, return the User that has the same username (case insensitive) AND password as the input. <br>
	 * For the password field, note that the User's password is stored in encryption form.
	 * @param username : the input username
	 * @param password : the input password
	 * @return User: either return the User with the same username and password as the input or return null.
	 * @throws NoSuchUserExistsException  
	 */
	public User getUserByLoginInformation(String username, String password) throws InvalidLoginException{
		try {
			// Attempt to retrieve the query by login information
			String SQLStatement = "SELECT * FROM Users WHERE lower(username) = ? AND password = ?";
			PreparedStatement ps = DBConnection.getConnection().prepareStatement(SQLStatement);
			String lowerUsername = username.toLowerCase(), passwordEncoded = masker.encode(password);
			ps.setString(1, lowerUsername);
			ps.setString(2, passwordEncoded);
						
			ResultSet rs = ps.executeQuery();
			
			// If there exists at least 1 ResultSet, then check if that result set's login and password match our input.
			if(rs.next()) {
				// In addition to checking the two conditions, check if there is another ResultSet entry.
				// If so, then something is wrong with our DB.
				if(!lowerUsername.equals(rs.getString(3)) && !passwordEncoded.equals(rs.getString(4)) && rs.next())
					throw new InvalidLoginException();
			}
			
			// For the boolean field (isSuperUser), make it 1 or 0, depending on what the returned parameter is.
			// How to read (condition ? do this : do that) => if the condition holds true, return "do this"; otherwise, return "do that".
			boolean isSuperUser = (rs.getInt(5) == 1 ? true : false);
			
			// Don't return the user yet: first create a new User with the 5 fields
			User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), isSuperUser);
			
			// Next, update the User's list of accounts
			user.setNewAccounts(accounts.getAllAccounts(user.getId()));
			
			// Also update their list of Transactions
			user.setNewTransactions(transactions.getRecentTransactions(user.getId())); 
			
			// Safely close the statements
			ps.close();
			rs.close();
			
			// Finally, return the user
			return user;
		}
		catch(SQLException e) {
			throw new InvalidLoginException();
		}
	}
	
	/**
	 * Retrieve all of the User's bank accounts.
	 * @return List<Account>: the list of bank accounts that the User has in their database.
	 */
	public List<Account> getAllUserBankAccounts(User user){
		return accounts.getAllAccounts(user.getId());
	}
	
	/**
	 * Simply return the number of users.
	 * @return number of users.
	 */
	public int getNumUsers() {
		//return this.users.size();
		return 0;
	}
	
	/**
	 * Return the cipher key used for encryption.
	 * @return key
	 */
	public int getKey() {
		return masker.getKey();
	}
	
	/**
	 * Simply call the AccountDAO's version of getAccount() and return it.
	 * @return Account: the Account retrieved from a query via the id and the account name.
	 */
	public Account getAccount(int id, String accountName) {
		return accounts.getAccount(id, accountName);
	}
	
	
	// Update
	
	/**
	 * Update the given account with the specific id.
	 * @param id : the user id
	 * @param account : the Account object containing the new balance
	 */
	public void updateBankAccount(int id, Account account) {
		accounts.updateAccount(id, account);
	}
	
	/**
	 * Add a new transaction for deposit/withdraw (mode) detailing the change in Account balance
	 * @param id : the user id
	 * @param accountName : the account name that is being despoited/withdrawn from
	 * @param mode : 0 for deposit, 1 for withdraw
	 * @param balance : the balance that is either deposited or withdrawn
	 */
	public void updateUserTransactions(int id, String accountName, int mode, double balance) {
		transactions.updateTransaction(id, accountName, mode, balance);
	}
	
	
	// Delete
	/**
	 * Given a username to query by, delete a User in the database if their username matches with the input.
	 * @param username : the username to search for a User to delete
	 * @return none: either the operation was successful or throw a NoSuchUserExistsException
	 */
	public void deleteUserByUsername(String username) throws NoSuchUserExistsException{
		try {
			String SQLStatement = "DELETE Users WHERE username = ?";
			CallableStatement cs = DBConnection.getConnection().prepareCall(SQLStatement);
			cs.setString(1, username);
			cs.execute();
			cs.close();
		}
		catch(SQLException e) {
			System.out.println("Error, something happened with the query: " + e.getMessage());
		}
	}
	
	/**
	 * Before removing the User from the database, remove their associated Transactions. <br>
	 * This is already implemented in TransactionDAO
	 * @param id : the user id
	 */
	public void deleteUserTransactions(int id) {
		transactions.removeTransactions(id);
	}
	
	/**
	 * Given a user id and accountName, delete the banking account with those 2 parameters.
	 * @param id : the User's id
	 * @param accountName : the account name of the bank belonging to the User.
	 * @return Boolean: True if the operation was successful, False otherwise.
	 */
	public boolean deleteBankAccount(int id, String accountName) {
		if(transactions.deleteTransaction(id, accountName))
			return accounts.deleteAccount(id, accountName);
		return false;
	}
}
