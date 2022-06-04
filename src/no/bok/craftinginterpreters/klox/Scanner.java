package no.bok.craftinginterpreters.klox;

import static no.bok.craftinginterpreters.klox.TokenType.AND;
import static no.bok.craftinginterpreters.klox.TokenType.BANG;
import static no.bok.craftinginterpreters.klox.TokenType.BANG_EQUAL;
import static no.bok.craftinginterpreters.klox.TokenType.CLASS;
import static no.bok.craftinginterpreters.klox.TokenType.COMMA;
import static no.bok.craftinginterpreters.klox.TokenType.DOT;
import static no.bok.craftinginterpreters.klox.TokenType.ELSE;
import static no.bok.craftinginterpreters.klox.TokenType.EOF;
import static no.bok.craftinginterpreters.klox.TokenType.EQUAL;
import static no.bok.craftinginterpreters.klox.TokenType.EQUAL_EQUAL;
import static no.bok.craftinginterpreters.klox.TokenType.FALSE;
import static no.bok.craftinginterpreters.klox.TokenType.FOR;
import static no.bok.craftinginterpreters.klox.TokenType.FUN;
import static no.bok.craftinginterpreters.klox.TokenType.GREATER;
import static no.bok.craftinginterpreters.klox.TokenType.GREATER_EQUAL;
import static no.bok.craftinginterpreters.klox.TokenType.IDENTIFIER;
import static no.bok.craftinginterpreters.klox.TokenType.IF;
import static no.bok.craftinginterpreters.klox.TokenType.LEFT_BRACE;
import static no.bok.craftinginterpreters.klox.TokenType.LEFT_PAREN;
import static no.bok.craftinginterpreters.klox.TokenType.LESS;
import static no.bok.craftinginterpreters.klox.TokenType.LESS_EQUAL;
import static no.bok.craftinginterpreters.klox.TokenType.MINUS;
import static no.bok.craftinginterpreters.klox.TokenType.NIL;
import static no.bok.craftinginterpreters.klox.TokenType.NUMBER;
import static no.bok.craftinginterpreters.klox.TokenType.OR;
import static no.bok.craftinginterpreters.klox.TokenType.PLUS;
import static no.bok.craftinginterpreters.klox.TokenType.PRINT;
import static no.bok.craftinginterpreters.klox.TokenType.RETURN;
import static no.bok.craftinginterpreters.klox.TokenType.RIGHT_BRACE;
import static no.bok.craftinginterpreters.klox.TokenType.RIGHT_PAREN;
import static no.bok.craftinginterpreters.klox.TokenType.SEMICOLON;
import static no.bok.craftinginterpreters.klox.TokenType.SLASH;
import static no.bok.craftinginterpreters.klox.TokenType.STAR;
import static no.bok.craftinginterpreters.klox.TokenType.STRING;
import static no.bok.craftinginterpreters.klox.TokenType.SUPER;
import static no.bok.craftinginterpreters.klox.TokenType.THIS;
import static no.bok.craftinginterpreters.klox.TokenType.TRUE;
import static no.bok.craftinginterpreters.klox.TokenType.VAR;
import static no.bok.craftinginterpreters.klox.TokenType.WHILE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("and", AND);
    keywords.put("class", CLASS);
    keywords.put("else", ELSE);
    keywords.put("false", FALSE);
    keywords.put("for", FOR);
    keywords.put("fun", FUN);
    keywords.put("if", IF);
    keywords.put("nil", NIL);
    keywords.put("or", OR);
    keywords.put("print", PRINT);
    keywords.put("return", RETURN);
    keywords.put("super", SUPER);
    keywords.put("this", THIS);
    keywords.put("true", TRUE);
    keywords.put("var", VAR);
    keywords.put("while", WHILE);
  }

  private final String source;
  private final List<Token> tokens = new ArrayList<>();

  private int start = 0;
  private int current = 0;
  private int line = 1;

  public Scanner(String source) {
    this.source = source;
  }

  public List<Token> scanTokens() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexeme.
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  private void scanToken() {
    char c = advance();
    switch (c) {
      case '(':
        addToken(LEFT_PAREN);
        break;
      case ')':
        addToken(RIGHT_PAREN);
        break;
      case '{':
        addToken(LEFT_BRACE);
        break;
      case '}':
        addToken(RIGHT_BRACE);
        break;
      case ',':
        addToken(COMMA);
        break;
      case '.':
        addToken(DOT);
        break;
      case '-':
        addToken(MINUS);
        break;
      case '+':
        addToken(PLUS);
        break;
      case ';':
        addToken(SEMICOLON);
        break;
      case '*':
        addToken(STAR);
        break; // [slash]
//> two-char-tokens
      case '!':
        addToken(match('=') ? BANG_EQUAL : BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL : EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL : LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL : GREATER);
        break;
//< two-char-tokens
//> slash
      case '/':
        if (match('/')) {
          // A comment goes until the end of the line.
          while (peek() != '\n' && !isAtEnd()) {
            advance();
          }
        } else {
          addToken(SLASH);
        }
        break;
//< slash
//> whitespace

      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace.
        break;

      case '\n':
        line++;
        break;
//< whitespace
//> string-start

      case '"':
        string();
        break;
//< string-start
//> char-error

      default:
/* Scanning char-error < Scanning digit-start
        Lox.error(line, "Unexpected character.");
*/
//> digit-start
        if (isDigit(c)) {
          number();
//> identifier-start
        } else if (isAlpha(c)) {
          identifier();
//< identifier-start
        } else {
          Klox.error(line, "Unexpected character: '" + source.charAt(current - 1) + "'.");
        }
//< digit-start
        break;
//< char-error
    }
  }

  //< scan-token
//> identifier
  private void identifier() {
    while (isAlphaNumeric(peek())) {
      advance();
    }

/* Scanning identifier < Scanning keyword-type
    addToken(IDENTIFIER);
*/
//> keyword-type
    String text = source.substring(start, current);
    TokenType type = keywords.get(text);
    if (type == null) {
      type = IDENTIFIER;
    }
    addToken(type);
//< keyword-type
  }

  //< identifier
//> number
  private void number() {
    while (isDigit(peek())) {
      advance();
    }

    // Look for a fractional part.
    if (peek() == '.' && isDigit(peekNext())) {
      // Consume the "."
      advance();

      while (isDigit(peek())) {
        advance();
      }
    }

    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  //< number
//> string
  private void string() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') {
        line++;
      }
      advance();
    }

    if (isAtEnd()) {
      Klox.error(line, "Unterminated string.");
      return;
    }

    // The closing ".
    advance();

    // Trim the surrounding quotes.
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  //< string
//> match
  private boolean match(char expected) {
    if (isAtEnd()) {
      return false;
    }
    if (source.charAt(current) != expected) {
      return false;
    }

    current++;
    return true;
  }

  //< match
//> peek
  private char peek() {
    if (isAtEnd()) {
      return '\0';
    }
    return source.charAt(current);
  }

  //< peek
//> peek-next
  private char peekNext() {
    if (current + 1 >= source.length()) {
      return '\0';
    }
    return source.charAt(current + 1);
  } // [peek-next]

  //< peek-next
//> is-alpha
  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  //< is-alpha
//> is-digit
  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  } // [is-digit]

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType tokenType, Object literal) {
    var text = source.substring(start, current);
    tokens.add(new Token(tokenType, text, literal, line));
  }

  private char advance() {
    //Eat current character
    char currentCharacter = source.charAt(current);
    // Advance to next character
    current++;
    return currentCharacter;
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }
}
