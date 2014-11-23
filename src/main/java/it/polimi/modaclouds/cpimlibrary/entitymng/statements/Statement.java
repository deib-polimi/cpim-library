package it.polimi.modaclouds.cpimlibrary.entitymng.statements;

import javax.management.Query;

/**
 * Represent a generic statement to be sent to the migration system.
 *
 * @author Fabio Arcidiacono.
 * @see it.polimi.modaclouds.cpimlibrary.entitymng.statements.InsertStatement
 */
public abstract class Statement {

    public static boolean isUpdate(Query query) {
        return matchStringInQuery(query, "UPDATE");
    }

    public static boolean isDelete(Query query) {
        return matchStringInQuery(query, "DELETE");
    }

    private static boolean matchStringInQuery(Query query, String toFind) {
        String[] tokens = tokenize(query);
        for (String token : tokens) {
            if (token.equalsIgnoreCase(toFind)) {
                return true;
            }
        }
        return false;
    }

    private static String[] tokenize(Query query) {
        String stringQuery = query.toString();
        System.out.println(stringQuery);
        return stringQuery.split("\\s+");
    }
}
