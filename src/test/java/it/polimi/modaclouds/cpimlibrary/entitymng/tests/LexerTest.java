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

import java.util.Iterator;
import java.util.List;

/**
 * @author Fabio Arcidiacono.
 */
public class LexerTest {

    @Test
    public void testUppercaseLowercase() {
        List<Token> tokens = Lexer.lex("UPDATE Update update");
        Iterator<Token> itr = tokens.iterator();
        Assert.assertEquals(TokenType.UPDATE, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.UPDATE, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.UPDATE, itr.next().getType());
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("DELETE Delete delete");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.DELETE, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.DELETE, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.DELETE, itr.next().getType());
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("FROM From from");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.FROM, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.FROM, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.FROM, itr.next().getType());
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("SET Set set");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.SET, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.SET, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.SET, itr.next().getType());
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("WHERE Where where");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.WHERE, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.WHERE, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.WHERE, itr.next().getType());
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void testOperators() {
        List<Token> tokens = Lexer.lex("AND And and");
        Iterator<Token> itr = tokens.iterator();
        Assert.assertEquals(TokenType.LOGICOP, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.LOGICOP, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.LOGICOP, itr.next().getType());
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("OR Or or");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.LOGICOP, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.LOGICOP, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.LOGICOP, itr.next().getType());
        Assert.assertFalse(itr.hasNext());

        tokens = Lexer.lex("<> >= <= > < =");
        itr = tokens.iterator();
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        Assert.assertEquals(TokenType.COMPAREOP, itr.next().getType());
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void testUpdateLexer() {
        List<Token> tokens = Lexer.lex("UPDATE Employee e SET e.salary = :s, e.name = :n2 WHERE e.name = :n");
        Iterator<Token> itr = tokens.iterator();
        Token token = itr.next();
        Assert.assertEquals(TokenType.UPDATE, token.getType());
        Assert.assertEquals("UPDATE", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.STRING, token.getType());
        Assert.assertEquals("Employee", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.STRING, token.getType());
        Assert.assertEquals("e", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.SET, token.getType());
        Assert.assertEquals("SET", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.COLUMN, token.getType());
        Assert.assertEquals("e.salary", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.COMPAREOP, token.getType());
        Assert.assertEquals("=", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.PARAM, token.getType());
        Assert.assertEquals(":s,", token.getData());
        // Assert.assertEquals(TokenType.COMMA, itr.next().getType());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.COLUMN, token.getType());
        Assert.assertEquals("e.name", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.COMPAREOP, token.getType());
        Assert.assertEquals("=", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.PARAM, token.getType());
        Assert.assertEquals(":n2", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.WHERE, token.getType());
        Assert.assertEquals("WHERE", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.COLUMN, token.getType());
        Assert.assertEquals("e.name", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.COMPAREOP, token.getType());
        Assert.assertEquals("=", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.PARAM, token.getType());
        Assert.assertEquals(":n", token.getData());
        Assert.assertFalse(itr.hasNext());
    }

    @Test
    public void testDeleteLexer() {
        List<Token> tokens = Lexer.lex("DELETE FROM Employee e WHERE e.name = :n");
        Iterator<Token> itr = tokens.iterator();
        Token token = itr.next();
        Assert.assertEquals(TokenType.DELETE, token.getType());
        Assert.assertEquals("DELETE", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.FROM, token.getType());
        Assert.assertEquals("FROM", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.STRING, token.getType());
        Assert.assertEquals("Employee", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.STRING, token.getType());
        Assert.assertEquals("e", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.WHERE, token.getType());
        Assert.assertEquals("WHERE", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.COLUMN, token.getType());
        Assert.assertEquals("e.name", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.COMPAREOP, token.getType());
        Assert.assertEquals("=", token.getData());
        Assert.assertEquals(TokenType.WHITESPACE, itr.next().getType());
        token = itr.next();
        Assert.assertEquals(TokenType.PARAM, token.getType());
        Assert.assertEquals(":n", token.getData());
        Assert.assertFalse(itr.hasNext());
    }
}
