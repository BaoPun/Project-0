package driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

import ciphers.PasswordMasker;
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
import services.UserServices;
import services.UserServicesImpl;
import util.DBConnection;

/**
 * The class that will run the entire banking system.
 * @author baoph
 */
public class BankApplication {
	
	// Private variables
	private static Scanner scanner = new Scanner(System.in);
	private static User userLoggedIn = null;							
	private static UserServices services = new UserServicesImpl(DBConnection.getKeyFromFile());
	
	/**
	 * Main function, all console output will be displayed here.
	 * @param args : command line parameters (completely useless for the sake of this project)
	 */
	public static void main(String[] args) {
		
		// Prematurely exit the program if the connection to the database has failed.
		if(!DBConnection.isConnected())
			System.exit(1);
		
		// Print hello message to being the program
		System.out.println("Welcome to my personal Bank Application.");
		
		// Preload the superuser account onto the database 
		try {
			services.registerUser(new User("Bao Phung", "Nindoge", "pass3word12", true));
		} 
		catch(InvalidPasswordException e) {
			// This will never happen :) 
			System.out.println("Something tragic happened, the program will now exit prematurely.");
			System.exit(1);
		}
		
		// Display the menu in an infinite loop; this is where the majority of the program will be run
		boolean continueProgram = true;
		while(continueProgram)
			continueProgram = displayMenu();
		
		// This message is a confirmation that the program is about to end
		System.out.println("Thanks for using my bank application, good bye!");
		
		// Make sure to close the scanner before the program ends!
		scanner.close();	
		
		// Right before closing the program, update the key in key.properties
		try {
			FileWriter writeNewKey = new FileWriter("src/main/resources/key.properties");
			writeNewKey.write("key=" + Integer.toString(services.getKey()));
			writeNewKey.close();
		} 
		catch (IOException e) {
			System.out.println("Error, failed to write onto key.properties");
		}
		
	}
	
