package daos;

import java.util.List;

import entities.Transaction;

public interface TransactionDAO {
	boolean createTransaction(int id, String accountName);
	
	List<Transaction> getRecentTransactions(int id);
	
	void updateTransaction(int id, String accountName, int mode, double balance);
	
	boolean deleteTransaction(int id, String accountName);
	void removeTransactions(int id);
}
