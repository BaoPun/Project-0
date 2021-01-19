package ciphers;

/**
 * This class combines both ciphers implemented within this package
 * 
 * @author baoph
 *
 */
public class PasswordMasker {
	private static CipherPhase1 caesar;
	private static CipherPhase2 atBash;
	
	/**
	 * Creates an instance of the PasswordMasker with both cipher methods activated.
	 */
	public PasswordMasker() {
		caesar = new CipherPhase1((int)(Math.random() * 21 + 10));	// key is within [10, 30]
		atBash = new CipherPhase2();
	}
	
	/**
	 * Creates an instance of the PasswordMasker with both cipher methods activated. <br>
	 * In addition, a specific key will be used for one of the ciphers. <br>
	 * If the specified key is outside [10, 30], then randomize a key within that range. <br>
	 * Only call this when we are returning to the previous state of the application.
	 */
	public PasswordMasker(int key) {
		if(key < 10 || key > 30)
			caesar = new CipherPhase1((int)(Math.random() * 21 + 10));
		else
			caesar = new CipherPhase1(key);								// key is specified
		atBash = new CipherPhase2();
	}
	
	/**
	 * A method that returns the key used for Caesar encryption. <br>
	 * Only use this if we're loading this application and need to restore it to its previous state.
	 * @return int: a key from Caesar cipher
	 */
	public int getKey() {
		return caesar.key;
	}
	
	/**
	 * To encode the password, the result is a composite: CipherPhase2.encode(CipherPhase1.encode(password)) => (encode) C1 * C2 
	 * @param password : a password
	 * @return String : an encoded version of the password
	 */
	public String encode(String password) {
		return atBash.encode(caesar.encode(password));
	}
	
	/**
	 * To decode the password, the result is a composite: CipherPhase1.decode(CipherPhase2.decode(password)) => (decode) C2 * C1
	 * @param password : an encrypted password
	 * @return String: a decrypted version of the encrypted password
	 */
	public String decode(String password) {
		return caesar.decode(atBash.decode(password));
	}
	
}
