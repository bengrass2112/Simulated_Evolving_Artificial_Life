//package main;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.Reader;
//
//import ast.Program;
//import parse.Parser;
//import parse.ParserFactory;
//
//public class ParseAndMutateApp {
//
//	public static void main(String[] args) {
//
//        int n = 0;
//        String file = null;
//        try {
//            if (args.length == 1) {
//                file = args[0];
//            } else if (args.length == 3 && args[0].equals("--mutate")) {
//                n = Integer.parseInt(args[1]);
//                if (n < 0) throw new IllegalArgumentException();
//                file = args[2];
//            } else {
//                throw new IllegalArgumentException();
//            }
//
//			Parser p = ParserFactory.getParser();
//			InputStream in = new FileInputStream(file);
//			Reader r = new InputStreamReader(in);
//			Program prog = p.parse(r);
//			System.out.println(prog.prettyPrint(new StringBuilder()));
//
//			for (int i = 1; i <= n; i++) {
//				System.out.println("Mutation " + i + ":\n");
//				prog = prog.mutate();
//				System.out.println(prog.prettyPrint(new StringBuilder()));
//			}
//
//
//        } catch (IllegalArgumentException e) {
//            System.out.println("Usage:\n  <input_file>\n  --mutate <n> <input_file>");
//		} catch (FileNotFoundException e) {
//			System.out.println("The input file was not valid.\nUsage:\\n  <input_file>\\n  --mutate <n> <input_file>");
//        }
//    }
//}
