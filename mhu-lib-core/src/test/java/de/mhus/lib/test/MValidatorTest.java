package de.mhus.lib.test;

import java.sql.SQLException;
import java.util.Locale;

import de.mhus.lib.core.MSql;
import de.mhus.lib.core.MValidator;
import junit.framework.TestCase;

public class MValidatorTest extends TestCase {

	public void testEMail() {
		assertEquals(false, MValidator.isEmailAddress(null));
		assertEquals(false, MValidator.isEmailAddress(""));
		assertEquals(false, MValidator.isEmailAddress("@mhus.de"));
		assertEquals(false, MValidator.isEmailAddress("mike@"));
		assertEquals(true, MValidator.isEmailAddress("mike@mhus.de"));
		assertEquals(false, MValidator.isEmailAddress("mike@alababa"));
		assertEquals(true, MValidator.isEmailAddress("mike@alababa.blabla.de"));
		assertEquals(true, MValidator.isEmailAddress("mike@alababa.aha.soso.local"));
	}
	
	public void testZip() {
		Locale l = new Locale("de","DE");
		assertEquals(true, MValidator.isZipCode(l, "04212"));
		assertEquals(true, MValidator.isZipCode(l, "95030"));
		assertEquals(false, MValidator.isZipCode(l,"00999"));
		assertEquals(false, MValidator.isZipCode(l,"100000"));
		assertEquals(false, MValidator.isZipCode(l,"2000"));
		l = new Locale("en","US");
		try {
			MValidator.isZipCode(l, "04212");
			assertEquals(true, false);
		} catch (Throwable t) {
		}
	}
	
	public void testNames() {
		assertEquals(true, MValidator.isFirstName("Güven"));
		assertEquals(true, MValidator.isFirstName("André"));
		assertEquals(true, MValidator.isLastName("Müller"));
	}

	public void testPhone() {
		assertEquals(true, MValidator.isPhoneNumber("+49 40 43214") );
		assertEquals(true, MValidator.isPhoneNumber("040-43214") );
		assertEquals(false, MValidator.isPhoneNumber("+49 40 abc") );
		assertEquals(false, MValidator.isPhoneNumber("+49") );
		assertEquals(false, MValidator.isPhoneNumber("+49 40 12345 12345 12345 12345") );
		assertEquals(true, MValidator.isPhoneNumber("+49-40/1234") );
		assertEquals(false, MValidator.isPhoneNumber("+49(0)40/1234") );
		
		assertEquals(true, MValidator.isPhoneNumber("+49-40/1234", Locale.GERMAN) );
		assertEquals(true, MValidator.isPhoneNumber("+49-40/1234", Locale.GERMANY) );
		
		assertEquals(true, MValidator.isPhoneNumber("(123) 123-1234", Locale.US) );
		assertEquals(true, MValidator.isPhoneNumber("123-123-1234", Locale.US) );
		assertEquals(false, MValidator.isPhoneNumber("+49-40/1234", Locale.US) );

	}
	
	public void testSqlColumnName() throws SQLException {
		MSql.column("_servus_mr_nobody");
		MSql.column("_servus_mr_nobody0");
		MSql.column("_Servus_Mr_Nobody");
		MSql.column("table.servus_mr_nobody");
		try {
			MSql.column("_servus_mr_nobody;");
			assertEquals(true, false);
		} catch (SQLException e) {
			
		}
		try {
			MSql.column("0_servus_mr_nobody");
			assertEquals(true, false);
		} catch (SQLException e) {
			
		}
		try {
			MSql.column("._servus_mr_nobody");
			assertEquals(true, false);
		} catch (SQLException e) {
			
		}

		MSql.column("`table.servus_mr_nobody`");
		try {
			MSql.column("`_servus_mr_nobody");
			assertEquals(true, false);
		} catch (SQLException e) {}
		try {
			MSql.column("_servus_mr_nobody`");
			assertEquals(true, false);
		} catch (SQLException e) {}
	}
	
}
