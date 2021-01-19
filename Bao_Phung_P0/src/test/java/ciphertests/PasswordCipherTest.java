package ciphertests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ciphers.PasswordMasker;

class PasswordCipherTest {
	
	// Test with a given key of 17
	private static PasswordMasker masker = new PasswordMasker(17);
	
	@Test
	void testPasswordEncryptionWithoutUppercase() {
		Assertions.assertEquals("dsaaz 6ebp1 0", masker.encode("pass3word12"));
	}
	
	@Test
	void testPasswordDecryptionWithoutUppercase() {
		Assertions.assertEquals("pass3word12", masker.decode("dsaaz 6ebp1 0"));
	}
	
	@Test
	void testPasswordEncryptionWithUppercase() {
		Assertions.assertEquals("dsaaz MebF1 0", masker.encode("pass3WorD12"));
	}
	
	@Test
	void testPasswordDecryptionWithUppercase() {
		Assertions.assertEquals("pass3WorD12", masker.decode("dsaaz MebF1 0"));
	}

}
