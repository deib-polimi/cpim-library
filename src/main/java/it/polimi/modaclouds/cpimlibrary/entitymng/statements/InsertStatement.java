package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.CompareOperator;
import it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.Filter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Iterator;

/**
 * Represents an INSERT statement.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement
 */
@Data
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class InsertStatement extends Statement {

    /* (non-Javadoc)
     *
     * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement#addCondition(String, String, Object)
     */
    @Override
    public void addCondition(String name, String operator, Object value) {
        // INSERT statements does not support WHERE clause
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     *
     * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement#addCondition(String, it.polimi.modaclouds.cpimlibrary.entitymng.statements.utils.CompareOperator, Object)
     */
    @Override
    public void addCondition(String name, CompareOperator operator, Object value) {
        // INSERT statements does not support WHERE clause
        throw new UnsupportedOperationException();
    }

    /* (non-Javadoc)
     *
     * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement#addCondition(String)
     */
    @Override
    public void addCondition(String operator) {
        // INSERT statements does not support WHERE clause
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        String columns = "";
        String values = "";
        Iterator<Filter> entries = getFieldsIterator();
        while (entries.hasNext()) {
            Filter filter = entries.next();
            columns += filter.getColumn();
            values += "'" + filter.getValue() + "'";
            if (entries.hasNext()) {
                columns += ", ";
                values += ", ";
            }
        }
        return "INSERT INTO " + getTable() + " (" + columns + ") VALUES (" + values + ")";
    }
}