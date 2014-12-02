package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.operators.CompareOperator;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.operators.LogicOperator;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public abstract class Statement {

    @Getter
    @Setter
    private String table;
    private List<Filter> fields = new ArrayList<>();
    private LinkedList<Object> conditions = new LinkedList<>();

    public abstract StatementBuilder getBuilder();

    public Iterator<Filter> getFieldsIterator() {
        return fields.iterator();
    }

    public Iterator<Object> getConditionsIterator() {
        return conditions.iterator();
    }

    public void addCondition(String name, String operator, Object value) {
        this.conditions.add(new Filter(name, CompareOperator.fromString(operator), value));
    }

    public void addCondition(String name, CompareOperator operator, Object value) {
        this.conditions.add(new Filter(name, operator, value));
    }

    public void addCondition(String operator) {
        this.conditions.add(LogicOperator.valueOf(operator));
    }

    public void addField(String name, Object value) {
        this.fields.add(new Filter(name, CompareOperator.EQUAL, value));
    }

    public void addField(String name, String operator, Object value) {
        this.conditions.add(new Filter(name, CompareOperator.fromString(operator), value));
    }

    public boolean haveConditions() {
        return !conditions.isEmpty();
    }
}
