package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer;

/**
 * Token used by {@link it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.Lexer}.
 * <p/>
 * Token are defined inside {@link it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.TokenType}.
 *
 * @author Fabio Arcidicono
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.Lexer
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.TokenType
 */
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

