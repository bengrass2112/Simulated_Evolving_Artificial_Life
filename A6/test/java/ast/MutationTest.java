package ast;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.jupiter.api.Test;

import parse.Parser;
import parse.ParserFactory;

/**
 * This testing strategy is a little convoluted, but if the resulting output
 * ends with a semicolon, then a valid program is created. The parser will
 * prevent any non-valid program from being written and print an error message.
 * Since no error message ends in a ';', if the output does, then the program is
 * valid
 */
class MutationTest {

    /** Checks to make sure there are no errors in MutationReplace */
    @Test
    public void testValidReplaceMutation() {

        InputStream in = ClassLoader.getSystemResourceAsStream("files/draw_critter.txt");
        Reader r = new BufferedReader(new InputStreamReader(in));
        Parser parser = ParserFactory.getParser();
        Program prog = parser.parse(r);

        StringBuilder sb = new StringBuilder();
        Mutate m = new MutationReplace();

        Program p = prog;

        for (int i = 0; i < prog.size() - prog.size() / 2; i++) {
            p = p.mutate(i, m);
        }

        String str = p.prettyPrint(sb).toString();
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        Reader reader = new BufferedReader(new InputStreamReader(stream));
        prog = parser.parse(reader);

        str = prog.prettyPrint(sb).toString();
        assertTrue(str.charAt(str.length() - 2) == (';'));

    }

    @Test
    public void testValidSwapMutation() {
        InputStream in = ClassLoader.getSystemResourceAsStream("files/draw_critter.txt");
        Reader r = new BufferedReader(new InputStreamReader(in));
        Parser parser = ParserFactory.getParser();
        Program prog = parser.parse(r);

        StringBuilder sb = new StringBuilder();
        Mutate m = new MutationSwap();

        Program p = prog;

        for (int i = 0; i < prog.size() - prog.size() / 2; i++) {
            p = p.mutate(i, m);
        }

        String str = p.prettyPrint(sb).toString();
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        Reader reader = new BufferedReader(new InputStreamReader(stream));
        prog = parser.parse(reader);

        str = prog.prettyPrint(sb).toString();
        assertTrue(str.charAt(str.length() - 2) == (';'));

    }

    /** Tests to make sure */
    @Test
    public void testValidTransformMutation() {
        InputStream in = ClassLoader.getSystemResourceAsStream("files/draw_critter.txt");
        Reader r = new BufferedReader(new InputStreamReader(in));
        Parser parser = ParserFactory.getParser();
        Program prog = parser.parse(r);

        StringBuilder sb = new StringBuilder();
        Mutate m = new MutationTransform();

        Program p = prog;

        for (int i = 0; i < prog.size() - prog.size() / 2; i++) {
            p = p.mutate(i, m);
        }

        String str = p.prettyPrint(sb).toString();
        InputStream stream = new ByteArrayInputStream(str.getBytes());
        Reader reader = new BufferedReader(new InputStreamReader(stream));
        prog = parser.parse(reader);

        str = prog.prettyPrint(sb).toString();
        assertTrue(str.charAt(str.length() - 2) == (';'));

    }

}
