package ciphers;

/**
 * As an attempt to obscure the password as much as possible, there will be 2 classes that will encrypt/decrypt a given password.
 * This is phase 2 of encryption and phase 1 of decryption.
 * @author baoph
 *
 */
public class CipherPhase2 implements InterfaceCipher{
	
	/**
	 * Given a password as a String, return an encoded representation of it. <br>
	 * Details of what this does will be kept hidden.
	 * @param password : a password
	 * @return String: an encrypted version of the password
	 */
	public String encode(String password) {	
		
		// The length of the char[] is the length of the string + extra spaces required
		// Extra spaces = (length / 5) if length > 5, otherwise 0
		// However, if length % 5 == 0, then we have to subtract length / 5 by 1.
		int extraSpaces;
		if(password.length() > 5) {
			if(password.length() % 5 > 0)
				extraSpaces = password.length() / 5;
			else
				extraSpaces = password.length() / 5 - 1;
		}
		else
			extraSpaces = 0;
		
		// Store each character on char[]
		char[] newString = new char[password.length() + extraSpaces];
		
		// This newSpace variable determines if we add a space.  Only add a space once this variable hits 5
		// Once the variable hits 5, newSpace will be reset to 0
		int newSpace = 0;
		
		// i refers to the current index of our input string, j refers to the current index of our new string stored in char[]
		for(int i = 0, j = 0; i < password.length() && j < newString.length; i++, j++) {
			
			// Add a space if applicable: see newSpace description above
			if(newSpace % 5 == 0 && newSpace > 0) {
				newString[j] = ' ';
				
				// Reset newSpace to 0 and decrement i so that we remain on the same ith character of the input string after the next iteration of the for loop
				newSpace = 0;
				i--;
				
			}
			// Otherwise, add the transposed character
			else {
				newString[j] = transpose(password.charAt(i));
				newSpace++;
			}
			
		}
		
		// At the end, return the new string result
		return new String(newString);
	}
	

	/**
	 * Given a password as a String, return a decoded representation of it. <br>
	 * Details of what this does will be kept hidden.
	 * @param password : an encoded password
	 * @return String: a decrypted version of the password
	 */
	public String decode(String password) {
		// Should be nearly idential to encode
		// But everything is reverted to lowercase and only alphanumeric characters are allowed
		
		// The length of the char[] is the length of the string - extra spaces that exist
		// Extra spaces = (length % 5) - 1 if length > 5, otherwise 0
		int extraSpaces;
		if(password.length() > 5) {
			if(password.length() % 5 > 0)
				extraSpaces = password.length() / 5;
			else
				extraSpaces = password.length() / 5 - 1;
		}
		else
			extraSpaces = 0;
		
		// Store each character on char[]
		char[] newString = new char[password.length() - extraSpaces];	
		
		// Iterate through the string
		for(int i = 0, j = 0; i < password.length(); i++, j++) {
		
			// If the current string[i] isn't a space, then add its transpose onto the new string.
			if(password.charAt(i) != ' ') 
				newString[j] = transpose(password.charAt(i));
			// Otherwise, we encountered a space character, so decrement j (which means the new string itself isn't affected)
			else
				j--;
		}
	
		return new String(newString);	
	}
	
	
	
	/** Helper function for both encode and decode: transpose each letter given an input character. <br>
	 * For [a-z0-9], a => 9, b -> 8, etc.. <br>
	 * For [A-Z], A => Z, B => Y, etc..
	 * @param c : a single character
	 * @return char: a transposed version of the input character
	 */
	private char transpose(char c) {
		switch(c) {
			case 'a': return '9';
			case 'b': return '8';
			case 'c': return '7';
			case 'd': return '6';
			case 'e': return '5';
			case 'f': return '4';
			case 'g': return '3';
			case 'h': return '2';
			case 'i': return '1';
			case 'j': return '0';
			case 'k': return 'z';
			case 'l': return 'y';
			case 'm': return 'x';
			case 'n': return 'w';
			case 'o': return 'v';
			case 'p': return 'u';
			case 'q': return 't';
			case 'r': return 's';
			case 's': return 'r';
			case 't': return 'q';
			case 'u': return 'p';
			case 'v': return 'o';
			case 'w': return 'n';
			case 'x': return 'm';
			case 'y': return 'l';
			case 'z': return 'k';
			case '0': return 'j';
			case '1': return 'i';
			case '2': return 'h';
			case '3': return 'g';
			case '4': return 'f';
			case '5': return 'e';
			case '6': return 'd';
			case '7': return 'c';
			case '8': return 'b';
			case '9': return 'a';
			case 'A': return 'Z';
			case 'B': return 'Y';
			case 'C': return 'X';
			case 'D': return 'W';
			case 'E': return 'V';
			case 'F': return 'U';
			case 'G': return 'T';
			case 'H': return 'S';
			case 'I': return 'R';
			case 'J': return 'Q';
			case 'K': return 'P';
			case 'L': return 'O';
			case 'M': return 'N';
			case 'N': return 'M';
			case 'O': return 'L';
			case 'P': return 'K';
			case 'Q': return 'J';
			case 'R': return 'I';
			case 'S': return 'H';
			case 'T': return 'G';
			case 'U': return 'F';
			case 'V': return 'E';
			case 'W': return 'D';
			case 'X': return 'C';
			case 'Y': return 'B';
			case 'Z': return 'A';
			default : return '?';
		}
	}
	
}