	/**
	 * A main menu, that is dependent on whether or not we are logged in. <br>
	 * If we are not logged in, we will be prompted to log in. <br>
	 * Otherwise, a menu displaying various options will be shown instead. 
	 * @return Boolean: false if we choose to quit the program while not logged in, true otherwise.
	 */
	public static boolean displayMenu() {
		int choice = Integer.MIN_VALUE;		
		
		// If no user is logged in
		if(userLoggedIn == null) {	
			// Create and view the menu
			System.out.println("\nYou are not logged in.  You have a few options below.");
			System.out.print("1. Log in\n2. Create an account\n3. Quit\nChoose an option above: ");
			String strChoice;
			boolean validInput = false;
			while(!validInput) {
				try {
					strChoice = scanner.next();
					choice = Integer.parseInt(strChoice); // if not successful, it will throw NumberFormatException
					validInput = true;
				}
				catch(NumberFormatException e) {
					System.out.print("Choose an option above: ");
				}
			}
		
			// Cycle through each option
			// Log in as user (could log in as either regular or super)
			if(choice == 1) {
				try {
					userLoggedIn = login();
				} 
				catch (InvalidLoginException e) {
					System.out.println(e.getMessage());
				}
			}
			
			// Register a new User account
			else if(choice == 2) {
				try {
					registerUser();
				}
				catch(InvalidPasswordException e) {
					System.out.println(e.getMessage());
				}
			}
			
			// Quit the program!
			else if(choice == 3)
				return false;
			
			// An invalid choice means that we have to reselect an option 
			else 
				System.out.println("Error, please choose a valid option\n");
		}
		
		// Either a regular user or a super user
		else {
			
			// Super user (Only 1)
			if(userLoggedIn.getSuperUserStatus()) {
				choice = getUserInputOnMenu();
				
				// Cycle through each option
				// View a specific user
				if(choice == 1) {
					try {
						System.out.print("Enter a username to view their balances: ");
						String username = scanner.next();
						User retrievedUser = services.getUserByUsername(username);
						services.viewAccounts(retrievedUser);
					}
					catch(NoSuchUserExistsException e) {
						System.out.println(e.getMessage());
					}
					catch(NullPointerException e) {
						System.out.println("Error, no user exists with that username");
					}
				}	
				// Create a new user
				else if(choice == 2) {
					try {
						registerUser();
					}
					catch(InvalidPasswordException e) {
						System.out.println(e.getMessage());
					}
				}
				// Update a specific user.
				// For now, the superuser is only permitted to alter bank accounts (deposit or withdraw)
				else if(choice == 3) {
					// Get the user to update.
					User localUser = getUserToUpdate("update");
					if(localUser == null)
						return true;
					if(localUser.getNumBankAccounts() == 0) {
						System.out.println("Error, you cannot deposit or withdraw from any accounts; you don't have any!");
						return true;
					}
					
					int innerChoice = Integer.MIN_VALUE;
					while(innerChoice != 3) {
						System.out.println("\nEditing [" + localUser.getUsername() + "]'s bank accounts, you are limited to the following options below:");
						System.out.print("1. Deposit\n2. Withdraw\n3. Quit\nChoose an option above: ");
						try{
							innerChoice = Integer.parseInt(scanner.next());
							
							if(innerChoice == 3) {
								System.out.println("No longer editing user [" + localUser.getUsername() + "], returning to the main menu");
								return true;
							}
							System.out.print("Enter account name to deposit/withdraw into: ");
							String accountName = scanner.next();
							double newBalance = 0;
							boolean isValidInput = false;
							while(!isValidInput) {
								try {
									System.out.print("Enter amount to deposit/withdraw from: ");
									newBalance = Double.parseDouble(scanner.next());
									isValidInput = true;
								}
								catch(NumberFormatException e) {
									
								}
							}
							if(innerChoice == 1) {
								try {
									localUser = services.deposit(localUser, accountName, newBalance);
								} catch (NoSuchBankExistsException e) {
									System.out.println(e.getMessage());
								} 
							}
							else if(innerChoice == 2) {
								try {
									localUser = services.withdraw(localUser, accountName, newBalance);
								} 
								catch (NoSuchBankExistsException e) {
									System.out.println(e.getMessage());
								} 
								catch (InvalidWithdrawException e) {
									System.out.println(e.getMessage());
								}
							}
							else
								System.out.println("Error, please choose a valid option");
						}
						catch(NumberFormatException e) {
							System.out.println("Please enter a valid option.\n");
						}
					}
				}
				// Delete a specific user (in order to delete a User, they must have 0 bank accounts)
				// Thus, if the superuser wants to delete a User with multiple bank accounts, they must delete the bank accounts first.
				else if(choice == 4) {
					// Get the user to delete.
					User localUser = getUserToUpdate("delete");
					
					// If the user to delete has no bank accounts and they are the superuser, then forcibly exit from this option
					if(localUser.getNumBankAccounts() == 0 && localUser.getSuperUserStatus()) {
						System.out.println("Error, you, the superuser, cannot be deleted.");
						return true;
					}
					else {
						
						// If the 
						if(localUser.getNumBankAccounts() > 0) {
							System.out.print("Enter a bank account name to delete from: ");
							String accountName = scanner.next();
							try {
								localUser = services.deleteBankAccount(localUser, accountName);
							} 
							catch(DeletingBankAccountWithMoneyException e) {
								System.out.println(e.getMessage());
							}
							catch (NoSuchBankExistsException e) {
								System.out.println(e.getMessage());
							}
						}
						else {
							// Get a single-character input (y or n) to determine if we want to delete the user account
							char input = ' ';
							System.out.println();
							
							// And error handle it.  No need for exceptions here
							while(input != 'y' && input != 'n') {
								System.out.print("Would you like to deactivate your account (y/n): ");
								input = Character.toLowerCase(scanner.next().charAt(0));
								if(input != 'y' && input != 'n')
									System.out.println("Please enter \'y\' or \'n\' for your answer.");
							}
							
							// If input is 'y', then delete the User from the database
							if(input == 'y') {
								try {
									localUser = services.deleteUser(localUser);
								} 
								catch (NoSuchUserExistsException e) {
									System.out.println(e.getMessage());
								} 
								catch (DeleteUserWithAccountsException e) {
									System.out.println(e.getMessage());
								}
							}
						}
					}
				}
				
				// Rest of the options are the regular user operations.
				else
					userOptionCycle(choice);
			}
			
			// Regular user
			else {
				
				choice = getUserInputOnMenu();
				
				// Cycle through each option as a regular user.
				userOptionCycle(choice);
			}
		}
		
		// Only 1 condition makes this function return false (if we are not logged in and we choose to exit the program)
		return true;
	}
	
	
	
