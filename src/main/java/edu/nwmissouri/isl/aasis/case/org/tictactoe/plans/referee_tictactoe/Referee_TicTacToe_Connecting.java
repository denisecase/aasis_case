package edu.nwmissouri.isl.aasis.case.org.tictactoe.plans.referee_tictactoe;


import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.capabilities.admin.TicTacToeAdminCapability;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.capabilities.connect.ITicTacToeConnectCapability;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.ParticipateCapability;
import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 This is the connecting step.  This step will continue until the simulation requirements for
 being fully initially connected are met.  See {@code Scenario} for more information.
 When complete, it will move to the processing registration state.
 */
public enum Referee_TicTacToe_Connecting implements IPlanState<Referee_TicTacToe_Plan> {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Referee_TicTacToe_Connecting.class);
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
        Objects.requireNonNull(ec.getCapability(ITicTacToeConnectCapability.class));
        Objects.requireNonNull(ec.getCapability(TicTacToeAdminCapability.class));

        ec.getCapability(ITicTacToeConnectCapability.class).init(ig);
        ec.getCapability(TicTacToeAdminCapability.class).init(ig);
        if (debug) LOG.debug("agent: initialized capabilities based on goal parameters. ");

        try {
            plan.heartBeat(this.getClass().getName(),
                    String.format("Unconnected=%s. All registered=%s.", ec.getCapability(ITicTacToeConnectCapability.class).getUnconnectedChildren().toString(),
                            ec.getCapability(TicTacToeAdminCapability.class).processingRegistrationIsComplete()));
        }
        catch(Exception ex){
            plan.heartBeat(this.getClass().getName());
        }

        if (ec.getCapability(ParticipateCapability.class) != null) {
            if (debug) LOG.debug("agent: waiting. ");

            if (ec.getCapability(ITicTacToeConnectCapability.class).isAllConnected()) {
                if (debug) LOG.debug("Changing state.");
                plan.getStateMachine().changeState(Referee_TicTacToe_Processing_Registrations.INSTANCE, ec, ig);
            }

            // check for connection messages from each child
            boolean allConnected = ec.getCapability(ITicTacToeConnectCapability.class).connectToChildren();
            if (debug) LOG.debug("agent: Parent sent connection messages to sub holons. All connected = {}", allConnected);

            if (ec.getCapability(ITicTacToeConnectCapability.class).isAllConnected()) {
                if (debug) LOG.debug("Changing state.");
                plan.getStateMachine().changeState(Referee_TicTacToe_Processing_Registrations.INSTANCE, ec, ig);
            }
            else {
                LOG.debug("agent: {} failed to connect to all sub holons. Will retry. Verify associated goals have been triggered. {}. Unconnected={}",
                        ec.getUniqueIdentifier().toString(),
                        ec.getCapability(ITicTacToeConnectCapability.class).getConnectionSummaryString(), ec.getCapability(ITicTacToeConnectCapability.class).getUnconnectedChildren());
            }
        }

        // if I'm the proxy persona running my new holonic organization
        else {
            if (debug) LOG.debug("proxy: super holon waiting to finish connecting to all subs. ");

            // initialize based on instance goal
            ec.getCapability(ITicTacToeConnectCapability.class).init(ig);
            if (debug) LOG.debug("proxy: initialized connections to sub holons based on goal parameters. ");

            if (debug) LOG.debug("proxy: Changing to main super holon state");
            plan.getStateMachine().changeState(Referee_TicTacToe.INSTANCE, ec, ig);
        }

        if ((RunManager.isStopped())) {
            plan.getStateMachine().changeState(Referee_TicTacToe_Stop.INSTANCE, ec, ig);
        }
    }

    @Override
    public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        // Nothing
    }
}
