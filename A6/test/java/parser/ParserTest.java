package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.jupiter.api.Test;

import ast.Program;
import parse.Parser;
import parse.ParserFactory;

/** This class contains tests for the Critter parser. */
public class ParserTest {

	@Test
	public void testProgramIsNull() throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/error2.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		assertNull(prog, "This should be null.");
		in.close();
	}

	@Test
	public void testProgramIsNull2() throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/error3.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		assertNull(prog, "This should be null.");
		in.close();
	}

	/**
	 * Checks that a valid critter program is not {@code null} when parsed.
	 * 
	 * @throws IOException
	 */
    @Test
	public void testProgramIsNotNull() throws IOException {
        InputStream in = ClassLoader.getSystemResourceAsStream("files/draw_critter.txt");
        Reader r = new BufferedReader(new InputStreamReader(in));
        Parser parser = ParserFactory.getParser();
        Program prog = parser.parse(r);
        assertNotNull(prog, "A valid critter program should not be null.");
		in.close();
    }

	@Test
	public void testProgramIsNotNull2() throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/example-rules.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		assertNotNull(prog, "A valid critter program should not be null.");
		in.close();
	}

	@Test
	public void testProgramIsNotNull3() throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/unmutated_critter.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		assertNotNull(prog, "A valid critter program should not be null.");
		in.close();
	}

	@Test
	public void testProgramIsNotNull4() throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/mutated_critter_1.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		assertNotNull(prog, "A valid critter program should not be null.");
		in.close();
	}

	@Test
	public void testProgramIsNotNull5() throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/mutated_critter_2.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		assertNotNull(prog, "A valid critter program should not be null.");
		in.close();
	}

	@Test
	public void testProgramIsNotNull6() throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/mutated_critter_3.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		assertNotNull(prog, "A valid critter program should not be null.");
		in.close();
	}

	@Test
	public void testProgramIsNotNull7() throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/mutated_critter_4.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		assertNotNull(prog, "A valid critter program should not be null.");
		in.close();
	}

	@Test
	public void testProgramIsNotNull8() throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/mutated_critter_5.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		assertNotNull(prog, "A valid critter program should not be null.");
		in.close();
	}

	@Test
	public void testProgramIsNotNull9() throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/mutated_critter_6.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		assertNotNull(prog, "A valid critter program should not be null.");
		in.close();
	}

	@Test
	public void testProgramPrint() throws IOException {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/serveError.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		System.out.println(prog.toString());
		in.close();
	}

	public void testPrettyPrintMatch1() {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/draw_critter.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		String pretty1 = prog.toString();

		InputStream prettyStream = new ByteArrayInputStream(pretty1.getBytes());
		Reader prettyReader = new InputStreamReader(prettyStream);
		Reader r2 = new BufferedReader(prettyReader);
		Parser parser2 = ParserFactory.getParser();
		Program prog2 = parser2.parse(r2);
		String pretty2 = prog2.toString();

		// Strings 1&2 are not equal bc of spacing

		InputStream prettyStream2 = new ByteArrayInputStream(pretty2.getBytes());
		Reader prettyReader2 = new InputStreamReader(prettyStream2);
		Reader r3 = new BufferedReader(prettyReader2);
		Parser parser3 = ParserFactory.getParser();
		Program prog3 = parser3.parse(r3);
		String pretty3 = prog3.toString();

		// System.out.println(pretty2 + "\n" + pretty3 + "\n" +
		// pretty2.equals(pretty3));
		assertEquals(pretty2, pretty3, pretty2 + "\nDOES NOT EQUAL\n" + pretty3);
	}


	public void testPrettyPrintMatch2() {
		InputStream in = ClassLoader.getSystemResourceAsStream("files/example-rules.txt");
		Reader r = new BufferedReader(new InputStreamReader(in));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(r);
		String pretty1 = prog.toString();

		InputStream prettyStream = new ByteArrayInputStream(pretty1.getBytes());
		Reader prettyReader = new InputStreamReader(prettyStream);
		Reader r2 = new BufferedReader(prettyReader);
		Parser parser2 = ParserFactory.getParser();
		Program prog2 = parser2.parse(r2);
		String pretty2 = prog2.toString();

		InputStream prettyStream2 = new ByteArrayInputStream(pretty2.getBytes());
		Reader prettyReader2 = new InputStreamReader(prettyStream2);
		Reader r3 = new BufferedReader(prettyReader2);
		Parser parser3 = ParserFactory.getParser();
		Program prog3 = parser3.parse(r3);
		String pretty3 = prog3.toString();

		// System.out.println(pretty2 + "\n" + pretty3 + "\n" +
		// pretty2.equals(pretty3));
		assertEquals(pretty2, pretty3, pretty2 + "\nDOES NOT EQUAL\n" + pretty3);
	}

}
