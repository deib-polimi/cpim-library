package it.polimi.modaclouds.cpimlibrary.exception;

/**
 * Unchecked exception thrown by NoSQL service when some problems
 * occurs when reading persistence metadata.
 *
 * @author Fabio Arcidiacono.
 */
public class PersistenceMetadataException extends RuntimeException {

    public PersistenceMetadataException(String msg) {
        super(msg);
    }

    public PersistenceMetadataException(String msg, Throwable e) {
        super(msg, e);
    }
}
