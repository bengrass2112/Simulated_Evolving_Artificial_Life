package ast;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.jupiter.api.Test;

import parse.Parser;
import parse.ParserFactory;

class AstTest {

    @Test
	void test() throws IOException {
		String genome = MetaTest.makeProgram(3);
		String first = parseAndPrint(genome);
		String second = parseAndPrint(first);
		assert first.equals(second);
    }

	static String parseAndPrint(String genome) throws IOException {
		System.out.println(genome);
		InputStream stream = new ByteArrayInputStream(genome.getBytes());
		Reader reader = new BufferedReader(new InputStreamReader(stream));
		Parser parser = ParserFactory.getParser();
		Program prog = parser.parse(reader);
		reader.close();
		return prog.toString();
	}

}
