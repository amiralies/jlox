package io.siever.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.siever.lox.TokenType.*;

class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

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

  Scanner(String source) {
    this.source = source;
  }

  List<Token> scanTokens() {
    while (!isAtEnd()) {
      // We are at the beginning of the next lexem.
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
      break;

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

    case '/':
      if (match('/')) {
        // '//' Comment goes till end of line
        while (peek() != '\n' && !isAtEnd()) {
          advance();
        }
      } else if (match('*')) {
        discardMultilineComment();
      } else {
        addToken(SLASH);
      }
      break;

    // Ignore whitespace
    case ' ':
    case '\t':
    case '\r':
      break;

    case '\n':
      line++;
      break;

    case '"':
      eatString();
      break;

    default:
      if (isDigit(c)) {
        eatNumber();
      } else if (isAlpha(c)) {
        eatIdentifier();
      } else {
        Lox.error(line, "Unexpected character '" + c + "'.");
      }

      break;
    }

  }

  private void eatIdentifier() {
    while (isAlphaNumeric(peek())) {
      advance();
    }

    String text = source.substring(start, current);
    TokenType keywordType = keywords.get(text);

    if (keywordType == null) {
      addToken(IDENTIFIER);
    } else {
      addToken(keywordType);
    }
  }

  private void eatNumber() {
    while (isDigit(peek())) {
      advance();
    }

    // Look for fractional part
    if (peek() == '.' && isDigit(peekNext())) {
      // consume the "."
      advance();

      while (isDigit(peek())) {
        advance();
      }
    }

    addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
  }

  private void eatString() {
    while (peek() != '"' && !isAtEnd()) {
      if (peek() == '\n') {
        line++;
      }
      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "Unterminated string.");
      return;
    }

    // Closing "
    advance();

    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  private void discardMultilineComment() {
    while (!(peek() == '*' && peekNext() == '/') && !isAtEnd()) {
      if (peek() == '\n') {
        line++;
      }

      advance();
    }

    if (isAtEnd()) {
      Lox.error(line, "Unterminated multiline comment.");
      return;
    }

    // Close comment
    // Consume *
    advance();
    // Consume /
    advance();
  }

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

  private char peek() {
    if (isAtEnd()) {
      return '\0';
    }

    return source.charAt(current);
  }

  private char peekNext() {
    if (current + 1 >= source.length()) {
      return '\0';
    }

    return source.charAt(current + 1);
  }

  private boolean isAlpha(char c) {
    return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_');
  }

  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private boolean isAtEnd() {
    return current >= source.length();
  }

  private char advance() {
    char currChar = source.charAt(current);
    current++;
    return currChar;
  }

  private void addToken(TokenType type) {
    addToken(type, null);
  }

  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }
}
