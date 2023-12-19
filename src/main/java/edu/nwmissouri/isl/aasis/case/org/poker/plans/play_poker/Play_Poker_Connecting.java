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
 * This state will check for a message from the super. Once received and the connection is
 * verified, it will attempt to register. If registration is successful it will move to the main {@code Play_Poker}
 * state.
 */
public enum Play_Poker_Connecting implements IPlanState<Play_Poker_Plan> {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Play_Poker_Connecting.class);
    private static final boolean debug = false;

    @Override
    public synchronized void Enter(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    }

    @Override
    public synchronized void Execute(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        if (debug) LOG.debug("Starting with instance goal: {}.", ig);
        Objects.requireNonNull(ec);
        Objects.requireNonNull(ig);
        Objects.requireNonNull(ec.getCapability(IPokerConnectCapability.class), "Role requires IPokerConnectCapability.");

        plan.heartBeat(this.getClass().getName());

        // initialize based on instance goal
        ec.getCapability(IPokerConnectCapability.class).init(ig);
        LOG.debug("agent: initialized capabilities from goal.");

        // if I'm the "real" persona in the sub holon agent
        if (ec.getCapability(ParticipateCapability.class) != null) {
            if (debug) LOG.debug("agent: connecting.");

            // check connections to parents - if any are not connected, attempt to connect
            ec.getCapability(IPokerConnectCapability.class).checkUpConnections();
            if (debug) LOG.debug("agent: checked broker connections.");

            if (ec.getCapability(IPokerConnectCapability.class).isAllConnected()) {
                LOG.info("{} fully connected to parent. Changing state.", ec.getUniqueIdentifier().toString());
                plan.getStateMachine().changeState(Play_Poker_Registering.INSTANCE, ec, ig);

            } else {
               LOG.debug("agent: {} failed to connect to broker. Will retry. {}. Unconnected={}",
                        ec.getUniqueIdentifier().toString(),
                        ec.getCapability(IPokerConnectCapability.class).getConnectionSummaryString(),
                        ec.getCapability(IPokerConnectCapability.class).getUnconnectedParents());
            }
        } else {
            if (debug) LOG.debug("proxy: Changing state.");
            plan.getStateMachine().changeState(Play_Poker_Registering.INSTANCE, ec, ig);
        }

        if ((RunManager.isStopped())) {
            LOG.info("STOP message received.");
            plan.getStateMachine().changeState(Play_Poker_Stop.INSTANCE, ec, ig);
        }
    }

    @Override
    public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        // Nothing
    }
}
