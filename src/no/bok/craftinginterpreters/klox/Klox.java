package no.bok.craftinginterpreters.klox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Klox {

  // Exit codes are used as indicated in https://www.freebsd.org/cgi/man.cgi?query=sysexits&manpath=FreeBSD+4.3-RELEASE
  public static final int EX_USAGE = 64; // The command was used incorrectly
  public static final int EX_DATAERR = 65; // The (user's) input data was incorrect in some way
  static boolean hadError = false;

  public static void main(String[] args) throws IOException {
    if (args.length > 1) {
      System.out.println("Usage: klox [script]");
      System.exit(EX_USAGE);
    } else if (args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }

  }

  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));

    //Scan, Parse, Interpret here

    if (hadError) {
      System.exit(EX_DATAERR);
    }
  }

  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for (; ; ) { // [repl]
      System.out.print("> ");
      String line = reader.readLine();
      if (line == null) {
        break;
      }
      run(line);
      hadError = false;
    }
  }

  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    for (Token token : tokens) {
      System.out.println(token);
    }
  }

  public static void error(int line, String errorMessage) {
    System.out.println("Error at line: " + line);
    System.out.println("Reason: " + errorMessage);
    hadError=true;
  }
}
