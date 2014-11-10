package it.polimi.modaclouds.cpimlibrary.entitymng;

import javax.persistence.Query;

/**
 * @author Fabio Arcidiacono.
 */
public class MigrationManager {

    private static MigrationManager instance = null;

    private MigrationManager() {
    }

    public static synchronized MigrationManager getInstance() {
        if (instance == null) {
            instance = new MigrationManager();
        }
        return instance;
    }

    public boolean isMigrating() {
        // TODO
        return false;
    }

    public void propagate(String statement) {
        // TODO
        // send statement to migration system
    }

    public String generateInsertStatement(Object entity) {
        //TODO
        // generate insert statement using reflections
        // need to check also CascadeType and generate inner object INSERT statement accordingly
        return null;
    }

    public boolean isUpdate(Query query) {
        //TODO
        return true;
    }

    public String generateUpdateStatement(Query query) {
        //TODO
        return null;
    }

    public String generateDeleteStatement(Query query) {
        //TODO
        return null;
    }
}
