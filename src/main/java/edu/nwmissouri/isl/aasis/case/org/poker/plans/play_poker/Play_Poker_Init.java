package edu.nwmissouri.isl.aasis.case.org.poker.plans.play_poker;

import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.connect.IPokerConnectCapability;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.ParticipateCapability;
import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This is the first step in the plan.  It performs initialization tasks once at the beginning of the plan. When complete,
 * it will move to the main working state.
 */
public enum Play_Poker_Init implements IPlanState<Play_Poker_Plan> {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Play_Poker_Init.class);
    private static final boolean debug = false;

    @Override
    public synchronized void Enter(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        // nothing
    }

    @Override
    public synchronized void Execute(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        if (debug) LOG.debug("Starting with instance goal: {}.", ig);
        Objects.requireNonNull(ig);
        Objects.requireNonNull(ec);

        plan.heartBeat(this.getClass().getName());

        if (ec.getCapability(ParticipateCapability.class) != null) {
            Objects.requireNonNull(ec.getCapability(IPokerConnectCapability.class), "Role requires IPokerConnectCapability.");
            ec.getCapability(IPokerConnectCapability.class).connectUp(ig);
            plan.getStateMachine().changeState(Play_Poker_Connecting.INSTANCE, ec, ig);
        }

        // if I'm the proxy persona
        else {
            plan.getStateMachine().changeState(Play_Poker_Connecting.INSTANCE, ec, ig);
        }

        if ((RunManager.isStopped())) {
            plan.getStateMachine().changeState(Play_Poker_Stop.INSTANCE, ec, ig);
        }
    }

    @Override
    public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        // nothing
    }
}
