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

import lombok.Getter;

/**
 * Token used by {@link it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.Lexer}.
 * <p/>
 * Token are defined inside {@link it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.TokenType}.
 *
 * @author Fabio Arcidicono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.Lexer
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.TokenType
 */
public class Token {

    @Getter private final TokenType type;
    @Getter private final String data;

    public Token(TokenType type, String data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("(%s '%s')", type.name(), data);
    }
}

