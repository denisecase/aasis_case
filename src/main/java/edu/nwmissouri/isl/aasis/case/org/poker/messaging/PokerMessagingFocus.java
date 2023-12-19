package edu.nwmissouri.isl.aasis.case.org.poker.messaging;

import edu.ksu.cis.macr.aasis.messaging.IMessagingFocus;

/**
 * The primary focus of a targeted type of communications and messaging used in the simulation.
 */
public enum PokerMessagingFocus implements IMessagingFocus {

  AGENT_INTERNAL(0),

  /**
   * Used for exchanging messages related to buying and selling power in authorized auctions.
   */
  POKER(6),

  /**
   * Used for exchanging messages related to organizational administration in power broker organizations.
   */
  POKER_PARTICIPATE(7);

  private final int value;

  PokerMessagingFocus(int value) {
    this.value = value;
  }

  /**
   * Get the integer value of the type.
   *
   * @return - the integer value (1 is the top level of the hierarchy)
   */
  public int getIntegerValue() {
    return this.value;
  }

}