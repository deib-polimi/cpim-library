package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Expose methods to build DELETE statements.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.Statement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DeleteStatement extends Statement {

    private String tableName;

    public static Deque<Statement> build(Object entity) {
        Deque<Statement> stack = new ArrayDeque<>();
        stack.addFirst(new DeleteStatement());
        return stack;
    }

    @Override
    public String toString() {
        return "TODO DELETE";
    }
}
