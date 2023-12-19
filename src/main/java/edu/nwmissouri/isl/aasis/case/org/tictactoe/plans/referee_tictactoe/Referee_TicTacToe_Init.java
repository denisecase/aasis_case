package edu.nwmissouri.isl.aasis.case.org.tictactoe.plans.referee_tictactoe;

import edu.ksu.cis.macr.aasis.agent.persona.IOrganization;
import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.capabilities.admin.TicTacToeAdminCapability;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.capabilities.admin.TicTacToeRefereeCapability;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.capabilities.connect.ITicTacToeConnectCapability;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.IPrimaryCommunicationCapability;
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
 * it will move to the next state.
 */
public enum Referee_TicTacToe_Init implements IPlanState<Referee_TicTacToe_Plan> {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Referee_TicTacToe_Init.class);
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
        Objects.requireNonNull(ec.getCapability(ITicTacToeConnectCapability.class), "Role requires ITicTacToeConnectCapability.");
        Objects.requireNonNull(ec.getCapability(TicTacToeAdminCapability.class), "Role requires TicTacToeAdminCapability.");
        Objects.requireNonNull(ec.getCapability(TicTacToeRefereeCapability.class), "Role requires TicTacToeRefereeCapability.");
        Objects.requireNonNull(ec.getCapability(IPrimaryCommunicationCapability.class), "Role requires IPrimaryCommunicationCapability.");

        plan.heartBeat(this.getClass().getName());

        // if I'm the "real" persona in the super holon agent, create and start the new org (which will handle registration)
        if (ec.getCapability(ParticipateCapability.class) != null) {

            // create external (and load guidelines, read objects and agents from files)
            IOrganization org = ec.getCapability(TicTacToeAdminCapability.class).createAndStartNewOrganization(ig);
            if (debug) LOG.debug("agent: New organization created. {}", org);

            // load the list of persona from the agent file
            ec.getCapability(TicTacToeAdminCapability.class).loadPersona();
            if (debug) LOG.debug("agent: Agents loaded from specification into {}.", org.getName());

            // start the new organization (kick off the goal model)
            ec.getCapability(TicTacToeAdminCapability.class).startOrganization();
            LOG.info("agent: New organization {} started.", org.getName());

            // set holon guidelines from top-down instance goals
            ec.getCapability(TicTacToeRefereeCapability.class).initializeFromGoal(ig);
            if (debug) LOG.debug("agent: Set holonic guidelines for {}.", org.getName());

            if (debug) LOG.debug("Changing state.");
            plan.getStateMachine().changeState(Referee_TicTacToe_Connecting.INSTANCE, ec, ig);
        }

        // if I'm the proxy persona running my new holonic organization
        else {
            LOG.info("Initializing proxy to use grid goal model to generate control assignments. ");

            // initialize my guidelines as defined in the parametrized instance goal
            ec.getCapability(ITicTacToeConnectCapability.class).init(ig);
            if (debug) LOG.debug("proxy: Set child connection guidelines.");

            ec.getCapability(IPrimaryCommunicationCapability.class).initializeChildConnections(ig);
            if (debug) LOG.debug("proxy: Set child power communication (external comm) guidelines.");

            // setup queues and bindings and send a hello to message to all children
            ec.getCapability(ITicTacToeConnectCapability.class).connectDown();
            if (debug) LOG.debug("proxy: Setup queues and bindings to connect to sub holons.");

            if (debug) LOG.debug("Changing state.");
            plan.getStateMachine().changeState(Referee_TicTacToe_Connecting.INSTANCE, ec, ig);
        }
        if ((RunManager.isStopped())) {
            if (debug) LOG.debug("Changing state.");
            plan.getStateMachine().changeState(Referee_TicTacToe_Stop.INSTANCE, ec, ig);
        }

    }

    @Override
    public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
        // Nothing
    }
}
