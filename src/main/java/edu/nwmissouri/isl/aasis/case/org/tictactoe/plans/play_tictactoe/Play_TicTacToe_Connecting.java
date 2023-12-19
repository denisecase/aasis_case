package edu.nwmissouri.isl.aasis.case.org.tictactoe.plans.play_tictactoe;

import edu.nwmissouri.isl.aasis.case.config.RunManager;
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
 The {@code Play_TicTacToe_Registering} state will check for a message from the super. Once received and the connection is
 verified, it will attempt to register. If registration is successful it will move to the main {@code Play_TicTacToe}
 state.
 */
public enum Play_TicTacToe_Connecting implements IPlanState<Play_TicTacToe_Plan> {
  INSTANCE;
  private static final Logger LOG = LoggerFactory.getLogger(Play_TicTacToe_Connecting.class);
  private static final boolean debug = false;

  @Override
  public synchronized void Enter(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {

  }

  @Override
  public synchronized void Execute(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    if (debug) LOG.debug("Starting with instance goal: {}.", ig);
    Objects.requireNonNull(ec);
    Objects.requireNonNull(ig);
    Objects.requireNonNull(ec.getCapability(ITicTacToeConnectCapability.class), "Role requires ITicTacToeConnectCapability.");

    plan.heartBeat(this.getClass().getName());

//      plan.heartBeat(this.getClass().getName(),
//              String.format("Unconnected=%s. Registered=%s.", ec.getCapability(ITicTacToeConnectCapability.class).getUnconnectedChildren().toString(),
//                      ec.getCapability(ParticipateInOrganizationCapability.class).isRegistered()));

    // if I'm the "real" persona in the sub holon agent
    if (ec.getCapability(ParticipateCapability.class) != null) {

      if (debug) LOG.info("agent: connecting.");

      // initialize based on instance goal
      ec.getCapability(ITicTacToeConnectCapability.class).init(ig);
      if (debug) LOG.debug("agent: Set guidelines: {}.", ig);

      // check connections to parents - if any are not connected, attempt to connect
      ec.getCapability(ITicTacToeConnectCapability.class).connectToParents();

      if (ec.getCapability(ITicTacToeConnectCapability.class).isAllConnected()) {
        if (debug) LOG.info("{} connected to super holon. Changing state.", ec.getUniqueIdentifier().toString());

        plan.getStateMachine().changeState(Play_TicTacToe_Registering.INSTANCE, ec, ig);

      } else {
        LOG.debug("agent: {} failed to connect to super holon. Will retry. {}. Unconnected={}",
                ec.getUniqueIdentifier().toString(),
                ec.getCapability(ITicTacToeConnectCapability.class).getConnectionSummaryString(), ec.getCapability(ITicTacToeConnectCapability.class).getUnconnectedParents());
      }
    }

    // if I'm the proxy persona participating in an outside organization
    else {
      LOG.debug("proxy: sub holon registering with super. ");

      // initialize based on instance goal
      ec.getCapability(ITicTacToeConnectCapability.class).initializeParentConnections(ig);

      if (debug) LOG.debug("proxy: Changing state.");
      plan.getStateMachine().changeState(Play_TicTacToe_Registering.INSTANCE, ec, ig);
    }

    if ((RunManager.isStopped())) {
      LOG.info("STOP message received.");
      plan.getStateMachine().changeState(Play_TicTacToe_Stop.INSTANCE, ec, ig);
    }
  }

  @Override
  public synchronized void Exit(final IExecutablePlan plan, final IExecutor ec, final InstanceGoal<?> ig) {
    // Nothing
  }
}
