package edu.nwmissouri.isl.aasis.case.org.tictactoe.plans.play_tictactoe;


import edu.ksu.cis.macr.aasis.common.IConnections;
import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.DateTimeCapability;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.IPrimaryCommunicationCapability;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.ParticipateCapability;
import edu.nwmissouri.isl.aasis.case.primary.messages.IPrimaryMessage;
import edu.nwmissouri.isl.aasis.case.primary.messages.IPrimaryMessageContent;
import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * This is the main execute step in the associated plan and can be shared by all agents employing this plan.  If the list
 * of registered agents changes, it will revert to the prior initialization ("Play_TicTacToe_Init") state.  If it receives a
 * stop message, it will move to the stop state.
 */
public enum Play_TicTacToe implements IPlanState<Play_TicTacToe_Plan> {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Play_TicTacToe.class);
    private static final boolean debug = false;

    @Override
    public synchronized void Enter(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        // Nothing
    }

    @Override
    public synchronized void Execute(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        if (debug) LOG.debug("Starting with instance goal: {}.", ig);
        Objects.requireNonNull(ig);
        Objects.requireNonNull(ec);
        Objects.requireNonNull(ec.getCapability(ParticipateCapability.class), "Role requires SelfParticipateCapability.");
         Objects.requireNonNull(ec.getCapability(IPrimaryCommunicationCapability.class), "Role requires IPrimaryCommunicationCapability.");
        Objects.requireNonNull(ec.getCapability(DateTimeCapability.class), "Role requires DateTimeCapability.");

        // wait extra long
        plan.heartBeat(this.getClass().getName(), 10);

        // if I'm the "real" persona in the sub holon agent, listen for messages to forward
        if (ec.getCapability(ParticipateCapability.class) != null) {


            // get the timeslice
            long thisTimeSlice = ec.getCapability(DateTimeCapability.class).getTimeSlicesElapsedSinceStart();

            // check for a new power message from self
            IPrimaryMessage m = ec.getCapability(IPrimaryCommunicationCapability.class).checkFromSelf();

            if (m != null) {
                LOG.info("agent: org particapant received message from self: {}", m.toString());

                long messageTimeSlice = ((IPrimaryMessageContent) m.getContent()).getTimeSlice();
                LOG.debug("agent: This timeslice={}, message timeslice={}", thisTimeSlice, messageTimeSlice);

                // track with scenario for simulation testing
                RunManager.addPrimaryMessageSelfToSub(m.getLocalSender().toString(), m.getLocalReceiver().toString(), messageTimeSlice);

            
             //   ec.getCapability(IPrimaryCommunicationCapability.class).sendUp(m, parents);
             //   LOG.info("agent: participant forwarded message to org administrator: {}. Up list: {}", m, parents);
            } else {
                LOG.info("agent: got nothing from up. Current time slice is {}.", thisTimeSlice);
            }

        }
       

        if ((RunManager.isStopped())) {
            plan.getStateMachine().changeState(Play_TicTacToe_Stop.INSTANCE, ec, ig);
        }
    }

    @Override
    public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        // Nothing
    }
}
