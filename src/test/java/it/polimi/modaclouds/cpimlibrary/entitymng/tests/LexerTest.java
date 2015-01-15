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
package it.polimi.modaclouds.cpimlibrary.entitymng.tests;

import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.Lexer;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.Token;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.lexer.TokenType;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author Fabio Arcidiacono.
 */
public class LexerTest {

    @Test
    public void testUppercaseLowercase() {
        ArrayList<Token> tokens = Lexer.lex("UPDATE Update update");
        Iterator<Token> itr = tokens.iterator();
        Assert.assertEquals(TokenType.UPDATE, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.UPDATE, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.UPDATE, itr.next().type);
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("DELETE Delete delete");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.DELETE, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.DELETE, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.DELETE, itr.next().type);
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("FROM From from");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.FROM, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.FROM, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.FROM, itr.next().type);
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("SET Set set");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.SET, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.SET, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.SET, itr.next().type);
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("WHERE Where where");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.WHERE, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.WHERE, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.WHERE, itr.next().type);
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void testOperators() {
        ArrayList<Token> tokens = Lexer.lex("AND And and");
        Iterator<Token> itr = tokens.iterator();
        Assert.assertEquals(TokenType.LOGICOP, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.LOGICOP, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.LOGICOP, itr.next().type);
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("OR Or or");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.LOGICOP, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.LOGICOP, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.LOGICOP, itr.next().type);
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("<> >= <= > < =");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().type);
        Assert.assertFalse(itr.hasNext());
    }
    @Test
    public void testUpdateLexer() {
        ArrayList<Token> tokens = Lexer.lex("UPDATE Employee e SET e.salary = :s, e.name = :n2 WHERE e.name = :n");
        Iterator<Token> itr = tokens.iterator();
        Token token = itr.next();
        Assert.assertEquals(TokenType.UPDATE, token.type);
        Assert.assertEquals("UPDATE", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.STRING, token.type);
        Assert.assertEquals("Employee", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.STRING, token.type);
        Assert.assertEquals("e", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.SET, token.type);
        Assert.assertEquals("SET", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.COLUMN, token.type);
        Assert.assertEquals("e.salary", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.COMPAREOP, token.type);
        Assert.assertEquals("=", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.PARAM, token.type);
        Assert.assertEquals(":s,", token.data);
        // Assert.assertEquals(TokenType.COMMA, itr.next().type);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.COLUMN, token.type);
        Assert.assertEquals("e.name", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.COMPAREOP, token.type);
        Assert.assertEquals("=", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.PARAM, token.type);
        Assert.assertEquals(":n2", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.WHERE, token.type);
        Assert.assertEquals("WHERE", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.COLUMN, token.type);
        Assert.assertEquals("e.name", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.COMPAREOP, token.type);
        Assert.assertEquals("=", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.PARAM, token.type);
        Assert.assertEquals(":n", token.data);
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void testDeleteLexer() {
        ArrayList<Token> tokens = Lexer.lex("DELETE FROM Employee e WHERE e.name = :n");
        Iterator<Token> itr = tokens.iterator();
        Token token = itr.next();
        Assert.assertEquals(TokenType.DELETE, token.type);
        Assert.assertEquals("DELETE", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.FROM, token.type);
        Assert.assertEquals("FROM", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.STRING, token.type);
        Assert.assertEquals("Employee", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.STRING, token.type);
        Assert.assertEquals("e", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.WHERE, token.type);
        Assert.assertEquals("WHERE", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.COLUMN, token.type);
        Assert.assertEquals("e.name", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.COMPAREOP, token.type);
        Assert.assertEquals("=", token.data);
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().type);
        token = itr.next();
        Assert.assertEquals(TokenType.PARAM, token.type);
        Assert.assertEquals(":n", token.data);
        Assert.assertFalse(itr.hasNext());
    }


}
