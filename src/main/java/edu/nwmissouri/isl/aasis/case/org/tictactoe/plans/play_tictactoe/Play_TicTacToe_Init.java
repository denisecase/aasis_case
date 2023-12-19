package edu.nwmissouri.isl.aasis.case.org.tictactoe.plans.play_tictactoe;

import edu.nwmissouri.isl.aasis.case.config.RunManager;
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
 This is the first step in the plan.  It performs initialization tasks once at the beginning of the plan. When complete,
 it will move to the next state.
 */
public enum Play_TicTacToe_Init implements IPlanState<Play_TicTacToe_Plan> {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(Play_TicTacToe_Init.class);
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
       Objects.requireNonNull(ec.getCapability(IPrimaryCommunicationCapability.class), "Role requires IPrimaryCommunicationCapability.");

    plan.heartBeat(this.getClass().getName());

    // if I'm the "real" persona in the agent
    if (ec.getCapability(ParticipateCapability.class) != null) {
        LOG.info("Sub-agent participant beginning plan.");


      // setup queues and bindings and send a hello to message to supers
      ec.getCapability(ITicTacToeConnectCapability.class).connectUp(ig);
      if (debug) LOG.debug("agent: Setup all queues and bindings to connect to super holon.");

        // then switch to Play_TicTacToe_Registering
        if (debug) LOG.debug("agent: Changing state.");
        plan.getStateMachine().changeState(Play_TicTacToe_Connecting.INSTANCE, ec, ig);
    }

    // if I'm the proxy persona participating in an outside organization
    else {
        LOG.info("Organization proxy beginning plan (used to determine assignments).");

      // get my connection guidelines for this one authorized connection
      ec.getCapability(ITicTacToeConnectCapability.class).init(ig);
      if (debug) LOG.debug("proxy: Set parent connection guidelines.");

         // then switch to Play_TicTacToe_Registering state
        if (debug) LOG.debug("proxy: Changing state.");
        plan.getStateMachine().changeState(Play_TicTacToe_Connecting.INSTANCE, ec, ig);
    }
    // if a (Scenario.isStopped()) is received, move to the stop state
    if ((RunManager.isStopped())) {
        LOG.debug("proxy: Moving to Play_TicTacToe_Stop");
        plan.getStateMachine().changeState(Play_TicTacToe_Stop.INSTANCE, ec, ig);
    }
  }


    @Override
    public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }
}
