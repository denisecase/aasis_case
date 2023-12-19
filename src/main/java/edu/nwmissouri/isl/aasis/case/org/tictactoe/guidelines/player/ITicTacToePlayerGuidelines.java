package edu.nwmissouri.isl.aasis.case.org.tictactoe.guidelines.player;

import edu.ksu.cis.macr.goal.model.InstanceParameters;
import edu.ksu.cis.macr.organization.model.identifiers.StringIdentifier;

/**
 * The goal parameter guidelines that tailor this agent's personal behavior.
 */
public interface ITicTacToePlayerGuidelines {

  public static ITicTacToePlayerGuidelines extractGuidelines(final InstanceParameters params) {
    return (ITicTacToePlayerGuidelines) params.getValue(StringIdentifier.getIdentifier("tictactoePlayerGuidelines"));
  }

  public long getOpeningTimeSlice();

  void setOpeningTimeSlice(long openingTimeSlice);


}
