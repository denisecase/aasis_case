package edu.nwmissouri.isl.aasis.case.org.poker.plans.play_poker;


import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.AbstractExecutablePlan;
import edu.ksu.cis.macr.organization.model.InstanceGoal;


public class Play_Poker_Plan extends AbstractExecutablePlan {

    public Play_Poker_Plan() {
        getStateMachine().setCurrentState(Play_Poker_Init.INSTANCE);
    }

    @Override
    public synchronized void execute(IExecutor ec, InstanceGoal<?> ig) {
        getStateMachine().update(ec, ig);
    }
}
