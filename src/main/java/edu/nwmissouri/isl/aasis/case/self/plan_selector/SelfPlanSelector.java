package edu.nwmissouri.isl.aasis.case.self.plan_selector;

import edu.nwmissouri.isl.aasis.case.org.poker.plan_selector.PokerPlanSelector;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.plan_selector.TicTacToePlanSelector;
import edu.nwmissouri.isl.aasis.case.self.goals.SelfGoalIdentifiers;
import edu.nwmissouri.isl.aasis.case.self.plans.self_control.Self_Control_Plan;
import edu.nwmissouri.isl.aasis.case.self.roles.SelfRoleIdentifiers;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec_ps.IPlanSelector;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;

/**
 Class that defines the logic for selecting the plan to perform a role assigned to achieve a goal.
 */
public class SelfPlanSelector implements IPlanSelector {

  public SelfPlanSelector() {
  }

  /**
   Returns the plan that will perform this role to achieve the desired goal.
  
   @param roleIdentifier - the assigned role
   @param goalIdentifier - the goal to be achieved
   @return - the execution plan to perform the role
   */
  public synchronized IExecutablePlan getPlan(final UniqueIdentifier roleIdentifier,
      final UniqueIdentifier goalIdentifier) {

    IExecutablePlan result = null;
    if (roleIdentifier.equals(SelfRoleIdentifiers.Self_Control_Role)
        && goalIdentifier.equals(SelfGoalIdentifiers.Self_Control)) {
      result = new Self_Control_Plan();
    }
    if (result != null)
      return result;

    result = new PokerPlanSelector().getPlan(roleIdentifier, goalIdentifier);
    if (result != null)
      return result;

    result = new TicTacToePlanSelector().getPlan(roleIdentifier, goalIdentifier);

    return result;
  }

}
