package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import it.polimi.modaclouds.cpimlibrary.entitymng.statements.operators.CompareOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a filter in the form 'column operator value'
 * where operator is one from {@link it.polimi.modaclouds.cpimlibrary.entitymng.statements.operators.CompareOperator}
 *
 * @author Fabio Arcidiacono.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Filter {
    private String column;
    private CompareOperator operator;
    private Object value;

    @Override
    public String toString() {
        return this.column + " " + this.operator.toString() + " '" + this.value + "'";
    }
}