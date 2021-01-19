package entities;

public class Transaction {
	// Private fields/variables
	private int id;
	private String message;
	
	// Constructors
	/**
	 * Default constructor that presets the id to -1 and the message to ""
	 */
	public Transaction() {
		this.id = -1;
		this.message = "";
	}
	
	/**
	 * 1-parameter constructor that sets the message and presets the id
	 * @param message : the message to set
	 */
	public Transaction(String message) {
		this.id = -1;
		this.message = message;
	}
	
	/**
	 * 2-parameter constructor that sets the id and message
	 * @param id : the unique transaction id to set
	 * @param message : the message to set
	 */
	public Transaction(int id, String message) {
		this.id = id;
		this.message = message;
	}
	
	// Getters
	/**
	 * Simply return the unique id of the transaction
	 * @return the unique id
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Simply return the messsage of the transaction
	 * @return the message
	 */
	public String getMessage() {
		return this.message;
	}
	
	// Setters
	/**
	 * Change the message to whatever is in the parameter
	 * @param message : the new message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
}