	/**
	 * Display the menu of the logged in user. <br>
	 * However, the menu differs depending on whether or not the User is a superuser.
	 * @return int: the valid input based on one of the menus.
	 */
	public static int getUserInputOnMenu() {
		int choice = Integer.MIN_VALUE;
		boolean validInput = false;
		
		// View either the superuser menu or the regular menu.
		if(userLoggedIn.getSuperUserStatus()) {
			// View the superuser menu
			System.out.println("\nWelcome superuser " + userLoggedIn.getUsername() + ".  You have a few options below.");
			System.out.println("01. View specific user\n02. Create new user\n03. Update specific user\n04. Delete user");
			System.out.println("05. View all accounts\n06. View recent transactions\n07. Create an account\n08. Delete an account");
			System.out.println("09. Deposit to an account\n10. Withdraw to an account\n11. Log out");
		}
		else {
			// View the regular user menu
			System.out.println("\nWelcome " + userLoggedIn.getUsername() + ".  You have a few options below.");
			System.out.println("1. View all accounts\n2. View recent transactions\n3. Create an account\n4. Delete an account");
			System.out.println("5. Deposit to an account\n6. Withdraw to an account\n7. Log out");
		}
		
		// After viewing the menu, ask the user to input an option. 
		// Continuously get input while an Integer input was not provided.
		System.out.print("Please choose an option above: ");
		while(!validInput) {
			try {
				choice = Integer.parseInt(scanner.next());
				validInput = true;
			}
			catch(NumberFormatException e) {
				System.out.print("Please choose a valid option above: ");
			}
		}
		
		// Afterwards, return the choice.
		return choice;
	}
	
