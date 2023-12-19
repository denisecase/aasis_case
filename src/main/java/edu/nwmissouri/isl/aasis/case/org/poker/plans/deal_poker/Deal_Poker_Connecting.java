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
 * This is the connecting step.  This step will continue until the simulation requirements for
 * being fully initially connected are met.  See {@code Scenario} for more information.
 * When complete, it will move to the processing registration state.
 */
public enum Deal_Poker_Connecting implements IPlanState<Deal_Poker_Plan> {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Deal_Poker_Connecting.class);
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
            plan.heartBeat(this.getClass().getName(),
                    String.format("Unconnected=%s. All registered=%s.",
                            ec.getCapability(IPokerConnectCapability.class).getUnconnectedChildren(ig).toString(),
                            ec.getCapability(PokerAdminCapability.class).processingRegistrationIsComplete(ig)));
        } catch (Exception ex) {
            plan.heartBeat(this.getClass().getName());
        }

        if (ec.getCapability(ParticipateCapability.class) != null) {
            if (debug) LOG.debug("agent: waiting. ");

//            ec.getCapability(IPokerConnectCapability.class).init(ig);
//            ec.getCapability(PokerAdminCapability.class).init(ig);
//            if (debug) LOG.debug("agent: initialized capabilities based on goal parameters. ");

            if (ec.getCapability(IPokerConnectCapability.class).isAllConnected(ig)) {
                if (debug) LOG.debug("Changing state.");
                plan.getStateMachine().changeState(Deal_Poker_Processing_Registrations.INSTANCE, ec, ig);
            }

            // check for connection messages from each child
            ec.getCapability(IPokerConnectCapability.class).checkDownConnections(ig);
            if (debug) LOG.debug("agent: Parent sent connection messages to sub holons.");

            else {
                if (debug)
                    LOG.debug("agent: {} failed to connect to all participants. Will retry. Verify associated goals have been triggered. {}. Unconnected={}",
                            ec.getUniqueIdentifier().toString(),
                            ec.getCapability(IPokerConnectCapability.class).getConnectionSummaryString(), ec.getCapability(IPokerConnectCapability.class).getUnconnectedChildren());
            }
        }

        // if I'm the proxy persona running my new organization
        else {
            if (debug) LOG.debug("proxy: waiting to finish connecting to all participants. ");

            if (debug) LOG.debug("proxy: Changing to main state");
            plan.getStateMachine().changeState(Deal_Poker.INSTANCE, ec, ig);
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
