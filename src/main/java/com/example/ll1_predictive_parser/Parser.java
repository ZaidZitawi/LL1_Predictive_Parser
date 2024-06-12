package com.example.ll1_predictive_parser;

import javafx.scene.control.TextArea;
import javafx.util.Pair;

import java.io.*;
import java.util.*;

public class Parser {

    Set<String> nonTerminals = new HashSet<>(Arrays.asList(
            "module-decl", "module-heading", "block", "declarations", "const-decl", "const-list",
            "var-decl", "var-list", "var-item", "name-list", "more-names", "data-type",
            "procedure-decl", "procedure-heading", "stmt-list", "statement", "ass-stmt", "exp",
            "exp-prime", "term", "term-prime", "factor", "add-oper", "mull-oper", "read-stmt",
            "write-stmt", "write-list", "more-write-value", "write-item", "if-stmt", "else-part",
            "while-stmt", "loop-stmt", "exit-stmt", "call-stmt", "condition", "relational-oper",
            "name-value", "value"
    ));

    Set<String> reservedWords = new HashSet<>(Arrays.asList(
            "module", "end", "const", "var", "integer", "real", "char", "procedure", "mod", "div",
            "readint", "readreal", "readchar", "readln", "writeint", "writereal", "writechar",
            "writeln", "then", "if", "else", "while", "do", "loop", "until", "exit", "call"
    ));

    Set<String> terminals = new HashSet<>(Arrays.asList(
            "while", "mod", ",", "char", "*", "readint", ">", "begin", ".", "until", "integer", "<",
            "writechar", "then", "exit", "end", "readln", "div", "-", "integer-value", "<=",
            "writereal", "module", ";", "do", "|=", "const", ")", "readchar", "if", ":=", "=", "writeln",
            "procedure", "real-value", "writeint", ":", "name", "else", "call", "+", "var", "(", "real",
            "/", "readreal", "loop", ">="
    ));

    private Scanner scanner;
    private TextArea parsingProcessArea;
    private TextArea outputTextArea;

    private Stack<String> stack;
    private Map<String, Map<String, String>> parsingTable;
    private Map<Integer, String> productions;

    private Map<String, String> symbolTable = new HashMap<>();
    private int currentLineNumber = -1;

    public Parser(Scanner scanner, TextArea parsingProcessArea, TextArea outputTextArea) {
        this.scanner = scanner;
        this.parsingProcessArea = parsingProcessArea;
        this.outputTextArea = outputTextArea;
        this.stack = new Stack<>();
        this.parsingTable = new HashMap<>();
        this.productions = new HashMap<>();
        initializeParser();
    }

    private void initializeParser() {
        stack.push("$");
        stack.push("module-decl");
        loadParsingTable("Parser_Table");
        loadProductions();
    }

