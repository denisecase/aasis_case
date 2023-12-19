package edu.nwmissouri.isl.aasis.case.org.poker.messages;

/**
 * The {@code PokerPerformative} indicates the types of {@code IMessages} that can be exchanged. 
 * Performatives indicate a primary characteristic of the message and 
 * may be used to configure behavior in plans.
 */

public enum PokerPerformative {

    /**
     * Associated message contains a play.
     */
    PLAY,


    /**
     * Associated message contains the results.
     */
    RESULT
}
