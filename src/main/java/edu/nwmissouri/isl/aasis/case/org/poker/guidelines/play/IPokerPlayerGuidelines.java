package edu.nwmissouri.isl.aasis.case.org.poker.guidelines.play;

import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;

/**
 * The goal parameter guidelines that tailor this agent's personal behavior.
 */
public interface IPokerPlayerGuidelines {

  public static IPokerPlayerGuidelines extractGuidelines(final InstanceParameters params) {
    return (IPokerPlayerGuidelines) params.getValue(StringIdentifier.getIdentifier("playerGuidelines"));
  }

  public long getOpeningTimeSlice();

  void setOpeningTimeSlice(long openingTimeSlice);

  public long getPurchaseTimeSlice();

  void setPurchaseTimeSlice(long purchaseTimeSlice);

}
