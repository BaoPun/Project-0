package ciphers;

/**
 * As an attempt to obscure the password as much as possible, there will be 2 classes that will encrypt/decrypt a given password. <br>
 * This is phase 1 of encryption and phase 2 of decryption.
 * @author baoph
 *
 */
public class CipherPhase1 implements InterfaceCipher{
	protected int key;

	/**
	 * Creates an instance of the Caesar Cipher algorithm with a given shift factor (key)
	 * @param key : a specific shift factor
	 */
	public CipherPhase1(int key) {
		this.key = key;
	}

	/**
	 * Given a password as a String, return an encoded representation of it. <br>
	 * Details of what this does will be kept hidden.
	 * @param password : a password
	 * @return String: an encrypted version of the password
	 */
	public String encode(String password) {
		// First, create a new array of chars of equivalent length 
		char[] newString = new char[password.length()];
		
		// Iterate through the entire string
		for(int i = 0; i < password.length(); i++) {
			
			// If the character is uppercase, then the code will be between 36 and 61
			if(Character.isUpperCase(password.charAt(i))) {
				// first, get the number code as if this were a lowercase, and then increment it by (36 + key)
				int normalCode = getCodeFromChar(Character.toLowerCase(password.charAt(i))) + 36 + key;	
				
				// second, make sure the number code remains within 0-25 w/o altering the variable
				// to do this, the parameter is (normalCode - 36) % 26
				// (normalCode - 36) undoes the + 36 from the declaration of normalCode
				// % 26 forces the result of above to remain within the range [0, 25], which is the range of lowercase alphabets
				char normalChar = getCharFromCode((normalCode - 36) % 26);								
				
				// Finally, uppercase the above variable and send it to newString[i]
				newString[i] = Character.toUpperCase(normalChar);
			}
			// Otherwise, the character is either lowercase letter or a number
			else {
				// Translate the character to code and then increment the code by the key (shift factor is key)
				int code = getCodeFromChar(password.charAt(i)) + key;
				
				// Afterwards, modulo the result by 36 to force it within [0, 35]
				code %= 36;
				
				// Finally, translate the new code to get a new character, and then add it to newString
				newString[i] = getCharFromCode(code);
			}
			
		}
		
		// Return the String version!
		return new String(newString);
	}
	
	/**
	 * Given a password as a String, return a decoded representation of it. <br>
	 * Details of what this does will be kept hidden. 
	 * @param password : an encrypted password
	 * @return String: a decrypted version of the encrypted password
	 */
	public String decode(String password) {
		// First, create a new array of chars of equivalent length 
		char[] newString = new char[password.length()];
		
		// Iterate through the entire string
		for(int i = 0; i < password.length(); i++) {
			// If the character is uppercase, then the code will be between 36 and 61
			if(Character.isUpperCase(password.charAt(i))) {
				// first, get the number code as if this were a lowercase, and then shift left by the key factor 
				int code = getCodeFromChar(Character.toLowerCase(password.charAt(i))) - key;	
				
				// second, make sure the number code remains within 0-25.  Since we are subtracting, increment the code by 26 if the code is below 0
				if(code < 0)
					code += 26;
				
				// third, get the new character from the code, but then make sure to uppercase the result
				char newChar = Character.toUpperCase(getCharFromCode(code));								
				
				// Finally, add the new character to our new string
				newString[i] = newChar;
			}
			// Otherwise, the character is either lowercase letter or number
			// The only difference between this version and the encode version is that we subtract by the key instead of adding
			else {
				// Get the new code; for decode, we are subtracting by the key instead of adding
				int code = getCodeFromChar(password.charAt(i)) - key;
				
				// If the key becomes negative, increment it by 36
				if(code < 0)
					code += 36;
				
				// Finally, get the new character from the code and then add it to our new string
				newString[i] = getCharFromCode(code);
			}
		}
		
		// Return the String version!
		return new String(newString);
	}
	
	/**
	 * Takes in a single character and then converts it into an int code.  <br>
	 * This will be used in conjunction with getCharFromCode => essentially a composite function. 
	 * @param c : the character to be translated into code
	 * @return int: a code
	 */
	private int getCodeFromChar(char c) {
		switch(c) {
			case 'a': return 0;
			case 'b': return 1;
			case 'c': return 2;
			case 'd': return 3;
			case 'e': return 4;
			case 'f': return 5;
			case 'g': return 6;
			case 'h': return 7;
			case 'i': return 8;
			case 'j': return 9;
			case 'k': return 10;
			case 'l': return 11;
			case 'm': return 12;
			case 'n': return 13;
			case 'o': return 14;
			case 'p': return 15;
			case 'q': return 16;
			case 'r': return 17;
			case 's': return 18;
			case 't': return 19;
			case 'u': return 20;
			case 'v': return 21;
			case 'w': return 22;
			case 'x': return 23;
			case 'y': return 24;
			case 'z': return 25;
			case '0': return 26;
			case '1': return 27;
			case '2': return 28;
			case '3': return 29;
			case '4': return 30;
			case '5': return 31;
			case '6': return 32;
			case '7': return 33;
			case '8': return 34;
			case '9': return 35;
			default : return -1;
		}
	}
	
	/**
	 * Takes in a single int code from getCharFromCode and then converts it to a char representation. <br>
	 * This will be used in conjunction with getCodeFromChar => essentially a composite function
	 * @param code : a numerical code within [0, 35]
	 * @return char: a single character
	 */
	private char getCharFromCode(int code) {
		switch(code) {
			case 0 : return 'a';
			case 1 : return 'b';
			case 2 : return 'c';
			case 3 : return 'd';
			case 4 : return 'e';
			case 5 : return 'f';
			case 6 : return 'g';
			case 7 : return 'h';
			case 8 : return 'i';
			case 9 : return 'j';
			case 10: return 'k';
			case 11: return 'l';
			case 12: return 'm';
			case 13: return 'n';
			case 14: return 'o';
			case 15: return 'p';
			case 16: return 'q';
			case 17: return 'r';
			case 18: return 's';
			case 19: return 't';
			case 20: return 'u';
			case 21: return 'v';
			case 22: return 'w';
			case 23: return 'x';
			case 24: return 'y';
			case 25: return 'z';
			case 26: return '0';
			case 27: return '1';
			case 28: return '2';
			case 29: return '3';
			case 30: return '4';
			case 31: return '5';
			case 32: return '6';
			case 33: return '7';
			case 34: return '8';
			case 35: return '9';
			default: return '?';
		}
	}

}
