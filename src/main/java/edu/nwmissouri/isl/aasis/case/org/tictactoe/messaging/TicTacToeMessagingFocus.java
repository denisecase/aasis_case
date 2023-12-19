package edu.nwmissouri.isl.aasis.case.org.tictactoe.messaging;

import edu.ksu.cis.macr.aasis.messaging.IMessagingFocus;

/**
 The primary focus of a targeted type of communications and messaging used in the simulation.
 */
public enum TicTacToeMessagingFocus implements IMessagingFocus {

  AGENT_INTERNAL(0),

  /**
   Special exchange for secure authorization and authentication from central power distribution system control center (to configure an agent to participate in reactive power control).
   */
  CONTROL_CENTER(1),

  /**
   Used for domain-specific communications.
   */
  TICTACTOE(4),

  /**
   Used for communications related to organizational administration in a distributed control system for the organization.
   */
  TICTACTOE_PARTICIPATE(5),

  /**
   * Used to communicate primary messages.
   */
  PRIMARY(6);

  /**
   Get the integer value of the type.
  
   @return - the integer value (1 is the top level of the hierarchy)
   */
  public int getIntegerValue() {
    return this.value;
  }

  private final int value;

  TicTacToeMessagingFocus(int value) {
    this.value = value;
  }

}