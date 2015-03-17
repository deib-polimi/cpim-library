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
 * Tokens for UPDATE and DELETE queries in JPQL.
 *
 * @author Fabio Arcidiacono.
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

    @Getter private final String pattern;

    private TokenType(String pattern) {
        this.pattern = pattern;
    }

}