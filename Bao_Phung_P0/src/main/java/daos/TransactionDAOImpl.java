package daos;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import entities.Transaction;
import util.DBConnection;

public class TransactionDAOImpl implements TransactionDAO{

	/**
	 * Create a new Transactions that deals with the creation of a new Bank account
	 * @param id : the user id that this transaction is associated with
	 * @param accountName : the name of the account that will be shown on the Transaction history
	 * @return Boolean: True if the transaction was successful, False otherwise
	 */
	@Override
	public boolean createTransaction(int id, String accountName) {
		try {
			String SQLStatement = "INSERT INTO Transactions VALUES (transaction_id_generator.nextval, ?, ?)";
			CallableStatement cs = DBConnection.getConnection().prepareCall(SQLStatement);
			cs.setString(1, "Created a new " + accountName + " Bank Account");
			cs.setInt(2, id);
			cs.execute();
			cs.close();
			return true;
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	/**
	 * Return the 10 most recent transactions from some user.
	 * @param id : the user id
	 * @return List<Transaction>: the new list of Transactions
	 */
	@Override
	public List<Transaction> getRecentTransactions(int id){
		List<Transaction> transactions = new ArrayList<Transaction>();
		try {
			// To get the 10 most recent transactions, sort the results of the transaction ids in descending order
			// And only limit the # of returned rows to be 10.
			String SQLStatement = "SELECT * FROM Transactions WHERE u_id = ? AND ROWNUM <= 10 ORDER BY t_id DESC";
			PreparedStatement ps = DBConnection.getConnection().prepareStatement(SQLStatement);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) 
				transactions.add(new Transaction(rs.getString(2)));
			
			// After retrieving all <= 10 transactions, return the list of transactions
			return transactions;
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	@Override
	public void updateTransaction(int id, String accountName, int mode, double balance) {
		try {
			String SQLStatement = "INSERT INTO Transactions VALUES (transaction_id_generator.nextval, ?, ?)";
			CallableStatement cs = DBConnection.getConnection().prepareCall(SQLStatement);
			if(mode == 0)
				cs.setString(1, "Deposited $" + (Math.round(balance)*100./100.) + " to the " + accountName + " Bank Account");
			else if(mode == 1)
				cs.setString(1, "Withdrawn $" + (Math.round(balance)*100./100.) + " from the " + accountName + " Bank Account");
			else
				throw new IllegalArgumentException("Error, invalid mode.  It must be 0 (deposit) or 1 (withdrawn)");
			cs.setInt(2, id);
			cs.execute();
			cs.close();
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		
	}

	/**
	 * Create a new transaction that deals with the deletion of a Bank account.
	 * @param id : the user id to query by
	 * @return Boolean: True if adding a new Transaction of a deleted Bank Account is successful, False otherwise
	 */
	@Override
	public boolean deleteTransaction(int id, String accountName) {
		try {
			String SQLStatement = "INSERT INTO Transactions VALUES (transaction_id_generator.nextval, ?, ?)";
			CallableStatement cs = DBConnection.getConnection().prepareCall(SQLStatement);
			cs.setString(1, "Deleted the " + accountName + " Bank Account");
			cs.setInt(2, id);
			cs.execute();
			cs.close();
			return true;
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}
	
	/**
	 * Remove all transactions containing the User id from the database.
	 * @param id : the user id
	 */
	@Override
	public void removeTransactions(int id) {
		try {
			String SQLStatement = "DELETE Transactions WHERE u_id = ?";
			CallableStatement cs = DBConnection.getConnection().prepareCall(SQLStatement);
			cs.setInt(1, id);
			cs.execute();
			cs.close();
		}
		catch(SQLException e) {
			System.out.println(e.getMessage());
		}
	}

}
