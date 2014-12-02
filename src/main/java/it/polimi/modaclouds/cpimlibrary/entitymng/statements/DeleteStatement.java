package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.DeleteBuilder;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.builders.StatementBuilder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

/**
 * Represents a DELETE statement.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement
 */
@Data
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DeleteStatement extends Statement {

    @Override
    public StatementBuilder getBuilder() {
        return new DeleteBuilder();
    }

    /*
     * DELETE statements does not support field lists
     */
    @Override
    public void addField(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    /*
     * DELETE statements does not support field lists
     */
    @Override
    public void addField(String name, String operator, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        if (!haveConditions()) {
            return "DELETE FROM " + getTable();
        } else {
            String conditions = "";
            Iterator entries = getConditionsIterator();
            while (entries.hasNext()) {
                Object next = entries.next();
                conditions += next.toString();
                if (entries.hasNext()) {
                    conditions += " ";
                }
            }
            return "DELETE FROM " + getTable() + " WHERE " + conditions;
        }
    }
}