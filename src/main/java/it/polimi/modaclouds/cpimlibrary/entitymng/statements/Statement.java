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
