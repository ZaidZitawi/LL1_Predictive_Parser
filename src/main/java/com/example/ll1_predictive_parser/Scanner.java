package com.example.ll1_predictive_parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {

    private ArrayList<Token> tokens;
    private int currentPosition = 0;
    private ArrayList<String> errors;

    public Scanner(String source) {
        errors = new ArrayList<>();
        tokens = tokenize(source);
    }

    public Token nextToken() {
        if (currentPosition < tokens.size()) {
            return tokens.get(currentPosition++);
        } else {
            return new Token("EOF", "", -1);
        }
    }

    public ArrayList<String> getErrors() {
        return errors;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    private ArrayList<Token> tokenize(String source) {
        ArrayList<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(source);
        int lineNumber = 1;
        int currentIndex = 0;

        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    String token = matcher.group(i);
                    if (token.equals("\n")) {
                        lineNumber++;
                        continue;
                    }
                    String type = determineType(token);
                    if (type.equals("invalid")) {
                        errors.add("Lexical error: Invalid token '" + token + "' on line " + lineNumber);
                    } else {
                        tokens.add(new Token(type, token, lineNumber));
                    }
                }
            }
            int end = matcher.end();
            if (currentIndex < end) {
                String invalidToken = source.substring(currentIndex, matcher.start());
                if (!invalidToken.trim().isEmpty()) {
                    errors.add("Lexical error: Invalid token '" + invalidToken + "' on line " + lineNumber);
                }
            }
            currentIndex = end;
        }
        return tokens;
    }

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            "(\\breadint\\b)|(\\breadreal\\b)|(\\breadchar\\b)|(\\breadln\\b)|" +
                    "(\\bwriteint\\b)|(\\bwritereal\\b)|(\\bwritechar\\b)|(\\bwriteln\\b)|" +
                    "(\\b[a-zA-Z_][a-zA-Z0-9_]*\\b)|" +
                    "(\"[^\"]*\")|" +
                    "(\\b\\d+\\.\\d*\\b)|" +
                    "(\\b\\d+\\b)|" +
                    "([+])|" +
                    "([-])|" +
                    "([*])|" +
                    "([/])|" +
                    "(mod\\b)|" +
                    "(:=)|" +
                    "(:)|" +
                    "(;)|" +
                    "(\\()|" +
                    "(\\))|" +
                    "(,)|" +
                    "(\\.)|" +
                    "(<=)|" +
                    "(>=)|" +
                    "(<)|" +
                    "(>)|" +
                    "(=)|" +
                    "(!=)|" +
                    "(&&)|" +
                    "(\\|\\|)|" +
                    "(!)|" +
                    "(\\|=)|" +
                    "(\\^)|" +
                    "(\\b(if|then|else|end|begin|const|var|procedure|module|loop|exit|readint|readreal|readchar|readln|writeint|writereal|writechar|writeln|while|call)\\b)|" +
                    "(\\n)"
    );

    private static String determineType(String token) {
        switch (token) {
            case "readint": case "readreal": case "readchar": case "readln":
            case "writeint": case "writereal": case "writechar": case "writeln":
                return token;
            case "module":
            case "const":
            case "var":
            case "procedure":
            case "begin":
            case "end":
            case "if":
            case "then":
            case "else":
            case "loop":
            case "exit":
            case "while":
            case "do":
            case "until":
            case "call":
            case ":=":
            case ":":
            case ";":
            case ",":
            case ".":
            case "+":
            case "-":
            case "*":
            case "/":
            case "mod":
            case "(":
            case ")":
            case "<":
            case "<=":
            case ">=":
            case ">":
            case "=":
            case "!=":
            case "&&":
            case "||":
            case "!":
            case "|=":
            case "integer":
            case "real":
            case "char":
                return token;
            default:
                if (token.matches("\\b\\d+\\b")) return "integer-value";
                if (token.matches("\\b\\d+\\.\\d*\\b")) return "real-value";
                if (token.matches("\"[^\"]*\"")) return "string";
                if (token.matches("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b")) return "name";
                if (token.matches("\\b\\d+[a-zA-Z_][a-zA-Z0-9_]*\\b")) return "invalid-identifier";
                if (token.matches("[\\^\\&\\*\\@\\!\\#\\$]")) return "invalid";
        }
        return "invalid";
    }
}
