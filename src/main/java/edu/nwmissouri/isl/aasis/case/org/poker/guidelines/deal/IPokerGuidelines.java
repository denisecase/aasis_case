package edu.nwmissouri.isl.aasis.case.org.poker.guidelines.deal;

import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;

import java.util.HashSet;

/**
 * The goal parameter guidelines that tailor this broker's behavior for brokering power auctions.
 */
public interface IPokerGuidelines {

  public static IPokerGuidelines extractGuidelines(InstanceParameters params) {
    return (IPokerGuidelines) params.getValue(StringIdentifier.getIdentifier("PokerGuidelines"));
  }

  HashSet<String> getAuthorizedParticipants();

  void setAuthorizedParticipants(HashSet<String> authorizedParticipants);

  int getMaxIteration();

  void setMaxIteration(int maxIteration);

  long getOpenTimeSlice();

  void setOpenTimeSlice(long openTimeSlice);

  long getPurchaseTimeSlice();

  void setPurchaseTimeSlice(long purchaseTimeslice);

}
