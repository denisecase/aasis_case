package edu.nwmissouri.isl.aasis.case.org.tictactoe.plans.referee_tictactoe;

import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.AbstractExecutablePlan;
import edu.ksu.cis.macr.organization.model.InstanceGoal;


public class Referee_TicTacToe_Plan extends AbstractExecutablePlan {

  public Referee_TicTacToe_Plan() {
    getStateMachine().setCurrentState(Referee_TicTacToe_Init.INSTANCE);
  }

  @Override
  public synchronized void execute(IExecutor ec, InstanceGoal<?> ig) {
    getStateMachine().update(ec, ig);
  }
}
