package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {
	
	
	private static Connection database = connectToDatabase();
	private static int key = -1;
	
	/**
	 * A specific method to return the key, but not before reading from key.properties.
	 * @return Int: the key
	 */
	public static int getKeyFromFile() {
		retrieveKey();
		return key;
	}
	
	/**
	 * Determine if we are connected to the database, i.e the object associated with the Connection is NOT NULL.  
	 * @return Boolean : True if the Connection object successfully references the database being connected, False otherwise
	 */
	public static boolean isConnected() {
		return (database != null);	
	}
	
	/**
	 * Attempt to connect to the database using the credentials provided in a hidden file. <br>
	 * If the Connection object remains NULL after this call, then the entire application will exit prematurely.
	 * @return Connection : a Connection object that references the connected database
	 */
	public static Connection connectToDatabase() {
		
		// Require 3 pieces of info: url, username, password
		String url = "jdbc:oracle:thin:@", username = "", password = "";
		
		// Normally, specifying the type of exception would be best.
		// However, in this case, this would result in 3 different try/catch blocks nested within another try/catch block.
		// Therefore, this will be simplified down to the base Exception object.
		try {
			
			// First thing to do, force a check onto Oracle Drivers
			// Oracle drivers had a problem when establishing a connection
			// To fix this, they added a "hotfix" to ensure that ojdbc drivers would correctly load at the beginning of program.
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			// Afterwards, read from a file using a composition of FileReader and BufferedReader
			/*
			FileReader fileReader = new FileReader(new File("src/main/resources/info.properties"));
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			// The information from info.properties is very simple: it just contains the url, username, and password all in their own separate lines.
			url = url.concat(bufferedReader.readLine() + ":1521:ORCL");
			username = bufferedReader.readLine();
			password = bufferedReader.readLine();
			
			// Close the BufferedReader afterwards!  No need to close the FileReader because the BufferedReader also closes the FileReader.
			bufferedReader.close();
			*/
			Properties p = new Properties();
			FileInputStream input = new FileInputStream(DBConnection.class.getClassLoader().getResource("info.properties").getFile());
			p.load(input);
			url = url.concat(p.getProperty("url") + ":1521:ORCL");
			username = p.getProperty("username");
			password = p.getProperty("password");
			input.close();
			
			// Finally, attempt to connect to the database!
			database = DriverManager.getConnection(url, username, password);
		}
		catch(Exception e) {
			System.out.println("Error, something wrong happened when trying to connect to the database.  The banking application will now exit.");
			return null;
		}
		
		// Assuming the try block executed successfully, return the Connection object!  
		return database;
	}
	
	/**
	 * Under the hood, retrieve the cipher key.
	 */
	private static int retrieveKey() {
		// Additionally, open a 2nd properties file to retrieve the key used for ciphering.
		try {
			Properties p = new Properties();
			FileInputStream input = new FileInputStream(DBConnection.class.getClassLoader().getResource("key.properties").getFile());
			p.load(input);
			if(p.getProperty("key") != null)
				key = Integer.parseInt(p.getProperty("key"));
			else
				return -1;
			input.close();
			return key;
		}
		catch(FileNotFoundException e) {
			//System.out.println("Error, the key.properties file could not be found");
		}
		catch(IOException e) {
			//System.out.println("Error, something's wrong with the BufferedReader: " + e.getMessage());
		}
		catch(NumberFormatException e) {
			
		}
		return -1;
	}
	
	/**
	 * A getter to retrieve the Connection, assuming that it was successful.
	 * @return Connection: the connection made to the database.
	 */
	public static Connection getConnection() {
		return database;
	}
}
