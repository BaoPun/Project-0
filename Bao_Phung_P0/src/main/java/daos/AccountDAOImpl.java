package daos;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entities.Account;
import entities.User;
import util.DBConnection;

public class AccountDAOImpl implements AccountDAO{
	
	// Create
	/**
	 * Given a user id and a new Account (only caring about the name and the balance), <br>
	 * Add it to the database.
	 * @param id : the user id
	 * @param Account : the Account object
	 * @return Boolean: True if we successfully added the Account to the database, False otherwise
	 */
	public boolean createAccount(int id, Account account) {
		// Add a new entry onto the Accounts table with the following values:
		// Account name, Account balance, User id.
		try {
			// Call the INSERT INTO statement and set the 3 fields appropriately
			String SQLStatement = "INSERT INTO Accounts VALUES (bank_account_id_generator.nextval, ?, ?, ?)";
			CallableStatement cs = DBConnection.getConnection().prepareCall(SQLStatement);
			cs.setString(1, account.getName());
			cs.setDouble(2, account.getBalance());
			cs.setInt(3, id);
			
			// After setting the fields, execute and then close the statement.
			cs.execute();
			cs.close();
						
			// Finally, return true
			return true;
		}
		catch(SQLException e) {
			System.out.println("Error, something happened while attemping to insert");
			System.out.println(e.getMessage());
			
			// IF we get into this catch block, return false
			return false;
		}
	}
	
	// Read/get
	/**
	 * Get all queried results onto a list.
	 * @param user : the inputted user.
	 * @throws SQLException if the User has no Accounts left.
	 * @return List<Account>: return a new List of Accounts.
	 */
	public List<Account> getAllAccounts(int id){
		
		List<Account> accountsList = new ArrayList<Account>();
		
		try {
			// First, query for all Accounts that belong to the User.
			String SQLStatement = "SELECT A.a_id, A.account_name, A.balance FROM Accounts A INNER JOIN Users U on A.u_id = U.u_id AND A.u_id = ?";
			PreparedStatement ps = DBConnection.getConnection().prepareStatement(SQLStatement);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			// Second, place every single Account onto the List
			while(rs.next()) 
				accountsList.add(new Account(rs.getInt(1), rs.getString(2), rs.getDouble(3)));
			
			// Safely close the statements.
			rs.close();
			ps.close();
		}
		catch(SQLException e) {
		}
		
		// No matter what happens with the query, return the list.
		return accountsList;
	}
	
	/**
	 * Retrieve a single user given the user id and the name of the account.
	 * @throws SQLException if the query fails (i.e there exists no user with the given id and account name).
	 * @return Account: the account associated with the user id and the account name.
	 */
	public Account getAccount(int id, String accountName) {
		try {
			// First, query for the specific Account by specifying the user id and the account name
			String SQLStatement = "SELECT * FROM Accounts WHERE u_id = ? AND LOWER(account_name) = ?";
			PreparedStatement ps = DBConnection.getConnection().prepareStatement(SQLStatement);
			ps.setInt(1, id);
			ps.setString(2, accountName.toLowerCase());
			ResultSet rs = ps.executeQuery();
			
			// If there is a result, then return a brand new Account
			if(rs.next())
				return new Account(id, accountName, rs.getInt(3));
			
		}
		catch(SQLException e) {
			System.out.println("Error, something happened with the query\n" + e.getMessage());
		}
		return null;
	}
	
	// Update
	/**
	 * Simply update the given Account with the appropriate user id.
	 */
	public void updateAccount(int id, Account account) {
		try {
			// Call the update query
			String SQLStatement = "UPDATE Accounts SET balance = ? WHERE u_id = ? AND LOWER(account_name) = ?";
			CallableStatement cs = DBConnection.getConnection().prepareCall(SQLStatement);
			cs.setDouble(1, account.getBalance());
			cs.setInt(2, id);
			cs.setString(3, account.getName().toLowerCase());
			
			// And execute it
			cs.execute();
			cs.close();
			
		}
		catch(SQLException e) {
			System.out.println("Error, something went wrong with the UPDATE query");
			System.out.println(e.getMessage());
		}
	}
	
	// Delete
	/**
	 * Delete a bank account given the Account name and the user id. <br>
	 * This assumes that the Account already has a balance of 0.
	 */
	public boolean deleteAccount(int id, String accountName) {
		try {
			// First, call the DELETE statement.
			String SQLStatement = "DELETE Accounts WHERE u_id = ? AND account_name = ?";
			CallableStatement cs = DBConnection.getConnection().prepareCall(SQLStatement);
			cs.setInt(1, id);
			cs.setString(2, accountName);
			
			// Execute and close
			cs.execute();
			cs.close();
			
			// Assuming the execution was successful, return True
			return true;
		}
		catch(SQLException e) {
			System.out.println("Error, something happened with the query\n" + e.getMessage());
			return false;
		}
	}
}
