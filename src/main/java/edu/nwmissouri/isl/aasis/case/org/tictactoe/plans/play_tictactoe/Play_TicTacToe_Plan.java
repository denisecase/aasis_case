package edu.nwmissouri.isl.aasis.case.org.tictactoe.plans.play_tictactoe;

import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.AbstractExecutablePlan;
import edu.ksu.cis.macr.organization.model.InstanceGoal;


public class Play_TicTacToe_Plan extends AbstractExecutablePlan {

  public Play_TicTacToe_Plan() {
    getStateMachine().setCurrentState(Play_TicTacToe_Init.INSTANCE);
  }

  @Override
  public synchronized void execute(IExecutor ec, InstanceGoal<?> ig) {
    getStateMachine().update(ec, ig);
  }
}
