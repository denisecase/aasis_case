package edu.nwmissouri.isl.aasis.case.org.tictactoe.plans.play_tictactoe;

import edu.nwmissouri.isl.aasis.case.config.RunManager;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.capabilities.connect.ITicTacToeConnectCapability;
import edu.nwmissouri.isl.aasis.case.org.tictactoe.capabilities.participate.TicTacToeParticipateCapability;
import edu.nwmissouri.isl.aasis.case.primary.capabilities.participate.ParticipateCapability;
import edu.ksu.cis.macr.obaa_pp.ec.base.IExecutor;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IExecutablePlan;
import edu.ksu.cis.macr.obaa_pp.ec.plans.IPlanState;
import edu.ksu.cis.macr.organization.model.InstanceGoal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 This state will attempt to register with the organization head. It will change state once registration is complete.
 state.
 */
public enum Play_TicTacToe_Registering implements IPlanState<Play_TicTacToe_Plan> {
  INSTANCE;
  private static final Logger LOG = LoggerFactory.getLogger(Play_TicTacToe_Registering.class);
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
    Objects.requireNonNull(ec.getCapability(TicTacToeParticipateCapability.class), "Role requires TicTacToeParticipateCapability.");

    plan.heartBeat(this.getClass().getName(),
            String.format("Unconnected=%s. Registered=%s.", ec.getCapability(ITicTacToeConnectCapability.class).getUnconnectedChildren().toString(),
                    ec.getCapability(TicTacToeParticipateCapability.class).isRegistered()));

      if (RunManager.isInitiallyConnected()) {

          // if I'm the "real" persona in the sub holon agent
          if (ec.getCapability(ParticipateCapability.class) != null) {

              if (debug) LOG.info("agent: registering.");

              // initialize based on instance goal
              ec.getCapability(ITicTacToeConnectCapability.class).initializeParentConnections(ig);
              ec.getCapability(TicTacToeParticipateCapability.class).initializeParentConnections(ig);


              // double-check still connected
              if (ec.getCapability(ITicTacToeConnectCapability.class).isAllConnected()) {
                  if (debug)
                      LOG.info("{} connected to super holon. Begin registration.", ec.getUniqueIdentifier().toString());

                  ec.getCapability(TicTacToeParticipateCapability.class).doRegistration();
                  if (debug) LOG.debug("{} tried registration. ", ec.getUniqueIdentifier().toString());

                  if (ec.getCapability(TicTacToeParticipateCapability.class).isRegistered()) {

                      if (debug) LOG.debug("Changing state.");
                      plan.getStateMachine().changeState(Play_TicTacToe.INSTANCE, ec, ig);
                  } else {
                      LOG.debug("agent: {} connected but failed to register with super holon. Will retry. Verify associated goals have been triggered. {}. Unregistered={}",
                              ec.getUniqueIdentifier().toString(),
                              ec.getCapability(ITicTacToeConnectCapability.class).getConnectionSummaryString(), ec.getCapability(TicTacToeParticipateCapability.class).getUnregisteredParents().toString());
                  }


              } else {
                  LOG.debug("agent: {} registering, but not connected. Returning to connect state. Verify associated goals have been triggered. {}. Unconnected={}",
                          ec.getUniqueIdentifier().toString(),
                          ec.getCapability(ITicTacToeConnectCapability.class).getConnectionSummaryString(), ec.getCapability(ITicTacToeConnectCapability.class).getUnconnectedParents());

                  if (debug) LOG.debug("Changing state.");
                  plan.getStateMachine().changeState(Play_TicTacToe_Connecting.INSTANCE, ec, ig);
              }
          }

          // if I'm the proxy persona participating in an outside organization
          else {
              LOG.debug("proxy: sub holon registering with super. ");

              // initialize based on instance goal
              ec.getCapability(ITicTacToeConnectCapability.class).initializeParentConnections(ig);

              if (debug) LOG.debug("proxy: Moving to Play_TicTacToe");
              plan.getStateMachine().changeState(Play_TicTacToe.INSTANCE, ec, ig);
          }
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
