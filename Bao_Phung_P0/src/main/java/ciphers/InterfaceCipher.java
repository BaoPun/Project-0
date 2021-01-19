package ciphers;

/**
 * The only methods this interface needs are to encode and decode a given String.
 * @author baoph
 *
 */
public interface InterfaceCipher {
	String encode(String string);
	String decode(String string);
}
