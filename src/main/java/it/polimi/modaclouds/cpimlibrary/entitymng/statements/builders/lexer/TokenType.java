package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer;

/**
 * Tokens for UPDATE and DELETE queries in JPQL.
 *
 * @author Fabio Arcidiacono
 */
public enum TokenType {
    UPDATE("[uU]pdate|UPDATE"),
    SET("[sS]et|SET"),
    DELETE("[dD]elete|DELETE"),
    FROM("[fF]rom|FROM"),
    WHERE("[wW]here|WHERE"),
    COMPAREOP("<>|>=|<=|>|<|="),
    LOGICOP("[Aa]nd|AND|[Oo]r|OR"),
    COMMA(","),
    PARAM(":\\S+"),
    COLUMN("[\\S+]\\.\\S+"),
    STRING("\\S+"),
    WHITESPACE("\\s+");

    public final String pattern;

    private TokenType(String pattern) {
        this.pattern = pattern;
    }

}