	/**
	 * From the superuser menu, if we choose to update a specific user, retrive their username and then query by it.
	 * @param update : affects how the prompt will look like.  Only used as "update" or "delete".
	 * @return User: the User that was found after inputting a username.
	 */
	public static User getUserToUpdate(String update) {
		User localUser = null;
		System.out.print("Enter a username to " + update + " accounts from: ");
		String username = scanner.next();
		
		// If we are trying to update the superuser itself, then don't allow this to happen.
		if(username.toLowerCase().equals("nindoge")) {
			System.out.println("If you want to edit your own information, please do not choose this option.");
			return null;
		}
		
		try {
			localUser = services.getUserByUsername(username);
			return localUser;
		} 
		catch (NoSuchUserExistsException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	/**
	 * Perform all of the regular user operations: view accounts, create new account, delete account, deposit, withdraw, logout. <br>
	 * The conditions will be slightly different depending on whether or not the user is a superuser.
	 * @param choice : the inputted choice from the main menu.
	 */
	public static void userOptionCycle(int choice) {
		// View all of the current logged in user's accounts
		if((choice == 1 && !userLoggedIn.getSuperUserStatus()) || (choice == 5 && userLoggedIn.getSuperUserStatus()))
			services.viewAccounts(userLoggedIn);
		// View recent transactions
		else if((choice == 2 && !userLoggedIn.getSuperUserStatus()) || (choice == 6 && userLoggedIn.getSuperUserStatus())) {
			System.out.println("\nViewing the 10 latest transactions");
			services.viewTransactions(userLoggedIn);
		}
		// Create a new bank account for the current logged in user
		else if((choice == 3 && !userLoggedIn.getSuperUserStatus()) || (choice == 7 && userLoggedIn.getSuperUserStatus())) {
			try {
				registerBankAccount();
			} 
			catch (SameUsernameException e) {
				System.out.println(e.getMessage());
			} 
		}
		// Delete an existing bank account for the current logged in user
		else if((choice == 4 && !userLoggedIn.getSuperUserStatus()) || (choice == 8 && userLoggedIn.getSuperUserStatus())) {
			try {
				deleteAccount();
			} 
			catch (NoSuchUserExistsException e) {
				System.out.println(e.getMessage());
			} 
			catch (DeleteUserWithAccountsException e) {
				System.out.println(e.getMessage());
			} 
			catch (NoSuchBankExistsException e) {
				System.out.println(e.getMessage());
			} 
			catch (DeletingBankAccountWithMoneyException e) {
				System.out.println(e.getMessage());
			}
		}
		// Deposit onto a certain bank account for the current logged in user
		else if((choice == 5 && !userLoggedIn.getSuperUserStatus()) || (choice == 9 && userLoggedIn.getSuperUserStatus())) {
			try {
				updateSpecificAccountBalance(0);
			} 
			catch (NoSuchBankExistsException e) {
				System.out.println(e.getMessage());
			} 
			catch (InvalidWithdrawException e) {
				System.out.println(e.getMessage());
			}
		}
		// Withdraw from a certain bank account fro the current logged in user
		else if((choice == 6 && !userLoggedIn.getSuperUserStatus()) || (choice == 10 && userLoggedIn.getSuperUserStatus())) {
			try {
				updateSpecificAccountBalance(1);
			} 
			catch (NoSuchBankExistsException e) {
				System.out.println(e.getMessage());
			} 
			catch (InvalidWithdrawException e) {
				System.out.println(e.getMessage());
			}
		}
		
		// Log out
		else if((choice == 7 && !userLoggedIn.getSuperUserStatus()) || (choice == 11 && userLoggedIn.getSuperUserStatus())) {
			System.out.println("Thanks for using this program, " + userLoggedIn.getUsername());
			userLoggedIn = null;
		}
		// Invalid choice, reselect a new option.
		else
			System.out.println("Error, please choose a valid option\n");
	}
	
	/**
	 * Input a username and password, and attempt to login with those credentials
	 * @return a new User object if login credentials were successful, throw a SameUsernameException otherwise
	 * @throws NoSuchUserExistsException is thrown if no username and no password with the input was found
	 */
	public static User login() throws InvalidLoginException{
		// Input both the username and password
		String username, password;
		System.out.print("\nUsername: ");
		scanner.nextLine();
		username = scanner.nextLine();
		
		System.out.print("Password: ");
		password = scanner.next();
		
		return services.login(username, password);
	}
	
	/**
	 * A method to register a User onto our database. <br>
	 * 3 inputs are required (not as parameters, but as local variables): the full name of the user, the username, and the password. 
	 * @throws SameUsernameException if the input username already exists in the database.
	 * @throws InvalidPasswordException if the input password fails @ least 1 condition: at least 1 non-alphanumeric character, length of password < 8
	 */
	public static void registerUser() throws InvalidPasswordException{
		// Get inputs for the user's name, username, and password.
		String fullName, username, password;
		System.out.print("\nFull name: ");
		scanner.nextLine();
		fullName = scanner.nextLine();
		
		System.out.print("Username: ");
		username = scanner.nextLine();
		
		System.out.print("Password (only alpha-numeric characters allowed, length >= 8): ");
		password = scanner.next();
		
		// After getting the User's full name, username, and password, attempt to register the User.  If at least one field fails, then registration will fail.
		services.registerUser(new User(fullName, username, password));
	}
	
	/**
	 * A method to add a new bank account to the current User. <br>
	 * No parameters, because input is gathered within the method. <br>
	 * This method will also update userLoggedIn - the user that is currently logged into the Banking System. 
	 * @throws SameUsernameException if the user attempts to add a banking account name that the User already has (also ignoring case sensitivity)
	 * @throws NoSuchUserExistsException should never be thrown, but is implemented via registerBankAccount.
	 */
	public static void registerBankAccount() throws SameUsernameException{
		// Get inputs for the account name and initial balance
		String accountName;
		double initialBalance = Integer.MIN_VALUE;
		System.out.print("\nAccount name: ");
		accountName = scanner.next();
		
		// Continuously error handle invalid balance inputs
		while(initialBalance == Integer.MIN_VALUE) {
			try {
				System.out.print("Initial balance: $");
				initialBalance = Double.parseDouble(scanner.next());
			}
			catch(NumberFormatException e) {
				System.out.println("Error, please input a valid balance");
			}
		}
		
		// If the balance input is less than 0, then force the balance to be 0
		if(initialBalance < 0)
			initialBalance = 0;
		
		// Attempt to add a new Bank Account to the User
		userLoggedIn = services.registerBankAccount(userLoggedIn, new Account(accountName, initialBalance));
	}
	
	/**
	 * A method to deposit/withdraw a certain amount of money to a specific account. <br>
	 * No parameters, because input is gathered within the method. <br>
	 * This method will also update userLoggedIn - the user that is currently logged into the Banking System. 
	 * @param flag : either 0 (deposit) or 1 (withdraw)
	 * @throws NoSuchUserExistsException should NEVER be thrown in this method.  
	 * @throws SameUsernameException should NEVER be thrown in this method.
	 * @throws NoSuchBankExistsException is thrown if the User doesn't have a Bank Account of the inputted AccountName.
	 */
	public static void updateSpecificAccountBalance(int flag) throws NoSuchBankExistsException, InvalidWithdrawException {
		// Get inputs for the account name and amount to deposit
		String accountName;
		double initialBalance = Integer.MIN_VALUE;
		System.out.print("\nAccount name to deposit/withdraw into: ");
		accountName = scanner.next();
		
		// Continuously error handle invalid balance inputs
		while(initialBalance <= 0) {
			try {
				System.out.print("Balance to deposit/withdraw: $");
				initialBalance = Double.parseDouble(scanner.next());
			}
			catch(NumberFormatException e) {
				System.out.println("Error, please input a valid balance");
			}
		}
		
		// Finally, either deposit or withdraw, depending on the flag
		if(flag == 0)
			userLoggedIn = services.deposit(userLoggedIn, accountName, initialBalance);
		else if(flag == 1)
			userLoggedIn = services.withdraw(userLoggedIn, accountName, initialBalance);
		else
			throw new IllegalArgumentException("Error, invalid flag.  Provide either 0 to deposit or 1 to withdraw for the argument.");
	}
	
	/**
	 * Delete a bank account from the User (or delete the User account itself if there are no more bank accounts). <br>
	 * No parameters, because input is gathered within the method. 
	 * @throws DeleteUserWithAccountsException should never be thrown by this method.
	 * @throws NoSuchUserExistsException should never be thrown by this method.
	 * @throws DeletingBankAccountWithMoneyException should be thrown if the Account with the inputted account name has money.
	 * @throws NoSuchBankExistsException should be thrown if the User doesn't have an account with the inputted account name.
	 */
	public static void deleteAccount() throws NoSuchUserExistsException, DeleteUserWithAccountsException, NoSuchBankExistsException, DeletingBankAccountWithMoneyException {
		// First, check if the user has at least 1 Bank Account
		// If so, prompt them for a bank account to delete.
		if(userLoggedIn.getNumBankAccounts() > 0) {
			System.out.print("\nEnter a bank account name to delete from: ");
			String accountName = scanner.next();
			userLoggedIn = services.deleteBankAccount(userLoggedIn, accountName);
		}
		
		// Otherwise, prompt the user to delete their entire account
		else {
			// If this is logged in user is the Superuser, then prematurely quit.
			if(userLoggedIn.getSuperUserStatus()) {
				System.out.println("Error, this one and only Superuser account cannot be deleted from the database.");
				return;
			}
			
			// Get a single-character input (y or n) to determine if we want to delete the user account
			char input = ' ';
			System.out.println();
			
			// And error handle it.  No need for exceptions here
			while(input != 'y' && input != 'n') {
				System.out.print("Would you like to deactivate your account (y/n): ");
				input = Character.toLowerCase(scanner.next().charAt(0));
				if(input != 'y' && input != 'n')
					System.out.println("Please enter \'y\' or \'n\' for your answer.");
			}
			
			// If input is 'y', then delete the User from the database and then log out.  Otherwise, don't do anything
			if(input == 'y') 
				userLoggedIn = services.deleteUser(userLoggedIn);
		}
	}
}
