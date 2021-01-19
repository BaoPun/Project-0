package dbtests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import util.DBConnection;

class ConnectionTest {

	@Test
	void testConnection() {
		Assertions.assertTrue(DBConnection.connectToDatabase() != null);
	}

}