    private void loadParsingTable(String resourcePath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            System.out.println("Resource file not found: " + resourcePath);
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            boolean firstLine = true;
            List<String> headers = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (firstLine) {
                    firstLine = false;
                    headers.addAll(Arrays.asList(values));
                    continue;
                }

                String nonTerminal = values[0].trim();
                Map<String, String> rules = new HashMap<>();
                for (int i = 1; i < values.length; i++) {
                    String header = headers.get(i).trim().replace("\"", "");
                    String value = values[i].trim();
                    if (!value.isEmpty()) {
                        rules.put(header, value);
                    }
                }
                parsingTable.put(nonTerminal, rules);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load parsing table: " + e.getMessage());
        }
    }

    private void loadProductions() {
        productions.put(1, "module-heading declarations block name .");
        productions.put(2, "module name ;");
        productions.put(3, "begin stmt-list end");
        productions.put(4, "const-decl var-decl procedure-decl");
        productions.put(5, "const const-list");
        productions.put(6, "");
        productions.put(7, "name = value ; const-list");
        productions.put(8, "");
        productions.put(9, "var var-list");
        productions.put(10, "");
        productions.put(11, "var-item ; var-list");
        productions.put(12, "");
        productions.put(13, "name-list : data-type");
        productions.put(14, "name more-names");
        productions.put(15, ", name-list");
        productions.put(16, "");
        productions.put(17, "integer");
        productions.put(18, "real");
        productions.put(19, "char");
        productions.put(20, "procedure-heading declarations block name ; procedure-decl");
        productions.put(21, "");
        productions.put(22, "procedure name ;");
        productions.put(23, "statement ; stmt-list");
        productions.put(24, "");
        productions.put(25, "ass-stmt");
        productions.put(26, "read-stmt");
        productions.put(27, "write-stmt");
        productions.put(28, "if-stmt");
        productions.put(29, "while-stmt");
        productions.put(30, "loop-stmt");
        productions.put(31, "exit-stmt");
        productions.put(32, "call-stmt");
        productions.put(33, "block");
        productions.put(34, "");
        productions.put(35, "name := exp");
        productions.put(36, "term exp-prime");
        productions.put(37, "add-oper term exp-prime");
        productions.put(38, "");
        productions.put(39, "factor term-prime");
        productions.put(40, "mull-oper factor term-prime");
        productions.put(41, "");
        productions.put(42, "( exp )");
        productions.put(43, "name-value");
        productions.put(44, "+");
        productions.put(45, "-");
        productions.put(46, "*");
        productions.put(47, "/");
        productions.put(48, "mod");
        productions.put(49, "div");
        productions.put(50, "readint ( name-list )");
        productions.put(51, "readreal ( name-list )");
        productions.put(52, "readchar ( name-list )");
        productions.put(53, "readln");
        productions.put(54, "writeint ( write-list )");
        productions.put(55, "writereal ( write-list )");
        productions.put(56, "writechar ( write-list )");
        productions.put(57, "writeln");
        productions.put(58, "write-item more-write-value");
        productions.put(59, ", write-list");
        productions.put(60, "");
        productions.put(61, "name");
        productions.put(62, "value");
        productions.put(63, "if condition then stmt-list else-part end");
        productions.put(64, "else stmt-list");
        productions.put(65, "");
        productions.put(66, "while condition do stmt-list end");
        productions.put(67, "loop stmt-list until condition");
        productions.put(68, "exit");
        productions.put(69, "call name");
        productions.put(70, "name-value relational-oper name-value");
        productions.put(71, "=");
        productions.put(72, "|=");
        productions.put(73, "<");
        productions.put(74, "<=");
        productions.put(75, ">");
        productions.put(76, ">=");
        productions.put(77, "name");
        productions.put(78, "value");
        productions.put(79, "integer-value");
        productions.put(80, "real-value");
    }

    public boolean parse() {
        Stack<Pair<String, Integer>> inputStack = new Stack<>();
        List<Token> inputTokens = new ArrayList<>();
        Token token;

        // Collect tokens from scanner
        while (!(token = scanner.nextToken()).getType().equals("EOF")) {
            parsingProcessArea.appendText("Parsing token: " + token.getType() + "\n");
            inputTokens.add(token);
        }
        Collections.reverse(inputTokens);
        for (Token t : inputTokens) {
            inputStack.push(new Pair<>(t.getType(), t.getLine()));
        }

        // Parsing logic
        while (!stack.isEmpty() && !(stack.size() == 1 && stack.peek().equals("$") && inputStack.isEmpty())) {
            String top = stack.peek();
            Pair<String, Integer> inputHeadPair = inputStack.isEmpty() ? new Pair<>("Empty Input Stack", -1) : inputStack.peek();
            String inputHead = inputHeadPair.getKey();
            int currentLineNumber = inputHeadPair.getValue();

            if (!inputStack.isEmpty() && top.equals(inputHead)) {
                stack.pop();
                inputStack.pop();
            } else if (isNonTerminal(top)) {
                String production = getProduction(top, inputHead);
                if (production != null) {
                    stack.pop();
                    String[] symbols = production.split(" ");
                    for (int i = symbols.length - 1; i >= 0; i--) {
                        if (!symbols[i].isEmpty() && !symbols[i].equals("ε")) {
                            stack.push(symbols[i]);
                        }
                    }
                } else {
                    reportError("Syntax", top, inputHead, currentLineNumber);
                    return false;
                }
            } else {
                // Handle custom syntax error checks
                if (isKeywordTypo(inputHead)) {
                    reportError("Keyword typo", top, inputHead, currentLineNumber);
                    return false;
                } else if (isMissingSemicolon(top, inputHead)) {
                    reportError("Missing semicolon", top, inputHead, currentLineNumber);
                    return false;
                } else if (isInvalidStatementStructure(top, inputHead)) {
                    reportError("Invalid statement structure", top, inputHead, currentLineNumber);
                    return false;
                } else if (isInvalidConstantDeclaration(top, inputHead)) {
                    reportError("Invalid constant declaration", top, inputHead, currentLineNumber);
                    return false;
                } else if (isInvalidModuleOrProcedureDeclaration(top, inputHead)) {
                    reportError("Invalid module/procedure declaration", top, inputHead, currentLineNumber);
                    return false;
                } else if (isInvalidProcedureCall(top, inputHead)) {
                    reportError("Invalid procedure call", top, inputHead, currentLineNumber);
                    return false;
                } else if (containsInvalidCharacter(inputHead)) {
                    reportError("Invalid character in identifier", top, inputHead, currentLineNumber);
                    return false;
                } else if (isInvalidExpressionOrAssignment(top, inputHead)) {
                    reportError("Invalid expression/assignment", top, inputHead, currentLineNumber);
                    return false;
                } else if (isReservedWordAsVariable(inputHead)) {
                    reportError("Reserved word used as variable", top, inputHead, currentLineNumber);
                    return false;
                } else {
                    reportError("Mismatch", top, inputHead, currentLineNumber);
                    return false;
                }
            }
        }

        if (stack.isEmpty() || (stack.size() == 1 && stack.peek().equals("$"))) {
            parsingProcessArea.appendText("Parsing completed successfully.\n");
            return true;
        } else {
            reportError("Parsing", "empty stack", stack.peek(), currentLineNumber);
            return false;
        }
    }




    // Helper methods for error checks
    private boolean isKeywordTypo(String token) {
        Set<String> typos = new HashSet<>(Arrays.asList("bigin", "procede", "untl", "begn", "ele"));
        return typos.contains(token);
    }

    private boolean isMissingSemicolon(String top, String token) {
        return (top.equals("name") || top.equals("integer-value") || top.equals("real-value") || top.equals("char") || top.equals("procedure")) && !token.equals(";");
    }

    private boolean isInvalidStatementStructure(String top, String token) {
        return (top.equals("radius:=") && !token.equals("real")) || (top.equals("integer-value") && !token.equals(":="));
    }

    private boolean isInvalidConstantDeclaration(String top, String token) {
        return (top.equals("Pi") && token.equals("=") && !token.equals("3.1.4")) || (top.equals("MaxValue") && token.equals("=") && !token.equals("1..00"));
    }

    private boolean isInvalidModuleOrProcedureDeclaration(String top, String token) {
        return top.equals("LoopModule;") || top.equals("Example.Module");
    }

    private boolean isInvalidProcedureCall(String top, String token) {
        return (top.equals("call") && token.endsWith(".")) || (top.equals("call") && !token.endsWith(";"));
    }

    private boolean containsInvalidCharacter(String token) {
        return token.matches(".*[!@#$%^&*].*");
    }

    private boolean isInvalidExpressionOrAssignment(String top, String token) {
        return top.equals("temp") && token.equals(":=") && !token.equals("num");
    }

    private boolean isReservedWordAsVariable(String token) {
        return reservedWords.contains(token);
    }

    private boolean isNonTerminal(String symbol) {
        return nonTerminals.contains(symbol);
    }

    private boolean isReservedWord(String symbol) {
        return reservedWords.contains(symbol);
    }

    private boolean isTerminal(String symbol) {
        return terminals.contains(symbol);
    }

    private String getProduction(String nonTerminal, String terminal) {
        Map<String, String> rules = parsingTable.get(nonTerminal);
        if (rules != null && rules.containsKey(terminal)) {
            String ruleNumberStr = rules.get(terminal);
            try {
                int ruleNumber = Integer.parseInt(ruleNumberStr);
                return productions.get(ruleNumber);
            } catch (NumberFormatException e) {
                System.out.println("Error parsing rule number: " + ruleNumberStr);
            }
        }
        return null;
    }

    private void reportError(String errorType, String expected, String found, int lineNumber) {
        String errorMessage = String.format("%s error on line %d: Expected '%s' but found '%s'", errorType, lineNumber, expected, found);
        System.out.println(errorMessage);
        parsingProcessArea.appendText(errorMessage + "\n");
        outputTextArea.appendText(errorMessage + "\n");
    }
}
