package edu.nwmissouri.isl.aasis.case.org.poker.plans.play_poker;

import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.connect.IPokerConnectCapability;
import edu.nwmissouri.isl.aasis.case.org.poker.capabilities.participate.PokerParticipateCapability;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.ParticipateCapability;
import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This state will attempt to register with the organization head. It will change state once registration is complete.
 * state.
 */
public enum Play_Poker_Registering implements IPlanState<Play_Poker_Plan> {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Play_Poker_Registering.class);
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
        Objects.requireNonNull(ec.getCapability(PokerParticipateCapability.class), "Role requires PokerParticipateCapability.");

        plan.heartBeat(this.getClass().getName());

        // initialize based on instance goal
        ec.getCapability(IPokerConnectCapability.class).init(ig);
        ec.getCapability(PokerParticipateCapability.class).init(ig);
        LOG.info("agent: initialized capabilities from goal.");


        if (!ec.getCapability(IPokerConnectCapability.class).isAllConnected()) {
            LOG.info("{} no longer connected to parent. Changing state.", ec.getUniqueIdentifier().toString());
            plan.getStateMachine().changeState(Play_Poker_Connecting.INSTANCE, ec, ig);
        } else {
            LOG.info("{} connected to broker. Registering.", ec.getUniqueIdentifier().toString());

            // if I'm the "real" persona in the sub holon agent
            if (ec.getCapability(ParticipateCapability.class) != null) {
                LOG.info("agent: registering.");

                ec.getCapability(PokerParticipateCapability.class).doRegistration();
                if (debug) LOG.debug("{} tried registration. ", ec.getUniqueIdentifier().toString());

                if (ec.getCapability(PokerParticipateCapability.class).isRegistered()) {
                    LOG.info("{} REGISTERED.  Changing state. ", ec.getUniqueIdentifier().toString());
                    plan.getStateMachine().changeState(Play_Poker.INSTANCE, ec, ig);
                } else {
                    LOG.debug("agent: {} Connected but failed to register with broker. Will retry. Verify associated goals have been triggered. {}. Unregistered={}",
                            ec.getUniqueIdentifier().toString(),
                            ec.getCapability(IPokerConnectCapability.class).getConnectionSummaryString(), ec.getCapability(PokerParticipateCapability.class).getUnregisteredParents().toString());
                }
            }

            // if I'm the proxy persona participating in an outside organization
            else {
                LOG.debug("proxy: registering. ");

                // initialize based on instance goal
                ec.getCapability(IPokerConnectCapability.class).init(ig);
                LOG.info("proxy: intialized capabilities from goal.");

                ec.getCapability(PokerParticipateCapability.class).doRegistration(ig);
                if (debug) LOG.debug("proxy {} tried registration. ", ec.getUniqueIdentifier().toString());

                if (ec.getCapability(PokerParticipateCapability.class).isRegistered()) {

                    if (debug) LOG.debug("proxy REGISTERED. changing state");
                    plan.getStateMachine().changeState(Play_Poker.INSTANCE, ec, ig);
                } else {
                    LOG.debug("proxy {} Connected but failed to register with broker. Will retry. Verify associated goals have been triggered. {}. Unregistered={}",
                            ec.getUniqueIdentifier().toString(),
                            ec.getCapability(IPokerConnectCapability.class).getConnectionSummaryString(), ec.getCapability(PokerParticipateCapability.class).getUnregisteredParents().toString());
                }
            }
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
