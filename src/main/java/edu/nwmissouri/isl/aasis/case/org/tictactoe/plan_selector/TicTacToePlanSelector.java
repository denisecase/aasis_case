package edu.nwmissouri.isl.aasis.case.org.tictactoe.plan_selector;

import edu.nwmissouri.isl.aasis.case.org.tictactoe.goals.TicTacToeGoalIdentifiers;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.plans.play_tictactoe.Play_TicTacToe_Plan;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.plans.referee_tictactoe.Referee_TicTacToe_Plan;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.roles.TicTacToeRoleIdentifiers;

import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec_ps.IPlanSelector;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;

/**
 Class that defines the logic for selecting the plan to perform a role assigned to achieve a goal.
 */
public class TicTacToePlanSelector implements IPlanSelector {

  public TicTacToePlanSelector() {
  }

  /**
   Returns the plan that will perform this role to achieve the desired goal.
  
   @param roleIdentifier - the assigned role
   @param goalIdentifier - the goal to be achieved
   @return - the execution plan to perform the role
   */
  public IExecutablePlan getPlan(final UniqueIdentifier roleIdentifier, final UniqueIdentifier goalIdentifier) {
    IExecutablePlan result = null;

    if (roleIdentifier.equals(TicTacToeRoleIdentifiers.Play_TicTacToe_Role)
        && goalIdentifier.equals(TicTacToeGoalIdentifiers.Play_TicTacToe)) {
      result = new Play_TicTacToe_Plan();
    } else if (roleIdentifier.equals(TicTacToeRoleIdentifiers.Referee_TicTacToe_Role)
        && goalIdentifier.equals(TicTacToeGoalIdentifiers.Referee_TicTacToe)) {
      result = new Referee_TicTacToe_Plan();
    }
    return result;
  }

}
