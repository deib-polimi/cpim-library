package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer;

public class Token {

    public final TokenType type;
    public final String data;

    public Token(TokenType type, String data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("(%s '%s')", type.name(), data);
    }
}

