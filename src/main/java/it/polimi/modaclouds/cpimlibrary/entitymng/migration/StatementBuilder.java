package it.polimi.modaclouds.cpimlibrary.entitymng.migration;

import javax.persistence.Query;

/**
 * Helpful methods to generate {@link it.polimi.modaclouds.cpimlibrary.entitymng.migration.Statement}.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.migration.Statement
 */
public class StatementBuilder {

    public static Statement generateInsertStatement(Object entity) {
        Statement s = new Statement();
        //TODO set statement things from entity
        return s;
    }

    public static Statement generateUpdateStatement(Object entity) {
        Statement s = new Statement();
        //TODO set statement things from query
        return s;
    }

    public static Statement generateDeleteStatement(Object entity) {
        Statement s = new Statement();
        //TODO set statement things from query
        return s;
    }

    public static Statement generateUpdateDeleteStatement(Query query) {
        if (!isUpdate(query)) {
            return generateDeleteStatement(query);
        }
        return generateUpdateStatement(query);
    }

    private static boolean isUpdate(Query query) {
        String stringQuery = query.toString();
        System.out.println(stringQuery);
        String[] tokens = stringQuery.split("\\s+");
        for (String token : tokens) {
            if ("UPDATE".equalsIgnoreCase(token)) {
                return true;
            }
        }
        return false;
    }

    private static Statement generateUpdateStatement(Query query) {
        Statement s = new Statement();
        //TODO set statement things from query
        return s;
    }

    private static Statement generateDeleteStatement(Query query) {
        Statement s = new Statement();
        //TODO set statement things from query
        return s;
    }
}
