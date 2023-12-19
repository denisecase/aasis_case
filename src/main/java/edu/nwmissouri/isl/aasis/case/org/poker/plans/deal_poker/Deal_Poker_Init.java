package edu.nwmissouri.isl.aasis.case.org.poker.plans.deal_poker;


import edu.nwmissouri.isl.aasis.case.agent_types.*;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.admin.DealPokerCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.admin.PokerAdminCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.connect.IPokerConnectCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.IPokerCommunicationCapability;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.ParticipateCapability;
import edu.ksu.cis.macr.aasis.config.RunManager;
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
public enum Deal_Poker_Init implements IPlanState<Deal_Poker_Plan> {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Deal_Poker_Init.class);
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
        Objects.requireNonNull(ec.getCapability(DealPokerCapability.class), "Role needs DealPokerCapability");
        Objects.requireNonNull(ec.getCapability(IPokerConnectCapability.class), "Role requires IPokerConnectCapability.");
        Objects.requireNonNull(ec.getCapability(PokerAdminCapability.class), "Role requires PokerAdminCapability.");

        plan.heartBeat(this.getClass().getName());

        // initialize based on instance goal
        ec.getCapability(DealPokerCapability.class).init(ig);
        ec.getCapability(IPokerConnectCapability.class).init(ig);
        ec.getCapability(PokerAdminCapability.class).init(ig);
        LOG.info("agent: initialized capabilities from goal.");

        // if I'm the "real" persona in the  agent, create and start the new org (which will handle registration)
        if (ec.getCapability(ParticipateCapability.class) != null) {
            LOG.info("Ready to form a new market organization. Instance goal is: {}.", ig);

            // create external (and load guidelines, read objects and agents from files)
           boolean success = ec.getCapability(PokerAdminCapability.class).createAndStartNewOrganization(ig);
            if (debug) LOG.debug("agent: New organization created and started. success = {}", success);
            if (success) {

                if (debug) LOG.debug("Changing state.");
                plan.getStateMachine().changeState(Deal_Poker_Connecting.INSTANCE, ec, ig);
            }
            // otherwise, keep trying..
        }

        // if I'm the proxy persona running my new organization
        else {
            LOG.info("Initializing proxy to use market goal model to generate auction/broker assignments. ");

            ec.getCapability(IPokerCommunicationCapability.class).initializeChildConnections(ig);
            if (debug) LOG.debug("proxy: Set auction participant guidelines.");

            // setup queues and bindings and send a hello to message to all children
            ec.getCapability(IPokerConnectCapability.class).connectDown(ig);
            if (debug) LOG.debug("proxy: Setup queues and bindings to connect to auction participants.");

            if (debug) LOG.debug("Messages sent. Changing state.");
            plan.getStateMachine().changeState(Deal_Poker_Connecting.INSTANCE, ec, ig);
        }
        if ((RunManager.isStopped())) {
            if (debug) LOG.debug("Changing state.");
            plan.getStateMachine().changeState(Deal_Poker_Stop.INSTANCE, ec, ig);
        }
    }

    @Override
    public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        // nothing
    }
}
