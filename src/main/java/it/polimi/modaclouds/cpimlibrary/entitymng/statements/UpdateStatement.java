package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Expose methods to build UPDATE statements.
 *
 * @author Fabio Arcidiacono.
 * @see Statement
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class UpdateStatement extends Statement {

    private String tableName;

    public static Deque<Statement> build(Object entity) {
        Deque<Statement> stack = new ArrayDeque<>();
        stack.addFirst(new UpdateStatement());
        return stack;
    }

    @Override
    public String toString() {
        return "TODO UPDATE";
    }
}
