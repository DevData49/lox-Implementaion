package com.devdata.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox{
  static boolean hadError = false;

  public static void main(String[] args) throws IOException{
    if(args.length > 1){
      System.out.println("usage: jLox [script]");
      System.exit(64);
    }else if(args.length == 1){
      runFile(args[0]);
    }else{
      runPrompt();
    }
  }

  //Handle the execution of a program from a file when its path is provided in
  //commandline.
  private static void runFile(String path) throws IOException{
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes,Charset.defaultCharset()));
    if(hadError)
    System.exit(65);
  }

  //starts an interactive repel if no path to a script is provided
  private static void runPrompt() throws IOException{
    InputStreamReader input =  new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for(;;){
      System.out.print("> ");
      run(reader.readLine());
      hadError = false;
    }
  }

//core function which handles actual interpreation and execution of source code
private static void run(String source){
  Scanner scanner = new Scanner(source);
  List<Token> tokens = scanner.scanTokens();
  Parser parser = new Parser(tokens);
  Expr expression = parser.parse();

  if(hadError) return;
  
  System.out.println(new AstPrinter().print(expression));
}

//error-reporting methiod
static void error(int line, String message){
  report(line,"",message);
}

private static void report(int line, String where, String message){
  System.err.println("[line "+ line + "] Error " +where+ ": "+message);
  hadError = true;
}

static void error(Token token, String message){
  if(token.type == TokenType.EOF){
    report(token.line, "at end", message);
  } else {
    report(token.line, " at ''"+token.lexeme+"' ", message);
  }
}
}
