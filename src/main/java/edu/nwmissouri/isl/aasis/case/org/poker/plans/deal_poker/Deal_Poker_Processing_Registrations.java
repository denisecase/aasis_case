package edu.nwmissouri.isl.aasis.case.org.poker.plans.deal_poker;


import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.admin.PokerAdminCapability;
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
 * This is the  step.  This step will continue until the simulation requirements for
 * being fully initially connected are met.  See {@code Scenario} for more information.
 * When complete, it will move to the processing registration state.
 */
public enum Deal_Poker_Processing_Registrations implements IPlanState<Deal_Poker_Plan> {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Deal_Poker_Processing_Registrations.class);
    private static final boolean debug = false;

    @Override
    public synchronized void Enter(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        // Nothing
    }

    @Override
    public synchronized void Execute(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        if (debug) LOG.debug("Starting with instance goal: {}.", ig);
        Objects.requireNonNull(ec);
        Objects.requireNonNull(ig);
        Objects.requireNonNull(ec.getCapability(IPokerConnectCapability.class));
        Objects.requireNonNull(ec.getCapability(PokerAdminCapability.class));

        // initialize based on instance goal
        ec.getCapability(IPokerConnectCapability.class).init(ig);
        ec.getCapability(PokerAdminCapability.class).init(ig);
        LOG.info("agent: initialized capabilities from goal.");

        try {
            plan.heartBeat(this.getClass().getName(), String.format("Unconnected=%s. All registered=%s.",
                    ec.getCapability(IPokerConnectCapability.class).getUnconnectedChildren(ig).toString(),
                    ec.getCapability(PokerAdminCapability.class).processingRegistrationIsComplete(ig)));
        } catch (Exception ex) {
            plan.heartBeat(this.getClass().getName());
        }

        if (!ec.getCapability(IPokerConnectCapability.class).isAllConnected(ig)) {
            LOG.info("{} no longer connected to all participants. Changing state.", ec.getUniqueIdentifier().toString());
            plan.getStateMachine().changeState(Deal_Poker_Connecting.INSTANCE, ec, ig);
        } else {
            LOG.info("{} connected to all children. Processing registrations.", ec.getUniqueIdentifier().toString());

            if (ec.getCapability(ParticipateCapability.class) != null) {
                if (debug) LOG.debug("Connected. Waiting for agents to register. ");

                ec.getCapability(IPokerConnectCapability.class).init(ig);
                if (debug) LOG.debug("initialized capabilities based on goal parameters. ");

                if (debug) LOG.debug("Beginning registration.");
                ec.getCapability(PokerAdminCapability.class).processRegistrationMessages(ig);
                if (debug) LOG.debug("checked for registration messages.");

                if (ec.getCapability(PokerAdminCapability.class).processingRegistrationIsComplete()) {
                    if (debug) LOG.debug("REGISTRATION PROCESSING COMPLETE. Changing state.");
                    plan.getStateMachine().changeState(Deal_Poker.INSTANCE, ec, ig);
                } else {

                    if (debug) LOG.debug("Not all participants have registered.");
                }
            }
            // if I'm the proxy persona running my new holonic organization
            else {
                if (debug) LOG.debug("proxy Connected. Waiting for agents to register. ");

                ec.getCapability(IPokerConnectCapability.class).init(ig);
                ec.getCapability(PokerAdminCapability.class).init(ig);
                if (debug) LOG.debug("proxy: initialized capabilities based on goal parameters. ");

                ec.getCapability(PokerAdminCapability.class).processRegistrationMessages();
                if (debug) LOG.debug("proxy: checked for registration messages.");

                if (ec.getCapability(PokerAdminCapability.class).processingRegistrationIsComplete()) {
                    if (debug) LOG.debug("proxy REGISTRATION PROCESSING COMPLETE. Changing state.");
                    plan.getStateMachine().changeState(Deal_Poker.INSTANCE, ec, ig);
                } else {
                    if (debug) LOG.debug("proxy: Not all participants have registered.");
                }
            }
        }

        if ((RunManager.isStopped())) {
            plan.getStateMachine().changeState(Deal_Poker_Stop.INSTANCE, ec, ig);
        }
    }

    @Override
    public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        // Nothing
    }
}
