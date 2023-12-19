package edu.nwmissouri.isl.aasis.case.org.tictactoe.guidelines.referee;

import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;

import java.util.HashSet;

/**
 * The goal parameter guidelines.
 */
public interface ITicTacToeGuidelines {

  public static ITicTacToeGuidelines extractGuidelines(InstanceParameters params) {
    return (ITicTacToeGuidelines) params.getValue(StringIdentifier.getIdentifier("TicTacToeGuidelines"));
  }

  HashSet<String> getAuthorizedParticipants();

  void setAuthorizedParticipants(HashSet<String> authorizedParticipants);

  long getOpenTimeSlice();

  void setOpenTimeSlice(long openTimeSlice);

  long getPurchaseTimeSlice();

  void setPurchaseTimeSlice(long purchaseTimeslice);

}
