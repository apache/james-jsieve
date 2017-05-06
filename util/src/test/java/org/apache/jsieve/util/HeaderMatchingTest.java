package org.apache.jsieve.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.jsieve.ConfigurationManager;
import org.apache.jsieve.SieveFactory;
import org.apache.jsieve.mail.ActionRedirect;
import org.apache.jsieve.parser.generated.Node;
import org.apache.jsieve.util.check.ScriptCheckMailAdapter;

import junit.framework.TestCase;

public class HeaderMatchingTest extends TestCase {

	private SieveFactory sieveFactory;

	static String foldedSubjectMatch = "if header :is \"subject\" \"Test with long long long long long long long long long "
			+ "long long long long long long long long long long long very very long folded subject email\" { "
			+ "redirect \"test@test.test\";" + "}";
	
	static String encodedSubjectMatch = "if header :is \"subject\" \"Test però £ ù €\" { "
			+ "redirect \"test@test.test\";" + "}";

	@Override
	protected void setUp() throws Exception {
		this.sieveFactory = new ConfigurationManager().build();
		super.setUp();
	}

	public void testFoldedHeader() throws Exception {

		// Load test message file
		File file = new File(this.getClass().getResource("FoldedSubjectEmail.eml").getFile());
		MimeMessage message = new MimeMessage(Session.getDefaultInstance(System.getProperties()),
				new FileInputStream(file));

		// Associate it to the adapter
		ScriptCheckMailAdapter adapter = new ScriptCheckMailAdapter();
		adapter.setMail(message);

		// Parse sieve script
		Node parsedSieve = sieveFactory.parse(new ByteArrayInputStream(foldedSubjectMatch.getBytes("UTF-8")));

		// Evaluate the script against the message
		sieveFactory.evaluate(adapter, parsedSieve);

		// Test is OK if we have the "redirect" action
		assertTrue( adapter.getActions().get(0) instanceof ActionRedirect);

	}

	public void testEncodedHeader() throws Exception {

		// Load test message file
		File file = new File(this.getClass().getResource("EncodedSubjectEmail.eml").getFile());
		MimeMessage message = new MimeMessage(Session.getDefaultInstance(System.getProperties()),
				new FileInputStream(file));

		// Associate it to the adapter
		ScriptCheckMailAdapter adapter = new ScriptCheckMailAdapter();
		adapter.setMail(message);

		// Parse sieve script
		Node parsedSieve = sieveFactory.parse(new ByteArrayInputStream(encodedSubjectMatch.getBytes("UTF-8")));

		// Evaluate the script against the message
		sieveFactory.evaluate(adapter, parsedSieve);

		// Test is OK if we have the "redirect" action
		assertTrue(adapter.getActions().get(0) instanceof ActionRedirect);

	}
}
