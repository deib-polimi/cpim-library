package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple lexer based on java regex.
 *
 * @author Fabio Arcidiacono.
 */
public class Lexer {

    public static ArrayList<Token> lex(String input) {
        ArrayList<Token> tokens = new ArrayList<>();

        // Build token patterns
        StringBuilder tokenPatternsBuffer = new StringBuilder();
        for (TokenType tokenType : TokenType.values()) {
            tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.pattern));
        }
        Pattern tokenPatterns = Pattern.compile(tokenPatternsBuffer.substring(1));

        // Matching tokens
        Matcher matcher = tokenPatterns.matcher(input);
        while (matcher.find()) {
            for (TokenType tokenType : TokenType.values()) {
                if (matcher.group(tokenType.name()) != null) {
                    tokens.add(new Token(tokenType, matcher.group(tokenType.name())));
                }
            }
        }

        return tokens;
    }
}
