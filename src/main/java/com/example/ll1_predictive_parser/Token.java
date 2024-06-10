package com.example.ll1_predictive_parser;

public class Token {


    // Token fields
    private final String type;
    private final String value;
    private final int line;

    public Token(String type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line=line;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public int getLine() {
        return line;
    }


    @Override
    public String toString() {
        return String.format("[%s: %s]", type, value);
    }
}

