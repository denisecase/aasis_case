package edu.nwmissouri.isl.aasis.case.org.poker.plans.deal_poker;

import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.AbstractExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.organization.model.InstanceGoal;

public class Deal_Poker_Plan extends AbstractExecutablePlan implements IExecutablePlan {

    public Deal_Poker_Plan() {
        getStateMachine().setCurrentState(Deal_Poker_Init.INSTANCE);
    }

    @Override
    public synchronized void execute(IExecutor ec, InstanceGoal<?> ig) {
        getStateMachine().update(ec, ig);
    }
}
