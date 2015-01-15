/**
 * Copyright 2013 deib-polimi
 * Contact: deib-polimi <marco.miglierina@polimi.it>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple lexer based on java regex.
 *
 * @author Fabio Arcidiacono.
 */
public class Lexer {

    private Lexer() {
    }

    public static List<Token> lex(String input) {
        List<Token> tokens = new ArrayList<>();

        // Build token patterns
        StringBuilder tokenPatternsBuffer = new StringBuilder();
        for (TokenType tokenType : TokenType.values()) {
            tokenPatternsBuffer.append(String.format("|(?<%s>%s)", tokenType.name(), tokenType.getPattern()));
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
