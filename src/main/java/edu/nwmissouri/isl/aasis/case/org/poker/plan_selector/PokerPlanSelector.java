package edu.nwmissouri.isl.aasis.case.org.poker.plan_selector;

import edu.nwmissouri.isl.aasis.case.org.poker.goals.PokerGoalIdentifiers;
import edu.nwmissouri.isl.aasis.case.org.poker.plans.play_poker.Play_Poker_Plan;
import edu.nwmissouri.isl.aasis.case.org.poker.plans.deal_poker.Deal_Poker_Plan;
import edu.nwmissouri.isl.aasis.case.org.poker.roles.PokerRoleIdentifiers;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec_ps.IPlanSelector;
import edu.ksu.cis.macr.organization.model.identifiers.UniqueIdentifier;

/**
 * Class that defines the logic for selecting the plan to perform a role assigned to achieve a goal.
 */
public class PokerPlanSelector implements IPlanSelector {


    public PokerPlanSelector() {
    }

    public static PokerPlanSelector createPlanSelector() {
        return new PokerPlanSelector();
    }

    /**
     * Returns the plan that will perform this role to achieve the desired goal.
     *
     * @param roleIdentifier - the assigned role
     * @param goalIdentifier - the goal to be achieved
     * @return - the execution plan to perform the role
     */
    public IExecutablePlan getPlan(final UniqueIdentifier roleIdentifier, final UniqueIdentifier goalIdentifier) {

        IExecutablePlan result = null;


        // Auction power role
        if (roleIdentifier.equals(PokerRoleIdentifiers.Play_Poker_Role) && goalIdentifier.equals(
                PokerGoalIdentifiers.Play_Poker)) {
            result = new Play_Poker_Plan();
        } else if (roleIdentifier.equals(PokerRoleIdentifiers.Deal_Poker_Role) && goalIdentifier.equals(
                PokerGoalIdentifiers.Deal_Poker)) {
            result = new Deal_Poker_Plan();
        }


        return result;
    }


}
