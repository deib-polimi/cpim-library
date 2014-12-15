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
package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.CompareOperator;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.Filter;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.LogicOperator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Abstract class to maintain information about statements.
 *
 * @author Fabio Arcidiacono.
 */
public abstract class Statement {

    @Getter @Setter private String table;
    /**
     * maintains fields both for SET clause in UPDATE and for values in INSERT statement
     */
    private List<Filter> fields = new ArrayList<>();
    /**
     * maintain conditions in the WHERE clause
     */
    private LinkedList<Object> conditions = new LinkedList<>();

    public Iterator<Filter> getFieldsIterator() {
        return fields.iterator();
    }

    public Iterator<Object> getConditionsIterator() {
        return conditions.iterator();
    }

    public void addField(String name, Object value) {
        this.fields.add(new Filter(name, CompareOperator.EQUAL, value));
    }

    public void addCondition(String name, CompareOperator operator, Object value) {
        this.conditions.add(new Filter(name, operator, value));
    }

    public void addCondition(String name, String operator, Object value) {
        this.conditions.add(new Filter(name, CompareOperator.fromString(operator), value));
    }

    public void addCondition(String operator) {
        this.conditions.add(LogicOperator.valueOf(operator));
    }

    public boolean haveConditions() {
        return !conditions.isEmpty();
    }
}
