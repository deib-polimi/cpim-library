package it.polimi.modaclouds.cpimlibrary.exception;

/**
 * Exception thrown when problems occurs with the interaction with the migration system.
 *
 * @author Fabio Arcidiacono.
 */
public class MigrationException extends RuntimeException {

    public MigrationException(String msg) {
        super(msg);
    }

    public MigrationException(String msg, Throwable e) {
        super(msg, e);
    }
}
