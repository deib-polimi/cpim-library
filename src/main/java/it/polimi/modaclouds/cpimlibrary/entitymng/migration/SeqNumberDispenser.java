package it.polimi.modaclouds.cpimlibrary.entitymng.migration;

/**
 * @author Fabio Arcidiacono.
 */
public interface SeqNumberDispenser {

    /**
     * Gives the next sequence number assigned by migration system.
     *
     * @return the next sequence number
     */
    public int nextSequenceNumber();
}
