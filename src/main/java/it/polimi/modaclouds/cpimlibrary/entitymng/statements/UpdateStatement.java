package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.UpdateBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

/**
 * Represents an UPDATE statement.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement
 */
@Data
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UpdateStatement extends Statement {

    @Override
    public StatementBuilder getBuilder() {
        return new UpdateBuilder();
    }

    @Override
    public String toString() {
        String setList = "";
        Iterator entries = getFieldsIterator();
        while (entries.hasNext()) {
            Filter filter = (Filter) entries.next();
            setList += filter.toString();
            if (entries.hasNext()) {
                setList += ", ";
            }
        }
        if (!haveConditions()) {
            return "UPDATE " + getTable() + " SET " + setList;
        } else {
            String conditions = "";
            entries = getConditionsIterator();
            while (entries.hasNext()) {
                Object next = entries.next();
                conditions += next.toString();
                if (entries.hasNext()) {
                    conditions += " ";
                }
            }
            return "UPDATE " + getTable() + " SET " + setList + " WHERE " + conditions;
        }
    }
}